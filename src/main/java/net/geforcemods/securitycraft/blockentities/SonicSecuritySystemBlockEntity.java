package net.geforcemods.securitycraft.blockentities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IEMPAffectedBE;
import net.geforcemods.securitycraft.api.ILockable;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.IntOption;
import net.geforcemods.securitycraft.blocks.SonicSecuritySystemBlock;
import net.geforcemods.securitycraft.components.GlobalPositions;
import net.geforcemods.securitycraft.components.Notes;
import net.geforcemods.securitycraft.components.Notes.NoteWrapper;
import net.geforcemods.securitycraft.misc.BlockEntityTracker;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ITickingBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap.Builder;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueInput.TypedInputList;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.ValueOutput.TypedOutputList;

public class SonicSecuritySystemBlockEntity extends DisguisableBlockEntity implements ITickingBlockEntity, IEMPAffectedBE {
	/** The delay between each ping sound in ticks */
	private static final int PING_DELAY = 100;
	/**
	 * The delay allowed between note block sounds while listening to a note sequence. Notes played within this many ticks of
	 * each other will be considered as part of the same "combination." Notes played after the delay has elapsed will start a new
	 * combination
	 */
	private static final int LISTEN_DELAY = 60;
	/** The listening and recording range of Sonic Security Systems (perhaps a config option?) */
	public static final int MAX_RANGE = 30;
	/** The maximum number of blocks that a Sonic Security System can be linked to at once (perhaps a config option?) */
	public static final int MAX_LINKED_BLOCKS = 30;
	/** Whether the ping sound should be emitted or not */
	private boolean emitsPings = true;
	private int pingCooldown = PING_DELAY;
	private IntOption signalLength = new IntOption("signalLength", 60, 5, 400, 5); //20 seconds max
	/** Used to control the number of ticks that Sonic Security Systems emit redstone power for */
	private int powerCooldown = 0;
	private float radarRotationDegrees = 0;
	private float oRadarRotationDegrees = 0;
	/** A list containing all of the blocks that this SSS is linked to */
	private List<GlobalPos> linkedBlocks = new ArrayList<>();
	/** Whether or not this Sonic Security System is on or off */
	private boolean isActive = true;
	/** Whether or not this Sonic Security System is currently recording a new note combination */
	private boolean isRecording = false;
	private List<NoteWrapper> recordedNotes = new ArrayList<>();
	private boolean wasCorrectTunePlayed = false;
	/** Whether or not this Sonic Security System is currently listening to notes */
	private boolean isListening = false;
	private int listeningTimer = LISTEN_DELAY;
	/**
	 * This field keeps track of the number of correct notes that have been listened to in order. If this number matches the size
	 * of the recordedNotes array, then the correct combination has been played
	 */
	private int listenPos = 0;
	private boolean tracked = false;
	private boolean shutDown = false;
	private boolean disableBlocksWhenTuneIsPlayed = false;

	public SonicSecuritySystemBlockEntity(BlockPos pos, BlockState state) {
		super(SCContent.SONIC_SECURITY_SYSTEM_BLOCK_ENTITY.get(), pos, state);
	}

	@Override
	public void tick(Level level, BlockPos pos, BlockState state) {
		// Add this SSS to the global tracker if it has not already been added
		if (!tracked) {
			BlockEntityTracker.SONIC_SECURITY_SYSTEM.track(this);
			tracked = true;
		}

		if (!level.isClientSide) {
			if (!isActive())
				return;

			if (wasCorrectTunePlayed) {
				if (powerCooldown > 0)
					powerCooldown--;
				else {
					wasCorrectTunePlayed = false;
					level.setBlockAndUpdate(worldPosition, level.getBlockState(worldPosition).setValue(SonicSecuritySystemBlock.POWERED, false));
					BlockUtils.updateIndirectNeighbors(level, worldPosition, SCContent.SONIC_SECURITY_SYSTEM.get(), Direction.DOWN);
				}
			}

			// If the SSS is currently listening to a note, check to see
			// if the timer has run out and needs to be reset
			if (isListening) {
				if (listeningTimer > 0) {
					listeningTimer--;
					return;
				}
				else
					stopListening();
			}

			if (pingCooldown > 0)
				pingCooldown--;
			else {
				// If this SSS isn't linked to any blocks, return as no sound should
				// be emitted and no blocks need to be removed
				if (linkedBlocks.stream().allMatch(Objects::isNull))
					return;

				ArrayList<GlobalPos> blocksToRemove = new ArrayList<>();
				Iterator<GlobalPos> iterator = linkedBlocks.iterator();

				while (iterator.hasNext()) {
					GlobalPos globalPos = iterator.next();

					if (globalPos != null && !(level.getBlockEntity(globalPos.pos()) instanceof ILockable))
						blocksToRemove.add(globalPos);
				}

				// This delinking part is in a separate loop to prevent a ConcurrentModificationException
				for (GlobalPos posToRemove : blocksToRemove) {
					delink(posToRemove, false);
					sync();
				}

				// Play the ping sound if it was not disabled
				if (emitsPings && !isRecording)
					level.playSound(null, worldPosition, SCSounds.PING.event, SoundSource.BLOCKS, 0.3F, 1.0F);

				pingCooldown = PING_DELAY;
			}
		}
		else {
			oRadarRotationDegrees = getRadarRotationDegrees();

			if (isActive() || isRecording()) {
				// Turn the radar dish slightly
				radarRotationDegrees = getRadarRotationDegrees() + 0.15F;

				if (getRadarRotationDegrees() >= 360)
					radarRotationDegrees = 0;
			}
		}
	}

