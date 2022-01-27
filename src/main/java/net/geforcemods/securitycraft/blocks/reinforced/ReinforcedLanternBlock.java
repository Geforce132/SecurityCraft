package net.geforcemods.securitycraft.blocks.reinforced;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.PushReaction;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;

public class ReinforcedLanternBlock extends BaseReinforcedBlock {
	public static final BooleanProperty HANGING = BlockStateProperties.HANGING;
	protected static final VoxelShape STANDING_SHAPE = VoxelShapes.or(Block.box(5.0D, 0.0D, 5.0D, 11.0D, 7.0D, 11.0D), Block.box(6.0D, 7.0D, 6.0D, 10.0D, 9.0D, 10.0D));
	protected static final VoxelShape HANGING_SHAPE = VoxelShapes.or(Block.box(5.0D, 1.0D, 5.0D, 11.0D, 8.0D, 11.0D), Block.box(6.0D, 8.0D, 6.0D, 10.0D, 10.0D, 10.0D));

	public ReinforcedLanternBlock(Block.Properties properties, Block vB) {
		super(properties, vB);

		registerDefaultState(stateDefinition.any().setValue(HANGING, false));
	}

	@Override
	@Nullable
	public BlockState getStateForPlacement(BlockItemUseContext ctx) {
		for (Direction dir : ctx.getNearestLookingDirections()) {
			if (dir.getAxis() == Direction.Axis.Y) {
				BlockState state = defaultBlockState().setValue(HANGING, dir == Direction.UP);

				if (state.canSurvive(ctx.getLevel(), ctx.getClickedPos()))
					return state;
			}
		}

		return null;
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext ctx) {
		return state.getValue(HANGING) ? HANGING_SHAPE : STANDING_SHAPE;
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(HANGING);
	}

	@Override
	public boolean canSurvive(BlockState state, IWorldReader world, BlockPos pos) {
		Direction dir = getConnectedDirection(state).getOpposite();

		return Block.canSupportCenter(world, pos.relative(dir), dir.getOpposite());
	}

	protected static Direction getConnectedDirection(BlockState state) {
		return state.getValue(HANGING) ? Direction.DOWN : Direction.UP;
	}

	@Override
	public PushReaction getPistonPushReaction(BlockState state) {
		return PushReaction.DESTROY;
	}

	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos) {
		return getConnectedDirection(state).getOpposite() == facing && !state.canSurvive(world, currentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, facing, facingState, world, currentPos, facingPos);
	}

	@Override
	public boolean isPathfindable(BlockState state, IBlockReader world, BlockPos pos, PathType type) {
		return false;
	}
}
