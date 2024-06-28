package net.geforcemods.securitycraft.blockentities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IEMPAffectedBE;
import net.geforcemods.securitycraft.api.ILockable;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.IntOption;
import net.geforcemods.securitycraft.blocks.SonicSecuritySystemBlock;
import net.geforcemods.securitycraft.items.SonicSecuritySystemItem;
import net.geforcemods.securitycraft.misc.BlockEntityTracker;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ITickingBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

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
	private Set<BlockPos> linkedBlocks = new HashSet<>();
	/** Whether or not this Sonic Security System is on or off */
	private boolean isActive = true;
	/** Whether or not this Sonic Security System is currently recording a new note combination */
	private boolean isRecording = false;
	private ArrayList<NoteWrapper> recordedNotes = new ArrayList<>();
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
			if (!isLinkedToBlock())
				return;

			if (pingCooldown > 0)
				pingCooldown--;
			else {
				ArrayList<BlockPos> blocksToRemove = new ArrayList<>();
				Iterator<BlockPos> iterator = linkedBlocks.iterator();

				while (iterator.hasNext()) {
					BlockPos blockPos = iterator.next();

					if (!(level.getBlockEntity(blockPos) instanceof ILockable))
						blocksToRemove.add(blockPos);
				}

				// This delinking part is in a separate loop to prevent a ConcurrentModificationException
				for (BlockPos posToRemove : blocksToRemove) {
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
	public void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);

		// If there are blocks to save but the tag doesn't have a CompoundNBT
		// to store them in, create one (shouldn't be needed)
		if (!linkedBlocks.isEmpty() && !tag.contains("LinkedBlocks"))
			tag.put("LinkedBlocks", new ListTag());

		Iterator<BlockPos> iterator = linkedBlocks.iterator();

		while (iterator.hasNext()) {
			BlockPos blockToSave = iterator.next();
			CompoundTag nbt = NbtUtils.writeBlockPos(blockToSave);

			tag.getList("LinkedBlocks", Tag.TAG_COMPOUND).add(nbt);

			if (!linkedBlocks.contains(blockToSave))
				linkedBlocks.add(blockToSave);
		}

		saveNotes(tag);
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
	public void load(CompoundTag tag) {
		super.load(tag);

		if (tag.contains("LinkedBlocks")) {
			ListTag list = tag.getList("LinkedBlocks", Tag.TAG_COMPOUND);

			// Read each saved position and add it to the linkedBlocks list
			for (int i = 0; i < list.size(); i++) {
				CompoundTag linkedBlock = list.getCompound(i);
				BlockPos linkedBlockPos = NbtUtils.readBlockPos(linkedBlock);

				linkedBlocks.add(linkedBlockPos);
			}
		}

		recordedNotes.clear();
		loadNotes(tag, recordedNotes);

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
	 * @param tag The tag to save the notes to
	 */
	public void saveNotes(CompoundTag tag) {
		ListTag notes = new ListTag();

		for (NoteWrapper note : recordedNotes) {
			CompoundTag noteNbt = new CompoundTag();

			noteNbt.putInt("noteID", note.noteID);
			noteNbt.putString("instrument", note.instrumentName);
			notes.add(noteNbt);
		}

		tag.put("Notes", notes);
	}

	/**
	 * Loads notes saved on a tag to a collection
	 *
	 * @param tag The tag containing the notes
	 * @param recordedNotes The collection to save the notes to
	 */
	public static <T extends Collection<NoteWrapper>> void loadNotes(CompoundTag tag, T recordedNotes) {
		if (tag.contains("Notes")) {
			ListTag list = tag.getList("Notes", Tag.TAG_COMPOUND);

			for (int i = 0; i < list.size(); i++) {
				CompoundTag note = list.getCompound(i);

				recordedNotes.add(new NoteWrapper(note.getInt("noteID"), note.getString("instrument")));
			}
		}
	}

	/**
	 * Copies the positions over from the SSS item's tag into this block entity.
	 *
	 * @param itemTag The CompoundTag of the Sonic Security System item to transfer over
	 */
	public boolean transferPositionsFromItem(CompoundTag itemTag) {
		Set<BlockPos> positions = SonicSecuritySystemItem.stackTagToBlockPosSet(itemTag);

		// If the block has already been linked with, don't add it to the list
		positions.removeIf(this::isLinkedToBlock);
		linkedBlocks.addAll(positions);

		if (!linkedBlocks.isEmpty())
			sync();

		return true;
	}

	/**
	 * @return If this Sonic Security System is linked to another block
	 */
	public boolean isLinkedToBlock() {
		return !linkedBlocks.isEmpty();
	}

	/**
	 * @return If this Sonic Security System is linked to a block at a specific position
	 * @param linkedBlockPos the position of the block to check
	 */
	public boolean isLinkedToBlock(BlockPos linkedBlockPos) {
		if (linkedBlocks.isEmpty())
			return false;

		return linkedBlocks.contains(linkedBlockPos);
	}

	/**
	 * Delinks this Sonic Security System from the given block
	 */
	public void delink(BlockPos linkedBlockPos, boolean shouldSync) {
		if (linkedBlocks.isEmpty())
			return;

		linkedBlocks.remove(linkedBlockPos);

		if (shouldSync)
			sync();
	}

	/**
	 * Delinks this Sonic Security System from all other blocks
	 */
	public void delinkAll() {
		linkedBlocks.clear();
		sync();
	}

	/**
	 * @return Returns the number of blocks that this Sonic Security System is linked to
	 */
	public int getNumberOfLinkedBlocks() {
		return linkedBlocks.size();
	}

	/**
	 * @return Returns the block positions that this Sonic Security System is linked to
	 */
	public Set<BlockPos> getLinkedBlocks() {
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
	 * @param instrumentName the name of the instrument that played the note
	 */
	public void recordNote(int noteID, String instrumentName) {
		recordedNotes.add(new NoteWrapper(noteID, instrumentName));

		if (!level.isClientSide)
			sync();
	}

	/**
	 * Listen to a note and check whether or not it is the next correct note that was recorded by the Sonic Security System.
	 * Handles the case where the whole correct tune was played
	 *
	 * @param noteID the ID of the note that was played
	 * @param instrumentName the name of the instrument that played the note
	 */
	public void listenToNote(int noteID, String instrumentName) {
		// No notes
		if (getNumberOfNotes() == 0 || listenPos >= getNumberOfNotes())
			return;

		if (!isListening) {
			isListening = true;
			sync();
		}

		if (recordedNotes.get(listenPos++).isSameNote(noteID, instrumentName)) {
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

	public boolean wasCorrectTunePlayed() {
		return wasCorrectTunePlayed;
	}

	public float getOriginalRadarRotationDegrees() {
		return oRadarRotationDegrees;
	}

	public float getRadarRotationDegrees() {
		return radarRotationDegrees;
	}

	/**
	 * A simple wrapper that makes it slightly easier to store and compare notes with
	 */
	public static record NoteWrapper(int noteID, String instrumentName) {
		/**
		 * Checks to see if a passed note ID and instrument matches the info of this note
		 *
		 * @param note the note ID to check
		 * @param instrument the instrument name of the note
		 * @return
		 */
		public boolean isSameNote(int note, String instrument) {
			return noteID == note && instrumentName.equals(instrument);
		}
	}
}
