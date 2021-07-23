package net.geforcemods.securitycraft.tileentity;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.CustomizableTileEntity;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.OwnableTileEntity;
import net.geforcemods.securitycraft.blocks.BlockPocketManagerBlock;
import net.geforcemods.securitycraft.blocks.BlockPocketWallBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedRotatedCrystalQuartzPillar;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedRotatedPillarBlock;
import net.geforcemods.securitycraft.containers.BlockPocketManagerContainer;
import net.geforcemods.securitycraft.inventory.InsertOnlyItemStackHandler;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.network.server.AssembleBlockPocket;
import net.geforcemods.securitycraft.network.server.ToggleBlockPocketManager;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.IBlockPocket;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class BlockPocketManagerTileEntity extends CustomizableTileEntity implements MenuProvider
{
	public static final int RENDER_DISTANCE = 100;
	private static final int BLOCK_PLACEMENTS_PER_TICK = 4;
	public boolean enabled = false;
	public boolean showOutline = false;
	public int size = 5;
	public int autoBuildOffset = 0;
	private List<BlockPos> blocks = new ArrayList<>();
	private List<BlockPos> walls = new ArrayList<>();
	private List<BlockPos> floor = new ArrayList<>();
	protected NonNullList<ItemStack> storage = NonNullList.withSize(56, ItemStack.EMPTY);
	private LazyOptional<IItemHandler> storageHandler;
	private LazyOptional<IItemHandler> insertOnlyHandler;
	private List<Pair<BlockPos,BlockState>> placeQueue = new ArrayList<>();
	private boolean shouldPlaceBlocks = false;

	public BlockPocketManagerTileEntity(BlockPos pos, BlockState state)
	{
		super(SCContent.teTypeBlockPocketManager, pos, state);
	}

	@Override
	public void tick()
	{
		super.tick();

		if(!level.isClientSide && shouldPlaceBlocks)
		{
			Player owner = PlayerUtils.getPlayerFromName(getOwner().getName());
			boolean isCreative = owner.isCreative();
			boolean placed4 = true;

			//place 4 blocks per tick
			//only place the next block if the previous one was placed
			//if any block failed to place, either the end was reached, or a block was in the way
			placeLoop: for(int i = 0; i < BLOCK_PLACEMENTS_PER_TICK; i++)
			{
				Pair<BlockPos,BlockState> toPlace;
				BlockState stateInWorld;

				do
				{
					if(placeQueue.isEmpty())
					{
						placed4 = false;
						break placeLoop;
					}

					toPlace = placeQueue.remove(0);

					if(!(toPlace.getRight().getBlock() instanceof IBlockPocket))
						throw new IllegalStateException(String.format("Tried to automatically place non-block pocket block \"%s\"! This mustn't happen!", toPlace.getRight().getBlock().getDescriptionId()));
				}
				//reach the next block that is missing for the block pocket
				while((stateInWorld = level.getBlockState(toPlace.getLeft())) == toPlace.getRight());

				if(stateInWorld.getMaterial().isReplaceable())
				{
					BlockPos pos = toPlace.getLeft();
					BlockState stateToPlace = toPlace.getRight();
					SoundType soundType = stateToPlace.getSoundType();
					BlockEntity te;

					if(!isCreative) //queue blocks for removal from the inventory
					{
						//remove blocks from inventory
						invLoop: for(int k = 0; k < storage.size(); k++)
						{
							ItemStack stackToCheck = storage.get(k);

							if(!stackToCheck.isEmpty() && ((BlockItem)stackToCheck.getItem()).getBlock() == stateToPlace.getBlock())
							{
								stackToCheck.shrink(1);
								break invLoop;
							}
						}
					}

					level.setBlockAndUpdate(pos, stateToPlace);
					level.playSound(null, pos, soundType.getPlaceSound(), SoundSource.BLOCKS, soundType.getVolume(), soundType.getPitch());
					te = level.getBlockEntity(pos);

					//assigning the owner
					if(te instanceof OwnableTileEntity)
						((OwnableTileEntity)te).setOwner(getOwner().getUUID(), getOwner().getName());

					continue;
				}

				//when an invalid block is in the way
				PlayerUtils.sendMessageToPlayer(owner, Utils.localize(SCContent.BLOCK_POCKET_MANAGER.get().getDescriptionId()), new TranslatableComponent("messages.securitycraft:blockpocket.assemblyFailed", getFormattedRelativeCoordinates(toPlace.getLeft(), getBlockState().getValue(BlockPocketManagerBlock.FACING)), new TranslatableComponent(stateInWorld.getBlock().getDescriptionId())), ChatFormatting.DARK_AQUA);
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
					setWalls(!hasModule(ModuleType.DISGUISE));
					PlayerUtils.sendMessageToPlayer(owner, Utils.localize(SCContent.BLOCK_POCKET_MANAGER.get().getDescriptionId()), new TranslatableComponent("messages.securitycraft:blockpocket.assembled"), ChatFormatting.DARK_AQUA);
				}

				shouldPlaceBlocks = false;
			}
		}
	}

	/**
	 * Enables the block pocket
	 * @return The feedback message. null if none should be sent.
	 */
	public TranslatableComponent enableMultiblock()
	{
		if(!enabled) //multiblock detection
		{
			if(level.isClientSide)
				SecurityCraft.channel.sendToServer(new ToggleBlockPocketManager(this, true, size));

			List<BlockPos> blocks = new ArrayList<>();
			List<BlockPos> sides = new ArrayList<>();
			List<BlockPos> floor = new ArrayList<>();
			final Direction managerFacing = level.getBlockState(worldPosition).getValue(BlockPocketManagerBlock.FACING);
			final Direction left = managerFacing.getClockWise();
			final Direction right = left.getOpposite();
			final Direction back = left.getClockWise();
			final BlockPos startingPos;
			final int lowest = 0;
			final int highest = size - 1;
			BlockPos pos = getBlockPos().immutable();
			int xi = lowest;
			int yi = lowest;
			int zi = lowest;
			int offset = 0;

			if (!(level.getBlockState(pos.relative(left)).getBlock() instanceof IBlockPocket)) { //when the block left of the manager is not a Block Pocket block (so the manager was just placed down), take the autoBuildOffset
				pos = pos.relative(left, offset = -autoBuildOffset + (size / 2));
			}
			else {
				for (int i = 1; i < size - 1; i++) { //find the bottom left corner
					if (!(level.getBlockState(pos.relative(left, i)).getBlock() instanceof ReinforcedRotatedCrystalQuartzPillar)) {
						pos = pos.relative(left, offset = i);
						break;
					}
				}

				if (offset == 0) //when the bottom left corner couldn't be evaluated, take the autoBuildOffset
					pos = pos.relative(left, offset = -autoBuildOffset + (size / 2));
			}

			startingPos = pos.immutable();

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

						BlockPos currentPos = pos.relative(right, xi);
						BlockState currentState = level.getBlockState(currentPos);

						if(currentState.getBlock() instanceof BlockPocketManagerBlock && !currentPos.equals(getBlockPos()))
							return new TranslatableComponent("messages.securitycraft:blockpocket.multipleManagers");

						//checking the lowest and highest level of the cube
						if((yi == lowest && !currentPos.equals(getBlockPos())) || yi == highest) //if (y level is lowest AND it's not the block pocket manager's position) OR (y level is highest)
						{
							//checking the corners
							if(((xi == lowest && zi == lowest) || (xi == lowest && zi == highest) || (xi == highest && zi == lowest) || (xi == highest && zi == highest)))
							{
								if(currentState.getBlock() != SCContent.REINFORCED_CHISELED_CRYSTAL_QUARTZ.get())
									return new TranslatableComponent("messages.securitycraft:blockpocket.invalidBlock", getFormattedRelativeCoordinates(currentPos, managerFacing), new TranslatableComponent(currentState.getBlock().asItem().getDescriptionId()), new TranslatableComponent(SCContent.REINFORCED_CHISELED_CRYSTAL_QUARTZ.get().getDescriptionId()));
							}
							//checking the sides parallel to the block pocket manager
							else if((zi == lowest || zi == highest) && xi > lowest && xi < highest)
							{
								Axis typeToCheckFor = managerFacing == Direction.NORTH || managerFacing == Direction.SOUTH ? Axis.X : Axis.Z;

								if(currentState.getBlock() != SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get() || currentState.getValue(BlockStateProperties.AXIS) != typeToCheckFor)
								{
									if(currentState.getBlock() == SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get())
										return new TranslatableComponent("messages.securitycraft:blockpocket.invalidBlock.rotation", getFormattedRelativeCoordinates(currentPos, managerFacing), new TranslatableComponent(currentState.getBlock().asItem().getDescriptionId()));
									return new TranslatableComponent("messages.securitycraft:blockpocket.invalidBlock", getFormattedRelativeCoordinates(currentPos, managerFacing), new TranslatableComponent(currentState.getBlock().asItem().getDescriptionId()), new TranslatableComponent(SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get().getDescriptionId()));
								}
							}
							//checking the sides orthogonal to the block pocket manager
							else if((xi == lowest || xi == highest) && zi > lowest && zi < highest)
							{
								Axis typeToCheckFor = managerFacing == Direction.NORTH || managerFacing == Direction.SOUTH ? Axis.Z : Axis.X;

								if(currentState.getBlock() != SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get() || currentState.getValue(BlockStateProperties.AXIS) != typeToCheckFor)
								{
									if (currentState.getBlock() == SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get())
										return new TranslatableComponent("messages.securitycraft:blockpocket.invalidBlock.rotation", getFormattedRelativeCoordinates(currentPos, managerFacing), new TranslatableComponent(currentState.getBlock().asItem().getDescriptionId()));
									return new TranslatableComponent("messages.securitycraft:blockpocket.invalidBlock", getFormattedRelativeCoordinates(currentPos, managerFacing), new TranslatableComponent(currentState.getBlock().asItem().getDescriptionId()), new TranslatableComponent(SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get().getDescriptionId()));
								}
							}
							//checking the middle plane
							else if(xi > lowest && zi > lowest && xi < highest && zi < highest)
							{
								if(!(currentState.getBlock() instanceof BlockPocketWallBlock))
									return new TranslatableComponent("messages.securitycraft:blockpocket.invalidBlock", getFormattedRelativeCoordinates(currentPos, managerFacing), new TranslatableComponent(currentState.getBlock().asItem().getDescriptionId()), new TranslatableComponent(SCContent.BLOCK_POCKET_WALL.get().getDescriptionId()));

								floor.add(currentPos);
								sides.add(currentPos);
							}
						}
						//checking the corner edges
						else if(yi != lowest && yi != highest && ((xi == lowest && zi == lowest) || (xi == lowest && zi == highest) || (xi == highest && zi == lowest) || (xi == highest && zi == highest)))
						{
							if(currentState.getBlock() != SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get() || currentState.getValue(BlockStateProperties.AXIS) != Axis.Y)
							{
								if (currentState.getBlock() == SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get())
									return new TranslatableComponent("messages.securitycraft:blockpocket.invalidBlock.rotation", getFormattedRelativeCoordinates(currentPos, managerFacing), new TranslatableComponent(currentState.getBlock().asItem().getDescriptionId()));
								return new TranslatableComponent("messages.securitycraft:blockpocket.invalidBlock", getFormattedRelativeCoordinates(currentPos, managerFacing), new TranslatableComponent(currentState.getBlock().asItem().getDescriptionId()), new TranslatableComponent(SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get().getDescriptionId()));
							}
						}
						//checking the walls
						else if(yi > lowest && yi < highest)
						{
							//checking the walls parallel to the block pocket manager
							if((zi == lowest || zi == highest) && xi > lowest && xi < highest)
							{
								if(!(currentState.getBlock() instanceof BlockPocketWallBlock))
									return new TranslatableComponent("messages.securitycraft:blockpocket.invalidBlock", getFormattedRelativeCoordinates(currentPos, managerFacing), new TranslatableComponent(currentState.getBlock().asItem().getDescriptionId()), new TranslatableComponent(SCContent.BLOCK_POCKET_WALL.get().getDescriptionId()));

								sides.add(currentPos);
							}
							//checking the walls orthogonal to the block pocket manager
							else if((xi == lowest || xi == highest) && zi > lowest && zi < highest)
							{
								if(!(currentState.getBlock() instanceof BlockPocketWallBlock))
									return new TranslatableComponent("messages.securitycraft:blockpocket.invalidBlock", getFormattedRelativeCoordinates(currentPos, managerFacing), new TranslatableComponent(currentState.getBlock().asItem().getDescriptionId()), new TranslatableComponent(SCContent.BLOCK_POCKET_WALL.get().getDescriptionId()));

								sides.add(currentPos);
							}
						}

						OwnableTileEntity te = (OwnableTileEntity)level.getBlockEntity(currentPos);

						if(!getOwner().owns(te))
							return new TranslatableComponent("messages.securitycraft:blockpocket.unowned", getFormattedRelativeCoordinates(currentPos, managerFacing), new TranslatableComponent(currentState.getBlock().asItem().getDescriptionId()));
						else
							blocks.add(currentPos);

						xi++;
					}

					xi = 0;
					zi++;
					pos = startingPos.above(yi).relative(back, zi);
				}

				zi = 0;
				yi++;
				pos = startingPos.above(yi);
			}

			this.blocks = blocks;
			this.walls = sides;
			this.floor = floor;
			enabled = true;
			this.autoBuildOffset = -offset + (size / 2);

			for(BlockPos blockPos : blocks)
			{
				BlockEntity te = level.getBlockEntity(blockPos);

				if(te instanceof BlockPocketTileEntity)
					((BlockPocketTileEntity)te).setManager(this);
			}

			for(BlockPos blockPos : floor)
			{
				level.setBlockAndUpdate(blockPos, level.getBlockState(blockPos).setValue(BlockPocketWallBlock.SOLID, true));
			}

			setWalls(!hasModule(ModuleType.DISGUISE));
			return new TranslatableComponent("messages.securitycraft:blockpocket.activated");
		}

		return null;
	}

	/**
	 * Auto-assembles the Block Pocket for a player.
	 * First it makes sure that the space isn't occupied, then it checks its inventory for the required items, then it places the blocks.
	 * @return The feedback message. null if none should be sent.
	 */
	public MutableComponent autoAssembleMultiblock()
	{
		if(!enabled)
		{
			if(level.isClientSide)
				SecurityCraft.channel.sendToServer(new AssembleBlockPocket(this, size));

			final Direction managerFacing = getBlockState().getValue(BlockPocketManagerBlock.FACING);
			final Direction left = managerFacing.getClockWise();
			final Direction right = left.getOpposite();
			final Direction back = left.getClockWise();
			final BlockPos startingPos;
			final int lowest = 0;
			final int half = (size - 1) / 2 - autoBuildOffset;
			final int highest = size - 1;
			BlockPos pos = getBlockPos().immutable();
			int xi = lowest;
			int yi = lowest;
			int zi = lowest;
			int wallsNeeded = 0;
			int pillarsNeeded = 0;
			int chiseledNeeded = 0;

			pos = pos.relative(right, -half);
			startingPos = pos.immutable();

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

						BlockPos currentPos = pos.relative(right, xi);
						BlockState currentState = level.getBlockState(currentPos);
						boolean replaceable = currentState.getMaterial().isReplaceable();

						//checking the lowest and highest level of the cube
						if((yi == lowest && !currentPos.equals(getBlockPos())) || yi == highest) //if (y level is lowest AND it's not the block pocket manager's position) OR (y level is highest)
						{
							//checking the corners
							if(((xi == lowest && zi == lowest) || (xi == lowest && zi == highest) || (xi == highest && zi == lowest) || (xi == highest && zi == highest)))
							{
								if(currentState.getBlock() != SCContent.REINFORCED_CHISELED_CRYSTAL_QUARTZ.get() && !replaceable)
									return new TranslatableComponent("messages.securitycraft:blockpocket.blockInWay", getFormattedRelativeCoordinates(currentPos, managerFacing), new TranslatableComponent(currentState.getBlock().asItem().getDescriptionId()));

								if(replaceable)
									chiseledNeeded++;
							}
							//checking the sides parallel to the block pocket manager
							else if((zi == lowest || zi == highest) && xi > lowest && xi < highest)
							{
								Axis typeToCheckFor = managerFacing == Direction.NORTH || managerFacing == Direction.SOUTH ? Axis.X : Axis.Z;

								if(currentState.getBlock() != SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get() && !replaceable || (currentState.getBlock() == SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get() && currentState.getValue(BlockStateProperties.AXIS) != typeToCheckFor))
									return new TranslatableComponent("messages.securitycraft:blockpocket.blockInWay", getFormattedRelativeCoordinates(currentPos, managerFacing), new TranslatableComponent(currentState.getBlock().asItem().getDescriptionId()));

								if(replaceable)
									pillarsNeeded++;
							}
							//checking the sides orthogonal to the block pocket manager
							else if((xi == lowest || xi == highest) && zi > lowest && zi < highest)
							{
								Axis typeToCheckFor = managerFacing == Direction.NORTH || managerFacing == Direction.SOUTH ? Axis.Z : Axis.X;

								if(currentState.getBlock() != SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get() && !replaceable || (currentState.getBlock() == SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get() && currentState.getValue(BlockStateProperties.AXIS) != typeToCheckFor))
									return new TranslatableComponent("messages.securitycraft:blockpocket.blockInWay", getFormattedRelativeCoordinates(currentPos, managerFacing), new TranslatableComponent(currentState.getBlock().asItem().getDescriptionId()));

								if(replaceable)
									pillarsNeeded++;
							}
							//checking the middle plane
							else if(xi > lowest && zi > lowest && xi < highest && zi < highest)
							{
								if(!(currentState.getBlock() instanceof BlockPocketWallBlock) && !replaceable)
									return new TranslatableComponent("messages.securitycraft:blockpocket.blockInWay", getFormattedRelativeCoordinates(currentPos, managerFacing), new TranslatableComponent(currentState.getBlock().asItem().getDescriptionId()));

								if(replaceable)
									wallsNeeded++;
							}
						}
						//checking the corner edges
						else if(yi != lowest && yi != highest && ((xi == lowest && zi == lowest) || (xi == lowest && zi == highest) || (xi == highest && zi == lowest) || (xi == highest && zi == highest)))
						{
							if(currentState.getBlock() != SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get() && !replaceable || (currentState.getBlock() == SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get() && currentState.getValue(BlockStateProperties.AXIS) != Axis.Y))
								return new TranslatableComponent("messages.securitycraft:blockpocket.blockInWay", getFormattedRelativeCoordinates(currentPos, managerFacing), new TranslatableComponent(currentState.getBlock().asItem().getDescriptionId()));

							if(replaceable)
								pillarsNeeded++;
						}
						//checking the walls
						else if(yi > lowest && yi < highest)
						{
							//checking the walls parallel to the block pocket manager
							if((zi == lowest || zi == highest) && xi > lowest && xi < highest)
							{
								if(!(currentState.getBlock() instanceof BlockPocketWallBlock) && !replaceable)
									return new TranslatableComponent("messages.securitycraft:blockpocket.blockInWay", getFormattedRelativeCoordinates(currentPos, managerFacing), new TranslatableComponent(currentState.getBlock().asItem().getDescriptionId()));

								if(replaceable)
									wallsNeeded++;
							}
							//checking the walls orthogonal to the block pocket manager
							else if((xi == lowest || xi == highest) && zi > lowest && zi < highest)
							{
								if(!(currentState.getBlock() instanceof BlockPocketWallBlock) && !replaceable)
									return new TranslatableComponent("messages.securitycraft:blockpocket.blockInWay", getFormattedRelativeCoordinates(currentPos, managerFacing), new TranslatableComponent(currentState.getBlock().asItem().getDescriptionId()));

								if(replaceable)
									wallsNeeded++;
							}
						}

						if(level.getBlockEntity(currentPos) instanceof OwnableTileEntity)
						{
							OwnableTileEntity te = (OwnableTileEntity)level.getBlockEntity(currentPos);

							if(!getOwner().owns(te))
								return new TranslatableComponent("messages.securitycraft:blockpocket.unowned", getFormattedRelativeCoordinates(currentPos, managerFacing), new TranslatableComponent(currentState.getBlock().asItem().getDescriptionId()));
						}

						xi++;
					}

					xi = 0;
					zi++;
					pos = startingPos.above(yi).relative(back, zi);
				}

				zi = 0;
				yi++;
				pos = startingPos.above(yi);
			} //if the code comes to this place, the space is either clear or occupied by blocks that would have been placed either way, or existing blocks can be replaced (like grass)

			if(chiseledNeeded + pillarsNeeded + wallsNeeded == 0) //this applies when no blocks are missing, so when the BP is already in place
				return new TranslatableComponent("messages.securitycraft:blockpocket.alreadyAssembled");

			pos = getBlockPos().immutable().relative(right, -half);
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

						BlockPos currentPos = pos.relative(right, xi);
						BlockState currentState = level.getBlockState(currentPos);

						if(currentState.getBlock() instanceof BlockPocketManagerBlock && !currentPos.equals(getBlockPos()))
							return new TranslatableComponent("messages.securitycraft:blockpocket.multipleManagers");

						//placing the lowest and highest level of the cube
						if((yi == lowest && !currentPos.equals(getBlockPos())) || yi == highest) //if (y level is lowest AND it's not the block pocket manager's position) OR (y level is highest)
						{
							//placing the corners
							if(((xi == lowest && zi == lowest) || (xi == lowest && zi == highest) || (xi == highest && zi == lowest) || (xi == highest && zi == highest)))
								placeQueue.add(Pair.of(currentPos, SCContent.REINFORCED_CHISELED_CRYSTAL_QUARTZ.get().defaultBlockState()));
							//placing the sides parallel to the block pocket manager
							else if((zi == lowest || zi == highest) && xi > lowest && xi < highest)
							{
								Axis typeToPlace = managerFacing == Direction.NORTH || managerFacing == Direction.SOUTH ? Axis.X : Axis.Z;

								placeQueue.add(Pair.of(currentPos, SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get().defaultBlockState().setValue(ReinforcedRotatedPillarBlock.AXIS, typeToPlace)));
							}
							//placing the sides orthogonal to the block pocket manager
							else if((xi == lowest || xi == highest) && zi > lowest && zi < highest)
							{
								Axis typeToPlace = managerFacing == Direction.NORTH || managerFacing == Direction.SOUTH ? Axis.Z : Axis.X;

								placeQueue.add(Pair.of(currentPos, SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get().defaultBlockState().setValue(ReinforcedRotatedPillarBlock.AXIS, typeToPlace)));
							}
							//placing the middle plane
							else if(xi > lowest && zi > lowest && xi < highest && zi < highest)
								placeQueue.add(Pair.of(currentPos, SCContent.BLOCK_POCKET_WALL.get().defaultBlockState()));
						}
						//placing the corner edges
						else if(yi != lowest && yi != highest && ((xi == lowest && zi == lowest) || (xi == lowest && zi == highest) || (xi == highest && zi == lowest) || (xi == highest && zi == highest)))
							placeQueue.add(Pair.of(currentPos, SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get().defaultBlockState().setValue(ReinforcedRotatedPillarBlock.AXIS, Axis.Y)));
						//placing the walls
						else if(yi > lowest && yi < highest)
						{
							//checking the walls parallel to the block pocket manager
							if((zi == lowest || zi == highest) && xi > lowest && xi < highest)
								placeQueue.add(Pair.of(currentPos, SCContent.BLOCK_POCKET_WALL.get().defaultBlockState()));
							//checking the walls orthogonal to the block pocket manager
							else if((xi == lowest || xi == highest) && zi > lowest && zi < highest)
								placeQueue.add(Pair.of(currentPos, SCContent.BLOCK_POCKET_WALL.get().defaultBlockState()));
						}

						xi++;
					}

					xi = 0;
					zi++;
					pos = startingPos.above(yi).relative(back, zi);
				}

				zi = 0;
				yi++;
				pos = startingPos.above(yi);
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
			if(level.isClientSide)
			{
				SecurityCraft.channel.sendToServer(new ToggleBlockPocketManager(this, false, size));
			}

			PlayerUtils.sendMessageToPlayer(SecurityCraft.proxy.getClientPlayer(), Utils.localize(SCContent.BLOCK_POCKET_MANAGER.get().getDescriptionId()), Utils.localize("messages.securitycraft:blockpocket.deactivated"), ChatFormatting.DARK_AQUA);
			enabled = false;

			for(BlockPos pos : blocks)
			{
				BlockEntity te = level.getBlockEntity(pos);

				if(te instanceof BlockPocketTileEntity)
					((BlockPocketTileEntity)te).removeManager();
			}

			for(BlockPos pos : floor)
			{
				BlockState state = level.getBlockState(pos);

				if(state.hasProperty(BlockPocketWallBlock.SOLID))
					level.setBlockAndUpdate(pos, state.setValue(BlockPocketWallBlock.SOLID, false));
			}

			if(hasModule(ModuleType.DISGUISE))
				setWalls(true);

			blocks.clear();
			walls.clear();
			floor.clear();
		}
	}

	private TranslatableComponent getFormattedRelativeCoordinates(BlockPos pos, Direction managerFacing) {
		BlockPos difference = pos.subtract(this.worldPosition);
		int offsetLeft;
		int offsetBehind;
		int offsetAbove = difference.getY();
		List<TranslatableComponent> components = new ArrayList<>();

		offsetLeft = switch(managerFacing)
				{
					case NORTH ->
					{
						offsetBehind = difference.getZ();
						yield difference.getX();
					}
					case SOUTH ->
					{
						offsetBehind = -difference.getZ();
						yield -difference.getX();
					}
					case WEST ->
					{
						offsetBehind = difference.getX();
						yield -difference.getZ();
					}
					case EAST ->
					{
						offsetBehind = -difference.getX();
						yield difference.getZ();
					}
					default -> throw new IllegalArgumentException("Invalid Block Pocket Manager direction: " + managerFacing.name());
				};

				if (offsetLeft > 0) {
					components.add(Utils.localize("messages.securitycraft:blockpocket.position.blocksLeft", offsetLeft));
				}
				else if (offsetLeft < 0) {
					components.add(Utils.localize("messages.securitycraft:blockpocket.position.blocksRight", -offsetLeft));
				}

				if (offsetBehind > 0) {
					components.add(Utils.localize("messages.securitycraft:blockpocket.position.blocksBehind", offsetBehind));
				}

				if (offsetAbove > 0) {
					components.add(Utils.localize("messages.securitycraft:blockpocket.position.blocksAbove", offsetAbove));
				}

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
			BlockState state = level.getBlockState(pos);

			if(state.getBlock() instanceof BlockPocketWallBlock)
				level.setBlockAndUpdate(pos, state.setValue(BlockPocketWallBlock.SEE_THROUGH, seeThrough));
		}
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side)
	{
		if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
		{
			if(isPlacingBlocks()) //prevent extracting while auto building the block pocket
				return getInsertOnlyHandler().cast();
			else return BlockUtils.getProtectedCapability(side, this, () -> getStorageHandler(), () -> getInsertOnlyHandler()).cast();
		}
		else return super.getCapability(cap, side);
	}

	@Override
	public void onTileEntityDestroyed()
	{
		super.onTileEntityDestroyed();

		if (level.getBlockState(worldPosition).getBlock() != SCContent.BLOCK_POCKET_MANAGER.get())
			disableMultiblock();
	}

	@Override
	public void onModuleInserted(ItemStack stack, ModuleType module)
	{
		super.onModuleInserted(stack, module);

		if(enabled && module == ModuleType.DISGUISE)
			setWalls(false);
	}

	@Override
	public void onModuleRemoved(ItemStack stack, ModuleType module)
	{
		super.onModuleRemoved(stack, module);

		if(enabled && module == ModuleType.DISGUISE)
			setWalls(true);
		else if(module == ModuleType.STORAGE)
		{
			getStorageHandler().ifPresent(handler -> {
				for(int i = 0; i < handler.getSlots(); i++)
				{
					Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), handler.getStackInSlot(i));
				}
			});
		}
	}

	@Override
	public CompoundTag save(CompoundTag tag)
	{
		tag.putBoolean("BlockPocketEnabled", enabled);
		tag.putBoolean("ShowOutline", showOutline);
		tag.putInt("Size", size);
		tag.putInt("AutoBuildOffset", autoBuildOffset);
		ContainerHelper.saveAllItems(tag, storage);

		for(int i = 0; i < blocks.size(); i++)
		{
			tag.putLong("BlocksList" + i, blocks.get(i).asLong());
		}

		for(int i = 0; i < walls.size(); i++)
		{
			tag.putLong("WallsList" + i, walls.get(i).asLong());
		}

		for(int i = 0; i < floor.size(); i++)
		{
			tag.putLong("FloorList" + i, floor.get(i).asLong());
		}

		return super.save(tag);
	}

	@Override
	public void load(CompoundTag tag)
	{
		int i = 0;

		super.load(tag);
		enabled = tag.getBoolean("BlockPocketEnabled");
		showOutline = tag.getBoolean("ShowOutline");
		size = tag.getInt("Size");
		autoBuildOffset = tag.getInt("AutoBuildOffset");
		ContainerHelper.loadAllItems(tag, storage);

		while(tag.contains("BlocksList" + i))
		{
			blocks.add(BlockPos.of(tag.getLong("BlocksList" + i)));
			i++;
		}

		i = 0;

		while(tag.contains("WallsList" + i))
		{
			walls.add(BlockPos.of(tag.getLong("WallsList" + i)));
			i++;
		}

		i = 0;

		while(tag.contains("FloorList" + i))
		{
			floor.add(BlockPos.of(tag.getLong("FloorList" + i)));
			i++;
		}
	}

	@Override
	public ModuleType[] acceptedModules()
	{
		return new ModuleType[] {
				ModuleType.DISGUISE,
				ModuleType.ALLOWLIST,
				ModuleType.STORAGE
		};
	}

	@Override
	public Option<?>[] customOptions()
	{
		return null;
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player)
	{
		return new BlockPocketManagerContainer(windowId, level, worldPosition, inv);
	}

	@Override
	public Component getDisplayName()
	{
		return new TranslatableComponent(SCContent.BLOCK_POCKET_MANAGER.get().getDescriptionId());
	}

	@Override
	public AABB getRenderBoundingBox()
	{
		return new AABB(getBlockPos()).inflate(RENDER_DISTANCE);
	}

	public LazyOptional<IItemHandler> getStorageHandler()
	{
		if(storageHandler == null)
		{
			storageHandler = LazyOptional.of(() -> new ItemStackHandler(storage) {
				@Override
				public boolean isItemValid(int slot, ItemStack stack)
				{
					return BlockPocketManagerTileEntity.isItemValid(stack);
				}
			});
		}

		return storageHandler;
	}

	private LazyOptional<IItemHandler> getInsertOnlyHandler()
	{
		if(insertOnlyHandler == null)
		{
			insertOnlyHandler = LazyOptional.of(() -> new InsertOnlyItemStackHandler(storage) {
				@Override
				public boolean isItemValid(int slot, ItemStack stack)
				{
					return BlockPocketManagerTileEntity.isItemValid(stack);
				}
			});
		}

		return insertOnlyHandler;
	}

	public boolean isPlacingBlocks()
	{
		return shouldPlaceBlocks;
	}

	public static boolean isItemValid(ItemStack stack)
	{
		if(stack.getItem() instanceof BlockItem)
		{
			Block block = ((BlockItem)stack.getItem()).getBlock();

			return block == SCContent.BLOCK_POCKET_WALL.get() || block == SCContent.REINFORCED_CHISELED_CRYSTAL_QUARTZ.get() || block == SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get();
		}

		return false;
	}
}
