package net.geforcemods.securitycraft.blocks;

import java.util.List;

import net.geforcemods.securitycraft.blockentities.DisguisableBlockEntity;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class SometimesVisibleBlock extends DisguisableBlock {
	public static final PropertyBool INVISIBLE = PropertyBool.create("invisible");
	public static final AxisAlignedBB EMPTY_AABB = new AxisAlignedBB(0, 0, 0, 0, 0, 0);

	public SometimesVisibleBlock(Material material) {
		super(material);
		setDefaultState(blockState.getBaseState().withProperty(INVISIBLE, true));
		destroyTimeForOwner = -1.0F;
	}

	@Override
	public EnumPushReaction getPushReaction(IBlockState state) {
		return EnumPushReaction.BLOCK;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
		return state.getValue(INVISIBLE) ? EMPTY_AABB : super.getBoundingBox(state, world, pos);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
		return state.getValue(INVISIBLE) ? getCollisionShapeWhenInvisible() : super.getCollisionBoundingBox(state, world, pos);
	}

	@Override
	public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World world, BlockPos pos) {
		return state.getValue(INVISIBLE) ? FULL_BLOCK_AABB : super.getSelectedBoundingBox(state, world, pos);
	}

	@Override
	public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, Entity entity, boolean isActualState) {
		if (state.getValue(INVISIBLE))
			addCollisionBoxToList(pos, entityBox, collidingBoxes, getCollisionShapeWhenInvisible());
		else
			super.addCollisionBoxToList(state, world, pos, entityBox, collidingBoxes, entity, isActualState);
	}

	public AxisAlignedBB getCollisionShapeWhenInvisible() {
		return FULL_BLOCK_AABB;
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(INVISIBLE) ? 1 : 0;
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(INVISIBLE, meta == 1);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new DisguisableBlockEntity();
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, INVISIBLE);
	}
}
