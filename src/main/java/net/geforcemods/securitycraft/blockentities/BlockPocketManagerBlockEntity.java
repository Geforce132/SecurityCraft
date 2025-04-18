package net.geforcemods.securitycraft.blockentities;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableBlockEntity;
import net.geforcemods.securitycraft.api.ILockable;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.OwnableBlockEntity;
import net.geforcemods.securitycraft.blocks.BlockPocketManagerBlock;
import net.geforcemods.securitycraft.blocks.BlockPocketWallBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedRotatedCrystalQuartzPillar;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedRotatedPillarBlock;
import net.geforcemods.securitycraft.inventory.BlockPocketManagerMenu;
import net.geforcemods.securitycraft.inventory.InsertOnlyItemStackHandler;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.IBlockPocket;
import net.geforcemods.securitycraft.util.ITickingBlockEntity;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;

public class BlockPocketManagerBlockEntity extends CustomizableBlockEntity implements MenuProvider, ITickingBlockEntity, ILockable {
	private static final int BLOCK_PLACEMENTS_PER_TICK = 4;
	private boolean enabled = false;
	private boolean showOutline = false;
	private int color = 0xFF0000FF;
	private int size = 5;
	private int autoBuildOffset = 0;
	private List<BlockPos> blocks = new ArrayList<>();
	private List<BlockPos> walls = new ArrayList<>();
	private List<BlockPos> floor = new ArrayList<>();
	protected NonNullList<ItemStack> storage = NonNullList.withSize(56, ItemStack.EMPTY);
	private List<Pair<BlockPos, BlockState>> placeQueue = new ArrayList<>();
	private boolean shouldPlaceBlocks = false;

	public BlockPocketManagerBlockEntity(BlockPos pos, BlockState state) {
		super(SCContent.BLOCK_POCKET_MANAGER_BLOCK_ENTITY.get(), pos, state);
	}

	@Override
	public void tick(Level level, BlockPos pos, BlockState state) {
		if (shouldPlaceBlocks) {
			Player owner = PlayerUtils.getPlayerFromName(getOwner().getName());

			//if the owner left the server, stop building the block pocket
			if (owner == null) {
				placeQueue.clear();
				shouldPlaceBlocks = false;
				return;
			}

			boolean isCreative = owner.isCreative();
			boolean placed4Blocks = true;

			//place 4 blocks per tick
			//only place the next block if the previous one was placed
			//if any block failed to place, either the end was reached, or a block was in the way
			placeLoop:
			for (int i = 0; i < BLOCK_PLACEMENTS_PER_TICK; i++) {
				Pair<BlockPos, BlockState> toPlace;
				BlockState stateInLevel;

				do {
					if (placeQueue.isEmpty()) {
						placed4Blocks = false;
						break placeLoop;
					}

					toPlace = placeQueue.remove(0);

					if (!(toPlace.getRight().getBlock() instanceof IBlockPocket))
						throw new IllegalStateException(String.format("Tried to automatically place non-block pocket block \"%s\"! This mustn't happen!", toPlace.getRight().getBlock().getDescriptionId()));
				}
				//reach the next block that is missing for the block pocket
				while ((stateInLevel = level.getBlockState(toPlace.getLeft())) == toPlace.getRight());

				if (stateInLevel.canBeReplaced()) {
					BlockPos placeLocation = toPlace.getLeft();
					BlockState stateToPlace = toPlace.getRight();
					SoundType soundType = stateToPlace.getSoundType();
					BlockEntity placedBe;

					if (!isCreative) {
						//remove blocks from inventory
						invLoop:
						for (int k = 0; k < storage.size(); k++) {
							ItemStack stackToCheck = storage.get(k);

							if (!stackToCheck.isEmpty() && ((BlockItem) stackToCheck.getItem()).getBlock() == stateToPlace.getBlock()) {
								stackToCheck.shrink(1);
								break invLoop;
							}
						}
					}

					level.setBlockAndUpdate(placeLocation, stateToPlace);
					level.playSound(null, placeLocation, soundType.getPlaceSound(), SoundSource.BLOCKS, soundType.getVolume(), soundType.getPitch());
					level.gameEvent(null, GameEvent.BLOCK_PLACE, placeLocation);
					placedBe = level.getBlockEntity(placeLocation);

					//assigning the owner
					if (placedBe instanceof OwnableBlockEntity ownable)
						ownable.setOwner(getOwner().getUUID(), getOwner().getName());
				}
				else {
					//when an invalid block is in the way
					PlayerUtils.sendMessageToPlayer(owner, Utils.localize(SCContent.BLOCK_POCKET_MANAGER.get().getDescriptionId()), Component.translatable("messages.securitycraft:blockpocket.assemblyFailed", getFormattedRelativeCoordinates(toPlace.getLeft(), state.getValue(BlockPocketManagerBlock.FACING)), Component.translatable(stateInLevel.getBlock().getDescriptionId())), ChatFormatting.DARK_AQUA);
					placed4Blocks = false;
					break placeLoop;
				}
			}

			if (!placed4Blocks) {
				if (!placeQueue.isEmpty()) //there are still blocks left to place, so a different block is blocking (heh) a space
					placeQueue.clear();
				else { //no more blocks left to place, assembling must be done
					setWalls(!isModuleEnabled(ModuleType.DISGUISE));
					PlayerUtils.sendMessageToPlayer(owner, Utils.localize(SCContent.BLOCK_POCKET_MANAGER.get().getDescriptionId()), Component.translatable("messages.securitycraft:blockpocket.assembled"), ChatFormatting.DARK_AQUA);
				}

				shouldPlaceBlocks = false;
			}
		}
	}

