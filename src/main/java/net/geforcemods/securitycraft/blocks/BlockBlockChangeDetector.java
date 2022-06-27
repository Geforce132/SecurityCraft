package net.geforcemods.securitycraft.blocks;

import java.util.Random;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.gui.GuiHandler;
import net.geforcemods.securitycraft.tileentity.TileEntityBlockChangeDetector;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.geforcemods.securitycraft.util.WorldUtils;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockBlockChangeDetector extends BlockDisguisable {
	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	public static final PropertyBool POWERED = PropertyBool.create("powered");
	public static final AxisAlignedBB SHAPE = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 9.0D / 16.0D, 1.0D);

	public BlockBlockChangeDetector(Material material) {
		super(material);
		setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(POWERED, false));
	}

	@Override
	public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) {
		return false;
	}

	@Override
	public boolean isFullBlock(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
		return SHAPE;
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		TileEntity tile = world.getTileEntity(pos);

		if (!(tile instanceof TileEntityBlockChangeDetector))
			return false;

		TileEntityBlockChangeDetector te = (TileEntityBlockChangeDetector) tile;

		if (te.isDisabled())
			player.sendStatusMessage(Utils.localize("gui.securitycraft:scManual.disabled"), true);
		else if (te.getOwner().isOwner(player) || ModuleUtils.isAllowed(te, player)) {
			player.openGui(SecurityCraft.instance, GuiHandler.BLOCK_CHANGE_DETECTOR, world, pos.getX(), pos.getY(), pos.getZ());
			return true;
		}

		return false;
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		TileEntity te = world.getTileEntity(pos);

		if (te instanceof TileEntityBlockChangeDetector) {
			EntityItem item = new EntityItem(world, pos.getX(), pos.getY(), pos.getZ(), ((TileEntityBlockChangeDetector) te).getStackInSlot(36));

			WorldUtils.addScheduledTask(world, () -> world.spawnEntity(item));
		}

		super.breakBlock(world, pos, state);
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
		world.setBlockState(pos, state.withProperty(POWERED, false));
		BlockUtils.updateIndirectNeighbors(world, pos, this);
	}

	@Override
	public boolean canProvidePower(IBlockState state) {
		return true;
	}

	@Override
	public boolean shouldCheckWeakPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		return false;
	}

	@Override
	public int getWeakPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		return state.getValue(POWERED) ? 15 : 0;
	}

	@Override
	public int getStrongPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		return state.getValue(POWERED) ? 15 : 0;
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		return getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).getHorizontalIndex() + (state.getValue(POWERED) ? 4 : 0);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		boolean powered = meta >= 4;

		if (powered)
			meta -= 4;

		return getDefaultState().withProperty(FACING, EnumFacing.byHorizontalIndex(meta));
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING, POWERED);
	}

	@Override
	public IBlockState withRotation(IBlockState state, Rotation rot) {
		return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
	}

	@Override
	public IBlockState withMirror(IBlockState state, Mirror mirror) {
		return state.withRotation(mirror.toRotation(state.getValue(FACING)));
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityBlockChangeDetector();
	}
}
