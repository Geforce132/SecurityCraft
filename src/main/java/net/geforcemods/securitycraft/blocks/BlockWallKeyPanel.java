package net.geforcemods.securitycraft.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockWallKeyPanel extends BlockKeyPanel
{
	public static final AxisAlignedBB WALL_N;
	public static final AxisAlignedBB WALL_E;
	public static final AxisAlignedBB WALL_S;
	public static final AxisAlignedBB WALL_W;

	static {
		float px = 1.0F / 16.0F;

		WALL_N = new AxisAlignedBB(2 * px, 1 * px, 15 * px, 14 * px, 15 * px, 16 * px);
		WALL_E = new AxisAlignedBB(0 * px, 1 * px, 2 * px, 1 * px, 15 * px, 14 * px);
		WALL_S = new AxisAlignedBB(2 * px, 1 * px, 0 * px, 14 * px, 15 * px, 1 * px);
		WALL_W = new AxisAlignedBB(15 * px, 1 * px, 2 * px, 16 * px, 15 * px, 14 * px);
	}

	public BlockWallKeyPanel(Material material)
	{
		super(material);
		setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(POWERED, false));
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		switch(state.getValue(FACING))
		{
			case NORTH: return WALL_N;
			case EAST: return WALL_E;
			case SOUTH: return WALL_S;
			case WEST: return WALL_W;
			default: return NULL_AABB;
		}
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand)
	{
		return getDefaultState().withProperty(FACING, facing).withProperty(POWERED, false);
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return getDefaultState().withProperty(FACING, EnumFacing.byHorizontalIndex(meta & 0b0011)).withProperty(POWERED, (meta & 0b0100) == 0b0100);
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		int meta = state.getValue(FACING).getHorizontalIndex();

		return state.getValue(POWERED) ? meta | 0b0100 : meta;
	}

	@Override
	protected EnumFacing getConnectedDirection(IBlockState state)
	{
		return state.getValue(FACING);
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, FACING, POWERED);
	}
}
