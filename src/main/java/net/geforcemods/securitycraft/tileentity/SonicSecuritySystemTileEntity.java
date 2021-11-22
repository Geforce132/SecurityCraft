package net.geforcemods.securitycraft.tileentity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.ILockable;
import net.geforcemods.securitycraft.api.SecurityCraftTileEntity;
import net.geforcemods.securitycraft.containers.GenericTEContainer;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.geforcemods.securitycraft.network.client.SyncSSSSettingsOnClient;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.PacketDistributor;

public class SonicSecuritySystemTileEntity extends SecurityCraftTileEntity implements INamedContainerProvider {

	// The delay between each ping sound in ticks
	private static final int PING_DELAY = 100;
	private static final int LISTEN_DELAY = 60;

	// How far away can this SSS reach (possibly a config option?)
	public static final int MAX_RANGE = 30;

	// How many blocks can be linked to a SSS (another config option?)
	public static final int MAX_LINKED_BLOCKS = 30;

	// Whether the ping sound should be emitted or not
	private boolean emitsPings = true;

	private int pingCooldown = PING_DELAY;
	private int powerCooldown = 40;
	public float radarRotationDegrees = 0;

	// A list containing all of the blocks that this SSS is linked to
	public Set<BlockPos> linkedBlocks = new HashSet<>();

	private boolean isActive = true;

	private boolean isRecording = false;
	private ArrayList<NoteWrapper> recordedNotes = new ArrayList<>();
	public boolean shouldEmitPower = false;

	private boolean isListening = false;
	private int listeningTimer = LISTEN_DELAY;
	private int listenPos = 0;

	public SonicSecuritySystemTileEntity()
	{
		super(SCContent.teTypeSonicSecuritySystem);
	}

	@Override
	public void tick()
	{
		if(!world.isRemote)
		{
			if(!isActive())
				return;

			if(shouldEmitPower)
			{
				if(powerCooldown > 0)
					powerCooldown--;
				else
				{
					powerCooldown = 40;
					shouldEmitPower = false;
					world.updateBlock(pos, SCContent.SONIC_SECURITY_SYSTEM.get());
				}
			}

			// If the SSS is currently listening to a note, check to see
			// if the timer has run out and needs to be reset
			if(isListening)
			{
				if(listeningTimer > 0)
				{
					listeningTimer--;
					return;
				}
				else
				{
					listeningTimer = LISTEN_DELAY;
					listenPos = 0;
					isListening = false;
					world.updateBlock(pos, SCContent.SONIC_SECURITY_SYSTEM.get());
					SecurityCraft.channel.send(PacketDistributor.ALL.noArg(), new SyncSSSSettingsOnClient(pos, SyncSSSSettingsOnClient.DataType.LISTENING_OFF));
				}
			}

			// If this SSS isn't linked to any blocks, return as no sound should
			// be emitted and no blocks need to be removed
			if(!isLinkedToBlock())
				return;

			if(pingCooldown > 0)
			{
				pingCooldown--;
			}
			else
			{
				// TODO: should the SSS automatically forget the positions of linked blocks
				// if they are broken?
				ArrayList<BlockPos> blocksToRemove = new ArrayList<BlockPos>();
				Iterator<BlockPos> iterator = linkedBlocks.iterator();

				while(iterator.hasNext())
				{
					BlockPos blockPos = iterator.next();

					if(!(world.getTileEntity(blockPos) instanceof ILockable))
						blocksToRemove.add(blockPos);
				}

				// This delinking part is in a separate loop to prevent a ConcurrentModificationException
				for(BlockPos posToRemove : blocksToRemove)
				{
					delink(posToRemove, false);
					sync();
				}

				// Play the ping sound if it was not disabled
				if(emitsPings && !isRecording)
					world.playSound(null, pos, SCSounds.PING.event, SoundCategory.BLOCKS, 0.3F, 1.0F);

				pingCooldown = PING_DELAY;
			}
		}
		else
		{
			if(isActive() || isRecording())
			{
				// Turn the radar dish slightly
				radarRotationDegrees += 0.15;

				if(radarRotationDegrees >= 360)
					radarRotationDegrees = 0;
			}
		}
	}

