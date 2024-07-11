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

public class BlockChangeDetectorFloorCeilingBlock extends BlockChangeDetectorBlock {
	public static final PropertyBool FLOOR = PropertyBool.create("floor");
	public static final AxisAlignedBB FLOOR_SHAPE = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 9.0D / 16.0D, 1.0D);
	public static final AxisAlignedBB CEILING_SHAPE = new AxisAlignedBB(0.0D, 7.0D / 16.0D, 0.0D, 1.0D, 1.0D, 1.0D);

	public BlockChangeDetectorFloorCeilingBlock(Material material) {
		super(material);
		setDefaultState(getDefaultState().withProperty(FLOOR, true));
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
		return state.getValue(FLOOR) ? FLOOR_SHAPE : CEILING_SHAPE;
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		return getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite()).withProperty(FLOOR, facing == EnumFacing.UP);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		int meta = state.getValue(FACING).getHorizontalIndex();

		if (state.getValue(POWERED))
			meta |= 0b0100;

		if (state.getValue(FLOOR))
			meta |= 0b1000;

		return meta;
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(FACING, EnumFacing.byHorizontalIndex(meta & 0b0011)).withProperty(POWERED, (meta & 0b0100) == 0b0000).withProperty(FLOOR, (meta & 0b1000) == 0b0000);
	}

	@Override
	public EnumFacing getConnectedDirection(IBlockState state) {
		return state.getValue(FLOOR) ? EnumFacing.UP : EnumFacing.DOWN;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING, POWERED, FLOOR);
	}
}
