package net.geforcemods.securitycraft.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockSecretSignWall extends BlockSecretSign
{
	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);

	public BlockSecretSignWall()
	{
		setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, BlockPos pos)
	{
		EnumFacing facing = world.getBlockState(pos).getValue(FACING);
		float fourthAndHalf = 0.28125F;
		float twelfthAndHalf = 0.78125F;
		float nothing = 0.0F;
		float full = 1.0F;
		float eigth = 0.125F;
		setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);

		switch (facing)
		{
			case NORTH:
				setBlockBounds(nothing, fourthAndHalf, 1.0F - eigth, full, twelfthAndHalf, 1.0F);
				break;
			case SOUTH:
				setBlockBounds(nothing, fourthAndHalf, 0.0F, full, twelfthAndHalf, eigth);
				break;
			case WEST:
				setBlockBounds(1.0F - eigth, fourthAndHalf, nothing, 1.0F, twelfthAndHalf, full);
				break;
			case EAST:
				setBlockBounds(0.0F, fourthAndHalf, nothing, eigth, twelfthAndHalf, full);
				break;
			case UP: case DOWN: break;
		}
	}

	@Override
	public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block neighborBlock)
	{
		EnumFacing facing = state.getValue(FACING);

		if (!world.getBlockState(pos.offset(facing.getOpposite())).getBlock().getMaterial().isSolid())
		{
			dropBlockAsItem(world, pos, state, 0);
			world.setBlockToAir(pos);
		}

		super.onNeighborBlockChange(world, pos, state, neighborBlock);
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		EnumFacing facing = EnumFacing.getFront(meta);

		if (facing.getAxis() == EnumFacing.Axis.Y)
		{
			facing = EnumFacing.NORTH;
		}

		return getDefaultState().withProperty(FACING, facing);
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return state.getValue(FACING).getIndex();
	}

	@Override
	protected BlockState createBlockState()
	{
		return new BlockState(this, new IProperty[] {FACING});
	}
}