	@Override
	public void setRemoved() {
		super.setRemoved();
		// Stop tracking SSSs when they are removed from the world
		BlockEntityTracker.SONIC_SECURITY_SYSTEM.stopTracking(this);
	}

	@Override
	public void onModuleRemoved(ItemStack stack, ModuleType module, boolean toggled) {
		if (module == ModuleType.REDSTONE) {
			level.setBlockAndUpdate(worldPosition, level.getBlockState(worldPosition).setValue(SonicSecuritySystemBlock.POWERED, false));
			BlockUtils.updateIndirectNeighbors(level, worldPosition, SCContent.SONIC_SECURITY_SYSTEM.get(), Direction.DOWN);
		}

		super.onModuleRemoved(stack, module, toggled);
	}

	@Override
	public void saveAdditional(ValueOutput tag) {
		super.saveAdditional(tag);

		TypedOutputList<GlobalPos> linkedBlocksList = tag.list("linked_blocks", GlobalPos.CODEC.orElse(GlobalPositions.DUMMY_GLOBAL_POS));
		TypedOutputList<NoteWrapper> notesList = tag.list("notes", NoteWrapper.CODEC);

		linkedBlocks.forEach(entry -> {
			if (entry == null)
				linkedBlocksList.add(GlobalPositions.DUMMY_GLOBAL_POS);
			else
				linkedBlocksList.add(entry);
		});
		recordedNotes.forEach(notesList::add);
		tag.putBoolean("emitsPings", emitsPings);
		tag.putBoolean("isActive", isActive);
		tag.putBoolean("isRecording", isRecording);
		tag.putBoolean("isListening", isListening);
		tag.putInt("listenPos", listenPos);
		tag.putBoolean("correctTuneWasPlayed", wasCorrectTunePlayed);
		tag.putInt("powerCooldown", powerCooldown);
		tag.putBoolean("shutDown", shutDown);
		tag.putBoolean("disableBlocksWhenTuneIsPlayed", disableBlocksWhenTuneIsPlayed);
	}

	@Override
	public void loadAdditional(ValueInput tag) {
		super.loadAdditional(tag);

		//TODO: does linked block saving and loading work with and the same as old data? test loading data from the old version with multiple bound blocks, and missing ones inbetween
		//what happens when this tries to load old BE data that has an empty compound tag?
		TypedInputList<GlobalPos> linkedBlocksList = tag.listOrEmpty("linked_blocks", GlobalPos.CODEC.orElse(GlobalPositions.DUMMY_GLOBAL_POS));
		TypedInputList<NoteWrapper> notesList = tag.listOrEmpty("notes", NoteWrapper.CODEC);

		linkedBlocks = new ArrayList<>();
		recordedNotes.clear();
		linkedBlocksList.forEach(entry -> {
			if (entry.equals(GlobalPositions.DUMMY_GLOBAL_POS))
				linkedBlocks.add(null);
			else
				linkedBlocks.add(entry);
		});
		notesList.forEach(recordedNotes::add);
		emitsPings = tag.getBooleanOr("emitsPings", emitsPings);
		isActive = tag.getBooleanOr("isActive", isActive);
		isRecording = tag.getBooleanOr("isRecording", false);
		isListening = tag.getBooleanOr("isListening", false);
		listenPos = tag.getIntOr("listenPos", 0);
		wasCorrectTunePlayed = tag.getBooleanOr("correctTuneWasPlayed", false);
		powerCooldown = tag.getIntOr("powerCooldown", 0);
		shutDown = tag.getBooleanOr("shutDown", false);
		disableBlocksWhenTuneIsPlayed = tag.getBooleanOr("disableBlocksWhenTuneIsPlayed", false);
	}

