package net.geforcemods.securitycraft.blockentities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.CustomizableBlockEntity;
import net.geforcemods.securitycraft.api.IEMPAffectedBE;
import net.geforcemods.securitycraft.api.ILockable;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.IntOption;
import net.geforcemods.securitycraft.blocks.SonicSecuritySystemBlock;
import net.geforcemods.securitycraft.components.IndexedPositions;
import net.geforcemods.securitycraft.components.IndexedPositions.Entry;
import net.geforcemods.securitycraft.components.Notes;
import net.geforcemods.securitycraft.components.Notes.NoteWrapper;
import net.geforcemods.securitycraft.misc.BlockEntityTracker;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ITickingBlockEntity;
import net.geforcemods.securitycraft.util.IncreasingInteger;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap.Builder;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;

public class SonicSecuritySystemBlockEntity extends CustomizableBlockEntity implements ITickingBlockEntity, IEMPAffectedBE {
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
	/** Whether the ping sound should be emitted or not */
	private boolean emitsPings = true;
	private int pingCooldown = PING_DELAY;
	private IntOption signalLength = new IntOption("signalLength", 60, 5, 400, 5, true); //20 seconds max
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

			// If this SSS isn't linked to any blocks, return as no sound should
			// be emitted and no blocks need to be removed
			if (linkedBlocks.isEmpty())
				return;

			if (pingCooldown > 0)
				pingCooldown--;
			else {
				ArrayList<GlobalPos> blocksToRemove = new ArrayList<>();
				Iterator<GlobalPos> iterator = linkedBlocks.iterator();

				while (iterator.hasNext()) {
					GlobalPos globalPos = iterator.next();

					if (!(level.getBlockEntity(globalPos.pos()) instanceof ILockable))
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
	public void saveAdditional(CompoundTag tag, HolderLookup.Provider lookupProvider) {
		super.saveAdditional(tag, lookupProvider);

		ListTag list = new ListTag();

		for (GlobalPos blockToSave : linkedBlocks) {
			list.add(GlobalPos.CODEC.encodeStart(lookupProvider.createSerializationContext(NbtOps.INSTANCE), blockToSave).getOrThrow());
		}

		tag.put("linked_blocks", list);
		saveNotes(tag, lookupProvider);
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
	public void loadAdditional(CompoundTag tag, HolderLookup.Provider lookupProvider) {
		super.loadAdditional(tag, lookupProvider);

		linkedBlocks = new ArrayList<>();

		if (tag.contains("linked_blocks")) {
			ListTag list = tag.getList("linked_blocks", Tag.TAG_COMPOUND);

			for (Tag entry : list) {
				try {
					linkedBlocks.add(GlobalPos.CODEC.decode(NbtOps.INSTANCE, entry).result().get().getFirst());
				}
				catch (Exception exception) {
					SecurityCraft.LOGGER.error("Failed to load global pos in Sonic Security System at position {}: {}", worldPosition, entry, exception);
				}
			}
		}
		else if (tag.contains("LinkedBlocks")) {
			ListTag list = tag.getList("LinkedBlocks", Tag.TAG_COMPOUND);

			for (int i = 0; i < list.size(); i++) {
				CompoundTag linkedBlock = list.getCompound(i);
				BlockPos linkedBlockPos = Utils.readBlockPos(linkedBlock);

				linkedBlocks.add(new GlobalPos(level != null ? level.dimension() : ResourceKey.create(Registries.DIMENSION, new ResourceLocation("overworld")), linkedBlockPos));
			}
		}

		recordedNotes.clear();
		loadNotes(tag);

		if (tag.contains("emitsPings"))
			emitsPings = tag.getBoolean("emitsPings");

		if (tag.contains("isActive"))
			isActive = tag.getBoolean("isActive");

		isRecording = tag.getBoolean("isRecording");
		isListening = tag.getBoolean("isListening");
		listenPos = tag.getInt("listenPos");
		wasCorrectTunePlayed = tag.getBoolean("correctTuneWasPlayed");
		powerCooldown = tag.getInt("powerCooldown");
		shutDown = tag.getBoolean("shutDown");
		disableBlocksWhenTuneIsPlayed = tag.getBoolean("disableBlocksWhenTuneIsPlayed");
	}

	/**
	 * Saves this block entity's notes to a tag
	 *
	 * @param stack The tag to save the notes to
	 * @param lookupProvider lookup for registry entries
	 */
	public void saveNotes(CompoundTag tag, HolderLookup.Provider lookupProvider) {
		ListTag list = new ListTag();

		for (NoteWrapper note : recordedNotes) {
			list.add(NoteWrapper.CODEC.encodeStart(lookupProvider.createSerializationContext(NbtOps.INSTANCE), note).getOrThrow());
		}

		tag.put("notes", list);
	}

	/**
	 * Loads notes saved on tag to a collection
	 *
	 * @param tag The tag containing the notes
	 */
	public void loadNotes(CompoundTag tag) {
		recordedNotes = new ArrayList<>();

		if (tag.contains("notes"))
			recordedNotes.addAll(Notes.CODEC.decode(NbtOps.INSTANCE, tag).result().get().getFirst().notes());
		else if (tag.contains("Notes")) {
			ListTag list = tag.getList("Notes", Tag.TAG_COMPOUND);

			for (int i = 0; i < list.size(); i++) {
				CompoundTag note = list.getCompound(i);

				recordedNotes.add(new NoteWrapper(note.getInt("noteID"), note.getString("instrument"), note.getString("customSoundId")));
			}
		}
	}

	/**
	 * @param linkedPos the position of the block to check
	 * @return If this Sonic Security System is linked to a block at a specific position
	 */
	public boolean isLinkedToBlock(BlockPos linkedPos) {
		return !linkedBlocks.isEmpty() && linkedBlocks.stream().map(GlobalPos::pos).anyMatch(linkedPos::equals);
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
		level.blockUpdated(worldPosition, SCContent.SONIC_SECURITY_SYSTEM.get());

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
				ModuleType.ALLOWLIST, ModuleType.REDSTONE
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
	protected void applyImplicitComponents(DataComponentInput input) {
		super.applyImplicitComponents(input);
		linkedBlocks = input.getOrDefault(SCContent.INDEXED_POSITIONS, IndexedPositions.EMPTY).positions().stream().map(Entry::globalPos).collect(Collectors.toList()); //needs to be modifiable
		recordedNotes = new ArrayList<>(input.getOrDefault(SCContent.NOTES, Notes.EMPTY).notes());
	}

	@Override
	protected void collectImplicitComponents(Builder builder) {
		IncreasingInteger counter = new IncreasingInteger(1);

		super.collectImplicitComponents(builder);
		builder.set(SCContent.INDEXED_POSITIONS, new IndexedPositions(linkedBlocks.stream().map(pos -> new Entry(counter.get(), pos)).toList()));
		builder.set(SCContent.NOTES, new Notes(new ArrayList<>(recordedNotes)));
	}

	@Override
	public void removeComponentsFromTag(CompoundTag tag) {
		super.removeComponentsFromTag(tag);
		tag.remove("linked_blocks");
		tag.remove("notes");
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
