package net.geforcemods.securitycraft.blockentities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableBlockEntity;
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
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;

public class SonicSecuritySystemBlockEntity extends CustomizableBlockEntity implements ITickable, IEMPAffectedBE {
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
	private IntOption signalLength = new IntOption(this::getPos, "signalLength", 60, 5, 400, 5); //20 seconds max
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

	@Override
	public void update() {
		// Add this SSS to the global tracker if it has not already been added
		if (!tracked) {
			BlockEntityTracker.SONIC_SECURITY_SYSTEM.track(this);
			tracked = true;
		}

		if (!world.isRemote) {
			if (!isActive())
				return;

			if (wasCorrectTunePlayed()) {
				if (powerCooldown > 0)
					powerCooldown--;
				else {
					wasCorrectTunePlayed = false;
					world.setBlockState(pos, world.getBlockState(pos).withProperty(SonicSecuritySystemBlock.POWERED, false));
					BlockUtils.updateIndirectNeighbors(world, pos, SCContent.sonicSecuritySystem, EnumFacing.DOWN);
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

					if (!(world.getTileEntity(blockPos) instanceof ILockable))
						blocksToRemove.add(blockPos);
				}

				// This delinking part is in a separate loop to prevent a ConcurrentModificationException
				for (BlockPos posToRemove : blocksToRemove) {
					delink(posToRemove, false);
					sync();
				}

				// Play the ping sound if it was not disabled
				if (emitsPings && !isRecording)
					world.playSound(null, pos, SCSounds.PING.event, SoundCategory.BLOCKS, 0.3F, 1.0F);

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
	public void invalidate() {
		super.invalidate();

		// Stop tracking SSSs when they are removed from the world
		BlockEntityTracker.SONIC_SECURITY_SYSTEM.stopTracking(this);
	}

	@Override
	public void onModuleRemoved(ItemStack stack, ModuleType module, boolean toggled) {
		if (module == ModuleType.REDSTONE) {
			world.setBlockState(pos, world.getBlockState(pos).withProperty(SonicSecuritySystemBlock.POWERED, false));
			BlockUtils.updateIndirectNeighbors(world, pos, SCContent.sonicSecuritySystem, EnumFacing.DOWN);
		}

		super.onModuleRemoved(stack, module, toggled);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);

		// If there are blocks to save but the tag doesn't have a CompoundNBT
		// to store them in, create one (shouldn't be needed)
		if (!linkedBlocks.isEmpty() && !tag.hasKey("LinkedBlocks"))
			tag.setTag("LinkedBlocks", new NBTTagList());

		Iterator<BlockPos> iterator = linkedBlocks.iterator();

		while (iterator.hasNext()) {
			BlockPos blockToSave = iterator.next();
			NBTTagCompound nbt = NBTUtil.createPosTag(blockToSave);

			tag.getTagList("LinkedBlocks", Constants.NBT.TAG_COMPOUND).appendTag(nbt);

			if (!linkedBlocks.contains(blockToSave))
				linkedBlocks.add(blockToSave);
		}

		saveNotes(tag);
		tag.setBoolean("emitsPings", emitsPings);
		tag.setBoolean("isActive", isActive);
		tag.setBoolean("isRecording", isRecording);
		tag.setBoolean("isListening", isListening);
		tag.setInteger("listenPos", listenPos);
		tag.setBoolean("correctTuneWasPlayed", wasCorrectTunePlayed());
		tag.setInteger("powerCooldown", powerCooldown);
		tag.setBoolean("shutDown", shutDown);
		tag.setBoolean("disableBlocksWhenTuneIsPlayed", disableBlocksWhenTuneIsPlayed);
		return tag;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);

		if (tag.hasKey("LinkedBlocks")) {
			NBTTagList list = tag.getTagList("LinkedBlocks", Constants.NBT.TAG_COMPOUND);

			// Read each saved position and add it to the linkedBlocks list
			for (int i = 0; i < list.tagCount(); i++) {
				NBTTagCompound linkedBlock = list.getCompoundTagAt(i);
				BlockPos linkedBlockPos = NBTUtil.getPosFromTag(linkedBlock);

				linkedBlocks.add(linkedBlockPos);
			}
		}

		recordedNotes.clear();
		loadNotes(tag, recordedNotes);

		if (tag.hasKey("emitsPings"))
			emitsPings = tag.getBoolean("emitsPings");

		if (tag.hasKey("isActive"))
			isActive = tag.getBoolean("isActive");

		isRecording = tag.getBoolean("isRecording");
		isListening = tag.getBoolean("isListening");
		listenPos = tag.getInteger("listenPos");
		wasCorrectTunePlayed = tag.getBoolean("correctTuneWasPlayed");
		powerCooldown = tag.getInteger("powerCooldown");
		shutDown = tag.getBoolean("shutDown");
		disableBlocksWhenTuneIsPlayed = tag.getBoolean("disableBlocksWhenTuneIsPlayed");
	}

	/**
	 * Saves this tile entity's notes to a tag
	 *
	 * @param tag The tag to save the notes to
	 */
	public void saveNotes(NBTTagCompound tag) {
		NBTTagList notes = new NBTTagList();

		for (NoteWrapper note : recordedNotes) {
			NBTTagCompound noteNbt = new NBTTagCompound();

			noteNbt.setInteger("noteID", note.noteID);
			noteNbt.setString("instrument", note.instrumentName);
			notes.appendTag(noteNbt);
		}

		tag.setTag("Notes", notes);
	}

	/**
	 * Loads notes saved on a tag to a collection
	 *
	 * @param tag The tag containing the notes
	 * @param recordedNotes The collection to save the notes to
	 */
	public static <T extends Collection<NoteWrapper>> void loadNotes(NBTTagCompound tag, T recordedNotes) {
		if (tag.hasKey("Notes")) {
			NBTTagList list = tag.getTagList("Notes", Constants.NBT.TAG_COMPOUND);

			for (int i = 0; i < list.tagCount(); i++) {
				NBTTagCompound note = list.getCompoundTagAt(i);

				recordedNotes.add(new NoteWrapper(note.getInteger("noteID"), note.getString("instrument")));
			}
		}
	}

	/**
	 * Copies the positions over from the SSS item's tag into this TileEntity.
	 *
	 * @param itemTag The CompoundNBT tag of the Sonic Security System item to transfer over
	 */
	public void transferPositionsFromItem(NBTTagCompound itemTag) {
		Set<BlockPos> positions = SonicSecuritySystemItem.stackTagToBlockPosSet(itemTag);

		// If the block has already been linked with, don't add it to the list
		positions.removeIf(this::isLinkedToBlock);
		linkedBlocks.addAll(positions);

		if (!linkedBlocks.isEmpty())
			sync();
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
		world.notifyNeighborsOfStateChange(pos, SCContent.sonicSecuritySystem, false);

		if (!world.isRemote)
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

		if (!world.isRemote)
			sync();
	}

	/**
	 * Listen to a note and check whether or not it is the next correct note that was recorded by the Sonic Security System
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
					world.setBlockState(getPos(), getWorld().getBlockState(getPos()).withProperty(SonicSecuritySystemBlock.POWERED, true));
					BlockUtils.updateIndirectNeighbors(world, getPos(), SCContent.sonicSecuritySystem, EnumFacing.DOWN);
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

	@Override
	public boolean isShutDown() {
		return shutDown;
	}

	@Override
	public void setShutDown(boolean shutDown) {
		this.shutDown = shutDown;
		sync();
	}

	public void setDisableBlocksWhenTuneIsPlayed(boolean disableBlocksWhenTuneIsPlayed) {
		this.disableBlocksWhenTuneIsPlayed = disableBlocksWhenTuneIsPlayed;
	}

	public boolean disablesBlocksWhenTuneIsPlayed() {
		return disableBlocksWhenTuneIsPlayed;
	}

	/**
	 * A simple wrapper that makes it slightly easier to store and compare notes with
	 */
	public static class NoteWrapper {
		public final int noteID;
		public final String instrumentName;

		public NoteWrapper(int note, String instrument) {
			this.noteID = note;
			this.instrumentName = instrument;
		}

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
