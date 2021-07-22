package net.geforcemods.securitycraft.blocks.reinforced;

import javax.annotation.Nullable;

import net.geforcemods.securitycraft.SCContent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IBucketPickupHandler;
import net.minecraft.block.ILiquidContainer;
import net.minecraft.block.SlabBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;

public class HorizontalReinforcedIronBars extends BaseReinforcedBlock implements IBucketPickupHandler, ILiquidContainer
{
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	protected static final VoxelShape SHAPE = Block.box(-8.0D, 14.0D, -8.0D, 24.0D, 16.0D, 24.0D);

	public HorizontalReinforcedIronBars(Block.Properties properties, Block vB)
	{
		super(properties, vB);
		registerDefaultState(stateDefinition.any().setValue(WATERLOGGED, false));
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
	{
		builder.add(WATERLOGGED);
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext ctx)
	{
		return SHAPE;
	}

	@Override
	@Nullable
	public BlockState getStateForPlacement(BlockItemUseContext ctx)
	{
		BlockState state = ctx.getLevel().getBlockState(ctx.getClickedPos());

		if(state.getBlock() == this)
			return state;
		else
			return defaultBlockState().setValue(WATERLOGGED, ctx.getLevel().getFluidState(ctx.getClickedPos()).getType() == Fluids.WATER);
	}

	@Override
	public Fluid takeLiquid(IWorld world, BlockPos pos, BlockState state)
	{
		if(state.getValue(WATERLOGGED))
		{
			world.setBlock(pos, state.setValue(WATERLOGGED, false), 3);
			return Fluids.WATER;
		}
		else
			return Fluids.EMPTY;
	}

	@Override
	public FluidState getFluidState(BlockState state)
	{
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Override
	public boolean canPlaceLiquid(IBlockReader world, BlockPos pos, BlockState state, Fluid fluid)
	{
		return !state.getValue(WATERLOGGED) && fluid == Fluids.WATER;
	}

	@Override
	public boolean placeLiquid(IWorld world, BlockPos pos, BlockState state, FluidState fluidState)
	{
		if(!state.getValue(WATERLOGGED) && fluidState.getType() == Fluids.WATER)
		{
			if(!world.isClientSide())
			{
				world.setBlock(pos, state.setValue(WATERLOGGED, true), 3);
				world.getLiquidTicks().scheduleTick(pos, fluidState.getType(), fluidState.getType().getTickDelay(world));
			}

			return true;
		}
		else
			return false;
	}

	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos)
	{
		if(state.getValue(WATERLOGGED))
			world.getLiquidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(world));

		return super.updateShape(state, facing, facingState, world, currentPos, facingPos);
	}

	@Override
	public boolean isPathfindable(BlockState state, IBlockReader world, BlockPos pos, PathType type)
	{
		return false;
	}

	@Override
	public BlockState getConvertedState(BlockState vanillaState)
	{
		return defaultBlockState().setValue(WATERLOGGED, vanillaState.getValue(SlabBlock.WATERLOGGED));
	}

	@Override
	public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player)
	{
		return new ItemStack(SCContent.REINFORCED_IRON_BARS.get());
	}
}