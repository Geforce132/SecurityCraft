package net.geforcemods.securitycraft.blocks;

import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.WallSignBlock;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class BlockSecretSignWall extends BlockSecretSign
{
	public static final DirectionProperty FACING = WallSignBlock.FACING;
	private static final Map<Direction, VoxelShape> SHAPES = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH, Block.makeCuboidShape(0.0D, 4.5D, 14.0D, 16.0D, 12.5D, 16.0D), Direction.SOUTH, Block.makeCuboidShape(0.0D, 4.5D, 0.0D, 16.0D, 12.5D, 2.0D), Direction.EAST, Block.makeCuboidShape(0.0D, 4.5D, 0.0D, 2.0D, 12.5D, 16.0D), Direction.WEST, Block.makeCuboidShape(14.0D, 4.5D, 0.0D, 16.0D, 12.5D, 16.0D)));

	public BlockSecretSignWall()
	{
		super();
		setDefaultState(stateContainer.getBaseState().with(FACING, Direction.NORTH).with(WATERLOGGED, true));
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader source, BlockPos pos, ISelectionContext ctx)
	{
		return SHAPES.get(state.get(FACING));
	}

	@Override
	public boolean isValidPosition(BlockState state, IWorldReader world, BlockPos pos)
	{
		return world.getBlockState(pos.offset(state.get(FACING).getOpposite())).getMaterial().isSolid();
	}

	@Override
	public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean flag)
	{
		if(!world.getBlockState(pos.offset(state.get(FACING).getOpposite())).getMaterial().isSolid())
			world.destroyBlock(pos, true);

		super.neighborChanged(state, world, pos, block, fromPos, flag);
	}

	@Override
	@Nullable
	public BlockState getStateForPlacement(BlockItemUseContext ctx)
	{
		BlockState state = getDefaultState();
		IFluidState fluidState = ctx.getWorld().getFluidState(ctx.getPos());
		IWorldReader world = ctx.getWorld();
		BlockPos pos = ctx.getPos();
		Direction[] nearestLookingDirections = ctx.getNearestLookingDirections();

		for(Direction facing : nearestLookingDirections)
		{
			if(facing.getAxis().isHorizontal())
			{
				Direction oppositeFacing = facing.getOpposite();

				state = state.with(FACING, oppositeFacing);

				if(state.isValidPosition(world, pos))
					return state.with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
			}
		}

		return null;
	}

	@Override
	public BlockState updatePostPlacement(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos)
	{
		return facing.getOpposite() == state.get(FACING) && !state.isValidPosition(world, currentPos) ? Blocks.AIR.getDefaultState() : super.updatePostPlacement(state, facing, facingState, world, currentPos, facingPos);
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot)
	{
		return state.with(FACING, rot.rotate(state.get(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror)
	{
		return state.rotate(mirror.toRotation(state.get(FACING)));
	}

	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder)
	{
		builder.add(FACING, WATERLOGGED);
	}
}