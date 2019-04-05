package net.geforcemods.securitycraft.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStandingSign;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

public class BlockSecretSignStanding extends BlockSecretSign
{
	public static final IntegerProperty ROTATION = BlockStandingSign.ROTATION;

	public BlockSecretSignStanding()
	{
		super();
		setDefaultState(stateContainer.getBaseState().with(ROTATION, 0).with(WATERLOGGED, false));
	}

	@Override
	public boolean isValidPosition(IBlockState state, IWorldReaderBase world, BlockPos pos)
	{
		return world.getBlockState(pos.down()).getMaterial().isSolid();
	}

	@Override
	public IBlockState getStateForPlacement(BlockItemUseContext ctx)
	{
		return getDefaultState().with(ROTATION, MathHelper.floor((180.0F + ctx.getPlacementYaw()) * 16.0F / 360.0F + 0.5D) & 15).with(WATERLOGGED, ctx.getWorld().getFluidState(ctx.getPos()).getFluid() == Fluids.WATER);
	}

	@Override
	public IBlockState updatePostPlacement(IBlockState state, EnumFacing facing, IBlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos)
	{
		return facing == EnumFacing.DOWN && !isValidPosition(state, worldIn, currentPos) ? Blocks.AIR.getDefaultState() : super.updatePostPlacement(state, facing, facingState, worldIn, currentPos, facingPos);
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos)
	{
		if(!world.getBlockState(pos.down()).getMaterial().isSolid())
		{
			dropBlockAsItemWithChance(state, world, pos, 1.0F, 0);
			world.removeBlock(pos);
		}

		super.neighborChanged(state, world, pos, block, fromPos);
	}

	@Override
	public IBlockState rotate(IBlockState state, Rotation rot)
	{
		return state.with(ROTATION, rot.rotate(state.get(ROTATION), 16));
	}


	@Override
	public IBlockState mirror(IBlockState state, Mirror mirror)
	{
		return state.with(ROTATION, mirror.mirrorRotation(state.get(ROTATION), 16));
	}

	@Override
	protected void fillStateContainer(Builder<Block, IBlockState> builder)
	{
		builder.add(ROTATION, WATERLOGGED);
	}
}