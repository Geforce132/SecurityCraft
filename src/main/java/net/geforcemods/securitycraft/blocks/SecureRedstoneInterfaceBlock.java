package net.geforcemods.securitycraft.blocks;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IDoorActivator;
import net.geforcemods.securitycraft.blockentities.SecureRedstoneInterfaceBlockEntity;
import net.geforcemods.securitycraft.network.client.OpenScreen;
import net.geforcemods.securitycraft.network.client.OpenScreen.DataType;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class SecureRedstoneInterfaceBlock extends DisguisableBlock {
	public static final PropertyBool SENDER = PropertyBool.create("sender");
	public static final PropertyDirection FACING = PropertyDirection.create("facing");
	private static final AxisAlignedBB[] SHAPES = {
			makeShape(0.0D, 9.0D, 0.0D, 16.0D, 16.0D, 16.0D), //down
			makeShape(0.0D, 0.0D, 0.0D, 16.0D, 7.0D, 16.0D), //up
			makeShape(0.0D, 0.0D, 9.0D, 16.0D, 16.0D, 16.0D), //north
			makeShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 7.0D), //south
			makeShape(9.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D), //west
			makeShape(0.0D, 0.0D, 0.0D, 7.0D, 16.0D, 16.0D) //east
	};

	private static AxisAlignedBB makeShape(double x1, double y1, double z1, double x2, double y2, double z2) {
		return new AxisAlignedBB(x1 / 16.0D, y1 / 16.0D, z1 / 16.0D, x2 / 16.0D, y2 / 16.0D, z2 / 16.0D);
	}

	public SecureRedstoneInterfaceBlock(Material material) {
		super(material);
		setDefaultState(blockState.getBaseState().withProperty(SENDER, true).withProperty(FACING, EnumFacing.UP));
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
		return SHAPES[state.getValue(FACING).getIndex()];
	}

	@Override
	public boolean onBlockActivated(World level, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		TileEntity te = level.getTileEntity(pos);

		if (te instanceof SecureRedstoneInterfaceBlockEntity) {
			SecureRedstoneInterfaceBlockEntity be = (SecureRedstoneInterfaceBlockEntity) te;

			if (be.isOwnedBy(player)) {
				if (!level.isRemote) {
					if (be.isDisabled())
						player.sendStatusMessage(Utils.localize("gui.securitycraft:scManual.disabled"), true);
					else if (be.getOwner().isValidated())
						SecurityCraft.network.sendTo(new OpenScreen(DataType.SECURE_REDSTONE_INTERFACE, pos), (EntityPlayerMP) player);
					else
						PlayerUtils.sendMessageToPlayer(player, Utils.localize(this), Utils.localize("messages.securitycraft:universalOwnerChanger.ownerInvalidated"), TextFormatting.RED);
				}

				return true;
			}
		}

		return false;
	}

	@Override
	public boolean canProvidePower(IBlockState state) {
		return !state.getValue(SENDER);
	}

	@Override
	public void neighborChanged(IBlockState state, World level, BlockPos pos, Block block, BlockPos fromPos) {
		TileEntity te = level.getTileEntity(pos);

		if (te instanceof SecureRedstoneInterfaceBlockEntity) {
			SecureRedstoneInterfaceBlockEntity be = (SecureRedstoneInterfaceBlockEntity) te;

			if (be.isSender() && !be.isDisabled())
				be.refreshPower();
		}
	}

	@Override
	public int getStrongPower(IBlockState state, IBlockAccess level, BlockPos pos, EnumFacing direction) {
		if (state.getValue(FACING) == direction)
			return getWeakPower(state, level, pos, direction);
		else
			return 0;
	}

	@Override
	public int getWeakPower(IBlockState state, IBlockAccess level, BlockPos pos, EnumFacing side) {
		TileEntity te = level.getTileEntity(pos);

		if (te instanceof SecureRedstoneInterfaceBlockEntity)
			return ((SecureRedstoneInterfaceBlockEntity) te).getRedstonePowerOutput();
		else
			return 0;
	}

	@Override
	public void breakBlock(World level, BlockPos pos, IBlockState state) {
		TileEntity te = level.getTileEntity(pos);

		if (te instanceof SecureRedstoneInterfaceBlockEntity) {
			SecureRedstoneInterfaceBlockEntity be = (SecureRedstoneInterfaceBlockEntity) te;

			be.disabled.setValue(true); //make sure receivers that update themselves don't check for this one

			if (be.isSender())
				be.tellSimilarReceiversToRefresh();
			else
				be.updateNeighbors(state);
		}

		super.breakBlock(level, pos, state);
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing clickedFace, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		return getDefaultState().withProperty(FACING, clickedFace);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).getIndex() + (state.getValue(SENDER) ? 6 : 0);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		boolean isSender = meta >= 6;

		return getDefaultState().withProperty(SENDER, isSender).withProperty(FACING, EnumFacing.byIndex(Math.min(isSender ? meta - 6 : meta, 6)));
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new SecureRedstoneInterfaceBlockEntity();
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, SENDER, FACING);
	}

	public static class DoorActivator implements Function<Object, IDoorActivator>, IDoorActivator {
		private final List<Block> blocks = Arrays.asList(SCContent.secureRedstoneInterface);

		@Override
		public IDoorActivator apply(Object o) {
			return this;
		}

		@Override
		public boolean isPowering(World level, BlockPos pos, IBlockState state, TileEntity be, EnumFacing direction, int distance) {
			return !state.getValue(SENDER) && be instanceof SecureRedstoneInterfaceBlockEntity && ((SecureRedstoneInterfaceBlockEntity) be).isProtectedSignal() && ((SecureRedstoneInterfaceBlockEntity) be).getPower() > 0;
		}

		@Override
		public List<Block> getBlocks() {
			return blocks;
		}
	}
}
