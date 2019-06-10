package net.geforcemods.securitycraft.blocks;

import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import net.minecraft.block.Block;
import net.minecraft.block.BlockWallSign;
import net.minecraft.block.state.IBlockState;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

public class BlockSecretSignWall extends BlockSecretSign
{
	public static final DirectionProperty FACING = BlockWallSign.FACING;
	private static final Map<EnumFacing, VoxelShape> SHAPES = Maps.newEnumMap(ImmutableMap.of(EnumFacing.NORTH, Block.makeCuboidShape(0.0D, 4.5D, 14.0D, 16.0D, 12.5D, 16.0D), EnumFacing.SOUTH, Block.makeCuboidShape(0.0D, 4.5D, 0.0D, 16.0D, 12.5D, 2.0D), EnumFacing.EAST, Block.makeCuboidShape(0.0D, 4.5D, 0.0D, 2.0D, 12.5D, 16.0D), EnumFacing.WEST, Block.makeCuboidShape(14.0D, 4.5D, 0.0D, 16.0D, 12.5D, 16.0D)));

	public BlockSecretSignWall()
	{
		super();
		setDefaultState(stateContainer.getBaseState().with(FACING, EnumFacing.NORTH).with(WATERLOGGED, true));
	}

	@Override
	public VoxelShape getShape(IBlockState state, IBlockReader source, BlockPos pos)
	{
		return SHAPES.get(state.get(FACING));
	}

	@Override
	public boolean isValidPosition(IBlockState state, IWorldReaderBase world, BlockPos pos)
	{
		return world.getBlockState(pos.offset(state.get(FACING).getOpposite())).getMaterial().isSolid();
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos)
	{
		if(!world.getBlockState(pos.offset(state.get(FACING).getOpposite())).getMaterial().isSolid())
		{
			dropBlockAsItemWithChance(state, world, pos, 1.0F, 0);
			world.removeBlock(pos);
		}

		super.neighborChanged(state, world, pos, block, fromPos);
	}

	@Override
	@Nullable
	public IBlockState getStateForPlacement(BlockItemUseContext ctx)
	{
		IBlockState state = getDefaultState();
		IFluidState fluidState = ctx.getWorld().getFluidState(ctx.getPos());
		IWorldReaderBase world = ctx.getWorld();
		BlockPos pos = ctx.getPos();
		EnumFacing[] nearestLookingDirections = ctx.getNearestLookingDirections();

		for(EnumFacing facing : nearestLookingDirections)
		{
			if(facing.getAxis().isHorizontal())
			{
				EnumFacing oppositeFacing = facing.getOpposite();

				state = state.with(FACING, oppositeFacing);

				if(state.isValidPosition(world, pos))
					return state.with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
			}
		}

		return null;
	}

	@Override
	public IBlockState updatePostPlacement(IBlockState state, EnumFacing facing, IBlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos)
	{
		return facing.getOpposite() == state.get(FACING) && !state.isValidPosition(world, currentPos) ? Blocks.AIR.getDefaultState() : super.updatePostPlacement(state, facing, facingState, world, currentPos, facingPos);
	}

	@Override
	public IBlockState rotate(IBlockState state, Rotation rot)
	{
		return state.with(FACING, rot.rotate(state.get(FACING)));
	}

	@Override
	public IBlockState mirror(IBlockState state, Mirror mirror)
	{
		return state.rotate(mirror.toRotation(state.get(FACING)));
	}

	@Override
	protected void fillStateContainer(Builder<Block, IBlockState> builder)
	{
		builder.add(FACING, WATERLOGGED);
	}
}