package net.geforcemods.securitycraft.blocks.reinforced;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockReinforcedBoneBlock extends BlockReinforcedBase
{
	public static final PropertyEnum<EnumFacing.Axis> AXIS = PropertyEnum.<EnumFacing.Axis>create("axis", EnumFacing.Axis.class);

	public BlockReinforcedBoneBlock(Material mat)
	{
		super(mat, 1, Blocks.BONE_BLOCK);
	}

	@Override
	public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis)
	{
		IBlockState state = world.getBlockState(pos);
		for (IProperty<?> prop : state.getProperties().keySet())
		{
			if (prop.getName().equals("axis"))
			{
				world.setBlockState(pos, state.cycleProperty(prop));
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed
	 * blockstate.
	 */
	public IBlockState withRotation(IBlockState state, Rotation rot)
	{
		switch (rot)
		{
			case COUNTERCLOCKWISE_90:
			case CLOCKWISE_90:

				switch (state.getValue(AXIS))
				{
					case X:
						return state.withProperty(AXIS, EnumFacing.Axis.Z);
					case Z:
						return state.withProperty(AXIS, EnumFacing.Axis.X);
					default:
						return state;
				}

			default:
				return state;
		}
	}

	/**
	 * Convert the given metadata into a BlockState for this Block
	 */
	public IBlockState getStateFromMeta(int meta)
	{
		EnumFacing.Axis axis = EnumFacing.Axis.Y;
		int axisMeta = meta & 12;

		if (axisMeta == 4)
		{
			axis = EnumFacing.Axis.X;
		}
		else if (axisMeta == 8)
		{
			axis = EnumFacing.Axis.Z;
		}

		return this.getDefaultState().withProperty(AXIS, axis);
	}

	/**
	 * Convert the BlockState into the correct metadata value
	 */
	public int getMetaFromState(IBlockState state)
	{
		int meta = 0;
		EnumFacing.Axis axis = state.getValue(AXIS);

		if (axis == EnumFacing.Axis.X)
		{
			meta |= 4;
		}
		else if (axis == EnumFacing.Axis.Z)
		{
			meta |= 8;
		}

		return meta;
	}

	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, new IProperty[] {AXIS});
	}

	protected ItemStack getSilkTouchDrop(IBlockState state)
	{
		return new ItemStack(Item.getItemFromBlock(this));
	}

	/**
	 * Called by ItemBlocks just before a block is actually set in the world, to allow for adjustments to the
	 * IBlockstate
	 */
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
	{
		return super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer).withProperty(AXIS, facing.getAxis());
	}
}
