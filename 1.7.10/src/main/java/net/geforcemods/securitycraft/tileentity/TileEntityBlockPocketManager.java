package net.geforcemods.securitycraft.tileentity;

import java.util.ArrayList;
import java.util.List;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.blocks.BlockBlockPocketManager;
import net.geforcemods.securitycraft.blocks.BlockBlockPocketWall;
import net.geforcemods.securitycraft.blocks.reinforced.BlockReinforcedCrystalQuartz;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.network.packets.PacketCToggleBlockPocketManager;
import net.geforcemods.securitycraft.util.BetterFacing;
import net.geforcemods.securitycraft.util.BlockPos;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.TranslatableString;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TileEntityBlockPocketManager extends CustomizableSCTE
{
	private static final int CHISELED = 1, PILLAR_Y = 2, PILLAR_X = 3, PILLAR_Z = 4;
	private static final int MG_NORTH = 0, MG_EAST = 1, MG_SOUTH = 2;
	public boolean enabled = false;
	public int size = 5;
	private List<BlockPos> blocks = new ArrayList<>();
	private List<BlockPos> walls = new ArrayList<>();

	/**
	 * Enables the block pocket
	 * @return The feedback message. null if none should be sent.
	 */
	public TranslatableString enableMultiblock()
	{
		if(!enabled) //multiblock detection
		{
			if(worldObj.isRemote)
				SecurityCraft.network.sendToServer(new PacketCToggleBlockPocketManager(this, true, size));

			List<BlockPos> blocks = new ArrayList<>();
			List<BlockPos> sides = new ArrayList<>();
			int managerMeta = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
			final BetterFacing managerFacing = managerMeta == MG_NORTH ? BetterFacing.NORTH : (managerMeta == MG_EAST ? BetterFacing.EAST : (managerMeta == MG_SOUTH ? BetterFacing.SOUTH: BetterFacing.WEST));
			final BetterFacing left = managerFacing.rotateY();
			final BetterFacing right = left.getOpposite();
			final BetterFacing back = left.rotateY();
			final BlockPos startingPos;
			final int lowest = 0;
			final int highest = size - 1;
			BlockPos pos = getPos();
			int xi = lowest;
			int yi = lowest;
			int zi = lowest;

			while(BlockUtils.getBlock(worldObj, pos = pos.offset(left)) instanceof BlockReinforcedCrystalQuartz) //find the bottom left corner
				;

			pos = pos.offset(right); //pos got offset one too far (which made the while loop above stop) so it needs to be corrected
			startingPos = pos.copy();

			//looping through cube level by level
			while(yi < size)
			{
				while(zi < size)
				{
					while(xi < size)
					{
						//skip the blocks in the middle
						if(xi > lowest && yi > lowest && zi > lowest && xi < highest && yi < highest && zi < highest)
						{
							xi++;
							continue;
						}

						BlockPos currentPos = pos.offset(right, xi);
						Block currentBlock = BlockUtils.getBlock(worldObj, currentPos);
						int currentMeta = worldObj.getBlockMetadata(currentPos.getX(), currentPos.getY(), currentPos.getZ());

						if(currentBlock instanceof BlockBlockPocketManager && !currentPos.equals(getPos()))
							return new TranslatableString("messages.securitycraft:blockpocket.multipleManagers");

						//checking the lowest and highest level of the cube
						if((yi == lowest && !currentPos.equals(getPos())) || yi == highest) //if (y level is lowest AND it's not the block pocket manager's position) OR (y level is highest)
						{
							//checking the corners
							if(((xi == lowest && zi == lowest) || (xi == lowest && zi == highest) || (xi == highest && zi == lowest) || (xi == highest && zi == highest)))
							{
								if(!(currentBlock instanceof BlockReinforcedCrystalQuartz) || currentMeta != CHISELED)
									return new TranslatableString("messages.securitycraft:blockpocket.invalidBlock", currentPos, new TranslatableString(currentBlock.getItem(worldObj, currentPos.getX(), currentPos.getY(), currentPos.getZ())));
							}
							//checking the sides parallel to the block pocket manager
							else if((zi == lowest || zi == highest) && xi > lowest && xi < highest)
							{
								int typeToCheckFor = managerFacing == BetterFacing.NORTH || managerFacing == BetterFacing.SOUTH ? PILLAR_X : PILLAR_Z;

								if(!(currentBlock instanceof BlockReinforcedCrystalQuartz) || currentMeta != typeToCheckFor)
									return new TranslatableString("messages.securitycraft:blockpocket.invalidBlock.pillar", currentPos, new TranslatableString(currentBlock.getItem(worldObj, currentPos.getX(), currentPos.getY(), currentPos.getZ())));
							}
							//checking the sides orthogonal to the block pocket manager
							else if((xi == lowest || xi == highest) && zi > lowest && zi < highest)
							{
								int typeToCheckFor = managerFacing == BetterFacing.NORTH || managerFacing == BetterFacing.SOUTH ? PILLAR_Z : PILLAR_X;

								if(!(currentBlock instanceof BlockReinforcedCrystalQuartz) || currentMeta != typeToCheckFor)
									return new TranslatableString("messages.securitycraft:blockpocket.invalidBlock.pillar", currentPos, new TranslatableString(currentBlock.getItem(worldObj, currentPos.getX(), currentPos.getY(), currentPos.getZ())));
							}
							//checking the middle plane
							else if(xi > lowest && zi > lowest && xi < highest && zi < highest)
							{
								if(!(currentBlock instanceof BlockBlockPocketWall))
									return new TranslatableString("messages.securitycraft:blockpocket.invalidBlock", currentPos, new TranslatableString(currentBlock.getItem(worldObj, currentPos.getX(), currentPos.getY(), currentPos.getZ())));

								sides.add(currentPos);
							}
						}
						//checking the corner edges
						else if(yi != lowest && yi != highest && ((xi == lowest && zi == lowest) || (xi == lowest && zi == highest) || (xi == highest && zi == lowest) || (xi == highest && zi == highest)))
						{
							if(!(currentBlock instanceof BlockReinforcedCrystalQuartz) || currentMeta != PILLAR_Y)
								return new TranslatableString("messages.securitycraft:blockpocket.invalidBlock.pillar", currentPos, new TranslatableString(currentBlock.getItem(worldObj, currentPos.getX(), currentPos.getY(), currentPos.getZ())));
						}
						//checking the walls
						else if(yi > lowest && yi < highest)
						{
							//checking the walls parallel to the block pocket manager
							if((zi == lowest || zi == highest) && xi > lowest && xi < highest)
							{
								if(!(currentBlock instanceof BlockBlockPocketWall))
									return new TranslatableString("messages.securitycraft:blockpocket.invalidBlock", currentPos, new TranslatableString(currentBlock.getItem(worldObj, currentPos.getX(), currentPos.getY(), currentPos.getZ())));

								sides.add(currentPos);
							}
							//checking the walls orthogonal to the block pocket manager
							else if((xi == lowest || xi == highest) && zi > lowest && zi < highest)
							{
								if(!(currentBlock instanceof BlockBlockPocketWall))
									return new TranslatableString("messages.securitycraft:blockpocket.invalidBlock", currentPos, new TranslatableString(currentBlock.getItem(worldObj, currentPos.getX(), currentPos.getY(), currentPos.getZ())));

								sides.add(currentPos);
							}
						}

						TileEntityOwnable te = (TileEntityOwnable)worldObj.getTileEntity(xCoord, yCoord, zCoord);

						if(!getOwner().owns(te))
							return new TranslatableString("messages.securitycraft:blockpocket.unowned", currentPos, new TranslatableString(currentBlock.getItem(worldObj, currentPos.getX(), currentPos.getY(), currentPos.getZ())));
						else
							blocks.add(currentPos);

						xi++;
					}

					xi = 0;
					zi++;
					pos = startingPos.up(yi).offset(back, zi);
				}

				zi = 0;
				yi++;
				pos = startingPos.up(yi);
			}

			this.blocks = blocks;
			this.walls = sides;
			enabled = true;

			for(BlockPos blockPos : blocks)
			{
				TileEntity te = worldObj.getTileEntity(blockPos.getX(), blockPos.getY(), blockPos.getZ());

				if(te instanceof TileEntityBlockPocket)
					((TileEntityBlockPocket)te).setManager(this);
			}

			setWalls(!hasModule(EnumCustomModules.DISGUISE));
		}

		return null;
	}

	public void disableMultiblock()
	{
		if(enabled)
		{
			if(worldObj.isRemote)
				SecurityCraft.network.sendToServer(new PacketCToggleBlockPocketManager(this, false, size));

			enabled = false;

			for(BlockPos pos : blocks)
			{
				TileEntity te = worldObj.getTileEntity(pos.getX(), pos.getY(), pos.getZ());

				if(te instanceof TileEntityBlockPocket)
					((TileEntityBlockPocket)te).removeManager();
			}

			if(hasModule(EnumCustomModules.DISGUISE))
				setWalls(true);

			blocks.clear();
			walls.clear();
		}
	}

	public void setWalls(boolean seeThrough)
	{
		for(BlockPos pos : walls)
		{
			Block block = BlockUtils.getBlock(worldObj, pos);

			if(block instanceof BlockBlockPocketWall)
				worldObj.setBlockMetadataWithNotify(pos.getX(), pos.getY(), pos.getZ(), seeThrough ? 0 : 1, 2);
		}
	}

	@Override
	public void invalidate()
	{
		super.invalidate();
		disableMultiblock();
	}

	@Override
	public void onTileEntityDestroyed()
	{
		super.onTileEntityDestroyed();
		disableMultiblock();
	}

	@Override
	public void onModuleInserted(ItemStack stack, EnumCustomModules module)
	{
		if(enabled && module == EnumCustomModules.DISGUISE)
			setWalls(false);
	}

	@Override
	public void onModuleRemoved(ItemStack stack, EnumCustomModules module)
	{
		if(enabled && module == EnumCustomModules.DISGUISE)
			setWalls(true);
	}

	@Override
	public void writeToNBT(NBTTagCompound tag)
	{
		tag.setBoolean("BlockPocketEnabled", enabled);

		for(int i = 0; i < blocks.size(); i++)
		{
			tag.setLong("BlocksList" + i, blocks.get(i).toLong());
		}

		for(int i = 0; i < walls.size(); i++)
		{
			tag.setLong("WallsList" + i, walls.get(i).toLong());
		}

		super.writeToNBT(tag);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		int i = 0;

		super.readFromNBT(tag);
		enabled = tag.getBoolean("BlockPocketEnabled");

		while(tag.hasKey("BlocksList" + i))
		{
			blocks.add(BlockPos.fromLong(tag.getLong("BlocksList" + i)));
			i++;
		}

		i = 0;

		while(tag.hasKey("WallsList" + i))
		{
			walls.add(BlockPos.fromLong(tag.getLong("WallsList" + i)));
			i++;
		}
	}

	@Override
	public EnumCustomModules[] acceptedModules()
	{
		return new EnumCustomModules[] {
				EnumCustomModules.DISGUISE,
				EnumCustomModules.WHITELIST
		};
	}

	private BlockPos getPos()
	{
		return new BlockPos(xCoord, yCoord, zCoord);
	}

	@Override
	public Option<?>[] customOptions()
	{
		return null;
	}
}