	/**
	 * @param linkedPos the position of the block to check
	 * @return If this Sonic Security System is linked to a block at a specific position
	 */
	public boolean isLinkedToBlock(BlockPos linkedPos) {
		return !linkedBlocks.isEmpty() && linkedBlocks.stream().filter(Objects::nonNull).map(GlobalPos::pos).anyMatch(linkedPos::equals);
	}

	/**
	 * Delinks this Sonic Security System from the given block
	 */
	public void delink(GlobalPos linkedGlobalPos, boolean shouldSync) {
		if (linkedBlocks.isEmpty())
			return;

		linkedBlocks.remove(linkedGlobalPos);

		if (shouldSync)
			sync();
	}

	/**
	 * @return Returns the block positions that this Sonic Security System is linked to
	 */
	public List<GlobalPos> getLinkedBlocks() {
		return linkedBlocks;
	}

	/**
	 * @return If this Sonic Security System is emitting ping sounds
	 */
	public boolean pings() {
		return emitsPings;
	}

	/**
	 * Toggles the ping emission sound on or off
	 *
	 * @param pings true if the periodic ping sounds should play, false if not
	 */
	public void setPings(boolean pings) {
		emitsPings = pings;
	}

	/**
	 * @return Whether or not this Sonic Security System is actively running
	 */
	public boolean isActive() {
		return isActive && !isShutDown();
	}

	/**
	 * Toggle the Sonic Security System on or off
	 *
	 * @param active true if the SSS should be powered on, false if not
	 */
	public void setActive(boolean active) {
		isActive = active;
	}

	/**
	 * @return True if this Sonic Security System is currently recording notes produced by Note Blocks, false otherwise
	 */
	public boolean isRecording() {
		return isRecording;
	}

	/**
	 * Toggle the recording state of the Sonic Security System
	 *
	 * @param recording true if the SSS should be recording, false if not
	 */
	public void setRecording(boolean recording) {
		isRecording = recording;
	}

	/**
	 * @return True if this Sonic Security System if listening to notes currently being played, false otherwise
	 */
	public boolean isListening() {
		return isListening;
	}

	/**
	 * Toggle the listening state of the Sonic Security System on
	 */
	public void startListening() {
		isListening = true;
	}

	/**
	 * Toggle the listening state of the Sonic Security System off and properly reset everything
	 */
	public void stopListening() {
		resetListeningTimer();
		listenPos = 0;
		isListening = false;
		level.updateNeighborsAt(worldPosition, SCContent.SONIC_SECURITY_SYSTEM.get());

		if (!level.isClientSide)
			sync();
	}

	/**
	 * Resets the delay before the SSS stops listening to new notes for the same tune
	 */
	public void resetListeningTimer() {
		listeningTimer = LISTEN_DELAY;
	}

	/**
	 * Record a note to use in the audio "passcode"
	 *
	 * @param noteID the ID of the note that was played
	 * @param instrument the instrument that played the note
	 * @param customSoundId the id of an optional custom sound, empty if none was played
	 */
	public void recordNote(int noteID, NoteBlockInstrument instrument, String customSoundId) {
		recordedNotes.add(new NoteWrapper(noteID, instrument.getSerializedName(), customSoundId));

		if (!level.isClientSide)
			sync();
	}

