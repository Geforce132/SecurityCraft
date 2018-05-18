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
		EnumFacing enumfacing = (EnumFacing)worldIn.getBlockState(pos).getValue(FACING);
		float f = 0.28125F;
		float f1 = 0.78125F;
		float f2 = 0.0F;
		float f3 = 1.0F;
		float f4 = 0.125F;
		setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);

		switch (BlockSecretSignWall.SwitchEnumFacing.FACING_LOOKUP[enumfacing.ordinal()])
		{
			case 1:
				setBlockBounds(f2, f, 1.0F - f4, f3, f1, 1.0F);
				break;
			case 2:
				setBlockBounds(f2, f, 0.0F, f3, f1, f4);
				break;
			case 3:
				setBlockBounds(1.0F - f4, f, f2, 1.0F, f1, f3);
				break;
			case 4:
				setBlockBounds(0.0F, f, f2, f4, f1, f3);
		}
	}

	@Override
	public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock)
	{
		EnumFacing enumfacing = (EnumFacing)state.getValue(FACING);

		if (!worldIn.getBlockState(pos.offset(enumfacing.getOpposite())).getBlock().getMaterial().isSolid())
		{
			dropBlockAsItem(worldIn, pos, state, 0);
			worldIn.setBlockToAir(pos);
		}

		super.onNeighborBlockChange(worldIn, pos, state, neighborBlock);
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