	@Override
	public CompoundNBT write(CompoundNBT tag)
	{
		super.write(tag);

		// If there are blocks to save but the tag doesn't have a CompoundNBT
		// to store them in, create one (shouldn't be needed)
		if(linkedBlocks.size() > 0 && !tag.contains("LinkedBlocks"))
		{
			tag.put("LinkedBlocks", new ListNBT());
		}

		Iterator<BlockPos> iterator = linkedBlocks.iterator();

		while(iterator.hasNext())
		{
			BlockPos blockToSave = iterator.next();

			CompoundNBT nbt = NBTUtil.writeBlockPos(blockToSave);

			tag.getList("LinkedBlocks", Constants.NBT.TAG_COMPOUND).add(nbt);

			if(!linkedBlocks.contains(blockToSave))
				linkedBlocks.add(blockToSave);
		}

		for(Iterator<NoteWrapper> notes = recordedNotes.iterator(); notes.hasNext(); )
		{
			NoteWrapper note = notes.next();

			if(!tag.contains("Notes"))
				tag.put("Notes", new ListNBT());

			CompoundNBT nbt = new CompoundNBT();
			nbt.putInt("noteID", note.noteID);
			nbt.putString("instrument", note.instrumentName);

			tag.getList("Notes", Constants.NBT.TAG_COMPOUND).add(nbt);
		}

		tag.putBoolean("emitsPings", emitsPings);
		tag.putBoolean("isActive", isActive);
		tag.putBoolean("isRecording", isRecording);
		tag.putInt("listenPos", listenPos);

		return tag;
	}

	@Override
	public void read(BlockState state, CompoundNBT tag)
	{
		super.read(state, tag);

		if(tag.contains("LinkedBlocks"))
		{
			ListNBT list = tag.getList("LinkedBlocks", Constants.NBT.TAG_COMPOUND);

			// Read each saved position and add it to the linkedBlocks list
			for(int i = 0; i < list.size(); i++)
			{
				CompoundNBT linkedBlock = list.getCompound(i);

				BlockPos linkedBlockPos = NBTUtil.readBlockPos(linkedBlock);

				linkedBlocks.add(linkedBlockPos);
			}
		}

		if(tag.contains("Notes"))
		{
			ListNBT list = tag.getList("Notes", Constants.NBT.TAG_COMPOUND);

			for(int i = 0; i < list.size(); i++)
			{
				CompoundNBT note = list.getCompound(i);

				recordedNotes.add(new NoteWrapper(note.getInt("noteID"), note.getString("instrument")));
			}
		}

		if(tag.contains("emitsPings"))
			emitsPings = tag.getBoolean("emitsPings");

		if(tag.contains("isActive"))
			isActive = tag.getBoolean("isActive");

		if(tag.contains("isRecording"))
			isRecording = tag.getBoolean("isRecording");

		if(tag.contains("listenPos"))
			listenPos = tag.getInt("listenPos");
	}

	/**
	 * Copies the positions over from the SSS item's tag into this TileEntity.
	 */
	public void transferPositionsFromItem(CompoundNBT itemTag)
	{
		if(itemTag == null || !itemTag.contains("LinkedBlocks"))
			return;

		ListNBT blocks = itemTag.getList("LinkedBlocks", Constants.NBT.TAG_COMPOUND);

		for(int i = 0; i < blocks.size(); i++)
		{
			CompoundNBT linkedBlock = blocks.getCompound(i);
			BlockPos linkedBlockPos = NBTUtil.readBlockPos(linkedBlock);

			// If the block has not already been linked with, add it to the list
			if(!isLinkedToBlock(linkedBlockPos))
			{
				linkedBlocks.add(linkedBlockPos);
			}
		}

		sync();
	}

	/**
	 * @return If this Sonic Security System is linked to another block
	 */
	public boolean isLinkedToBlock()
	{
		return !linkedBlocks.isEmpty();
	}

	/**
	 * @return If this Sonic Security System is linked to a block at a specific position
	 */
	public boolean isLinkedToBlock(BlockPos linkedBlockPos)
	{
		if(linkedBlocks.isEmpty())
			return false;

		return linkedBlocks.contains(linkedBlockPos);
	}