	/**
	 * Listen to a note and check whether or not it is the next correct note that was recorded by the Sonic Security System.
	 * Handles the case where the whole correct tune was played
	 *
	 * @param noteID the ID of the note that was played
	 * @param instrument the instrument that played the note
	 * @param customSoundId the id of an optional custom sound, empty if none was played
	 */
	public void listenToNote(int noteID, NoteBlockInstrument instrument, String customSoundId) {
		// No notes
		if (getNumberOfNotes() == 0 || listenPos >= getNumberOfNotes())
			return;

		if (!isListening) {
			isListening = true;
			sync();
		}

		if (recordedNotes.get(listenPos++).isSameNote(noteID, instrument, customSoundId)) {
			resetListeningTimer();

			// true if the entire tune was correctly played, false if it was only partly played but more notes are needed
			if (listenPos >= recordedNotes.size()) {
				wasCorrectTunePlayed = true;
				powerCooldown = signalLength.get();

				if (isModuleEnabled(ModuleType.REDSTONE)) {
					level.setBlockAndUpdate(getBlockPos(), getLevel().getBlockState(getBlockPos()).setValue(SonicSecuritySystemBlock.POWERED, true));
					BlockUtils.updateIndirectNeighbors(getLevel(), getBlockPos(), SCContent.SONIC_SECURITY_SYSTEM.get(), Direction.DOWN);
				}
			}
		}

		// An incorrect note was played
	}

	/**
	 * @return The notes that this Sonic Security System has recorded
	 */
	public List<NoteWrapper> getRecordedNotes() {
		return recordedNotes;
	}

	/**
	 * @return The number of notes that this Sonic Security System has recorded
	 */
	public int getNumberOfNotes() {
		return recordedNotes.size();
	}

	/**
	 * Clears all recorded notes
	 */
	public void clearNotes() {
		recordedNotes.clear();
	}

	private void sync() {
		setChanged();
		level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
	}

	@Override
	public boolean isShutDown() {
		return shutDown;
	}

	@Override
	public void setShutDown(boolean shutDown) {
		this.shutDown = shutDown;
	}

	public void setDisableBlocksWhenTuneIsPlayed(boolean disableBlocksWhenTuneIsPlayed) {
		this.disableBlocksWhenTuneIsPlayed = disableBlocksWhenTuneIsPlayed;
	}

	public boolean disablesBlocksWhenTuneIsPlayed() {
		return disableBlocksWhenTuneIsPlayed;
	}

	@Override
	public ModuleType[] acceptedModules() {
		return new ModuleType[] {
				ModuleType.ALLOWLIST, ModuleType.REDSTONE, ModuleType.DISGUISE
		};
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[] {
				signalLength
		};
	}

	public ItemStack getItem() {
		ItemStack stack = new ItemStack(SCContent.SONIC_SECURITY_SYSTEM_ITEM.get());

		stack.applyComponents(collectComponents());
		return stack;
	}

	@Override
	protected void applyImplicitComponents(DataComponentGetter input) {
		super.applyImplicitComponents(input);

		GlobalPositions sssLinkedBlocks = input.get(SCContent.SSS_LINKED_BLOCKS);

		if (sssLinkedBlocks != null)
			linkedBlocks = new ArrayList<>(sssLinkedBlocks.positions()); //needs to be modifiable
		else
			linkedBlocks = new ArrayList<>();

		linkedBlocks.removeIf(Objects::isNull);
		recordedNotes = new ArrayList<>(input.getOrDefault(SCContent.NOTES, Notes.EMPTY).notes());
	}

	@Override
	protected void collectImplicitComponents(Builder builder) {
		super.collectImplicitComponents(builder);
		linkedBlocks = new ArrayList<>(linkedBlocks);

		int linkedBlocksCount = linkedBlocks.size();

		if (linkedBlocksCount < MAX_LINKED_BLOCKS) {
			for (int i = linkedBlocksCount; i < MAX_LINKED_BLOCKS; i++) {
				linkedBlocks.add(null);
			}
		}
		else if (linkedBlocksCount > MAX_LINKED_BLOCKS)
			linkedBlocks = new ArrayList<>(linkedBlocks.subList(0, MAX_LINKED_BLOCKS));

		builder.set(SCContent.SSS_LINKED_BLOCKS, new GlobalPositions(new ArrayList<>(linkedBlocks)));
		builder.set(SCContent.NOTES, new Notes(new ArrayList<>(recordedNotes)));
	}

	@Override
	public void removeComponentsFromTag(ValueOutput tag) {
		super.removeComponentsFromTag(tag);
		tag.discard("linked_blocks");
		tag.discard("notes");
	}

	public boolean wasCorrectTunePlayed() {
		return wasCorrectTunePlayed;
	}

	public float getOriginalRadarRotationDegrees() {
		return oRadarRotationDegrees;
	}

	public float getRadarRotationDegrees() {
		return radarRotationDegrees;
	}
}
