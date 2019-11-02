package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.IBucketPickupHandler;
import net.minecraft.block.ILiquidContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.SlabType;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
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
	public int getOpacity(IBlockState state, IBlockReader world, BlockPos pos)
	{
		return world.getMaxLightLevel();
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder)
	{
		builder.add(TYPE, WATERLOGGED);
	}

	@Override
	protected boolean canSilkHarvest()
	{
		return false;
	}

	@Override
	public VoxelShape getShape(IBlockState state, IBlockReader world, BlockPos pos)
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
	public boolean isTopSolid(IBlockState state)
	{
		return state.get(TYPE) == SlabType.DOUBLE || state.get(TYPE) == SlabType.TOP;
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockReader world, IBlockState state, BlockPos pos, EnumFacing face)
	{
		SlabType slabType = state.get(TYPE);

		if (slabType == SlabType.DOUBLE)
			return BlockFaceShape.SOLID;
		else if (face == EnumFacing.UP && slabType == SlabType.TOP)
			return BlockFaceShape.SOLID;
		else
			return face == EnumFacing.DOWN && slabType == SlabType.BOTTOM ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
	}

	@Override
	@Nullable
	public IBlockState getStateForPlacement(BlockItemUseContext ctx)
	{
		IBlockState state = ctx.getWorld().getBlockState(ctx.getPos());

		if(state.getBlock() == this)
			return state.with(TYPE, SlabType.DOUBLE).with(WATERLOGGED, false);
		else
		{
			IFluidState fluidState = ctx.getWorld().getFluidState(ctx.getPos());
			IBlockState newState = getDefaultState().with(TYPE, SlabType.BOTTOM).with(WATERLOGGED, Boolean.valueOf(fluidState.getFluid() == Fluids.WATER));

			EnumFacing enumfacing = ctx.getFace();
			return enumfacing != EnumFacing.DOWN && (enumfacing == EnumFacing.UP || !(ctx.getHitY() > 0.5D)) ? newState : newState.with(TYPE, SlabType.TOP);
		}
	}

	@Override
	public int quantityDropped(IBlockState state, Random random)
	{
		return state.get(TYPE) == SlabType.DOUBLE ? 2 : 1;
	}

	@Override
	public boolean isFullCube(IBlockState state)
	{
		return state.get(TYPE) == SlabType.DOUBLE;
	}

	@Override
	public boolean isReplaceable(IBlockState state, BlockItemUseContext ctx)
	{
		ItemStack stack = ctx.getItem();
		SlabType slabType = state.get(TYPE);

		if(slabType != SlabType.DOUBLE && stack.getItem() == asItem())
		{
			if(ctx.replacingClickedOnBlock())
			{
				boolean hitTop = ctx.getHitY() > 0.5D;
				EnumFacing facing = ctx.getFace();

				if(slabType == SlabType.BOTTOM)
					return facing == EnumFacing.UP || hitTop && facing.getAxis().isHorizontal();
				else
					return facing == EnumFacing.DOWN || !hitTop && facing.getAxis().isHorizontal();
			}
			else
				return true;
		}
		else
			return false;
	}

	@Override
	public Fluid pickupFluid(IWorld world, BlockPos pos, IBlockState state)
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
	public IFluidState getFluidState(IBlockState state)
	{
		return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
	}

	@Override
	public boolean canContainFluid(IBlockReader world, BlockPos pos, IBlockState state, Fluid fluid)
	{
		return state.get(TYPE) != SlabType.DOUBLE && !state.get(WATERLOGGED) && fluid == Fluids.WATER;
	}

	@Override
	public boolean receiveFluid(IWorld world, BlockPos pos, IBlockState state, IFluidState fluidState)
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
	public IBlockState updatePostPlacement(IBlockState state, EnumFacing facing, IBlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos)
	{
		if(state.get(WATERLOGGED))
			world.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(world));

		return super.updatePostPlacement(state, facing, facingState, world, currentPos, facingPos);
	}

	@Override
	public boolean allowsMovement(IBlockState state, IBlockReader world, BlockPos pos, PathType type)
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

	@Override
	public IBlockState getConvertedState(IBlockState vanillaState)
	{
		return getDefaultState().with(TYPE, vanillaState.get(BlockSlab.TYPE)).with(WATERLOGGED, vanillaState.get(BlockSlab.WATERLOGGED));
	}
}