	/**
	 * Delinks this Sonic Security System from the given block
	 */
	public void delink(BlockPos linkedBlockPos, boolean shouldSync)
	{
		if(linkedBlocks.isEmpty())
			return;

		linkedBlocks.remove(linkedBlockPos);

		if(shouldSync)
			sync();
	}

	/**
	 * Delinks this Sonic Security System from all other blocks
	 */
	public void delinkAll()
	{
		linkedBlocks.clear();
		sync();
	}

	/**
	 * @return Returns the number of blocks that this Sonic Security System is linked to
	 */
	public int getNumberOfLinkedBlocks()
	{
		return linkedBlocks.size();
	}

	public boolean pings()
	{
		return emitsPings;
	}

	public void setPings(boolean pings)
	{
		emitsPings = pings;
	}

	/**
	 * @return Whether or not this Sonic Security System is actively running
	 */
	public boolean isActive()
	{
		return isActive;
	}

	/**
	 * Toggle the Sonic Security System on or off
	 * @param active true if the SSS should be powered on, false if not
	 */
	public void setActive(boolean active)
	{
		isActive = active;
	}

	/**
	 * @return True if this Sonic Security System is currently recording notes produced by Note Blocks,
	 * 		   false otherwise
	 */
	public boolean isRecording()
	{
		return isRecording;
	}

	/**
	 * Toggle the recording state of the Sonic Security System
	 * @param recording true if the SSS should be recording, false if not
	 */
	public void setRecording(boolean recording)
	{
		isRecording = recording;
	}

	/**
	 * @return True if this Sonic Security System if listening to notes currently being played,
	 * 		   false otherwise
	 */
	public boolean isListening()
	{
		return isListening;
	}

	/**
	 * Toggle the listening state of the Sonic Security System
	 * @param listening true if the SSS should be listening, false if not
	 */
	public void setListening(boolean listening)
	{
		isListening = listening;
	}

	/**
	 * Record a note to use in the audio "passcode"
	 * @param noteID the ID of the note that was played
	 * @param instrumentName the name of the instrument that played the note
	 */
	public void recordNote(int noteID, String instrumentName)
	{
		recordedNotes.add(new NoteWrapper(noteID, instrumentName));
	}

	/**
	 * Listen to a note and check whether or not it is the next correct note
	 * that was recorded by the Sonic Security System
	 *
	 * @param noteID the ID of the note that was played
	 * @param instrumentName the name of the instrument that played the note
	 */
	public boolean listenToNote(int noteID, String instrumentName)
	{
		// No notes
		if(getNumberOfNotes() == 0)
			return false;

		if(!isListening)
			isListening = true;

		if(recordedNotes.get(listenPos++).isSameNote(noteID, instrumentName))
		{
			// Played the entire tune correctly
			if(listenPos >= recordedNotes.size())
			{
				listenPos = 0;
				return true;
			}
			// Played part of the tune correctly but more notes are needed
			else
			{
				return false;
			}
		}

		// An incorrect note was played
		listenPos = 0;
		return false;
	}

	/**
	 * @return The notes that this Sonic Security System has recorded
	 */
	public ArrayList<NoteWrapper> getRecordedNotes() {
		return recordedNotes;
	}

	/**
	 * @return The number of notes that this Sonic Security System has recorded
	 */
	public int getNumberOfNotes()
	{
		return recordedNotes.size();
	}

	/**
	 * Clears all recorded notes
	 */
	public void clearNotes()
	{
		recordedNotes.clear();
	}

	@Override
	public Container createMenu(int windowId, PlayerInventory inv, PlayerEntity player)
	{
		return new GenericTEContainer(SCContent.cTypeSonicSecuritySystem, windowId, world, pos);
	}

	@Override
	public ITextComponent getDisplayName()
	{
		return new TranslationTextComponent(SCContent.SONIC_SECURITY_SYSTEM.get().getTranslationKey());
	}

	/**
	 * A simple wrapper that makes it slightly easier to store and compare notes with
	 */
	public static class NoteWrapper {

		public final int noteID;
		public final String instrumentName;

		public NoteWrapper(int note, String instrument)
		{
			this.noteID = note;
			this.instrumentName = instrument;
		}

		public boolean isSameNote(int note, String instrument)
		{
			return noteID == note && instrumentName.equals(instrument);
		}
	}
}
