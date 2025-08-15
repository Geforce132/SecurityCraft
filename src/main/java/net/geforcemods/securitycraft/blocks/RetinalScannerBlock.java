package net.geforcemods.securitycraft.blocks;

import java.util.Random;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blockentities.RetinalScannerBlockEntity;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class RetinalScannerBlock extends DisguisableBlock {
	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	public static final PropertyBool POWERED = PropertyBool.create("powered");

	public RetinalScannerBlock(Material material) {
		super(material);
		destroyTimeForOwner = 3.5F;
		setHarvestLevel("pickaxe", 0);
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entity, ItemStack stack) {
		super.onBlockPlacedBy(world, pos, state, entity, stack);

		IBlockState north = world.getBlockState(pos.north());
		IBlockState south = world.getBlockState(pos.south());
		IBlockState west = world.getBlockState(pos.west());
		IBlockState east = world.getBlockState(pos.east());
		EnumFacing facing = state.getValue(FACING);

		if (facing == EnumFacing.NORTH && north.isFullBlock() && !south.isFullBlock())
			facing = EnumFacing.SOUTH;
		else if (facing == EnumFacing.SOUTH && south.isFullBlock() && !north.isFullBlock())
			facing = EnumFacing.NORTH;
		else if (facing == EnumFacing.WEST && west.isFullBlock() && !east.isFullBlock())
			facing = EnumFacing.EAST;
		else if (facing == EnumFacing.EAST && east.isFullBlock() && !west.isFullBlock())
			facing = EnumFacing.WEST;

		world.setBlockState(pos, state.withProperty(FACING, facing), 2);

		if (entity instanceof EntityPlayer) {
			TileEntity tileentity = world.getTileEntity(pos);

			if (!world.isRemote && tileentity instanceof RetinalScannerBlockEntity)
				((RetinalScannerBlockEntity) tileentity).setPlayerProfile(((EntityPlayer) entity).getGameProfile());
		}
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random random) {
		if (!world.isRemote && state.getValue(POWERED)) {
			world.setBlockState(pos, state.withProperty(POWERED, false));
			BlockUtils.updateIndirectNeighbors(world, pos, SCContent.retinalScanner);
		}
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		if (state.getValue(POWERED)) {
			world.notifyNeighborsOfStateChange(pos, this, false);
			BlockUtils.updateIndirectNeighbors(world, pos, this);
		}

		super.breakBlock(world, pos, state);
	}

	@Override
	public boolean canProvidePower(IBlockState state) {
		return true;
	}

	@Override
	public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		if (blockState.getValue(POWERED))
			return 15;
		else
			return 0;
	}

	@Override
	public int getStrongPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		return state.getValue(POWERED) ? 15 : 0;
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		return getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite()).withProperty(POWERED, false);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		if (meta <= 5)
			return getDefaultState().withProperty(FACING, EnumFacing.values()[meta].getAxis() == EnumFacing.Axis.Y ? EnumFacing.NORTH : EnumFacing.values()[meta]).withProperty(POWERED, false);
		else
			return getDefaultState().withProperty(FACING, EnumFacing.values()[meta - 6]).withProperty(POWERED, true);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		if (state.getProperties().containsKey(POWERED) && state.getValue(POWERED))
			return (state.getValue(FACING).getIndex() + 6);
		else if (state.getProperties().containsKey(FACING))
			return state.getValue(FACING).getIndex();
		else
			return 0;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING, POWERED);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new RetinalScannerBlockEntity();
	}

	@Override
	public IBlockState withRotation(IBlockState state, Rotation rot) {
		return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
	}

	@Override
	public IBlockState withMirror(IBlockState state, Mirror mirror) {
		return state.withRotation(mirror.toRotation(state.getValue(FACING)));
	}
}
