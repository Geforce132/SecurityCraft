package net.geforcemods.securitycraft.blocks.reinforced;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.OwnableBlockEntity;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blockentities.ReinforcedDoorBlockEntity;
import net.geforcemods.securitycraft.blocks.OwnableBlock;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ReinforcedDoorBlock extends OwnableBlock {
	public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
	public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
	public static final EnumProperty<DoorHingeSide> HINGE = BlockStateProperties.DOOR_HINGE;
	public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
	protected static final VoxelShape SOUTH_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 3.0D);
	protected static final VoxelShape NORTH_AABB = Block.box(0.0D, 0.0D, 13.0D, 16.0D, 16.0D, 16.0D);
	protected static final VoxelShape WEST_AABB = Block.box(13.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
	protected static final VoxelShape EAST_AABB = Block.box(0.0D, 0.0D, 0.0D, 3.0D, 16.0D, 16.0D);

	public ReinforcedDoorBlock(BlockBehaviour.Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(OPEN, false).setValue(HINGE, DoorHingeSide.LEFT).setValue(HALF, DoubleBlockHalf.LOWER));
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		Direction facing = state.getValue(FACING);
		boolean isNotOpen = !state.getValue(OPEN);
		boolean isHingeRight = state.getValue(HINGE) == DoorHingeSide.RIGHT;

		return switch (facing) {
			case EAST -> isNotOpen ? EAST_AABB : (isHingeRight ? NORTH_AABB : SOUTH_AABB);
			case SOUTH -> isNotOpen ? SOUTH_AABB : (isHingeRight ? EAST_AABB : WEST_AABB);
			case WEST -> isNotOpen ? WEST_AABB : (isHingeRight ? SOUTH_AABB : NORTH_AABB);
			case NORTH -> isNotOpen ? NORTH_AABB : (isHingeRight ? WEST_AABB : EAST_AABB);
			default -> isNotOpen ? EAST_AABB : (isHingeRight ? NORTH_AABB : SOUTH_AABB);
		};
	}

	@Override
	public BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess tickAccess, BlockPos pos, Direction facing, BlockPos facingPos, BlockState facingState, RandomSource random) {
		DoubleBlockHalf doorHalf = state.getValue(HALF);

		if (facing.getAxis() == Direction.Axis.Y && doorHalf == DoubleBlockHalf.LOWER == (facing == Direction.UP))
			return facingState.getBlock() == this && facingState.getValue(HALF) != doorHalf ? state.setValue(FACING, facingState.getValue(FACING)).setValue(OPEN, facingState.getValue(OPEN)).setValue(HINGE, facingState.getValue(HINGE)) : Blocks.AIR.defaultBlockState();
		else
			return doorHalf == DoubleBlockHalf.LOWER && facing == Direction.DOWN && !state.canSurvive(level, pos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, level, tickAccess, pos, facing, facingPos, facingState, random);
	}

	@Override
	public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, BlockEntity be, ItemStack stack) {
		super.playerDestroy(level, player, pos, Blocks.AIR.defaultBlockState(), be, stack);
	}

	@Override
	public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
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

		return super.playerWillDestroy(level, pos, state, player);
	}

	@Override
	public boolean isPathfindable(BlockState state, PathComputationType type) {
		return switch (type) {
			case LAND -> state.getValue(OPEN);
			case AIR -> state.getValue(OPEN);
			default -> false;
		};
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		BlockPos pos = context.getClickedPos();
		Level level = context.getLevel();

		if (pos.getY() < level.getMaxY() - 1 && level.getBlockState(pos.above()).canBeReplaced(context)) {
			boolean hasActiveSCBlock = BlockUtils.hasActiveSCBlockNextTo(level, pos) || BlockUtils.hasActiveSCBlockNextTo(level, pos.above());

			return defaultBlockState().setValue(FACING, context.getHorizontalDirection()).setValue(HINGE, getHingeSide(context)).setValue(OPEN, hasActiveSCBlock).setValue(HALF, DoubleBlockHalf.LOWER);
		}

		return null;
	}

	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, Orientation orientation, boolean flag) {
		onNeighborChanged(level, pos, block, orientation);
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
				int stepX = horizontalDirection.getStepX();
				int stepY = horizontalDirection.getStepZ();
				Vec3 clickLocation = ctx.getClickLocation();
				double clickedX = clickLocation.x - clickedPos.getX();
				double clickedY = clickLocation.z - clickedPos.getZ();

				return (stepX >= 0 || clickedY >= 0.5D) && (stepX <= 0 || clickedY <= 0.5D) && (stepY >= 0 || clickedX <= 0.5D) && (stepY <= 0 || clickedX >= 0.5D) ? DoorHingeSide.LEFT : DoorHingeSide.RIGHT;
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
	 * @param level The level the change occured in
	 * @param firstDoorPos The position of this block
	 * @param neighbor The position of the changed block
	 */
	public void onNeighborChanged(Level level, BlockPos firstDoorPos, Block neighborBlock, Orientation orientation) {
		BlockState firstDoorState = level.getBlockState(firstDoorPos);
		Owner previousOwner = null;

		if (level.getBlockEntity(firstDoorPos) instanceof OwnableBlockEntity ownable)
			previousOwner = ownable.getOwner();

		if (firstDoorState.getValue(HALF) == DoubleBlockHalf.UPPER) {
			BlockPos blockBelow = firstDoorPos.below();
			BlockState stateBelow = level.getBlockState(blockBelow);

			if (stateBelow.getBlock() != this)
				level.destroyBlock(firstDoorPos, false);
			else if (neighborBlock != this)
				onNeighborChanged(level, blockBelow, neighborBlock, orientation);
		}
		else {
			boolean drop = false;
			BlockPos blockAbove = firstDoorPos.above();
			BlockState stateAbove = level.getBlockState(blockAbove);

			if (stateAbove.getBlock() != this) {
				level.destroyBlock(firstDoorPos, false);
				drop = true;
			}

			if (!BlockUtils.isSideSolid(level, firstDoorPos.below(), Direction.UP)) {
				level.destroyBlock(firstDoorPos, false);
				drop = true;

				if (stateAbove.getBlock() == this)
					level.destroyBlock(firstDoorPos, false);
			}

			if (drop) {
				if (!level.isClientSide) {
					level.destroyBlock(firstDoorPos, false);
					Block.popResource(level, firstDoorPos, new ItemStack(SCContent.REINFORCED_DOOR_ITEM.get()));
				}
			}
			else if (neighborBlock != this) {
				boolean hasActiveSCBlock = BlockUtils.hasActiveSCBlockNextTo(level, firstDoorPos) || BlockUtils.hasActiveSCBlockNextTo(level, firstDoorPos.above());
				Direction directionToCheck = firstDoorState.getValue(FACING).getClockWise();
				BlockPos secondDoorPos = firstDoorPos.relative(directionToCheck);
				BlockState secondDoorState = level.getBlockState(secondDoorPos);

				if (!(secondDoorState != null && secondDoorState.getBlock() == SCContent.REINFORCED_DOOR.get() && secondDoorState.getValue(HINGE) == DoorHingeSide.RIGHT && firstDoorState.getValue(HINGE) != secondDoorState.getValue(HINGE))) {
					secondDoorPos = firstDoorPos.relative(directionToCheck.getOpposite());
					secondDoorState = level.getBlockState(secondDoorPos);

					if (!(secondDoorState != null && secondDoorState.getBlock() == SCContent.REINFORCED_DOOR.get() && secondDoorState.getValue(HINGE) == DoorHingeSide.LEFT && firstDoorState.getValue(HINGE) != secondDoorState.getValue(HINGE)))
						secondDoorPos = null;
				}

				boolean hasSecondDoorActiveSCBlock = secondDoorPos != null && (BlockUtils.hasActiveSCBlockNextTo(level, secondDoorPos) || BlockUtils.hasActiveSCBlockNextTo(level, secondDoorPos.above()));
				boolean shouldBeOpen = hasActiveSCBlock != hasSecondDoorActiveSCBlock || hasActiveSCBlock;

				if (shouldBeOpen != firstDoorState.getValue(OPEN))
					setDoorState(level, firstDoorPos, firstDoorState, shouldBeOpen);

				if (secondDoorPos != null && shouldBeOpen != secondDoorState.getValue(OPEN))
					setDoorState(level, secondDoorPos, secondDoorState, shouldBeOpen);
			}
		}

		if (previousOwner != null && level.getBlockEntity(firstDoorPos) instanceof OwnableBlockEntity thisBe && level.getBlockEntity(firstDoorPos.above()) instanceof OwnableBlockEntity aboveBe) {
			thisBe.setOwner(previousOwner.getUUID(), previousOwner.getName());
			aboveBe.setOwner(previousOwner.getUUID(), previousOwner.getName());
		}
	}

	public void setDoorState(Level level, BlockPos pos, BlockState state, boolean open) {
		level.setBlock(pos, state.setValue(OPEN, open), 2);
		level.playSound(null, pos, open ? SoundEvents.IRON_DOOR_OPEN : SoundEvents.IRON_DOOR_CLOSE, SoundSource.BLOCKS, 1.0F, 1.0F);
		level.gameEvent(null, open ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);
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
	public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
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