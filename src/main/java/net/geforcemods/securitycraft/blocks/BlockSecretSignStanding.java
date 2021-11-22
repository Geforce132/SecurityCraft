package net.geforcemods.securitycraft.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockSecretSignStanding extends BlockSecretSign
{
	public static final PropertyInteger ROTATION = PropertyInteger.create("rotation", 0, 15);

	public BlockSecretSignStanding()
	{
		setDefaultState(blockState.getBaseState().withProperty(ROTATION, 0));
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos)
	{
		if (!world.getBlockState(pos.down()).getMaterial().isSolid())
		{
			dropBlockAsItem(world, pos, state, 0);
			world.setBlockToAir(pos);
		}

		super.neighborChanged(state, world, pos, block, fromPos);
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return getDefaultState().withProperty(ROTATION, meta);
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return state.getValue(ROTATION);
	}

	@Override
	public IBlockState withRotation(IBlockState state, Rotation rot)
	{
		return state.withProperty(ROTATION, rot.rotate(state.getValue(ROTATION), 16));
	}

	@Override
	public IBlockState withMirror(IBlockState state, Mirror mirror)
	{
		return state.withProperty(ROTATION, mirror.mirrorRotation(state.getValue(ROTATION), 16));
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, ROTATION);
	}
}