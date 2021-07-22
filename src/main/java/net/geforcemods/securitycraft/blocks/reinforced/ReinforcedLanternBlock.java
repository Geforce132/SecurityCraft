package net.geforcemods.securitycraft.blocks.reinforced;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ReinforcedLanternBlock extends BaseReinforcedBlock{
	public static final BooleanProperty HANGING = BlockStateProperties.HANGING;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	protected static final VoxelShape STANDING_SHAPE = Shapes.or(Block.box(5.0D, 0.0D, 5.0D, 11.0D, 7.0D, 11.0D), Block.box(6.0D, 7.0D, 6.0D, 10.0D, 9.0D, 10.0D));
	protected static final VoxelShape HANGING_SHAPE = Shapes.or(Block.box(5.0D, 1.0D, 5.0D, 11.0D, 8.0D, 11.0D), Block.box(6.0D, 8.0D, 6.0D, 10.0D, 10.0D, 10.0D));

	public ReinforcedLanternBlock(Block.Properties properties, Block vB) {
		super(properties, vB);
		this.registerDefaultState(this.stateDefinition.any().setValue(HANGING, false).setValue(WATERLOGGED, false));
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());

		for(Direction direction : context.getNearestLookingDirections()) {
			if (direction.getAxis() == Direction.Axis.Y) {
				BlockState blockstate = this.defaultBlockState().setValue(HANGING, direction == Direction.UP);
				if (blockstate.canSurvive(context.getLevel(), context.getClickedPos())) {
					return blockstate.setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
				}
			}
		}

		return null;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		return state.getValue(HANGING) ? HANGING_SHAPE : STANDING_SHAPE;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(HANGING, WATERLOGGED);
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
		Direction direction = getBlockConnected(state).getOpposite();
		return Block.canSupportCenter(world, pos.relative(direction), direction.getOpposite());
	}

	protected static Direction getBlockConnected(BlockState state) {
		return state.getValue(HANGING) ? Direction.DOWN : Direction.UP;
	}

	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor world, BlockPos currentPos, BlockPos facingPos) {
		if (state.getValue(WATERLOGGED)) {
			world.getLiquidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
		}

		return getBlockConnected(state).getOpposite() == facing && !state.canSurvive(world, currentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, facing, facingState, world, currentPos, facingPos);
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Override
	public boolean isPathfindable(BlockState state, BlockGetter world, BlockPos pos, PathComputationType type) {
		return false;
	}
}
