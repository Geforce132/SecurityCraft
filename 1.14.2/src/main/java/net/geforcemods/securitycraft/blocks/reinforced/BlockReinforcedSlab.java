package net.geforcemods.securitycraft.blocks.reinforced;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IBucketPickupHandler;
import net.minecraft.block.ILiquidContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.SlabType;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;

public class BlockReinforcedSlab extends BlockReinforcedBase implements IBucketPickupHandler, ILiquidContainer
{
	public static final EnumProperty<SlabType> TYPE = BlockStateProperties.SLAB_TYPE;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	protected static final VoxelShape BOTTOM_SHAPE = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
	protected static final VoxelShape TOP_SHAPE = Block.makeCuboidShape(0.0D, 8.0D, 0.0D, 16.0D, 16.0D, 16.0D);

	public BlockReinforcedSlab(SoundType soundType, Material mat, Block vB, String registryPath)
	{
		super(soundType, mat, vB, registryPath);
		setDefaultState(stateContainer.getBaseState().with(TYPE, SlabType.BOTTOM).with(WATERLOGGED, false));
	}

	@Override
	public int getOpacity(BlockState state, IBlockReader world, BlockPos pos)
	{
		return world.getMaxLightLevel();
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
	{
		builder.add(TYPE, WATERLOGGED);
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext ctx)
	{
		SlabType slabtype = state.get(TYPE);

		switch(slabtype)
		{
			case DOUBLE:
				return VoxelShapes.fullCube();
			case TOP:
				return TOP_SHAPE;
			default:
				return BOTTOM_SHAPE;
		}
	}

	@Override
	@Nullable
	public BlockState getStateForPlacement(BlockItemUseContext ctx)
	{
		BlockState state = ctx.getWorld().getBlockState(ctx.getPos());

		if(state.getBlock() == this)
			return state.with(TYPE, SlabType.DOUBLE).with(WATERLOGGED, false);
		else
		{
			IFluidState fluidState = ctx.getWorld().getFluidState(ctx.getPos());
			BlockState newState = getDefaultState().with(TYPE, SlabType.BOTTOM).with(WATERLOGGED, Boolean.valueOf(fluidState.getFluid() == Fluids.WATER));

			Direction direction = ctx.getFace();
			return direction != Direction.DOWN && (direction == Direction.UP || !(ctx.func_221532_j().y > 0.5D)) ? newState : newState.with(TYPE, SlabType.TOP);
		}
	}

	@Override
	public boolean isReplaceable(BlockState state, BlockItemUseContext ctx)
	{
		ItemStack stack = ctx.getItem();
		SlabType slabType = state.get(TYPE);

		if(slabType != SlabType.DOUBLE && stack.getItem() == asItem())
		{
			if(ctx.replacingClickedOnBlock())
			{
				boolean hitTop = ctx.func_221532_j().y > 0.5D;
				Direction facing = ctx.getFace();

				if(slabType == SlabType.BOTTOM)
					return facing == Direction.UP || hitTop && facing.getAxis().isHorizontal();
				else
					return facing == Direction.DOWN || !hitTop && facing.getAxis().isHorizontal();
			}
			else
				return true;
		}
		else
			return false;
	}

	@Override
	public Fluid pickupFluid(IWorld world, BlockPos pos, BlockState state)
	{
		if(state.get(WATERLOGGED))
		{
			world.setBlockState(pos, state.with(WATERLOGGED, false), 3);
			return Fluids.WATER;
		}
		else
			return Fluids.EMPTY;
	}

	@Override
	public IFluidState getFluidState(BlockState state)
	{
		return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
	}

	@Override
	public boolean canContainFluid(IBlockReader world, BlockPos pos, BlockState state, Fluid fluid)
	{
		return state.get(TYPE) != SlabType.DOUBLE && !state.get(WATERLOGGED) && fluid == Fluids.WATER;
	}

	@Override
	public boolean receiveFluid(IWorld world, BlockPos pos, BlockState state, IFluidState fluidState)
	{
		if(state.get(TYPE) != SlabType.DOUBLE && !state.get(WATERLOGGED) && fluidState.getFluid() == Fluids.WATER)
		{
			if(!world.isRemote())
			{
				world.setBlockState(pos, state.with(WATERLOGGED, true), 3);
				world.getPendingFluidTicks().scheduleTick(pos, fluidState.getFluid(), fluidState.getFluid().getTickRate(world));
			}

			return true;
		}
		else
			return false;
	}

	@Override
	public BlockState updatePostPlacement(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos)
	{
		if(state.get(WATERLOGGED))
			world.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(world));

		return super.updatePostPlacement(state, facing, facingState, world, currentPos, facingPos);
	}

	@Override
	public boolean allowsMovement(BlockState state, IBlockReader world, BlockPos pos, PathType type)
	{
		switch(type)
		{
			case LAND:
				return state.get(TYPE) == SlabType.BOTTOM;
			case WATER:
				return world.getFluidState(pos).isTagged(FluidTags.WATER);
			case AIR:
				return false;
			default:
				return false;
		}
	}
}