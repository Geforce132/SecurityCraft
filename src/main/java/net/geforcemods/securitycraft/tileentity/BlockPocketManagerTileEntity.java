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
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.IBlockPocket;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class BlockPocketManagerTileEntity extends CustomizableTileEntity implements INamedContainerProvider
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

	public BlockPocketManagerTileEntity()
	{
		super(SCContent.teTypeBlockPocketManager);
	}

	@Override
	public void tick()
	{
		super.tick();

		if(!world.isRemote && shouldPlaceBlocks)
		{
			PlayerEntity owner = PlayerUtils.getPlayerFromName(getOwner().getName());
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
						throw new IllegalStateException(String.format("Tried to automatically place non-block pocket block \"%s\"! This mustn't happen!", toPlace.getRight().getBlock().getTranslationKey()));
				}
				//reach the next block that is missing for the block pocket
				while((stateInWorld = world.getBlockState(toPlace.getLeft())) == toPlace.getRight());

				if(stateInWorld.getMaterial().isReplaceable())
				{
					BlockPos pos = toPlace.getLeft();
					BlockState stateToPlace = toPlace.getRight();
					SoundType soundType = stateToPlace.getSoundType();
					TileEntity te;

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

					world.setBlockState(pos, stateToPlace);
					world.playSound(null, pos, soundType.getPlaceSound(), SoundCategory.BLOCKS, soundType.getVolume(), soundType.getPitch());
					te = world.getTileEntity(pos);

					//assigning the owner
					if(te instanceof OwnableTileEntity)
						((OwnableTileEntity)te).getOwner().set(getOwner());

					continue;
				}

				//when an invalid block is in the way
				PlayerUtils.sendMessageToPlayer(owner, ClientUtils.localize(SCContent.BLOCK_POCKET_MANAGER.get().getTranslationKey()), new TranslationTextComponent("messages.securitycraft:blockpocket.assemblyFailed", getFormattedRelativeCoordinates(toPlace.getLeft(), getBlockState().get(BlockPocketManagerBlock.FACING)), new TranslationTextComponent(stateInWorld.getBlock().getTranslationKey())), TextFormatting.DARK_AQUA);
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
					PlayerUtils.sendMessageToPlayer(owner, ClientUtils.localize(SCContent.BLOCK_POCKET_MANAGER.get().getTranslationKey()), new TranslationTextComponent("messages.securitycraft:blockpocket.assembled"), TextFormatting.DARK_AQUA);
				}

				shouldPlaceBlocks = false;
			}
		}
	}

	/**
	 * Enables the block pocket
	 * @return The feedback message. null if none should be sent.
	 */
	public TranslationTextComponent enableMultiblock()
	{
		if(!enabled) //multiblock detection
		{
			if(world.isRemote)
				SecurityCraft.channel.sendToServer(new ToggleBlockPocketManager(this, true, size));

			List<BlockPos> blocks = new ArrayList<>();
			List<BlockPos> sides = new ArrayList<>();
			List<BlockPos> floor = new ArrayList<>();
			final Direction managerFacing = world.getBlockState(pos).get(BlockPocketManagerBlock.FACING);
			final Direction left = managerFacing.rotateY();
			final Direction right = left.getOpposite();
			final Direction back = left.rotateY();
			final BlockPos startingPos;
			final int lowest = 0;
			final int highest = size - 1;
			BlockPos pos = getPos().toImmutable();
			int xi = lowest;
			int yi = lowest;
			int zi = lowest;
			int offset = 0;

			if (!(world.getBlockState(pos.offset(left)).getBlock() instanceof IBlockPocket)) { //when the block left of the manager is not a Block Pocket block (so the manager was just placed down), take the autoBuildOffset
				pos = pos.offset(left, offset = -autoBuildOffset + (size / 2));
			}
			else {
				for (int i = 1; i < size - 1; i++) { //find the bottom left corner
					if (!(world.getBlockState(pos.offset(left, i)).getBlock() instanceof ReinforcedRotatedCrystalQuartzPillar)) {
						pos = pos.offset(left, offset = i);
						break;
					}
				}

				if (offset == 0) //when the bottom left corner couldn't be evaluated, take the autoBuildOffset
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
						BlockState currentState = world.getBlockState(currentPos);

						if(currentState.getBlock() instanceof BlockPocketManagerBlock && !currentPos.equals(getPos()))
							return new TranslationTextComponent("messages.securitycraft:blockpocket.multipleManagers");

						//checking the lowest and highest level of the cube
						if((yi == lowest && !currentPos.equals(getPos())) || yi == highest) //if (y level is lowest AND it's not the block pocket manager's position) OR (y level is highest)
						{
							//checking the corners
							if(((xi == lowest && zi == lowest) || (xi == lowest && zi == highest) || (xi == highest && zi == lowest) || (xi == highest && zi == highest)))
							{
								if(currentState.getBlock() != SCContent.REINFORCED_CHISELED_CRYSTAL_QUARTZ.get())
									return new TranslationTextComponent("messages.securitycraft:blockpocket.invalidBlock", getFormattedRelativeCoordinates(currentPos, managerFacing), new TranslationTextComponent(currentState.getBlock().asItem().getTranslationKey()), new TranslationTextComponent(SCContent.REINFORCED_CHISELED_CRYSTAL_QUARTZ.get().getTranslationKey()));
							}
							//checking the sides parallel to the block pocket manager
							else if((zi == lowest || zi == highest) && xi > lowest && xi < highest)
							{
								Axis typeToCheckFor = managerFacing == Direction.NORTH || managerFacing == Direction.SOUTH ? Axis.X : Axis.Z;

								if(currentState.getBlock() != SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get() || currentState.get(BlockStateProperties.AXIS) != typeToCheckFor)
								{
									if(currentState.getBlock() == SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get())
										return new TranslationTextComponent("messages.securitycraft:blockpocket.invalidBlock.rotation", getFormattedRelativeCoordinates(currentPos, managerFacing), new TranslationTextComponent(currentState.getBlock().asItem().getTranslationKey()));
									return new TranslationTextComponent("messages.securitycraft:blockpocket.invalidBlock", getFormattedRelativeCoordinates(currentPos, managerFacing), new TranslationTextComponent(currentState.getBlock().asItem().getTranslationKey()), new TranslationTextComponent(SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get().getTranslationKey()));
								}
							}
							//checking the sides orthogonal to the block pocket manager
							else if((xi == lowest || xi == highest) && zi > lowest && zi < highest)
							{
								Axis typeToCheckFor = managerFacing == Direction.NORTH || managerFacing == Direction.SOUTH ? Axis.Z : Axis.X;

								if(currentState.getBlock() != SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get() || currentState.get(BlockStateProperties.AXIS) != typeToCheckFor)
								{
									if (currentState.getBlock() == SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get())
										return new TranslationTextComponent("messages.securitycraft:blockpocket.invalidBlock.rotation", getFormattedRelativeCoordinates(currentPos, managerFacing), new TranslationTextComponent(currentState.getBlock().asItem().getTranslationKey()));
									return new TranslationTextComponent("messages.securitycraft:blockpocket.invalidBlock", getFormattedRelativeCoordinates(currentPos, managerFacing), new TranslationTextComponent(currentState.getBlock().asItem().getTranslationKey()), new TranslationTextComponent(SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get().getTranslationKey()));
								}
							}
							//checking the middle plane
							else if(xi > lowest && zi > lowest && xi < highest && zi < highest)
							{
								if(!(currentState.getBlock() instanceof BlockPocketWallBlock))
									return new TranslationTextComponent("messages.securitycraft:blockpocket.invalidBlock", getFormattedRelativeCoordinates(currentPos, managerFacing), new TranslationTextComponent(currentState.getBlock().asItem().getTranslationKey()), new TranslationTextComponent(SCContent.BLOCK_POCKET_WALL.get().getTranslationKey()));

								floor.add(currentPos);
								sides.add(currentPos);
							}
						}
						//checking the corner edges
						else if(yi != lowest && yi != highest && ((xi == lowest && zi == lowest) || (xi == lowest && zi == highest) || (xi == highest && zi == lowest) || (xi == highest && zi == highest)))
						{
							if(currentState.getBlock() != SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get() || currentState.get(BlockStateProperties.AXIS) != Axis.Y)
							{
								if (currentState.getBlock() == SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get())
									return new TranslationTextComponent("messages.securitycraft:blockpocket.invalidBlock.rotation", getFormattedRelativeCoordinates(currentPos, managerFacing), new TranslationTextComponent(currentState.getBlock().asItem().getTranslationKey()));
								return new TranslationTextComponent("messages.securitycraft:blockpocket.invalidBlock", getFormattedRelativeCoordinates(currentPos, managerFacing), new TranslationTextComponent(currentState.getBlock().asItem().getTranslationKey()), new TranslationTextComponent(SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get().getTranslationKey()));
							}
						}
						//checking the walls
						else if(yi > lowest && yi < highest)
						{
							//checking the walls parallel to the block pocket manager
							if((zi == lowest || zi == highest) && xi > lowest && xi < highest)
							{
								if(!(currentState.getBlock() instanceof BlockPocketWallBlock))
									return new TranslationTextComponent("messages.securitycraft:blockpocket.invalidBlock", getFormattedRelativeCoordinates(currentPos, managerFacing), new TranslationTextComponent(currentState.getBlock().asItem().getTranslationKey()), new TranslationTextComponent(SCContent.BLOCK_POCKET_WALL.get().getTranslationKey()));

								sides.add(currentPos);
							}
							//checking the walls orthogonal to the block pocket manager
							else if((xi == lowest || xi == highest) && zi > lowest && zi < highest)
							{
								if(!(currentState.getBlock() instanceof BlockPocketWallBlock))
									return new TranslationTextComponent("messages.securitycraft:blockpocket.invalidBlock", getFormattedRelativeCoordinates(currentPos, managerFacing), new TranslationTextComponent(currentState.getBlock().asItem().getTranslationKey()), new TranslationTextComponent(SCContent.BLOCK_POCKET_WALL.get().getTranslationKey()));

								sides.add(currentPos);
							}
						}

						OwnableTileEntity te = (OwnableTileEntity)world.getTileEntity(currentPos);

						if(!getOwner().owns(te))
							return new TranslationTextComponent("messages.securitycraft:blockpocket.unowned", getFormattedRelativeCoordinates(currentPos, managerFacing), new TranslationTextComponent(currentState.getBlock().asItem().getTranslationKey()));
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
			this.autoBuildOffset = -offset + (size / 2);

			for(BlockPos blockPos : blocks)
			{
				TileEntity te = world.getTileEntity(blockPos);

				if(te instanceof BlockPocketTileEntity)
					((BlockPocketTileEntity)te).setManager(this);
			}

			for(BlockPos blockPos : floor)
			{
				world.setBlockState(blockPos, world.getBlockState(blockPos).with(BlockPocketWallBlock.SOLID, true));
			}

			setWalls(!hasModule(ModuleType.DISGUISE));
			return new TranslationTextComponent("messages.securitycraft:blockpocket.activated");
		}

		return null;
	}

	/**
	 * Auto-assembles the Block Pocket for a player.
	 * First it makes sure that the space isn't occupied, then it checks its inventory for the required items, then it places the blocks.
	 * @return The feedback message. null if none should be sent.
	 */
	public IFormattableTextComponent autoAssembleMultiblock()
	{
		if(!enabled)
		{
			if(world.isRemote)
				SecurityCraft.channel.sendToServer(new AssembleBlockPocket(this, size));

			final Direction managerFacing = getBlockState().get(BlockPocketManagerBlock.FACING);
			final Direction left = managerFacing.rotateY();
			final Direction right = left.getOpposite();
			final Direction back = left.rotateY();
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
						BlockState currentState = world.getBlockState(currentPos);
						boolean replaceable = currentState.getMaterial().isReplaceable();

						//checking the lowest and highest level of the cube
						if((yi == lowest && !currentPos.equals(getPos())) || yi == highest) //if (y level is lowest AND it's not the block pocket manager's position) OR (y level is highest)
						{
							//checking the corners
							if(((xi == lowest && zi == lowest) || (xi == lowest && zi == highest) || (xi == highest && zi == lowest) || (xi == highest && zi == highest)))
							{
								if(currentState.getBlock() != SCContent.REINFORCED_CHISELED_CRYSTAL_QUARTZ.get() && !replaceable)
									return new TranslationTextComponent("messages.securitycraft:blockpocket.blockInWay", getFormattedRelativeCoordinates(currentPos, managerFacing), new TranslationTextComponent(currentState.getBlock().asItem().getTranslationKey()));

								if(replaceable)
									chiseledNeeded++;
							}
							//checking the sides parallel to the block pocket manager
							else if((zi == lowest || zi == highest) && xi > lowest && xi < highest)
							{
								Axis typeToCheckFor = managerFacing == Direction.NORTH || managerFacing == Direction.SOUTH ? Axis.X : Axis.Z;

								if(currentState.getBlock() != SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get() && !replaceable || (currentState.getBlock() == SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get() && currentState.get(BlockStateProperties.AXIS) != typeToCheckFor))
									return new TranslationTextComponent("messages.securitycraft:blockpocket.blockInWay", getFormattedRelativeCoordinates(currentPos, managerFacing), new TranslationTextComponent(currentState.getBlock().asItem().getTranslationKey()));

								if(replaceable)
									pillarsNeeded++;
							}
							//checking the sides orthogonal to the block pocket manager
							else if((xi == lowest || xi == highest) && zi > lowest && zi < highest)
							{
								Axis typeToCheckFor = managerFacing == Direction.NORTH || managerFacing == Direction.SOUTH ? Axis.Z : Axis.X;

								if(currentState.getBlock() != SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get() && !replaceable || (currentState.getBlock() == SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get() && currentState.get(BlockStateProperties.AXIS) != typeToCheckFor))
									return new TranslationTextComponent("messages.securitycraft:blockpocket.blockInWay", getFormattedRelativeCoordinates(currentPos, managerFacing), new TranslationTextComponent(currentState.getBlock().asItem().getTranslationKey()));

								if(replaceable)
									pillarsNeeded++;
							}
							//checking the middle plane
							else if(xi > lowest && zi > lowest && xi < highest && zi < highest)
							{
								if(!(currentState.getBlock() instanceof BlockPocketWallBlock) && !replaceable)
									return new TranslationTextComponent("messages.securitycraft:blockpocket.blockInWay", getFormattedRelativeCoordinates(currentPos, managerFacing), new TranslationTextComponent(currentState.getBlock().asItem().getTranslationKey()));

								if(replaceable)
									wallsNeeded++;
							}
						}
						//checking the corner edges
						else if(yi != lowest && yi != highest && ((xi == lowest && zi == lowest) || (xi == lowest && zi == highest) || (xi == highest && zi == lowest) || (xi == highest && zi == highest)))
						{
							if(currentState.getBlock() != SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get() && !replaceable || (currentState.getBlock() == SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get() && currentState.get(BlockStateProperties.AXIS) != Axis.Y))
								return new TranslationTextComponent("messages.securitycraft:blockpocket.blockInWay", getFormattedRelativeCoordinates(currentPos, managerFacing), new TranslationTextComponent(currentState.getBlock().asItem().getTranslationKey()));

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
									return new TranslationTextComponent("messages.securitycraft:blockpocket.blockInWay", getFormattedRelativeCoordinates(currentPos, managerFacing), new TranslationTextComponent(currentState.getBlock().asItem().getTranslationKey()));

								if(replaceable)
									wallsNeeded++;
							}
							//checking the walls orthogonal to the block pocket manager
							else if((xi == lowest || xi == highest) && zi > lowest && zi < highest)
							{
								if(!(currentState.getBlock() instanceof BlockPocketWallBlock) && !replaceable)
									return new TranslationTextComponent("messages.securitycraft:blockpocket.blockInWay", getFormattedRelativeCoordinates(currentPos, managerFacing), new TranslationTextComponent(currentState.getBlock().asItem().getTranslationKey()));

								if(replaceable)
									wallsNeeded++;
							}
						}

						if(world.getTileEntity(currentPos) instanceof OwnableTileEntity)
						{
							OwnableTileEntity te = (OwnableTileEntity)world.getTileEntity(currentPos);

							if(!getOwner().owns(te))
								return new TranslationTextComponent("messages.securitycraft:blockpocket.unowned", getFormattedRelativeCoordinates(currentPos, managerFacing), new TranslationTextComponent(currentState.getBlock().asItem().getTranslationKey()));
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
				return new TranslationTextComponent("messages.securitycraft:blockpocket.alreadyAssembled");

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
						BlockState currentState = world.getBlockState(currentPos);

						if(currentState.getBlock() instanceof BlockPocketManagerBlock && !currentPos.equals(getPos()))
							return new TranslationTextComponent("messages.securitycraft:blockpocket.multipleManagers");

						//placing the lowest and highest level of the cube
						if((yi == lowest && !currentPos.equals(getPos())) || yi == highest) //if (y level is lowest AND it's not the block pocket manager's position) OR (y level is highest)
						{
							//placing the corners
							if(((xi == lowest && zi == lowest) || (xi == lowest && zi == highest) || (xi == highest && zi == lowest) || (xi == highest && zi == highest)))
								placeQueue.add(Pair.of(currentPos, SCContent.REINFORCED_CHISELED_CRYSTAL_QUARTZ.get().getDefaultState()));
							//placing the sides parallel to the block pocket manager
							else if((zi == lowest || zi == highest) && xi > lowest && xi < highest)
							{
								Axis typeToPlace = managerFacing == Direction.NORTH || managerFacing == Direction.SOUTH ? Axis.X : Axis.Z;

								placeQueue.add(Pair.of(currentPos, SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get().getDefaultState().with(ReinforcedRotatedPillarBlock.AXIS, typeToPlace)));
							}
							//placing the sides orthogonal to the block pocket manager
							else if((xi == lowest || xi == highest) && zi > lowest && zi < highest)
							{
								Axis typeToPlace = managerFacing == Direction.NORTH || managerFacing == Direction.SOUTH ? Axis.Z : Axis.X;

								placeQueue.add(Pair.of(currentPos, SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get().getDefaultState().with(ReinforcedRotatedPillarBlock.AXIS, typeToPlace)));
							}
							//placing the middle plane
							else if(xi > lowest && zi > lowest && xi < highest && zi < highest)
								placeQueue.add(Pair.of(currentPos, SCContent.BLOCK_POCKET_WALL.get().getDefaultState()));
						}
						//placing the corner edges
						else if(yi != lowest && yi != highest && ((xi == lowest && zi == lowest) || (xi == lowest && zi == highest) || (xi == highest && zi == lowest) || (xi == highest && zi == highest)))
							placeQueue.add(Pair.of(currentPos, SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get().getDefaultState().with(ReinforcedRotatedPillarBlock.AXIS, Axis.Y)));
						//placing the walls
						else if(yi > lowest && yi < highest)
						{
							//checking the walls parallel to the block pocket manager
							if((zi == lowest || zi == highest) && xi > lowest && xi < highest)
								placeQueue.add(Pair.of(currentPos, SCContent.BLOCK_POCKET_WALL.get().getDefaultState()));
							//checking the walls orthogonal to the block pocket manager
							else if((xi == lowest || xi == highest) && zi > lowest && zi < highest)
								placeQueue.add(Pair.of(currentPos, SCContent.BLOCK_POCKET_WALL.get().getDefaultState()));
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
				SecurityCraft.channel.sendToServer(new ToggleBlockPocketManager(this, false, size));
			}

			PlayerUtils.sendMessageToPlayer(SecurityCraft.proxy.getClientPlayer(), ClientUtils.localize(SCContent.BLOCK_POCKET_MANAGER.get().getTranslationKey()), ClientUtils.localize("messages.securitycraft:blockpocket.deactivated"), TextFormatting.DARK_AQUA);
			enabled = false;

			for(BlockPos pos : blocks)
			{
				TileEntity te = world.getTileEntity(pos);

				if(te instanceof BlockPocketTileEntity)
					((BlockPocketTileEntity)te).removeManager();
			}

			for(BlockPos pos : floor)
			{
				BlockState state = world.getBlockState(pos);

				if(state.hasProperty(BlockPocketWallBlock.SOLID))
					world.setBlockState(pos, state.with(BlockPocketWallBlock.SOLID, false));
			}

			if(hasModule(ModuleType.DISGUISE))
				setWalls(true);

			blocks.clear();
			walls.clear();
			floor.clear();
		}
	}

	private TranslationTextComponent getFormattedRelativeCoordinates(BlockPos pos, Direction managerFacing) {
		BlockPos difference = pos.subtract(this.pos);
		int offsetLeft;
		int offsetBehind;
		int offsetAbove = difference.getY();
		List<TranslationTextComponent> components = new ArrayList<>();

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

		if (offsetLeft > 0) {
			components.add(ClientUtils.localize("messages.securitycraft:blockpocket.position.blocksLeft", offsetLeft));
		}
		else if (offsetLeft < 0) {
			components.add(ClientUtils.localize("messages.securitycraft:blockpocket.position.blocksRight", -offsetLeft));
		}

		if (offsetBehind > 0) {
			components.add(ClientUtils.localize("messages.securitycraft:blockpocket.position.blocksBehind", offsetBehind));
		}

		if (offsetAbove > 0) {
			components.add(ClientUtils.localize("messages.securitycraft:blockpocket.position.blocksAbove", offsetAbove));
		}

		return ClientUtils.localize("messages.securitycraft:blockpocket.position." + components.size(), components.toArray());
	}

	public void toggleOutline()
	{
		showOutline = !showOutline;
	}

	public void setWalls(boolean seeThrough)
	{
		for(BlockPos pos : walls)
		{
			BlockState state = world.getBlockState(pos);

			if(state.getBlock() instanceof BlockPocketWallBlock)
				world.setBlockState(pos, state.with(BlockPocketWallBlock.SEE_THROUGH, seeThrough));
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

		if (world.getBlockState(pos).getBlock() != SCContent.BLOCK_POCKET_MANAGER.get())
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
					InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), handler.getStackInSlot(i));
				}
			});
		}
	}

	@Override
	public CompoundNBT write(CompoundNBT tag)
	{
		tag.putBoolean("BlockPocketEnabled", enabled);
		tag.putBoolean("ShowOutline", showOutline);
		tag.putInt("Size", size);
		tag.putInt("AutoBuildOffset", autoBuildOffset);
		ItemStackHelper.saveAllItems(tag, storage);

		for(int i = 0; i < blocks.size(); i++)
		{
			tag.putLong("BlocksList" + i, blocks.get(i).toLong());
		}

		for(int i = 0; i < walls.size(); i++)
		{
			tag.putLong("WallsList" + i, walls.get(i).toLong());
		}

		for(int i = 0; i < floor.size(); i++)
		{
			tag.putLong("FloorList" + i, floor.get(i).toLong());
		}

		return super.write(tag);
	}

	@Override
	public void read(BlockState state, CompoundNBT tag)
	{
		int i = 0;

		super.read(state, tag);
		enabled = tag.getBoolean("BlockPocketEnabled");
		showOutline = tag.getBoolean("ShowOutline");
		size = tag.getInt("Size");
		autoBuildOffset = tag.getInt("AutoBuildOffset");
		ItemStackHelper.loadAllItems(tag, storage);

		while(tag.contains("BlocksList" + i))
		{
			blocks.add(BlockPos.fromLong(tag.getLong("BlocksList" + i)));
			i++;
		}

		i = 0;

		while(tag.contains("WallsList" + i))
		{
			walls.add(BlockPos.fromLong(tag.getLong("WallsList" + i)));
			i++;
		}

		i = 0;

		while(tag.contains("FloorList" + i))
		{
			floor.add(BlockPos.fromLong(tag.getLong("FloorList" + i)));
			i++;
		}
	}

	@Override
	public ModuleType[] acceptedModules()
	{
		return new ModuleType[] {
				ModuleType.DISGUISE,
				ModuleType.WHITELIST,
				ModuleType.STORAGE
		};
	}

	@Override
	public Option<?>[] customOptions()
	{
		return null;
	}

	@Override
	public Container createMenu(int windowId, PlayerInventory inv, PlayerEntity player)
	{
		return new BlockPocketManagerContainer(windowId, world, pos, inv);
	}

	@Override
	public ITextComponent getDisplayName()
	{
		return new TranslationTextComponent(SCContent.BLOCK_POCKET_MANAGER.get().getTranslationKey());
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox()
	{
		return new AxisAlignedBB(getPos()).grow(RENDER_DISTANCE);
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
