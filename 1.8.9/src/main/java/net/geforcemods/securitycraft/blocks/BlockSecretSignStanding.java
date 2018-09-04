package net.geforcemods.securitycraft.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class BlockSecretSignStanding extends BlockSecretSign
{
	public static final PropertyInteger ROTATION = PropertyInteger.create("rotation", 0, 15);

	public BlockSecretSignStanding()
	{
		setDefaultState(blockState.getBaseState().withProperty(ROTATION, Integer.valueOf(0)));
	}

	@Override
	public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block neighborBlock)
	{
		if (!world.getBlockState(pos.down()).getBlock().getMaterial().isSolid())
		{
			dropBlockAsItem(world, pos, state, 0);
			world.setBlockToAir(pos);
		}

		super.onNeighborBlockChange(world, pos, state, neighborBlock);
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return getDefaultState().withProperty(ROTATION, Integer.valueOf(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return state.getValue(ROTATION).intValue();
	}

	@Override
	protected BlockState createBlockState()
	{
		return new BlockState(this, new IProperty[] {ROTATION});
	}
}