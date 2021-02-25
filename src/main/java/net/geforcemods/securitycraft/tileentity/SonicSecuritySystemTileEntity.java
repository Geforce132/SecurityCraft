package net.geforcemods.securitycraft.tileentity;

import java.util.ArrayList;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableTileEntity;
import net.geforcemods.securitycraft.api.ILockable;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.BooleanOption;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;

public class SonicSecuritySystemTileEntity extends CustomizableTileEntity {
	
	// The delay between each ping sound in ticks
	private final static int DELAY = 100;
	
	// How far away can this SSS reach (possibly a config option?)
	public final static int MAX_RANGE = 30;
	
	// How many blocks can be linked to a SSS (another config option?)
	private final int MAX_LINKED_BLOCKS = 30;

	// Whether the ping sound should be emitted or not
	private BooleanOption isSilent = new BooleanOption("isSilent", false);

	private int cooldown = DELAY;
	public float radarRotationDegrees = 0;
	
	// A list containing all of the blocks that this SSS is linked to
	private ArrayList<BlockPos> linkedBlocks = new ArrayList<BlockPos>();
	
	// Is this SSS active? Not used yet but will be in the future to allow
	// the player to disable the SSS
	private boolean isActive = true;
	
	public SonicSecuritySystemTileEntity()
	{
		super(SCContent.teTypeSonicSecuritySystem);
	}
	
	@Override
	public void tick()
	{
		if(!world.isRemote) 
		{
			// If this SSS isn't linked to any blocks, return as no sound should
			// be emitted and no blocks need to be removed
			if(!isLinkedToBlock())
				return;
			
			if(cooldown > 0)
			{
				cooldown--;
			}
			else
			{	
				// TODO: should the SSS automatically forget the positions of linked blocks
				// if they are broken?
				ArrayList<BlockPos> blocksToRemove = new ArrayList<BlockPos>();
				for(BlockPos linkedBlockPos : linkedBlocks)
				{
					if(!(world.getTileEntity(linkedBlockPos) instanceof ILockable))
						blocksToRemove.add(linkedBlockPos);
				}
				
				// This delinking part is in a separate loop to prevent a ConcurrentModificationException
				for(BlockPos posToRemove : blocksToRemove)
				{
					delink(posToRemove, false);
					sync();
				}

				// Play the ping sound if it was not disabled
				if(!isSilent.get())
					world.playSound(null, pos, SCSounds.PING.event, SoundCategory.BLOCKS, 0.3F, 1.0F);

				cooldown = DELAY;
			}
		}
		else 
		{
			// Turn the radar dish slightly
			radarRotationDegrees += 0.15;
			
			if(radarRotationDegrees >= 360)
				radarRotationDegrees = 0;
		}
	}
	
	@Override
	public CompoundNBT write(CompoundNBT tag)
	{
		super.write(tag);
		
		// Write each linked block's position to the SSS's tag
		for(int i = 0; i < linkedBlocks.size(); i++)
		{
			BlockPos linkedBlockPos = linkedBlocks.get(i);
			
			int x = 0, y = 0, z = 0;
			boolean foundBlock = false;
			for(int j = 0; j < MAX_LINKED_BLOCKS; j++)
			{
				if(tag.contains("linkedBlock" + j))
				{
					x = tag.getCompound("linkedBlock" + j).getInt("x");
					y = tag.getCompound("linkedBlock" + j).getInt("y");
					z = tag.getCompound("linkedBlock" + j).getInt("z");
					
					// The position is already saved to the tag, don't need to add it again
					if(!(x == 0 && y == 0 && z == 0) && linkedBlockPos.equals(new BlockPos(x, y, z)))
					{
						foundBlock = true;
						break;
					}
				}
			}
			
			// If the position is not already saved in the tag, add it
			if(!foundBlock)
			{
				for(int j = 0; j < MAX_LINKED_BLOCKS; j++)
				{
					if(!tag.contains("linkedBlock" + j))
					{
						CompoundNBT newNBT = new CompoundNBT();
						newNBT.putInt("x", linkedBlockPos.getX());
						newNBT.putInt("y", linkedBlockPos.getY());
						newNBT.putInt("z", linkedBlockPos.getZ());

						tag.put("linkedBlock" + j, newNBT);
					}
				}
			}
		}

		tag.putBoolean("isActive", isActive);

		return tag;
	}

	@Override
	public void read(BlockState state, CompoundNBT tag)
	{
		super.read(state, tag);

		// Read each saved position and add it to the linkedBlocks list
		for(int i = 0; i < MAX_LINKED_BLOCKS; i++)
		{
			if(tag.contains("linkedBlock" + i))
			{
				int x = tag.getCompound("linkedBlock" + i).getInt("x");
				int y = tag.getCompound("linkedBlock" + i).getInt("y");
				int z = tag.getCompound("linkedBlock" + i).getInt("z");

				BlockPos linkedBlockPos = new BlockPos(x, y, z);
				linkedBlocks.add(linkedBlockPos);
			}
		}

		isActive = tag.getBoolean("isActive");
	}

	/**
	 * Copies the positions over from the SSS item's tag into this TileEntity.
	 * I know that the tag structures are different right now, I'll fix that :P
	 */
	public void transferPositionsFromItem(CompoundNBT itemTag)
	{
		if(itemTag == null || !itemTag.contains("LinkedBlocks"))
			return;
		
		for(int i = 0; i < MAX_LINKED_BLOCKS; i++)
		{
			CompoundNBT linkedBlock = itemTag.getCompound("LinkedBlocks");

			if(linkedBlock.contains("block" + i))
			{
				int x = linkedBlock.getCompound("block" + i).getInt("x");
				int y = linkedBlock.getCompound("block" + i).getInt("y");
				int z = linkedBlock.getCompound("block" + i).getInt("z");
				
				BlockPos linkedBlockPos = new BlockPos(x, y, z);

				// If the block has not already been linked with, add it to the list
				if(!isLinkedToBlock(linkedBlockPos))
				{
					linkedBlocks.add(linkedBlockPos);
				}
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
		
		for(int i = 0; i < linkedBlocks.size(); i++)
		{
			if(linkedBlocks.get(i).equals(linkedBlockPos))
				return true;
		}
		
		return false;
	}

	/**
	 * Delinks this Sonic Security System from the given block
	 */
	public void delink(BlockPos linkedBlockPos, boolean shouldSync)
	{
		if(linkedBlocks.isEmpty())
			return;
		
		for(int i = 0; i < linkedBlocks.size(); i++)
		{
			if(linkedBlocks.get(i).equals(linkedBlockPos))
				linkedBlocks.remove(i);
		}

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
	 * Toggles this Sonic Security System on or off (not used yet)
	 */
	public void toggle()
	{
		isActive = !isActive;
		sync();
	}
	
	/**
	 * @return Is this Sonic Security System active?
	 */
	public boolean isActive()
	{
		return isActive;
	}

	@Override
	public ModuleType[] acceptedModules() 
	{
		return new ModuleType[]{};
	}

	@Override
	public Option<?>[] customOptions() 
	{
		return new Option[] { isSilent };
	}

}
