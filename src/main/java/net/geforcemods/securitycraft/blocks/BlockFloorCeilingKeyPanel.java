package net.geforcemods.securitycraft.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockFloorCeilingKeyPanel extends BlockKeyPanel
{
	public static final PropertyBool FLOOR = PropertyBool.create("floor");
	public static final AxisAlignedBB FLOOR_NS;
	public static final AxisAlignedBB FLOOR_EW;
	public static final AxisAlignedBB CEILING_NS;
	public static final AxisAlignedBB CEILING_EW;

	static {
		float px = 1.0F / 16.0F;

		FLOOR_NS = new AxisAlignedBB(2 * px, 0 * px, 1 * px, 14 * px, 1 * px, 15 * px);
		FLOOR_EW = new AxisAlignedBB(1 * px, 0 * px, 2 * px, 15 * px, 1 * px, 14 * px);
		CEILING_NS = new AxisAlignedBB(2 * px, 15 * px, 1 * px, 14 * px, 16 * px, 15 * px);
		CEILING_EW = new AxisAlignedBB(1 * px, 15 * px, 2 * px, 15 * px, 16 * px, 14 * px);
	}

	public BlockFloorCeilingKeyPanel(Material material)
	{
		super(material);
		setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(POWERED, false).withProperty(FLOOR, true));
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		if(state.getValue(FLOOR))
		{
			switch(state.getValue(FACING))
			{
				case NORTH: return FLOOR_NS;
				case EAST: return FLOOR_EW;
				case SOUTH: return FLOOR_NS;
				case WEST: return FLOOR_EW;
				default: return NULL_AABB;
			}
		}
		else
		{
			switch(state.getValue(FACING))
			{
				case NORTH: return CEILING_NS;
				case EAST: return CEILING_EW;
				case SOUTH: return CEILING_NS;
				case WEST: return CEILING_EW;
				default: return NULL_AABB;
			}
		}
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand)
	{
		return getDefaultState().withProperty(FACING, placer == null ? EnumFacing.NORTH : placer.getHorizontalFacing()).withProperty(POWERED, false).withProperty(FLOOR, facing == EnumFacing.UP);
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return getDefaultState().withProperty(FACING, EnumFacing.byHorizontalIndex(meta & 0b0011)).withProperty(POWERED, (meta & 0b0100) == 0b0100).withProperty(FLOOR, (meta & 0b1000) == 0b1000);
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		int meta = state.getValue(FACING).getHorizontalIndex();

		if(state.getValue(POWERED))
			meta |= 0b0100;

		if(state.getValue(FLOOR))
			meta |= 0b1000;

		return meta;
	}

	@Override
	protected EnumFacing getConnectedDirection(IBlockState state)
	{
		return state.getValue(FLOOR) ? EnumFacing.UP : EnumFacing.DOWN;
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, FACING, POWERED, FLOOR);
	}
}
