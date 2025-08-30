package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.blockentities.MotionActivatedLightBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class MotionActivatedLightBlock extends OwnableBlock {
	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	public static final PropertyBool LIT = PropertyBool.create("lit");

	public MotionActivatedLightBlock(Material material) {
		super(material);
		setSoundType(SoundType.GLASS);
		setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
		destroyTimeForOwner = 5.0F;
		setHarvestLevel("pickaxe", 1);
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isNormalCube(IBlockState state) {
		return false;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
		EnumFacing dir = state.getValue(FACING);
		float px = 1.0F / 16.0F;

		if (dir == EnumFacing.NORTH)
			return new AxisAlignedBB(px * 5, px * 3, 0F, px * 11, px * 10, px * 3);
		else if (dir == EnumFacing.SOUTH)
			return new AxisAlignedBB(px * 5, px * 3, 1F, px * 11, px * 10, 1F - (px * 3));
		else if (dir == EnumFacing.EAST)
			return new AxisAlignedBB(1F, px * 3, px * 5, 1F - (px * 3), px * 10, px * 11);
		else if (dir == EnumFacing.WEST)
			return new AxisAlignedBB(0F, px * 3, px * 5, px * 3, px * 10, px * 11);

		return new AxisAlignedBB(px * 6, px * 3, 0F, px * 10, px * 9, px * 3);
	}

	@Override
	public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
		return state.getValue(LIT) ? 15 : 0;
	}

	@Override
	public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side) {
		if (side == EnumFacing.UP || side == EnumFacing.DOWN)
			return false;

		return world.isSideSolid(pos.offset(side.getOpposite()), side);
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
		if (!canPlaceBlockOnSide(world, pos, state.getValue(FACING).getOpposite())) {
			dropBlockAsItem(world, pos, state, 0);
			world.setBlockToAir(pos);
		}
	}

	@Override
	public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
		//prevents dropping twice the amount of modules when breaking the block in creative mode
		if (player.isCreative()) {
			TileEntity te = world.getTileEntity(pos);

			if (te instanceof IModuleInventory)
				((IModuleInventory) te).getInventory().clear();
		}

		super.onBlockHarvested(world, pos, state, player);
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		TileEntity te = world.getTileEntity(pos);

		if (!ConfigHandler.vanillaToolBlockBreaking && te instanceof IModuleInventory)
			((IModuleInventory) te).dropAllModules();

		super.breakBlock(world, pos, state);
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		return getDefaultState().withProperty(FACING, facing.getOpposite()).withProperty(LIT, false);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		if (meta == 15)
			return getDefaultState();

		if (meta <= 5)
			return getDefaultState().withProperty(FACING, EnumFacing.values()[meta].getAxis() == EnumFacing.Axis.Y ? EnumFacing.NORTH : EnumFacing.values()[meta]).withProperty(LIT, false);
		else
			return getDefaultState().withProperty(FACING, EnumFacing.values()[meta - 6]).withProperty(LIT, true);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		if (state.getProperties().containsKey(LIT) && state.getValue(LIT))
			return (state.getValue(FACING).getIndex() + 6);
		else {
			if (!state.getProperties().containsKey(FACING))
				return 15;

			return state.getValue(FACING).getIndex();
		}
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING, LIT);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new MotionActivatedLightBlockEntity();
	}

	@Override
	public IBlockState withRotation(IBlockState state, Rotation rot) {
		return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
	}

	@Override
	public IBlockState withMirror(IBlockState state, Mirror mirror) {
		EnumFacing facing = state.getValue(FACING);

		switch (mirror) {
			case LEFT_RIGHT:
				if (facing.getAxis() == Axis.Z)
					return state.withProperty(FACING, facing.getOpposite());
				break;
			case FRONT_BACK:
				if (facing.getAxis() == Axis.X)
					return state.withProperty(FACING, facing.getOpposite());
				break;
			case NONE:
				break;
		}

		return state;
	}
}
