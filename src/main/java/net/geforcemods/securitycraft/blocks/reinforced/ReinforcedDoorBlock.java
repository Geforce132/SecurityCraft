package net.geforcemods.securitycraft.blocks.reinforced;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.OwnableBlockEntity;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blockentities.ReinforcedDoorBlockEntity;
import net.geforcemods.securitycraft.blocks.OwnableBlock;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.DoorHingeSide;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ReinforcedDoorBlock extends OwnableBlock {
	public static final DirectionProperty FACING = HorizontalBlock.FACING;
	public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
	public static final EnumProperty<DoorHingeSide> HINGE = BlockStateProperties.DOOR_HINGE;
	public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
	protected static final VoxelShape SOUTH_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 3.0D);
	protected static final VoxelShape NORTH_AABB = Block.box(0.0D, 0.0D, 13.0D, 16.0D, 16.0D, 16.0D);
	protected static final VoxelShape WEST_AABB = Block.box(13.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
	protected static final VoxelShape EAST_AABB = Block.box(0.0D, 0.0D, 0.0D, 3.0D, 16.0D, 16.0D);

	public ReinforcedDoorBlock(AbstractBlock.Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(OPEN, false).setValue(HINGE, DoorHingeSide.LEFT).setValue(HALF, DoubleBlockHalf.LOWER));
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader level, BlockPos pos, ISelectionContext context) {
		Direction facing = state.getValue(FACING);
		boolean isNotOpen = !state.getValue(OPEN);
		boolean isHingeRight = state.getValue(HINGE) == DoorHingeSide.RIGHT;

		switch (facing) {
			case SOUTH:
				return isNotOpen ? SOUTH_AABB : (isHingeRight ? EAST_AABB : WEST_AABB);
			case WEST:
				return isNotOpen ? WEST_AABB : (isHingeRight ? SOUTH_AABB : NORTH_AABB);
			case NORTH:
				return isNotOpen ? NORTH_AABB : (isHingeRight ? WEST_AABB : EAST_AABB);
			case EAST:
			default:
				return isNotOpen ? EAST_AABB : (isHingeRight ? NORTH_AABB : SOUTH_AABB);
		}
	}

	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, IWorld level, BlockPos currentPos, BlockPos facingPos) {
		DoubleBlockHalf doorHalf = state.getValue(HALF);

		if (facing.getAxis() == Direction.Axis.Y && doorHalf == DoubleBlockHalf.LOWER == (facing == Direction.UP))
			return facingState.getBlock() == this && facingState.getValue(HALF) != doorHalf ? state.setValue(FACING, facingState.getValue(FACING)).setValue(OPEN, facingState.getValue(OPEN)).setValue(HINGE, facingState.getValue(HINGE)) : Blocks.AIR.defaultBlockState();
		else
			return doorHalf == DoubleBlockHalf.LOWER && facing == Direction.DOWN && !state.canSurvive(level, currentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, facing, facingState, level, currentPos, facingPos);
	}

	@Override
	public void playerDestroy(World level, PlayerEntity player, BlockPos pos, BlockState state, TileEntity be, ItemStack stack) {
		super.playerDestroy(level, player, pos, Blocks.AIR.defaultBlockState(), be, stack);
	}

	@Override
	public void playerWillDestroy(World level, BlockPos pos, BlockState state, PlayerEntity player) {
		if (!level.isClientSide && player.isCreative()) {
			DoubleBlockHalf doorHalf = state.getValue(HALF);

			if (doorHalf == DoubleBlockHalf.UPPER) {
				BlockPos posBelow = pos.below();
				BlockState stateBelow = level.getBlockState(posBelow);

				if (stateBelow.getBlock() == state.getBlock() && stateBelow.getValue(HALF) == DoubleBlockHalf.LOWER) {
					level.setBlock(posBelow, Blocks.AIR.defaultBlockState(), 35);
					level.levelEvent(player, 2001, posBelow, Block.getId(stateBelow));
				}
			}
		}

		super.playerWillDestroy(level, pos, state, player);
	}

	@Override
	public boolean isPathfindable(BlockState state, IBlockReader level, BlockPos pos, PathType type) {
		switch (type) {
			case LAND:
				return state.getValue(OPEN);
			case WATER:
				return false;
			case AIR:
				return state.getValue(OPEN);
			default:
				return false;
		}
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		BlockPos pos = context.getClickedPos();
		World level = context.getLevel();

		if (pos.getY() < 255 && level.getBlockState(pos.above()).canBeReplaced(context)) {
			boolean hasActiveSCBlock = BlockUtils.hasActiveSCBlockNextTo(level, pos) || BlockUtils.hasActiveSCBlockNextTo(level, pos.above());

			return defaultBlockState().setValue(FACING, context.getHorizontalDirection()).setValue(HINGE, getHingeSide(context)).setValue(OPEN, hasActiveSCBlock).setValue(HALF, DoubleBlockHalf.LOWER);
		}

		return null;
	}

	@Override
	public void neighborChanged(BlockState state, World level, BlockPos pos, Block block, BlockPos fromPos, boolean flag) {
		onNeighborChanged(level, pos, fromPos);
	}

	@Override
	public void setPlacedBy(World level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		super.setPlacedBy(level, pos, state, placer, stack);
		level.setBlock(pos.above(), state.setValue(HALF, DoubleBlockHalf.UPPER), 3);
	}

	private DoorHingeSide getHingeSide(BlockItemUseContext ctx) {
		IBlockReader level = ctx.getLevel();
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
				Vector3d clickLocation = ctx.getClickLocation();
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
	public void onNeighborChanged(World level, BlockPos firstDoorPos, BlockPos neighbor) {
		BlockState firstDoorState = level.getBlockState(firstDoorPos);
		Block neighborBlock = level.getBlockState(neighbor).getBlock();
		Owner previousOwner = null;

		if (level.getBlockEntity(firstDoorPos) instanceof OwnableBlockEntity)
			previousOwner = ((OwnableBlockEntity) level.getBlockEntity(firstDoorPos)).getOwner();

		if (firstDoorState.getValue(HALF) == DoubleBlockHalf.UPPER) {
			BlockPos blockBelow = firstDoorPos.below();
			BlockState stateBelow = level.getBlockState(blockBelow);

			if (stateBelow.getBlock() != this)
				level.destroyBlock(firstDoorPos, false);
			else if (neighborBlock != this)
				onNeighborChanged(level, blockBelow, neighbor);
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

		if (previousOwner != null && level.getBlockEntity(firstDoorPos) instanceof OwnableBlockEntity && level.getBlockEntity(firstDoorPos.above()) instanceof OwnableBlockEntity) {
			((OwnableBlockEntity) level.getBlockEntity(firstDoorPos)).setOwner(previousOwner.getUUID(), previousOwner.getName());
			((OwnableBlockEntity) level.getBlockEntity(firstDoorPos.above())).setOwner(previousOwner.getUUID(), previousOwner.getName());
		}
	}

	public void setDoorState(World level, BlockPos pos, BlockState state, boolean open) {
		level.setBlock(pos, state.setValue(OPEN, open), 2);
		level.levelEvent(null, open ? 1005 : 1011, pos, 0);
	}

	@Override
	public boolean canSurvive(BlockState state, IWorldReader level, BlockPos pos) {
		BlockPos posBelow = pos.below();
		BlockState stateBelow = level.getBlockState(posBelow);

		if (state.getValue(HALF) == DoubleBlockHalf.LOWER)
			return stateBelow.isFaceSturdy(level, posBelow, Direction.UP);
		else
			return stateBelow.getBlock() == this;
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
	@OnlyIn(Dist.CLIENT)
	public long getSeed(BlockState state, BlockPos pos) {
		return MathHelper.getSeed(pos.getX(), pos.below(state.getValue(HALF) == DoubleBlockHalf.LOWER ? 0 : 1).getY(), pos.getZ());
	}

	@Override
	public boolean triggerEvent(BlockState state, World level, BlockPos pos, int id, int param) {
		TileEntity be = level.getBlockEntity(pos);

		return be != null && be.triggerEvent(id, param);
	}

	@Override
	public ItemStack getCloneItemStack(IBlockReader worldIn, BlockPos pos, BlockState state) {
		return new ItemStack(SCContent.REINFORCED_DOOR_ITEM.get());
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader level) {
		return new ReinforcedDoorBlockEntity();
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(HALF, FACING, OPEN, HINGE);
	}
}