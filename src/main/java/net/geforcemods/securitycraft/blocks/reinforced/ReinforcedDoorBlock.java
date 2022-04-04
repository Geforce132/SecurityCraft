package net.geforcemods.securitycraft.blocks.reinforced;

import javax.annotation.Nullable;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.OwnableBlockEntity;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blockentities.ReinforcedDoorBlockEntity;
import net.geforcemods.securitycraft.blocks.OwnableBlock;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ReinforcedDoorBlock extends OwnableBlock {
	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
	public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
	public static final EnumProperty<DoorHingeSide> HINGE = BlockStateProperties.DOOR_HINGE;
	public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
	protected static final VoxelShape SOUTH_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 3.0D);
	protected static final VoxelShape NORTH_AABB = Block.box(0.0D, 0.0D, 13.0D, 16.0D, 16.0D, 16.0D);
	protected static final VoxelShape WEST_AABB = Block.box(13.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
	protected static final VoxelShape EAST_AABB = Block.box(0.0D, 0.0D, 0.0D, 3.0D, 16.0D, 16.0D);

	public ReinforcedDoorBlock(Block.Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(OPEN, false).setValue(HINGE, DoorHingeSide.LEFT).setValue(HALF, DoubleBlockHalf.LOWER));
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		Direction facing = state.getValue(FACING);
		boolean isNotOpen = !state.getValue(OPEN);
		boolean isHingeRight = state.getValue(HINGE) == DoorHingeSide.RIGHT;

		switch (facing) {
			case EAST:
			default:
				return isNotOpen ? EAST_AABB : (isHingeRight ? NORTH_AABB : SOUTH_AABB);
			case SOUTH:
				return isNotOpen ? SOUTH_AABB : (isHingeRight ? EAST_AABB : WEST_AABB);
			case WEST:
				return isNotOpen ? WEST_AABB : (isHingeRight ? SOUTH_AABB : NORTH_AABB);
			case NORTH:
				return isNotOpen ? NORTH_AABB : (isHingeRight ? WEST_AABB : EAST_AABB);
		}
	}

	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
		DoubleBlockHalf doorHalf = state.getValue(HALF);

		if (facing.getAxis() == Direction.Axis.Y && doorHalf == DoubleBlockHalf.LOWER == (facing == Direction.UP))
			return facingState.getBlock() == this && facingState.getValue(HALF) != doorHalf ? state.setValue(FACING, facingState.getValue(FACING)).setValue(OPEN, facingState.getValue(OPEN)).setValue(HINGE, facingState.getValue(HINGE)) : Blocks.AIR.defaultBlockState();
		else
			return doorHalf == DoubleBlockHalf.LOWER && facing == Direction.DOWN && !state.canSurvive(level, currentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, facing, facingState, level, currentPos, facingPos);
	}

	@Override
	public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, BlockEntity be, ItemStack stack) {
		super.playerDestroy(level, player, pos, Blocks.AIR.defaultBlockState(), be, stack);
	}

	@Override
	public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
		if (!level.isClientSide && player.isCreative()) {
			DoubleBlockHalf doorHalf = state.getValue(HALF);

			if (doorHalf == DoubleBlockHalf.UPPER) {
				BlockPos posBelow = pos.below();
				BlockState stateBelow = level.getBlockState(posBelow);

				if (stateBelow.getBlock() == state.getBlock() && stateBelow.getValue(HALF) == DoubleBlockHalf.LOWER) {
					level.setBlock(posBelow, Blocks.AIR.defaultBlockState(), 35);
					level.levelEvent(player, LevelEvent.PARTICLES_DESTROY_BLOCK, posBelow, Block.getId(stateBelow));
				}
			}
		}

		super.playerWillDestroy(level, pos, state, player);
	}

	@Override
	public boolean isPathfindable(BlockState state, BlockGetter level, BlockPos pos, PathComputationType type) {
		return switch (type) {
			case LAND -> state.getValue(OPEN);
			case AIR -> state.getValue(OPEN);
			default -> false;
		};
	}

	@Override
	@Nullable
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		BlockPos pos = context.getClickedPos();
		Level level = context.getLevel();

		if (pos.getY() < level.getHeight() - 1 && level.getBlockState(pos.above()).canBeReplaced(context)) {
			boolean hasNeighborSignal = level.hasNeighborSignal(pos) || level.hasNeighborSignal(pos.above());
			return defaultBlockState().setValue(FACING, context.getHorizontalDirection()).setValue(HINGE, getHingeSide(context)).setValue(OPEN, hasNeighborSignal).setValue(HALF, DoubleBlockHalf.LOWER);
		}
		else
			return null;
	}

	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean flag) {
		onNeighborChanged(level, pos, fromPos);
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		super.setPlacedBy(level, pos, state, placer, stack);
		level.setBlock(pos.above(), state.setValue(HALF, DoubleBlockHalf.UPPER), 3);
	}

	private DoorHingeSide getHingeSide(BlockPlaceContext ctx) {
		BlockGetter level = ctx.getLevel();
		BlockPos clickedPos = ctx.getClickedPos();
		Direction horizontalDirection = ctx.getHorizontalDirection();
		BlockPos posAbove = clickedPos.above();
		Direction horizontalCCW = horizontalDirection.getCounterClockWise();
		BlockPos clickedPosCCW = clickedPos.relative(horizontalCCW);
		BlockState stateCCW = level.getBlockState(clickedPosCCW);
		BlockPos posAboveCCW = posAbove.relative(horizontalCCW);
		BlockState stateAboveCCW = level.getBlockState(posAboveCCW);
		Direction horizontalCW = horizontalDirection.getClockWise();
		BlockPos clickedPosCW = clickedPos.relative(horizontalCW);
		BlockState stateCW = level.getBlockState(clickedPosCW);
		BlockPos posAboveCW = posAbove.relative(horizontalCW);
		BlockState stateAboveCW = level.getBlockState(posAboveCW);
		int i = (stateCCW.isCollisionShapeFullBlock(level, clickedPosCCW) ? -1 : 0) + (stateAboveCCW.isCollisionShapeFullBlock(level, posAboveCCW) ? -1 : 0) + (stateCW.isCollisionShapeFullBlock(level, clickedPosCW) ? 1 : 0) + (stateAboveCW.isCollisionShapeFullBlock(level, posAboveCW) ? 1 : 0);
		boolean isCCWLower = stateCCW.getBlock() == this && stateCCW.getValue(HALF) == DoubleBlockHalf.LOWER;
		boolean isCWLower = stateCW.getBlock() == this && stateCW.getValue(HALF) == DoubleBlockHalf.LOWER;

		if ((!isCCWLower || isCWLower) && i <= 0) {
			if ((!isCWLower || isCCWLower) && i >= 0) {
				int j = horizontalDirection.getStepX();
				int k = horizontalDirection.getStepZ();
				Vec3 vec3d = ctx.getClickLocation();
				double d0 = vec3d.x - clickedPos.getX();
				double d1 = vec3d.z - clickedPos.getZ();
				return (j >= 0 || !(d1 < 0.5D)) && (j <= 0 || !(d1 > 0.5D)) && (k >= 0 || !(d0 > 0.5D)) && (k <= 0 || !(d0 < 0.5D)) ? DoorHingeSide.LEFT : DoorHingeSide.RIGHT;
			}
			else
				return DoorHingeSide.LEFT;
		}
		else
			return DoorHingeSide.RIGHT;
	}

	/**
	 * Old method, renamed because I am lazy. Called by neighborChanged
	 *
	 * @param world The world the change occured in
	 * @param pos The position of this block
	 * @param neighbor The position of the changed block
	 */
	public void onNeighborChanged(Level world, BlockPos pos, BlockPos neighbor) {
		BlockState state = world.getBlockState(pos);
		Block neighborBlock = world.getBlockState(neighbor).getBlock();
		Owner previousOwner = null;

		if (world.getBlockEntity(pos) instanceof OwnableBlockEntity)
			previousOwner = ((OwnableBlockEntity) world.getBlockEntity(pos)).getOwner();

		if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
			BlockPos blockBelow = pos.below();
			BlockState stateBelow = world.getBlockState(blockBelow);

			if (stateBelow.getBlock() != this)
				world.destroyBlock(pos, false);
			else if (neighborBlock != this)
				onNeighborChanged(world, blockBelow, neighbor);
		}
		else {
			boolean drop = false;
			BlockPos blockAbove = pos.above();
			BlockState stateAbove = world.getBlockState(blockAbove);

			if (stateAbove.getBlock() != this) {
				world.destroyBlock(pos, false);
				drop = true;
			}

			if (!BlockUtils.isSideSolid(world, pos.below(), Direction.UP)) {
				world.destroyBlock(pos, false);
				drop = true;

				if (stateAbove.getBlock() == this)
					world.destroyBlock(pos, false);
			}

			if (drop) {
				if (!world.isClientSide) {
					world.destroyBlock(pos, false);
					Block.popResource(world, pos, new ItemStack(SCContent.REINFORCED_DOOR_ITEM.get()));
				}
			}
			else {
				boolean hasActiveSCBlock = BlockUtils.hasActiveSCBlockNextTo(world, pos) || BlockUtils.hasActiveSCBlockNextTo(world, pos.above());

				if (neighborBlock != this && hasActiveSCBlock != stateAbove.getValue(OPEN)) {
					if (hasActiveSCBlock != state.getValue(OPEN)) {
						world.setBlock(pos, state.setValue(OPEN, hasActiveSCBlock), 2);

						BlockState secondDoorState;

						if (state.getValue(FACING) == Direction.WEST) {
							secondDoorState = world.getBlockState(pos.north());

							if (secondDoorState != null && secondDoorState.getBlock() == SCContent.REINFORCED_DOOR.get() && secondDoorState.getValue(OPEN) != hasActiveSCBlock)
								world.setBlock(pos.north(), secondDoorState.setValue(OPEN, hasActiveSCBlock), 2);
							else {
								secondDoorState = world.getBlockState(pos.south());

								if (secondDoorState != null && secondDoorState.getBlock() == SCContent.REINFORCED_DOOR.get() && secondDoorState.getValue(OPEN) != hasActiveSCBlock)
									world.setBlock(pos.south(), secondDoorState.setValue(OPEN, hasActiveSCBlock), 2);
							}
						}
						else if (state.getValue(FACING) == Direction.NORTH) {
							secondDoorState = world.getBlockState(pos.east());

							if (secondDoorState != null && secondDoorState.getBlock() == SCContent.REINFORCED_DOOR.get() && secondDoorState.getValue(OPEN) != hasActiveSCBlock)
								world.setBlock(pos.east(), secondDoorState.setValue(OPEN, hasActiveSCBlock), 2);
							else {
								secondDoorState = world.getBlockState(pos.west());

								if (secondDoorState != null && secondDoorState.getBlock() == SCContent.REINFORCED_DOOR.get() && secondDoorState.getValue(OPEN) != hasActiveSCBlock)
									world.setBlock(pos.west(), secondDoorState.setValue(OPEN, hasActiveSCBlock), 2);
							}
						}
						else if (state.getValue(FACING) == Direction.EAST) {
							secondDoorState = world.getBlockState(pos.south());

							if (secondDoorState != null && secondDoorState.getBlock() == SCContent.REINFORCED_DOOR.get() && secondDoorState.getValue(OPEN) != hasActiveSCBlock)
								world.setBlock(pos.south(), secondDoorState.setValue(OPEN, hasActiveSCBlock), 2);
							else {
								secondDoorState = world.getBlockState(pos.north());

								if (secondDoorState != null && secondDoorState.getBlock() == SCContent.REINFORCED_DOOR.get() && secondDoorState.getValue(OPEN) != hasActiveSCBlock)
									world.setBlock(pos.north(), secondDoorState.setValue(OPEN, hasActiveSCBlock), 2);
							}
						}
						else if (state.getValue(FACING) == Direction.SOUTH) {
							secondDoorState = world.getBlockState(pos.west());

							if (secondDoorState != null && secondDoorState.getBlock() == SCContent.REINFORCED_DOOR.get() && secondDoorState.getValue(OPEN) != hasActiveSCBlock)
								world.setBlock(pos.west(), secondDoorState.setValue(OPEN, hasActiveSCBlock), 2);
							else {
								secondDoorState = world.getBlockState(pos.east());

								if (secondDoorState != null && secondDoorState.getBlock() == SCContent.REINFORCED_DOOR.get() && secondDoorState.getValue(OPEN) != hasActiveSCBlock)
									world.setBlock(pos.east(), secondDoorState.setValue(OPEN, hasActiveSCBlock), 2);
							}
						}

						world.levelEvent(null, hasActiveSCBlock ? LevelEvent.SOUND_OPEN_IRON_DOOR : LevelEvent.SOUND_CLOSE_IRON_DOOR, pos, 0);
					}
				}
			}
		}

		if (previousOwner != null && world.getBlockEntity(pos) instanceof OwnableBlockEntity thisBe && world.getBlockEntity(pos.above()) instanceof OwnableBlockEntity aboveBe) {
			thisBe.setOwner(previousOwner.getUUID(), previousOwner.getName());
			aboveBe.setOwner(previousOwner.getUUID(), previousOwner.getName());
		}
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		BlockPos posBelow = pos.below();
		BlockState stateBelow = level.getBlockState(posBelow);

		if (state.getValue(HALF) == DoubleBlockHalf.LOWER)
			return stateBelow.isFaceSturdy(level, posBelow, Direction.UP);
		else
			return stateBelow.is(this);
	}

	@Override
	public PushReaction getPistonPushReaction(BlockState state) {
		return PushReaction.BLOCK;
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		return mirror == Mirror.NONE ? state : state.rotate(mirror.getRotation(state.getValue(FACING))).cycle(HINGE);
	}

	@Override
	public long getSeed(BlockState state, BlockPos pos) {
		return Mth.getSeed(pos.getX(), pos.below(state.getValue(HALF) == DoubleBlockHalf.LOWER ? 0 : 1).getY(), pos.getZ());
	}

	@Override
	public boolean triggerEvent(BlockState state, Level level, BlockPos pos, int id, int param) {
		super.triggerEvent(state, level, pos, id, param);
		BlockEntity blockEntity = level.getBlockEntity(pos);
		return blockEntity == null ? false : blockEntity.triggerEvent(id, param);
	}

	@Override
	public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
		return new ItemStack(SCContent.REINFORCED_DOOR_ITEM.get());
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new ReinforcedDoorBlockEntity(pos, state);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(HALF, FACING, OPEN, HINGE);
	}
}