	/**
	 * Enables the block pocket
	 *
	 * @return The feedback message. null if none should be sent.
	 */
	public MutableComponent enableMultiblock() {
		if (level.isClientSide)
			return Component.translatable("enableMultiblock called on client! Send a ToggleBlockPocketManager packet instead.");

		if (!isEnabled()) { //multiblock detection
			List<BlockPos> blocks = new ArrayList<>();
			List<BlockPos> sides = new ArrayList<>();
			List<BlockPos> floor = new ArrayList<>();
			final Direction managerFacing = level.getBlockState(worldPosition).getValue(BlockPocketManagerBlock.FACING);
			final Direction left = managerFacing.getClockWise();
			final Direction right = left.getOpposite();
			final Direction back = left.getClockWise();
			final BlockPos startingPos;
			final int lowest = 0;
			final int highest = getSize() - 1;
			BlockPos pos = getBlockPos().immutable();
			int xi = lowest;
			int yi = lowest;
			int zi = lowest;
			int offset = 0;

			if (!(level.getBlockState(pos.relative(left)).getBlock() instanceof IBlockPocket)) {
				//when the block left of the manager is not a Block Pocket block (so the manager was just placed down), take the autoBuildOffset
				offset = -getAutoBuildOffset() + (getSize() / 2);
				pos = pos.relative(left, offset);
			}
			else {
				for (int i = 1; i < getSize() - 1; i++) { //find the bottom left corner
					if (!(level.getBlockState(pos.relative(left, i)).getBlock() instanceof ReinforcedRotatedCrystalQuartzPillar)) {
						offset = i;
						pos = pos.relative(left, offset);
						break;
					}
				}

				if (offset == 0) {
					//when the bottom left corner couldn't be evaluated, take the autoBuildOffset
					offset = -getAutoBuildOffset() + (getSize() / 2);
					pos = pos.relative(left, offset);
				}
			}

			startingPos = pos.immutable();

			//looping through cube level by level
			while (yi < getSize()) {
				while (zi < getSize()) {
					while (xi < getSize()) {
						//skip the blocks in the middle
						if (xi > lowest && yi > lowest && zi > lowest && xi < highest && yi < highest && zi < highest) {
							xi++;
							continue;
						}

						BlockPos currentPos = pos.relative(right, xi);
						BlockState currentState = level.getBlockState(currentPos);

						if (currentState.getBlock() instanceof BlockPocketManagerBlock && !currentPos.equals(getBlockPos()))
							return Component.translatable("messages.securitycraft:blockpocket.multipleManagers");

						//checking the lowest and highest level of the cube
						if ((yi == lowest && !currentPos.equals(getBlockPos())) || yi == highest) { //if (y level is lowest AND it's not the block pocket manager's position) OR (y level is highest)
							//checking the corners
							if (((xi == lowest && zi == lowest) || (xi == lowest && zi == highest) || (xi == highest && zi == lowest) || (xi == highest && zi == highest))) {
								if (currentState.getBlock() != SCContent.REINFORCED_CHISELED_CRYSTAL_QUARTZ.get())
									return Component.translatable("messages.securitycraft:blockpocket.invalidBlock", getFormattedRelativeCoordinates(currentPos, managerFacing), Component.translatable(currentState.getBlock().asItem().getDescriptionId()), Component.translatable(SCContent.REINFORCED_CHISELED_CRYSTAL_QUARTZ.get().getDescriptionId()));
							}
							//checking the sides parallel to the block pocket manager
							else if ((zi == lowest || zi == highest) && xi > lowest && xi < highest) {
								Axis typeToCheckFor = managerFacing == Direction.NORTH || managerFacing == Direction.SOUTH ? Axis.X : Axis.Z;

								if (currentState.getBlock() != SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get() || currentState.getValue(BlockStateProperties.AXIS) != typeToCheckFor) {
									if (currentState.getBlock() == SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get())
										return Component.translatable("messages.securitycraft:blockpocket.invalidBlock.rotation", getFormattedRelativeCoordinates(currentPos, managerFacing), Component.translatable(currentState.getBlock().asItem().getDescriptionId()));
									return Component.translatable("messages.securitycraft:blockpocket.invalidBlock", getFormattedRelativeCoordinates(currentPos, managerFacing), Component.translatable(currentState.getBlock().asItem().getDescriptionId()), Component.translatable(SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get().getDescriptionId()));
								}
							}
							//checking the sides orthogonal to the block pocket manager
							else if ((xi == lowest || xi == highest) && zi > lowest && zi < highest) {
								Axis typeToCheckFor = managerFacing == Direction.NORTH || managerFacing == Direction.SOUTH ? Axis.Z : Axis.X;

								if (currentState.getBlock() != SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get() || currentState.getValue(BlockStateProperties.AXIS) != typeToCheckFor) {
									if (currentState.getBlock() == SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get())
										return Component.translatable("messages.securitycraft:blockpocket.invalidBlock.rotation", getFormattedRelativeCoordinates(currentPos, managerFacing), Component.translatable(currentState.getBlock().asItem().getDescriptionId()));
									return Component.translatable("messages.securitycraft:blockpocket.invalidBlock", getFormattedRelativeCoordinates(currentPos, managerFacing), Component.translatable(currentState.getBlock().asItem().getDescriptionId()), Component.translatable(SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get().getDescriptionId()));
								}
							}
							//checking the middle plane
							else if (xi > lowest && zi > lowest && xi < highest && zi < highest) {
								if (!(currentState.getBlock() instanceof BlockPocketWallBlock))
									return Component.translatable("messages.securitycraft:blockpocket.invalidBlock", getFormattedRelativeCoordinates(currentPos, managerFacing), Component.translatable(currentState.getBlock().asItem().getDescriptionId()), Component.translatable(SCContent.BLOCK_POCKET_WALL.get().getDescriptionId()));

								floor.add(currentPos);
								sides.add(currentPos);
							}
						}
						//checking the corner edges
						else if (yi != lowest && yi != highest && ((xi == lowest && zi == lowest) || (xi == lowest && zi == highest) || (xi == highest && zi == lowest) || (xi == highest && zi == highest))) {
							if (currentState.getBlock() != SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get() || currentState.getValue(BlockStateProperties.AXIS) != Axis.Y) {
								if (currentState.getBlock() == SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get())
									return Component.translatable("messages.securitycraft:blockpocket.invalidBlock.rotation", getFormattedRelativeCoordinates(currentPos, managerFacing), Component.translatable(currentState.getBlock().asItem().getDescriptionId()));
								return Component.translatable("messages.securitycraft:blockpocket.invalidBlock", getFormattedRelativeCoordinates(currentPos, managerFacing), Component.translatable(currentState.getBlock().asItem().getDescriptionId()), Component.translatable(SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get().getDescriptionId()));
							}
						}
						//checking the walls parallel to the block pocket manager and orthogonal to the block pocket manager
						else if (yi > lowest && yi < highest && (((zi == lowest || zi == highest) && xi > lowest && xi < highest) || ((xi == lowest || xi == highest) && zi > lowest && zi < highest))) {
							if (!(currentState.getBlock() instanceof BlockPocketWallBlock))
								return Component.translatable("messages.securitycraft:blockpocket.invalidBlock", getFormattedRelativeCoordinates(currentPos, managerFacing), Component.translatable(currentState.getBlock().asItem().getDescriptionId()), Component.translatable(SCContent.BLOCK_POCKET_WALL.get().getDescriptionId()));

							sides.add(currentPos);
						}

						if (!getOwner().owns((OwnableBlockEntity) level.getBlockEntity(currentPos)))
							return Component.translatable("messages.securitycraft:blockpocket.unowned", getFormattedRelativeCoordinates(currentPos, managerFacing), Component.translatable(currentState.getBlock().asItem().getDescriptionId()));
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
			setEnabled(true);
			setAutoBuildOffset(-offset + (getSize() / 2));
			setChanged();

			for (BlockPos blockPos : blocks) {
				if (level.getBlockEntity(blockPos) instanceof BlockPocketBlockEntity be)
					be.setManager(this);
			}

			for (BlockPos blockPos : floor) {
				level.setBlockAndUpdate(blockPos, level.getBlockState(blockPos).setValue(BlockPocketWallBlock.SOLID, true));
			}

			setWalls(!isModuleEnabled(ModuleType.DISGUISE));
			return Component.translatable("messages.securitycraft:blockpocket.activated");
		}

		return null;
	}

	/**
	 * Auto-assembles the Block Pocket for a player. First it makes sure that the space isn't occupied, then it checks its
	 * inventory for the required items, then it places the blocks.
	 *
	 * @return The feedback message. null if none should be sent.
	 */
	public MutableComponent autoAssembleMultiblock() {
		if (level.isClientSide)
			return Component.translatable("autoAssembleMultiblock called on client! Send an AssembleBlockPocket packet instead.");

		if (!isEnabled()) {
			final Direction managerFacing = getBlockState().getValue(BlockPocketManagerBlock.FACING);
			final Direction left = managerFacing.getClockWise();
			final Direction right = left.getOpposite();
			final Direction back = left.getClockWise();
			final BlockPos startingPos;
			final int lowest = 0;
			final int half = (getSize() - 1) / 2 - getAutoBuildOffset();
			final int highest = getSize() - 1;
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
			while (yi < getSize()) {
				while (zi < getSize()) {
					while (xi < getSize()) {
						//skip the blocks in the middle
						if (xi > lowest && yi > lowest && zi > lowest && xi < highest && yi < highest && zi < highest) {
							xi++;
							continue;
						}

						BlockPos currentPos = pos.relative(right, xi);
						BlockState currentState = level.getBlockState(currentPos);
						boolean replaceable = currentState.canBeReplaced();

						//checking the lowest and highest level of the cube
						//if (y level is lowest AND it's not the block pocket manager's position) OR (y level is highest)
						if ((yi == lowest && !currentPos.equals(getBlockPos())) || yi == highest) {
							//checking the corners
							if (((xi == lowest && zi == lowest) || (xi == lowest && zi == highest) || (xi == highest && zi == lowest) || (xi == highest && zi == highest))) {
								if (currentState.getBlock() != SCContent.REINFORCED_CHISELED_CRYSTAL_QUARTZ.get() && !replaceable)
									return Component.translatable("messages.securitycraft:blockpocket.blockInWay", getFormattedRelativeCoordinates(currentPos, managerFacing), Component.translatable(currentState.getBlock().asItem().getDescriptionId()));

								if (replaceable)
									chiseledNeeded++;
							}
							//checking the sides parallel to the block pocket manager
							else if ((zi == lowest || zi == highest) && xi > lowest && xi < highest) {
								Axis typeToCheckFor = managerFacing == Direction.NORTH || managerFacing == Direction.SOUTH ? Axis.X : Axis.Z;

								if (currentState.getBlock() != SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get() && !replaceable || (currentState.getBlock() == SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get() && currentState.getValue(BlockStateProperties.AXIS) != typeToCheckFor))
									return Component.translatable("messages.securitycraft:blockpocket.blockInWay", getFormattedRelativeCoordinates(currentPos, managerFacing), Component.translatable(currentState.getBlock().asItem().getDescriptionId()));

								if (replaceable)
									pillarsNeeded++;
							}
							//checking the sides orthogonal to the block pocket manager
							else if ((xi == lowest || xi == highest) && zi > lowest && zi < highest) {
								Axis typeToCheckFor = managerFacing == Direction.NORTH || managerFacing == Direction.SOUTH ? Axis.Z : Axis.X;

								if (currentState.getBlock() != SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get() && !replaceable || (currentState.getBlock() == SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get() && currentState.getValue(BlockStateProperties.AXIS) != typeToCheckFor))
									return Component.translatable("messages.securitycraft:blockpocket.blockInWay", getFormattedRelativeCoordinates(currentPos, managerFacing), Component.translatable(currentState.getBlock().asItem().getDescriptionId()));

								if (replaceable)
									pillarsNeeded++;
							}
							//checking the middle plane
							else if (xi > lowest && zi > lowest && xi < highest && zi < highest) {
								if (!(currentState.getBlock() instanceof BlockPocketWallBlock) && !replaceable)
									return Component.translatable("messages.securitycraft:blockpocket.blockInWay", getFormattedRelativeCoordinates(currentPos, managerFacing), Component.translatable(currentState.getBlock().asItem().getDescriptionId()));

								if (replaceable)
									wallsNeeded++;
							}
						}
						//checking the corner edges
						else if (yi != lowest && yi != highest && ((xi == lowest && zi == lowest) || (xi == lowest && zi == highest) || (xi == highest && zi == lowest) || (xi == highest && zi == highest))) {
							if (currentState.getBlock() != SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get() && !replaceable || (currentState.getBlock() == SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get() && currentState.getValue(BlockStateProperties.AXIS) != Axis.Y))
								return Component.translatable("messages.securitycraft:blockpocket.blockInWay", getFormattedRelativeCoordinates(currentPos, managerFacing), Component.translatable(currentState.getBlock().asItem().getDescriptionId()));

							if (replaceable)
								pillarsNeeded++;
						}
						//checking the walls parallel to the block pocket manager and orthogonal to the block pocket manager
						else if (yi > lowest && yi < highest && (((zi == lowest || zi == highest) && xi > lowest && xi < highest) || ((xi == lowest || xi == highest) && zi > lowest && zi < highest))) {
							if (!(currentState.getBlock() instanceof BlockPocketWallBlock) && !replaceable)
								return Component.translatable("messages.securitycraft:blockpocket.blockInWay", getFormattedRelativeCoordinates(currentPos, managerFacing), Component.translatable(currentState.getBlock().asItem().getDescriptionId()));

							if (replaceable)
								wallsNeeded++;
						}

						if (level.getBlockEntity(currentPos) instanceof OwnableBlockEntity be && !getOwner().owns(be))
							return Component.translatable("messages.securitycraft:blockpocket.unowned", getFormattedRelativeCoordinates(currentPos, managerFacing), Component.translatable(currentState.getBlock().asItem().getDescriptionId()));

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

			if (chiseledNeeded + pillarsNeeded + wallsNeeded == 0) //this applies when no blocks are missing, so when the BP is already in place
				return Component.translatable("messages.securitycraft:blockpocket.alreadyAssembled");

			pos = getBlockPos().immutable().relative(right, -half);
			xi = lowest;
			yi = lowest;
			zi = lowest;

			//add blocks to the auto building queue
			while (yi < getSize()) {
				while (zi < getSize()) {
					while (xi < getSize()) {
						//skip the blocks in the middle again
						if (xi > lowest && yi > lowest && zi > lowest && xi < highest && yi < highest && zi < highest) {
							xi++;
							continue;
						}

						BlockPos currentPos = pos.relative(right, xi);
						BlockState currentState = level.getBlockState(currentPos);

						if (currentState.getBlock() instanceof BlockPocketManagerBlock && !currentPos.equals(getBlockPos()))
							return Component.translatable("messages.securitycraft:blockpocket.multipleManagers");

						//placing the lowest and highest level of the cube
						//if (y level is lowest AND it's not the block pocket manager's position) OR (y level is highest)
						if ((yi == lowest && !currentPos.equals(getBlockPos())) || yi == highest) {
							//placing the corners
							if (((xi == lowest && zi == lowest) || (xi == lowest && zi == highest) || (xi == highest && zi == lowest) || (xi == highest && zi == highest)))
								placeQueue.add(Pair.of(currentPos, SCContent.REINFORCED_CHISELED_CRYSTAL_QUARTZ.get().defaultBlockState()));
							else if ((zi == lowest || zi == highest) && xi > lowest && xi < highest) { //placing the sides parallel to the block pocket manager
								Axis typeToPlace = managerFacing == Direction.NORTH || managerFacing == Direction.SOUTH ? Axis.X : Axis.Z;

								placeQueue.add(Pair.of(currentPos, SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get().defaultBlockState().setValue(ReinforcedRotatedPillarBlock.AXIS, typeToPlace)));
							}
							//placing the sides orthogonal to the block pocket manager
							else if ((xi == lowest || xi == highest) && zi > lowest && zi < highest) {
								Axis typeToPlace = managerFacing == Direction.NORTH || managerFacing == Direction.SOUTH ? Axis.Z : Axis.X;

								placeQueue.add(Pair.of(currentPos, SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get().defaultBlockState().setValue(ReinforcedRotatedPillarBlock.AXIS, typeToPlace)));
							}
							//placing the middle plane
							else if (xi > lowest && zi > lowest && xi < highest && zi < highest)
								placeQueue.add(Pair.of(currentPos, SCContent.BLOCK_POCKET_WALL.get().defaultBlockState()));
						}
						//placing the corner edges
						else if (yi != lowest && yi != highest && ((xi == lowest && zi == lowest) || (xi == lowest && zi == highest) || (xi == highest && zi == lowest) || (xi == highest && zi == highest)))
							placeQueue.add(Pair.of(currentPos, SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get().defaultBlockState().setValue(ReinforcedRotatedPillarBlock.AXIS, Axis.Y)));
						//placing the walls parallel and orthogonal to the block pocket manager
						else if (yi > lowest && yi < highest && (((zi == lowest || zi == highest) && xi > lowest && xi < highest) || ((xi == lowest || xi == highest) && zi > lowest && zi < highest)))
							placeQueue.add(Pair.of(currentPos, SCContent.BLOCK_POCKET_WALL.get().defaultBlockState()));

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

	public MutableComponent disableMultiblock() {
		if (level.isClientSide)
			return Component.translatable("disableMultiblock called on client! Send a ToggleBlockPocketManager packet instead.");

		if (isEnabled()) {
			setEnabled(false);

			for (BlockPos pos : blocks) {
				if (level.getBlockEntity(pos) instanceof BlockPocketBlockEntity be)
					be.removeManager();
			}

			for (BlockPos pos : floor) {
				BlockState state = level.getBlockState(pos);

				if (state.hasProperty(BlockPocketWallBlock.SOLID))
					level.setBlockAndUpdate(pos, state.setValue(BlockPocketWallBlock.SOLID, false));
			}

			if (isModuleEnabled(ModuleType.DISGUISE))
				setWalls(true);

			blocks.clear();
			walls.clear();
			floor.clear();
			setChanged();
			return Utils.localize("messages.securitycraft:blockpocket.deactivated");
		}

		return null;
	}

	private Component getFormattedRelativeCoordinates(BlockPos pos, Direction managerFacing) {
		BlockPos difference = pos.subtract(worldPosition);
		int offsetBehind;
		int offsetAbove = difference.getY();
		List<Component> components = new ArrayList<>();
		int offsetLeft = switch (managerFacing) {
			case NORTH -> {
				offsetBehind = difference.getZ();
				yield difference.getX();
			}
			case SOUTH -> {
				offsetBehind = -difference.getZ();
				yield -difference.getX();
			}
			case WEST -> {
				offsetBehind = difference.getX();
				yield -difference.getZ();
			}
			case EAST -> {
				offsetBehind = -difference.getX();
				yield difference.getZ();
			}
			default -> throw new IllegalArgumentException("Invalid Block Pocket Manager direction: " + managerFacing.name());
		};

		if (offsetLeft > 0)
			components.add(Utils.localize("messages.securitycraft:blockpocket.position.blocksLeft", offsetLeft));
		else if (offsetLeft < 0)
			components.add(Utils.localize("messages.securitycraft:blockpocket.position.blocksRight", -offsetLeft));

		if (offsetBehind > 0)
			components.add(Utils.localize("messages.securitycraft:blockpocket.position.blocksBehind", offsetBehind));

		if (offsetAbove > 0)
			components.add(Utils.localize("messages.securitycraft:blockpocket.position.blocksAbove", offsetAbove));

		return Utils.localize("messages.securitycraft:blockpocket.position." + components.size(), components.toArray());
	}

	public void toggleOutline() {
		setShowOutline(!showsOutline());
		setChanged();
	}

	public void setWalls(boolean seeThrough) {
		for (BlockPos pos : walls) {
			BlockState state = level.getBlockState(pos);

			if (state.getBlock() instanceof BlockPocketWallBlock)
				level.setBlockAndUpdate(pos, state.setValue(BlockPocketWallBlock.SEE_THROUGH, seeThrough));
		}
	}

	public static IItemHandler getCapability(BlockPocketManagerBlockEntity be, Direction side) {
		//prevent extracting while auto building the block pocket
		if (!be.isPlacingBlocks() && BlockUtils.isAllowedToExtractFromProtectedObject(side, be))
			return new ValidityCheckItemStackHandler(be.storage);
		else
			return new ValidityCheckInsertOnlyItemStackHandler(be.storage);
	}

	public NonNullList<ItemStack> getStorage() {
		return storage;
	}

	@Override
	public void setRemoved() {
		super.setRemoved();

		if (level.isLoaded(worldPosition) && level.getBlockState(worldPosition).getBlock() != SCContent.BLOCK_POCKET_MANAGER.get())
			disableMultiblock();
	}

	@Override
	public void onModuleInserted(ItemStack stack, ModuleType module, boolean toggled) {
		super.onModuleInserted(stack, module, toggled);

		if (isEnabled() && module == ModuleType.DISGUISE)
			setWalls(false);
	}

	@Override
	public void onModuleRemoved(ItemStack stack, ModuleType module, boolean toggled) {
		super.onModuleRemoved(stack, module, toggled);

		if (isEnabled() && module == ModuleType.DISGUISE)
			setWalls(true);
		else if (module == ModuleType.STORAGE)
			Containers.dropContents(level, worldPosition, storage);
	}

	@Override
	public void saveAdditional(CompoundTag tag) {
		tag.putBoolean("BlockPocketEnabled", isEnabled());
		tag.putBoolean("ShowOutline", showsOutline());
		tag.putInt("Size", getSize());
		tag.putInt("AutoBuildOffset", getAutoBuildOffset());
		tag.putInt("Color", color);
		ContainerHelper.saveAllItems(tag, storage);

		for (int i = 0; i < blocks.size(); i++) {
			tag.putLong("BlocksList" + i, blocks.get(i).asLong());
		}

		for (int i = 0; i < walls.size(); i++) {
			tag.putLong("WallsList" + i, walls.get(i).asLong());
		}

		for (int i = 0; i < floor.size(); i++) {
			tag.putLong("FloorList" + i, floor.get(i).asLong());
		}

		super.saveAdditional(tag);
	}

	@Override
	public void load(CompoundTag tag) {
		int i = 0;

		super.load(tag);
		setEnabled(tag.getBoolean("BlockPocketEnabled"));
		setShowOutline(tag.getBoolean("ShowOutline"));
		setSize(tag.getInt("Size"));
		setAutoBuildOffset(tag.getInt("AutoBuildOffset"));
		setColor(tag.getInt("Color"));
		ContainerHelper.loadAllItems(tag, storage);

		while (tag.contains("BlocksList" + i)) {
			blocks.add(BlockPos.of(tag.getLong("BlocksList" + i)));
			i++;
		}

		i = 0;

		while (tag.contains("WallsList" + i)) {
			walls.add(BlockPos.of(tag.getLong("WallsList" + i)));
			i++;
		}

		i = 0;

		while (tag.contains("FloorList" + i)) {
			floor.add(BlockPos.of(tag.getLong("FloorList" + i)));
			i++;
		}
	}

	@Override
	public void writeClientSideData(AbstractContainerMenu menu, FriendlyByteBuf buffer) {
		MenuProvider.super.writeClientSideData(menu, buffer);
		buffer.writeBlockPos(worldPosition);
	}

	@Override
	public ModuleType[] acceptedModules() {
		return new ModuleType[] {
				ModuleType.DISGUISE, ModuleType.ALLOWLIST, ModuleType.STORAGE
		};
	}

	@Override
	public String getModuleDescriptionId(String denotation, ModuleType module) {
		return IModuleInventory.getBaseModuleDescriptionId(denotation, module); //the disguise module description is generic and doesn't apply to the block pocket manager
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[0];
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
		return new BlockPocketManagerMenu(windowId, level, worldPosition, inv);
	}

	@Override
	public Component getDisplayName() {
		return super.getDisplayName();
	}

	public boolean isPlacingBlocks() {
		return shouldPlaceBlocks;
	}

	public static boolean isItemValid(ItemStack stack) {
		if (stack.getItem() instanceof BlockItem blockItem) {
			Block block = blockItem.getBlock();

			return block == SCContent.BLOCK_POCKET_WALL.get() || block == SCContent.REINFORCED_CHISELED_CRYSTAL_QUARTZ.get() || block == SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get();
		}

		return false;
	}

	public void setColor(int color) {
		this.color = Mth.clamp(color, 0xFF000000, 0xFFFFFFFF);
	}

	public int getColor() {
		return color;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getSize() {
		return size;
	}

	public void setAutoBuildOffset(int autoBuildOffset) {
		this.autoBuildOffset = autoBuildOffset;
	}

	public int getAutoBuildOffset() {
		return autoBuildOffset;
	}

	public void setShowOutline(boolean showOutline) {
		this.showOutline = showOutline;
	}

	public boolean showsOutline() {
		return showOutline;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public static class ValidityCheckInsertOnlyItemStackHandler extends InsertOnlyItemStackHandler {
		public ValidityCheckInsertOnlyItemStackHandler(NonNullList<ItemStack> stacks) {
			super(stacks);
		}

		@Override
		public boolean isItemValid(int slot, ItemStack stack) {
			return BlockPocketManagerBlockEntity.isItemValid(stack);
		}
	}

	public static class ValidityCheckItemStackHandler extends ItemStackHandler {
		public ValidityCheckItemStackHandler(NonNullList<ItemStack> stacks) {
			super(stacks);
		}

		@Override
		public boolean isItemValid(int slot, ItemStack stack) {
			return BlockPocketManagerBlockEntity.isItemValid(stack);
		}
	}
}
