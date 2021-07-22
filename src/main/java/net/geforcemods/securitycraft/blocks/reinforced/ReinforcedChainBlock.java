package net.geforcemods.securitycraft.blocks.reinforced;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;

public class ReinforcedChainBlock extends ReinforcedRotatedPillarBlock{
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	protected static final VoxelShape X_AXIS_SHAPE = Block.box(0.0D, 6.5D, 6.5D, 16.0D, 9.5D, 9.5D);
	protected static final VoxelShape Y_AXIS_SHAPE = Block.box(6.5D, 0.0D, 6.5D, 9.5D, 16.0D, 9.5D);
	protected static final VoxelShape Z_AXIS_SHAPE = Block.box(6.5D, 6.5D, 0.0D, 9.5D, 9.5D, 16.0D);

	public ReinforcedChainBlock(Block.Properties properties, Block vB) {
		super(properties, vB);
		this.registerDefaultState(stateDefinition.any().setValue(WATERLOGGED, false).setValue(AXIS, Direction.Axis.Y));
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
		switch(state.getValue(AXIS)) {
			case X:
			default:
				return X_AXIS_SHAPE;
			case Y:
				return Y_AXIS_SHAPE;
			case Z:
				return Z_AXIS_SHAPE;
		}
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
		boolean isWater = fluidstate.getType() == Fluids.WATER;
		return super.getStateForPlacement(context).setValue(WATERLOGGED, isWater);
	}

	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos) {
		if (state.getValue(WATERLOGGED)) {
			world.getLiquidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
		}

		return super.updateShape(state, facing, facingState, world, currentPos, facingPos);
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(WATERLOGGED).add(AXIS);
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}
}
