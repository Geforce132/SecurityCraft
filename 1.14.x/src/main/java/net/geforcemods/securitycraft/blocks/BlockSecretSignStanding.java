package net.geforcemods.securitycraft.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.StandingSignBlock;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class BlockSecretSignStanding extends BlockSecretSign
{
	public static final IntegerProperty ROTATION = StandingSignBlock.ROTATION;

	public BlockSecretSignStanding()
	{
		super();
		setDefaultState(stateContainer.getBaseState().with(ROTATION, 0).with(WATERLOGGED, false));
	}

	@Override
	public boolean isValidPosition(BlockState state, IWorldReader world, BlockPos pos)
	{
		return world.getBlockState(pos.down()).getMaterial().isSolid();
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext ctx)
	{
		return getDefaultState().with(ROTATION, MathHelper.floor((180.0F + ctx.getPlacementYaw()) * 16.0F / 360.0F + 0.5D) & 15).with(WATERLOGGED, ctx.getWorld().getFluidState(ctx.getPos()).getFluid() == Fluids.WATER);
	}

	@Override
	public BlockState updatePostPlacement(BlockState state, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos)
	{
		return facing == Direction.DOWN && !isValidPosition(state, worldIn, currentPos) ? Blocks.AIR.getDefaultState() : super.updatePostPlacement(state, facing, facingState, worldIn, currentPos, facingPos);
	}

	@Override
	public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean flag)
	{
		if(!world.getBlockState(pos.down()).getMaterial().isSolid())
			world.destroyBlock(pos, true);

		super.neighborChanged(state, world, pos, block, fromPos, flag);
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot)
	{
		return state.with(ROTATION, rot.rotate(state.get(ROTATION), 16));
	}


	@Override
	public BlockState mirror(BlockState state, Mirror mirror)
	{
		return state.with(ROTATION, mirror.mirrorRotation(state.get(ROTATION), 16));
	}

	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder)
	{
		builder.add(ROTATION, WATERLOGGED);
	}
}