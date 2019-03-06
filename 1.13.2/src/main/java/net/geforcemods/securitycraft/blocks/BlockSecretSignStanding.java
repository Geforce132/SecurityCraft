package net.geforcemods.securitycraft.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockSecretSignStanding extends BlockSecretSign
{
	public static final IntegerProperty ROTATION = BlockStateProperties.ROTATION_0_15;

	public BlockSecretSignStanding()
	{
		super();
		setDefaultState(stateContainer.getBaseState().with(ROTATION, 0));
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos)
	{
		if (!world.getBlockState(pos.down()).getMaterial().isSolid())
		{
			dropBlockAsItemWithChance(state, world, pos, 1.0F, 0);
			world.removeBlock(pos);
		}

		super.neighborChanged(state, world, pos, block, fromPos);
	}

	@Override
	public IBlockState rotate(IBlockState state, Rotation rot) {
		return state.with(ROTATION, rot.rotate(state.get(ROTATION), 16));
	}


	@Override
	public IBlockState mirror(IBlockState state, Mirror mirror) {
		return state.with(ROTATION, mirror.mirrorRotation(state.get(ROTATION), 16));
	}

	@Override
	protected void fillStateContainer(Builder<Block, IBlockState> builder)
	{
		builder.add(ROTATION);
	}
}