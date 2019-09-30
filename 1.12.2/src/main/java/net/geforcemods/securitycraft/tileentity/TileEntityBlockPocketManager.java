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
import net.minecraft.block.BlockQuartz.EnumType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;

public class TileEntityBlockPocketManager extends CustomizableSCTE
{
	public boolean enabled = false;
	public int size = 5;
	private List<BlockPos> blocks = new ArrayList<>();
	private List<BlockPos> walls = new ArrayList<>();

	/**
	 * Enables the block pocket
	 * @return The feedback message. null if none should be sent.
	 */
	public TextComponentTranslation enableMultiblock()
	{
		if(!enabled) //multiblock detection
		{
			if(world.isRemote)
				SecurityCraft.network.sendToServer(new PacketCToggleBlockPocketManager(this, true, size));

			List<BlockPos> blocks = new ArrayList<>();
			List<BlockPos> sides = new ArrayList<>();
			final EnumFacing managerFacing = world.getBlockState(pos).getValue(BlockBlockPocketManager.FACING);
			final EnumFacing left = managerFacing.rotateY();
			final EnumFacing right = left.getOpposite();
			final EnumFacing back = left.rotateY();
			final BlockPos startingPos;
			final int lowest = 0;
			final int highest = size - 1;
			BlockPos pos = getPos().toImmutable();
			int xi = lowest;
			int yi = lowest;
			int zi = lowest;

			while(world.getBlockState(pos = pos.offset(left)).getBlock() instanceof BlockReinforcedCrystalQuartz) //find the bottom left corner
				;

			pos = pos.offset(right); //pos got offset one too far (which made the while loop above stop) so it needs to be corrected
			startingPos = pos.toImmutable();

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
						IBlockState currentState = world.getBlockState(currentPos);

						if(currentState.getBlock() instanceof BlockBlockPocketManager && !currentPos.equals(getPos()))
							return new TextComponentTranslation("messages.securitycraft:blockpocket.multipleManagers");

						//checking the lowest and highest level of the cube
						if((yi == lowest && !currentPos.equals(getPos())) || yi == highest) //if (y level is lowest AND it's not the block pocket manager's position) OR (y level is highest)
						{
							//checking the corners
							if(((xi == lowest && zi == lowest) || (xi == lowest && zi == highest) || (xi == highest && zi == lowest) || (xi == highest && zi == highest)))
							{
								if(!(currentState.getBlock() instanceof BlockReinforcedCrystalQuartz) || currentState.getValue(BlockReinforcedCrystalQuartz.VARIANT) != EnumType.CHISELED)
									return new TextComponentTranslation("messages.securitycraft:blockpocket.invalidBlock", currentPos, new TextComponentTranslation(currentState.getBlock().getItem(world, currentPos, currentState).getTranslationKey() + ".name"));
							}
							//checking the sides parallel to the block pocket manager
							else if((zi == lowest || zi == highest) && xi > lowest && xi < highest)
							{
								EnumType typeToCheckFor = managerFacing == EnumFacing.NORTH || managerFacing == EnumFacing.SOUTH ? EnumType.LINES_X : EnumType.LINES_Z;

								if(!(currentState.getBlock() instanceof BlockReinforcedCrystalQuartz) || currentState.getValue(BlockReinforcedCrystalQuartz.VARIANT) != typeToCheckFor)
									return new TextComponentTranslation("messages.securitycraft:blockpocket.invalidBlock.pillar", currentPos, new TextComponentTranslation(currentState.getBlock().getItem(world, currentPos, currentState).getTranslationKey() + ".name"));
							}
							//checking the sides orthogonal to the block pocket manager
							else if((xi == lowest || xi == highest) && zi > lowest && zi < highest)
							{
								EnumType typeToCheckFor = managerFacing == EnumFacing.NORTH || managerFacing == EnumFacing.SOUTH ? EnumType.LINES_Z : EnumType.LINES_X;

								if(!(currentState.getBlock() instanceof BlockReinforcedCrystalQuartz) || currentState.getValue(BlockReinforcedCrystalQuartz.VARIANT) != typeToCheckFor)
									return new TextComponentTranslation("messages.securitycraft:blockpocket.invalidBlock.pillar", currentPos, new TextComponentTranslation(currentState.getBlock().getItem(world, currentPos, currentState).getTranslationKey() + ".name"));
							}
							//checking the middle plane
							else if(xi > lowest && zi > lowest && xi < highest && zi < highest)
							{
								if(!(currentState.getBlock() instanceof BlockBlockPocketWall))
									return new TextComponentTranslation("messages.securitycraft:blockpocket.invalidBlock", currentPos, new TextComponentTranslation(currentState.getBlock().getItem(world, currentPos, currentState).getTranslationKey() + ".name"));

								sides.add(currentPos);
							}
						}
						//checking the corner edges
						else if(yi != lowest && yi != highest && ((xi == lowest && zi == lowest) || (xi == lowest && zi == highest) || (xi == highest && zi == lowest) || (xi == highest && zi == highest)))
						{
							if(!(currentState.getBlock() instanceof BlockReinforcedCrystalQuartz) || currentState.getValue(BlockReinforcedCrystalQuartz.VARIANT) != EnumType.LINES_Y)
								return new TextComponentTranslation("messages.securitycraft:blockpocket.invalidBlock.pillar", currentPos, new TextComponentTranslation(currentState.getBlock().getItem(world, currentPos, currentState).getTranslationKey() + ".name"));
						}
						//checking the walls
						else if(yi > lowest && yi < highest)
						{
							//checking the walls parallel to the block pocket manager
							if((zi == lowest || zi == highest) && xi > lowest && xi < highest)
							{
								if(!(currentState.getBlock() instanceof BlockBlockPocketWall))
									return new TextComponentTranslation("messages.securitycraft:blockpocket.invalidBlock", currentPos, new TextComponentTranslation(currentState.getBlock().getItem(world, currentPos, currentState).getTranslationKey() + ".name"));

								sides.add(currentPos);
							}
							//checking the walls orthogonal to the block pocket manager
							else if((xi == lowest || xi == highest) && zi > lowest && zi < highest)
							{
								if(!(currentState.getBlock() instanceof BlockBlockPocketWall))
									return new TextComponentTranslation("messages.securitycraft:blockpocket.invalidBlock", currentPos, new TextComponentTranslation(currentState.getBlock().getItem(world, currentPos, currentState).getTranslationKey() + ".name"));

								sides.add(currentPos);
							}
						}

						TileEntityOwnable te = (TileEntityOwnable)world.getTileEntity(currentPos);

						if(!getOwner().owns(te))
							return new TextComponentTranslation("messages.securitycraft:blockpocket.unowned", currentPos, new TextComponentTranslation(currentState.getBlock().getItem(world, currentPos, currentState).getTranslationKey() + ".name"));
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
				TileEntity te = world.getTileEntity(blockPos);

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
			if(world.isRemote)
				SecurityCraft.network.sendToServer(new PacketCToggleBlockPocketManager(this, false, size));

			enabled = false;

			for(BlockPos pos : blocks)
			{
				TileEntity te = world.getTileEntity(pos);

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
			IBlockState state = world.getBlockState(pos);

			if(state.getBlock() instanceof BlockBlockPocketWall)
				world.setBlockState(pos, state.withProperty(BlockBlockPocketWall.SEE_THROUGH, seeThrough));
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
	public NBTTagCompound writeToNBT(NBTTagCompound tag)
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

		return super.writeToNBT(tag);
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

	@Override
	public Option<?>[] customOptions()
	{
		return null;
	}
}
