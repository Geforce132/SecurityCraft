package net.geforcemods.securitycraft.tileentity;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.ILockable;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.TileEntityOwnable;
import net.geforcemods.securitycraft.blocks.BlockBlockPocketManager;
import net.geforcemods.securitycraft.blocks.BlockBlockPocketWall;
import net.geforcemods.securitycraft.blocks.reinforced.BlockReinforcedCrystalQuartz;
import net.geforcemods.securitycraft.inventory.InsertOnlyItemStackHandler;
import net.geforcemods.securitycraft.misc.EnumModuleType;
import net.geforcemods.securitycraft.network.server.AssembleBlockPocket;
import net.geforcemods.securitycraft.network.server.ToggleBlockPocketManager;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockQuartz.EnumType;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class TileEntityBlockPocketManager extends CustomizableSCTE implements ITickable, ILockable
{
	public static final int RENDER_DISTANCE = 100;
	private static final int BLOCK_PLACEMENTS_PER_TICK = 4;
	private static final ItemStack REINFORCED_CHISELED_CRYSTAL_QUARTZ = new ItemStack(SCContent.reinforcedCrystalQuartz, 1, 1);
	private static final ItemStack REINFORCED_CRYSTAL_QUARTZ_PILLAR = new ItemStack(SCContent.reinforcedCrystalQuartz, 1, 2);
	public boolean enabled = false;
	public boolean showOutline = false;
	public int autoBuildOffset = 0;
	public int size = 5;
	private List<BlockPos> blocks = new ArrayList<>();
	private List<BlockPos> walls = new ArrayList<>();
	private List<BlockPos> floor = new ArrayList<>();
	protected NonNullList<ItemStack> storage = NonNullList.withSize(56, ItemStack.EMPTY);
	private IItemHandler storageHandler;
	private IItemHandler insertOnlyHandler;
	private List<Pair<BlockPos,IBlockState>> placeQueue = new ArrayList<>();
	private boolean shouldPlaceBlocks = false;

	@Override
	public void update()
	{
		if(!world.isRemote && shouldPlaceBlocks)
		{
			EntityPlayer owner = PlayerUtils.getPlayerFromName(getOwner().getName());
			boolean isCreative = owner.isCreative();
			boolean placed4 = true;

			//place 4 blocks per tick
			//only place the next block if the previous one was placed
			//if any block failed to place, either the end was reached, or a block was in the way
			placeLoop: for(int i = 0; i < BLOCK_PLACEMENTS_PER_TICK; i++)
			{
				Pair<BlockPos,IBlockState> toPlace;
				IBlockState stateInWorld;

				do
				{
					if(placeQueue.isEmpty())
					{
						placed4 = false;
						break placeLoop;
					}

					toPlace = placeQueue.remove(0);

					Block block = toPlace.getRight().getBlock();

					//only allow block pocket walls and reinforced crystal quartz that is not default (so either chiseled or any of the lines types)
					if(block != SCContent.blockPocketWall && (block != SCContent.reinforcedCrystalQuartz || toPlace.getRight().getValue(BlockReinforcedCrystalQuartz.VARIANT) == EnumType.DEFAULT))
						throw new IllegalStateException(String.format("Tried to automatically place non-block pocket block \"%s\"! This mustn't happen!", toPlace.getRight()));
				}
				//reach the next block that is missing for the block pocket
				while((stateInWorld = world.getBlockState(toPlace.getLeft())) == toPlace.getRight());

				if(stateInWorld.getMaterial().isReplaceable())
				{
					BlockPos pos = toPlace.getLeft();
					IBlockState stateToPlace = toPlace.getRight();
					SoundType soundType = stateToPlace.getBlock().getSoundType(stateToPlace, world, pos, owner);
					TileEntity te;

					if(!isCreative) //queue blocks for removal from the inventory
					{
						//remove blocks from inventory
						invLoop: for(int k = 0; k < storage.size(); k++)
						{
							ItemStack stackToCheck = storage.get(k);

							if(!stackToCheck.isEmpty() && ((ItemBlock)stackToCheck.getItem()).getBlock() == stateToPlace.getBlock())
							{
								stackToCheck.shrink(1);
								break invLoop;
							}
						}
					}

					world.setBlockState(pos, stateToPlace);
					world.playSound(null, pos, soundType.getPlaceSound(), SoundCategory.BLOCKS, soundType.getVolume(), soundType.getPitch());
					te = world.getTileEntity(pos);

					//assigning the owner
					if(te instanceof TileEntityOwnable)
						((TileEntityOwnable)te).setOwner(getOwner().getUUID(), getOwner().getName());

					continue;
				}

				//when an invalid block is in the way
				PlayerUtils.sendMessageToPlayer(owner, Utils.localize(SCContent.blockPocketManager.getTranslationKey() + ".name"), new TextComponentTranslation("messages.securitycraft:blockpocket.assemblyFailed", getFormattedRelativeCoordinates(toPlace.getLeft(), world.getBlockState(pos).getValue(BlockBlockPocketManager.FACING)), new TextComponentTranslation(stateInWorld.getBlock().getTranslationKey())), TextFormatting.DARK_AQUA);
				placed4 = false;
				break placeLoop;
			}

			if(!placed4)
			{
				//there are still blocks left to place, so a different block is blocking (heh) a space
				if(!placeQueue.isEmpty())
					placeQueue.clear();
				else //no more blocks left to place, assembling must be done
				{
					setWalls(!hasModule(EnumModuleType.DISGUISE));
					PlayerUtils.sendMessageToPlayer(owner, Utils.localize(SCContent.blockPocketManager.getTranslationKey() + ".name"), new TextComponentTranslation("messages.securitycraft:blockpocket.assembled"), TextFormatting.DARK_AQUA);
				}

				shouldPlaceBlocks = false;
			}
		}
	}

	/**
	 * Enables the block pocket
	 * @return The feedback message. null if none should be sent.
	 */
	public TextComponentTranslation enableMultiblock()
	{
		if(!enabled) //multiblock detection
		{
			if(world.isRemote)
				SecurityCraft.network.sendToServer(new ToggleBlockPocketManager(this, true, size));

			List<BlockPos> blocks = new ArrayList<>();
			List<BlockPos> sides = new ArrayList<>();
			List<BlockPos> floor = new ArrayList<>();
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
			int offset = 0;

			if(!(world.getBlockState(pos.offset(left)).getBlock() instanceof BlockReinforcedCrystalQuartz)) //when the block left of the manager is not a Block Pocket block (so the manager was just placed down), take the autoBuildOffset
				pos = pos.offset(left, offset = -autoBuildOffset + (size / 2));
			else
			{
				for(int i = 1; i < size - 1; i++) //find the bottom left corner
				{
					if(!(world.getBlockState(pos.offset(left, i)).getValue(BlockReinforcedCrystalQuartz.VARIANT).getMetadata() >= 2)) //pillars
					{
						pos = pos.offset(left, offset = i);
						break;
					}
				}

				if(offset == 0) //when the bottom left corner couldn't be evaluated, take the autoBuildOffset
					pos = pos.offset(left, offset = -autoBuildOffset + (size / 2));
			}

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
									return new TextComponentTranslation("messages.securitycraft:blockpocket.invalidBlock", getFormattedRelativeCoordinates(currentPos, managerFacing), new TextComponentTranslation(currentState.getBlock().getItem(world, currentPos, currentState).getTranslationKey() + ".name"), new TextComponentTranslation(REINFORCED_CHISELED_CRYSTAL_QUARTZ.getTranslationKey() + ".name"));
							}
							//checking the sides parallel to the block pocket manager
							else if((zi == lowest || zi == highest) && xi > lowest && xi < highest)
							{
								EnumType typeToCheckFor = managerFacing == EnumFacing.NORTH || managerFacing == EnumFacing.SOUTH ? EnumType.LINES_X : EnumType.LINES_Z;

								if(currentState.getBlock() instanceof BlockReinforcedCrystalQuartz)
								{
									if(currentState.getValue(BlockReinforcedCrystalQuartz.VARIANT) != typeToCheckFor)
										return new TextComponentTranslation("messages.securitycraft:blockpocket.invalidBlock.rotation", getFormattedRelativeCoordinates(currentPos, managerFacing), new TextComponentTranslation(currentState.getBlock().getItem(world, currentPos, currentState).getTranslationKey() + ".name"));
								}
								else
									return new TextComponentTranslation("messages.securitycraft:blockpocket.invalidBlock", getFormattedRelativeCoordinates(currentPos, managerFacing), new TextComponentTranslation(currentState.getBlock().getItem(world, currentPos, currentState).getTranslationKey() + ".name"), new TextComponentTranslation(REINFORCED_CRYSTAL_QUARTZ_PILLAR.getTranslationKey() + ".name"));
							}
							//checking the sides orthogonal to the block pocket manager
							else if((xi == lowest || xi == highest) && zi > lowest && zi < highest)
							{
								EnumType typeToCheckFor = managerFacing == EnumFacing.NORTH || managerFacing == EnumFacing.SOUTH ? EnumType.LINES_Z : EnumType.LINES_X;

								if(currentState.getBlock() instanceof BlockReinforcedCrystalQuartz)
								{
									if(currentState.getValue(BlockReinforcedCrystalQuartz.VARIANT) != typeToCheckFor)
										return new TextComponentTranslation("messages.securitycraft:blockpocket.invalidBlock.rotation", getFormattedRelativeCoordinates(currentPos, managerFacing), new TextComponentTranslation(currentState.getBlock().getItem(world, currentPos, currentState).getTranslationKey() + ".name"));
								}
								else
									return new TextComponentTranslation("messages.securitycraft:blockpocket.invalidBlock", getFormattedRelativeCoordinates(currentPos, managerFacing), new TextComponentTranslation(currentState.getBlock().getItem(world, currentPos, currentState).getTranslationKey() + ".name"), new TextComponentTranslation(REINFORCED_CRYSTAL_QUARTZ_PILLAR.getTranslationKey() + ".name"));
							}
							//checking the middle plane
							else if(xi > lowest && zi > lowest && xi < highest && zi < highest)
							{
								if(!(currentState.getBlock() instanceof BlockBlockPocketWall))
									return new TextComponentTranslation("messages.securitycraft:blockpocket.invalidBlock", getFormattedRelativeCoordinates(currentPos, managerFacing), new TextComponentTranslation(currentState.getBlock().getItem(world, currentPos, currentState).getTranslationKey() + ".name"), new TextComponentTranslation(SCContent.blockPocketWall.getTranslationKey() + ".name"));

								floor.add(currentPos);
								sides.add(currentPos);
							}
						}
						//checking the corner edges
						else if(yi != lowest && yi != highest && ((xi == lowest && zi == lowest) || (xi == lowest && zi == highest) || (xi == highest && zi == lowest) || (xi == highest && zi == highest)))
						{
							if(currentState.getBlock() instanceof BlockReinforcedCrystalQuartz)
							{
								if(currentState.getValue(BlockReinforcedCrystalQuartz.VARIANT) != EnumType.LINES_Y)
									return new TextComponentTranslation("messages.securitycraft:blockpocket.invalidBlock.rotation", getFormattedRelativeCoordinates(currentPos, managerFacing), new TextComponentTranslation(currentState.getBlock().getItem(world, currentPos, currentState).getTranslationKey() + ".name"));
							}
							else
								return new TextComponentTranslation("messages.securitycraft:blockpocket.invalidBlock", getFormattedRelativeCoordinates(currentPos, managerFacing), new TextComponentTranslation(currentState.getBlock().getItem(world, currentPos, currentState).getTranslationKey() + ".name"), new TextComponentTranslation(REINFORCED_CRYSTAL_QUARTZ_PILLAR.getTranslationKey() + ".name"));
						}
						//checking the walls
						else if(yi > lowest && yi < highest)
						{
							//checking the walls parallel to the block pocket manager
							if((zi == lowest || zi == highest) && xi > lowest && xi < highest)
							{
								if(!(currentState.getBlock() instanceof BlockBlockPocketWall))
									return new TextComponentTranslation("messages.securitycraft:blockpocket.invalidBlock", getFormattedRelativeCoordinates(currentPos, managerFacing), new TextComponentTranslation(currentState.getBlock().getItem(world, currentPos, currentState).getTranslationKey() + ".name"), new TextComponentTranslation(SCContent.blockPocketWall.getTranslationKey() + ".name"));

								sides.add(currentPos);
							}
							//checking the walls orthogonal to the block pocket manager
							else if((xi == lowest || xi == highest) && zi > lowest && zi < highest)
							{
								if(!(currentState.getBlock() instanceof BlockBlockPocketWall))
									return new TextComponentTranslation("messages.securitycraft:blockpocket.invalidBlock", getFormattedRelativeCoordinates(currentPos, managerFacing), new TextComponentTranslation(currentState.getBlock().getItem(world, currentPos, currentState).getTranslationKey() + ".name"), new TextComponentTranslation(SCContent.blockPocketWall.getTranslationKey() + ".name"));

								sides.add(currentPos);
							}
						}

						TileEntityOwnable te = (TileEntityOwnable)world.getTileEntity(currentPos);

						if(!getOwner().owns(te))
							return new TextComponentTranslation("messages.securitycraft:blockpocket.unowned", getFormattedRelativeCoordinates(currentPos, managerFacing), new TextComponentTranslation(currentState.getBlock().getItem(world, currentPos, currentState).getTranslationKey() + ".name"));
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
			this.floor = floor;
			enabled = true;
			autoBuildOffset = -offset + (size / 2);

			for(BlockPos blockPos : blocks)
			{
				TileEntity te = world.getTileEntity(blockPos);

				if(te instanceof TileEntityBlockPocket)
					((TileEntityBlockPocket)te).setManager(this);
			}

			for(BlockPos blockPos : floor)
			{
				world.setBlockState(blockPos, world.getBlockState(blockPos).withProperty(BlockBlockPocketWall.SOLID, true));
			}

			setWalls(!hasModule(EnumModuleType.DISGUISE));
			return new TextComponentTranslation("messages.securitycraft:blockpocket.activated");
		}

		return null;
	}

	/**
	 * Auto-assembles the Block Pocket for a player.
	 * First it makes sure that the space isn't occupied, then it checks its inventory for the required items, then it places the blocks.
	 * @return The feedback message. null if none should be sent.
	 */
	public ITextComponent autoAssembleMultiblock()
	{
		if(!enabled)
		{
			if(world.isRemote)
				SecurityCraft.network.sendToServer(new AssembleBlockPocket(this, size));

			final EnumFacing managerFacing = world.getBlockState(pos).getValue(BlockBlockPocketManager.FACING);
			final EnumFacing left = managerFacing.rotateY();
			final EnumFacing right = left.getOpposite();
			final EnumFacing back = left.rotateY();
			final BlockPos startingPos;
			final int lowest = 0;
			final int half = (size - 1) / 2 - autoBuildOffset;
			final int highest = size - 1;
			BlockPos pos = getPos().toImmutable();
			int xi = lowest;
			int yi = lowest;
			int zi = lowest;
			int wallsNeeded = 0;
			int pillarsNeeded = 0;
			int chiseledNeeded = 0;

			pos = pos.offset(right, -half);
			startingPos = pos.toImmutable();

			//loop through the cube level by level to make sure the building space isn't occupied
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
						boolean replaceable = currentState.getMaterial().isReplaceable();

						//checking the lowest and highest level of the cube
						if((yi == lowest && !currentPos.equals(getPos())) || yi == highest) //if (y level is lowest AND it's not the block pocket manager's position) OR (y level is highest)
						{
							//checking the corners
							if(((xi == lowest && zi == lowest) || (xi == lowest && zi == highest) || (xi == highest && zi == lowest) || (xi == highest && zi == highest)))
							{
								if(!(currentState.getBlock() instanceof BlockReinforcedCrystalQuartz && currentState.getValue(BlockReinforcedCrystalQuartz.VARIANT) == EnumType.CHISELED) && !replaceable)
									return new TextComponentTranslation("messages.securitycraft:blockpocket.blockInWay", getFormattedRelativeCoordinates(currentPos, managerFacing), new TextComponentTranslation(currentState.getBlock().getItem(world, currentPos, currentState).getTranslationKey() + ".name"));

								if(replaceable)
									chiseledNeeded++;
							}
							//checking the sides parallel to the block pocket manager
							else if((zi == lowest || zi == highest) && xi > lowest && xi < highest)
							{
								EnumType typeToCheckFor = managerFacing == EnumFacing.NORTH || managerFacing == EnumFacing.SOUTH ? EnumType.LINES_X : EnumType.LINES_Z;

								if(!isReinforcedCrystalQuartzPillar(currentState) && !replaceable || (currentState.getBlock() instanceof BlockReinforcedCrystalQuartz && currentState.getValue(BlockReinforcedCrystalQuartz.VARIANT) != typeToCheckFor))
									return new TextComponentTranslation("messages.securitycraft:blockpocket.blockInWay", getFormattedRelativeCoordinates(currentPos, managerFacing), new TextComponentTranslation(currentState.getBlock().getItem(world, currentPos, currentState).getTranslationKey() + ".name"));

								if(replaceable)
									pillarsNeeded++;
							}
							//checking the sides orthogonal to the block pocket manager
							else if((xi == lowest || xi == highest) && zi > lowest && zi < highest)
							{
								EnumType typeToCheckFor = managerFacing == EnumFacing.NORTH || managerFacing == EnumFacing.SOUTH ? EnumType.LINES_Z : EnumType.LINES_X;

								if(!isReinforcedCrystalQuartzPillar(currentState) && !replaceable || (currentState.getBlock() instanceof BlockReinforcedCrystalQuartz && currentState.getValue(BlockReinforcedCrystalQuartz.VARIANT) != typeToCheckFor))
									return new TextComponentTranslation("messages.securitycraft:blockpocket.blockInWay", getFormattedRelativeCoordinates(currentPos, managerFacing), new TextComponentTranslation(currentState.getBlock().getItem(world, currentPos, currentState).getTranslationKey() + ".name"));

								if(replaceable)
									pillarsNeeded++;
							}
							//checking the middle plane
							else if(xi > lowest && zi > lowest && xi < highest && zi < highest)
							{
								if(!(currentState.getBlock() instanceof BlockBlockPocketWall) && !replaceable)
									return new TextComponentTranslation("messages.securitycraft:blockpocket.blockInWay", getFormattedRelativeCoordinates(currentPos, managerFacing), new TextComponentTranslation(currentState.getBlock().getItem(world, currentPos, currentState).getTranslationKey() + ".name"));

								if(replaceable)
									wallsNeeded++;
							}
						}
						//checking the corner edges
						else if(yi != lowest && yi != highest && ((xi == lowest && zi == lowest) || (xi == lowest && zi == highest) || (xi == highest && zi == lowest) || (xi == highest && zi == highest)))
						{
							if(!isReinforcedCrystalQuartzPillar(currentState) && !replaceable || (currentState.getBlock() instanceof BlockReinforcedCrystalQuartz && currentState.getValue(BlockReinforcedCrystalQuartz.VARIANT) != EnumType.LINES_Y))
								return new TextComponentTranslation("messages.securitycraft:blockpocket.blockInWay", getFormattedRelativeCoordinates(currentPos, managerFacing), new TextComponentTranslation(currentState.getBlock().getItem(world, currentPos, currentState).getTranslationKey() + ".name"));

							if(replaceable)
								pillarsNeeded++;
						}
						//checking the walls
						else if(yi > lowest && yi < highest)
						{
							//checking the walls parallel to the block pocket manager
							if((zi == lowest || zi == highest) && xi > lowest && xi < highest)
							{
								if(!(currentState.getBlock() instanceof BlockBlockPocketWall) && !replaceable)
									return new TextComponentTranslation("messages.securitycraft:blockpocket.blockInWay", getFormattedRelativeCoordinates(currentPos, managerFacing), new TextComponentTranslation(currentState.getBlock().getItem(world, currentPos, currentState).getTranslationKey() + ".name"));

								if(replaceable)
									wallsNeeded++;
							}
							//checking the walls orthogonal to the block pocket manager
							else if((xi == lowest || xi == highest) && zi > lowest && zi < highest)
							{
								if(!(currentState.getBlock() instanceof BlockBlockPocketWall) && !replaceable)
									return new TextComponentTranslation("messages.securitycraft:blockpocket.blockInWay", getFormattedRelativeCoordinates(currentPos, managerFacing), new TextComponentTranslation(currentState.getBlock().getItem(world, currentPos, currentState).getTranslationKey() + ".name"));

								if(replaceable)
									wallsNeeded++;
							}
						}

						if(world.getTileEntity(currentPos) instanceof TileEntityOwnable)
						{
							TileEntityOwnable te = (TileEntityOwnable)world.getTileEntity(currentPos);

							if(!getOwner().owns(te))
								return new TextComponentTranslation("messages.securitycraft:blockpocket.unowned", getFormattedRelativeCoordinates(currentPos, managerFacing), new TextComponentTranslation(currentState.getBlock().getItem(world, currentPos, currentState).getTranslationKey() + ".name"));
						}

						xi++;
					}

					xi = 0;
					zi++;
					pos = startingPos.up(yi).offset(back, zi);
				}

				zi = 0;
				yi++;
				pos = startingPos.up(yi);
			} //if the code comes to this place, the space is either clear or occupied by blocks that would have been placed either way, or existing blocks can be replaced (like grass)

			if(chiseledNeeded + pillarsNeeded + wallsNeeded == 0) //this applies when no blocks are missing, so when the BP is already in place
				return new TextComponentTranslation("messages.securitycraft:blockpocket.alreadyAssembled");

			pos = getPos().toImmutable().offset(right, -half);
			xi = lowest;
			yi = lowest;
			zi = lowest;

			//add blocks to the auto building queue
			while(yi < size)
			{
				while(zi < size)
				{
					while(xi < size)
					{
						//skip the blocks in the middle again
						if(xi > lowest && yi > lowest && zi > lowest && xi < highest && yi < highest && zi < highest)
						{
							xi++;
							continue;
						}

						BlockPos currentPos = pos.offset(right, xi);
						IBlockState currentState = world.getBlockState(currentPos);

						if(currentState.getBlock() instanceof BlockBlockPocketManager && !currentPos.equals(getPos()))
							return new TextComponentTranslation("messages.securitycraft:blockpocket.multipleManagers");

						//placing the lowest and highest level of the cube
						if((yi == lowest && !currentPos.equals(getPos())) || yi == highest) //if (y level is lowest AND it's not the block pocket manager's position) OR (y level is highest)
						{
							//placing the corners
							if(((xi == lowest && zi == lowest) || (xi == lowest && zi == highest) || (xi == highest && zi == lowest) || (xi == highest && zi == highest)))
								placeQueue.add(Pair.of(currentPos, SCContent.reinforcedCrystalQuartz.getDefaultState().withProperty(BlockReinforcedCrystalQuartz.VARIANT, EnumType.CHISELED)));
							//placing the sides parallel to the block pocket manager
							else if((zi == lowest || zi == highest) && xi > lowest && xi < highest)
							{
								EnumType typeToPlace = managerFacing == EnumFacing.NORTH || managerFacing == EnumFacing.SOUTH ? EnumType.LINES_X : EnumType.LINES_Z;

								placeQueue.add(Pair.of(currentPos, SCContent.reinforcedCrystalQuartz.getDefaultState().withProperty(BlockReinforcedCrystalQuartz.VARIANT, typeToPlace)));
							}
							//placing the sides orthogonal to the block pocket manager
							else if((xi == lowest || xi == highest) && zi > lowest && zi < highest)
							{
								EnumType typeToPlace = managerFacing == EnumFacing.NORTH || managerFacing == EnumFacing.SOUTH ? EnumType.LINES_Z : EnumType.LINES_X;

								placeQueue.add(Pair.of(currentPos, SCContent.reinforcedCrystalQuartz.getDefaultState().withProperty(BlockReinforcedCrystalQuartz.VARIANT, typeToPlace)));
							}
							//placing the middle plane
							else if(xi > lowest && zi > lowest && xi < highest && zi < highest)
								placeQueue.add(Pair.of(currentPos, SCContent.blockPocketWall.getDefaultState()));
						}
						//placing the corner edges
						else if(yi != lowest && yi != highest && ((xi == lowest && zi == lowest) || (xi == lowest && zi == highest) || (xi == highest && zi == lowest) || (xi == highest && zi == highest)))
							placeQueue.add(Pair.of(currentPos, SCContent.reinforcedCrystalQuartz.getDefaultState().withProperty(BlockReinforcedCrystalQuartz.VARIANT, EnumType.LINES_Y)));
						//placing the walls
						else if(yi > lowest && yi < highest)
						{
							//checking the walls parallel to the block pocket manager
							if((zi == lowest || zi == highest) && xi > lowest && xi < highest)
								placeQueue.add(Pair.of(currentPos, SCContent.blockPocketWall.getDefaultState()));
							//checking the walls orthogonal to the block pocket manager
							else if((xi == lowest || xi == highest) && zi > lowest && zi < highest)
								placeQueue.add(Pair.of(currentPos, SCContent.blockPocketWall.getDefaultState()));
						}

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

			shouldPlaceBlocks = true;
			return null;
		}

		return null;
	}

	public void disableMultiblock()
	{
		if(enabled)
		{
			if(world.isRemote)
			{
				SecurityCraft.network.sendToServer(new ToggleBlockPocketManager(this, false, size));
			}

			PlayerUtils.sendMessageToPlayer(SecurityCraft.proxy.getClientPlayer(), Utils.localize(SCContent.blockPocketManager.getTranslationKey() + ".name"), Utils.localize("messages.securitycraft:blockpocket.deactivated"), TextFormatting.DARK_AQUA, true);
			enabled = false;

			for(BlockPos pos : blocks)
			{
				TileEntity te = world.getTileEntity(pos);

				if(te instanceof TileEntityBlockPocket)
					((TileEntityBlockPocket)te).removeManager();
			}

			for(BlockPos pos : floor)
			{
				IBlockState state = world.getBlockState(pos);

				if(state.getProperties().containsKey(BlockBlockPocketWall.SOLID))
					world.setBlockState(pos, state.withProperty(BlockBlockPocketWall.SOLID, false));
			}

			if(hasModule(EnumModuleType.DISGUISE))
				setWalls(true);

			blocks.clear();
			walls.clear();
			floor.clear();
		}
	}

	private TextComponentTranslation getFormattedRelativeCoordinates(BlockPos pos, EnumFacing managerFacing)
	{
		BlockPos difference = pos.subtract(this.pos);
		int offsetLeft;
		int offsetBehind;
		int offsetAbove = difference.getY();
		List<TextComponentTranslation> components = new ArrayList<>();

		switch (managerFacing) {
			case NORTH:
				offsetBehind = difference.getZ();
				offsetLeft = difference.getX();
				break;
			case SOUTH:
				offsetBehind = -difference.getZ();
				offsetLeft = -difference.getX();
				break;
			case WEST:
				offsetBehind = difference.getX();
				offsetLeft = -difference.getZ();
				break;
			case EAST:
				offsetBehind = -difference.getX();
				offsetLeft = difference.getZ();
				break;
			default: throw new IllegalArgumentException("Invalid Block Pocket Manager direction: " + managerFacing.name());
		}

		if(offsetLeft > 0)
			components.add(Utils.localize("messages.securitycraft:blockpocket.position.blocksLeft", offsetLeft));
		else if(offsetLeft < 0)
			components.add(Utils.localize("messages.securitycraft:blockpocket.position.blocksRight", -offsetLeft));

		if(offsetBehind > 0)
			components.add(Utils.localize("messages.securitycraft:blockpocket.position.blocksBehind", offsetBehind));

		if(offsetAbove > 0)
			components.add(Utils.localize("messages.securitycraft:blockpocket.position.blocksAbove", offsetAbove));

		return Utils.localize("messages.securitycraft:blockpocket.position." + components.size(), components.toArray());
	}

	public void toggleOutline()
	{
		showOutline = !showOutline;
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
	public boolean hasCapability(Capability<?> capability, EnumFacing facing)
	{
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
	}

	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing side)
	{
		if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
		{
			if(isPlacingBlocks()) //prevent extracting while auto building the block pocket
				return (T)getInsertOnlyHandler();
			else return (T)BlockUtils.getProtectedCapability(side, this, () -> getStorageHandler(), () -> getInsertOnlyHandler());
		}
		else return super.getCapability(cap, side);
	}

	@Override
	public void invalidate()
	{
		super.invalidate();

		if(world.isBlockLoaded(pos) && world.getBlockState(pos).getBlock() != SCContent.blockPocketManager)
			disableMultiblock();
	}

	@Override
	public void onModuleInserted(ItemStack stack, EnumModuleType module)
	{
		super.onModuleInserted(stack, module);

		if(enabled && module == EnumModuleType.DISGUISE)
			setWalls(false);
	}

	@Override
	public void onModuleRemoved(ItemStack stack, EnumModuleType module)
	{
		super.onModuleRemoved(stack, module);

		if(enabled && module == EnumModuleType.DISGUISE)
			setWalls(true);
		else if(module == EnumModuleType.STORAGE)
		{
			IItemHandler handler = getStorageHandler();

			for(int i = 0; i < handler.getSlots(); i++)
			{
				InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), handler.getStackInSlot(i));
			}
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag)
	{
		tag.setBoolean("BlockPocketEnabled", enabled);
		tag.setBoolean("ShowOutline", showOutline);
		tag.setInteger("Size", size);
		tag.setInteger("AutoBuildOffset", autoBuildOffset);
		ItemStackHelper.saveAllItems(tag, storage);

		for(int i = 0; i < blocks.size(); i++)
		{
			tag.setLong("BlocksList" + i, blocks.get(i).toLong());
		}

		for(int i = 0; i < walls.size(); i++)
		{
			tag.setLong("WallsList" + i, walls.get(i).toLong());
		}

		for(int i = 0; i < floor.size(); i++)
		{
			tag.setLong("FloorList" + i, floor.get(i).toLong());
		}

		return super.writeToNBT(tag);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		int i = 0;

		super.readFromNBT(tag);
		enabled = tag.getBoolean("BlockPocketEnabled");
		showOutline = tag.getBoolean("ShowOutline");
		size = tag.getInteger("Size");
		autoBuildOffset = tag.getInteger("AutoBuildOffset");
		ItemStackHelper.loadAllItems(tag, storage);

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

		i = 0;

		while(tag.hasKey("FloorList" + i))
		{
			floor.add(BlockPos.fromLong(tag.getLong("FloorList" + i)));
			i++;
		}
	}

	@Override
	public EnumModuleType[] acceptedModules()
	{
		return new EnumModuleType[] {
				EnumModuleType.DISGUISE,
				EnumModuleType.ALLOWLIST,
				EnumModuleType.STORAGE
		};
	}

	@Override
	public Option<?>[] customOptions()
	{
		return null;
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox()
	{
		return new AxisAlignedBB(getPos()).grow(RENDER_DISTANCE);
	}

	private boolean isReinforcedCrystalQuartzPillar(IBlockState state)
	{
		if(state.getBlock() instanceof BlockReinforcedCrystalQuartz)
		{
			EnumType type = state.getValue(BlockReinforcedCrystalQuartz.VARIANT);

			return type == EnumType.LINES_X || type == EnumType.LINES_Y || type == EnumType.LINES_Z;
		}
		else return false;
	}

	public IItemHandler getStorageHandler()
	{
		if(storageHandler == null)
		{
			storageHandler = new ItemStackHandler(storage) {
				@Override
				public boolean isItemValid(int slot, ItemStack stack)
				{
					return TileEntityBlockPocketManager.isItemValid(stack);
				}
			};
		}

		return storageHandler;
	}

	private IItemHandler getInsertOnlyHandler()
	{
		if(insertOnlyHandler == null)
		{
			insertOnlyHandler = new InsertOnlyItemStackHandler(storage) {
				@Override
				public boolean isItemValid(int slot, ItemStack stack)
				{
					return TileEntityBlockPocketManager.isItemValid(stack);
				}
			};
		}

		return insertOnlyHandler;
	}

	public boolean isPlacingBlocks()
	{
		return shouldPlaceBlocks;
	}

	public static boolean isItemValid(ItemStack stack)
	{
		if(stack.getItem() instanceof ItemBlock)
		{
			Block block = ((ItemBlock)stack.getItem()).getBlock();

			return block == SCContent.blockPocketWall || (block == SCContent.reinforcedCrystalQuartz && stack.getMetadata() >= 1);
		}

		return false;
	}
}
