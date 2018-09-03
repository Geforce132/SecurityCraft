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
	public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos)
	{
		EnumFacing facing = (EnumFacing)worldIn.getBlockState(pos).getValue(FACING);
		float fourthAndHalf = 0.28125F;
		float twelfthAndHalf = 0.78125F;
		float nothing = 0.0F;
		float full = 1.0F;
		float eigth = 0.125F;
		setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);

		switch (BlockSecretSignWall.SwitchEnumFacing.FACING_LOOKUP[facing.ordinal()])
		{
			case 1:
				setBlockBounds(nothing, fourthAndHalf, 1.0F - eigth, full, twelfthAndHalf, 1.0F);
				break;
			case 2:
				setBlockBounds(nothing, fourthAndHalf, 0.0F, full, twelfthAndHalf, eigth);
				break;
			case 3:
				setBlockBounds(1.0F - eigth, fourthAndHalf, nothing, 1.0F, twelfthAndHalf, full);
				break;
			case 4:
				setBlockBounds(0.0F, fourthAndHalf, nothing, eigth, twelfthAndHalf, full);
		}
	}

	@Override
	public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block neighborBlock)
	{
		EnumFacing enumfacing = (EnumFacing)state.getValue(FACING);

		if (!world.getBlockState(pos.offset(enumfacing.getOpposite())).getBlock().getMaterial().isSolid())
		{
			dropBlockAsItem(world, pos, state, 0);
			world.setBlockToAir(pos);
		}

		super.onNeighborBlockChange(world, pos, state, neighborBlock);
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		EnumFacing enumfacing = EnumFacing.getFront(meta);

		if (enumfacing.getAxis() == EnumFacing.Axis.Y)
		{
			enumfacing = EnumFacing.NORTH;
		}

		return getDefaultState().withProperty(FACING, enumfacing);
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return ((EnumFacing)state.getValue(FACING)).getIndex();
	}

	@Override
	protected BlockState createBlockState()
	{
		return new BlockState(this, new IProperty[] {FACING});
	}

	static final class SwitchEnumFacing
	{
		static final int[] FACING_LOOKUP = new int[EnumFacing.values().length];

		static
		{
			try
			{
				FACING_LOOKUP[EnumFacing.NORTH.ordinal()] = 1;
			}
			catch (NoSuchFieldError var4)
			{
				;
			}

			try
			{
				FACING_LOOKUP[EnumFacing.SOUTH.ordinal()] = 2;
			}
			catch (NoSuchFieldError var3)
			{
				;
			}

			try
			{
				FACING_LOOKUP[EnumFacing.WEST.ordinal()] = 3;
			}
			catch (NoSuchFieldError var2)
			{
				;
			}

			try
			{
				FACING_LOOKUP[EnumFacing.EAST.ordinal()] = 4;
			}
			catch (NoSuchFieldError var1)
			{
				;
			}
		}
	}
}