package net.geforcemods.securitycraft.blocks;

import java.util.Arrays;
import java.util.List;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IDoorActivator;
import net.geforcemods.securitycraft.blockentities.SecureRedstoneInterfaceBlockEntity;
import net.geforcemods.securitycraft.network.client.OpenScreen;
import net.geforcemods.securitycraft.network.client.OpenScreen.DataType;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class SecureRedstoneInterfaceBlock extends DisguisableBlock {
	public static final PropertyBool SENDER = PropertyBool.create("sender");

	public SecureRedstoneInterfaceBlock(Material material) {
		super(material);
		setDefaultState(blockState.getBaseState().withProperty(SENDER, false));
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
		TileEntity te = level.getTileEntity(pos);

		if (te instanceof SecureRedstoneInterfaceBlockEntity) {
			SecureRedstoneInterfaceBlockEntity be = (SecureRedstoneInterfaceBlockEntity) te;

			if (!be.isSender() && !be.isDisabled())
				return be.getPower();
		}

		return 0;
	}

	@Override
	public int getWeakPower(IBlockState state, IBlockAccess level, BlockPos pos, EnumFacing side) {
		return getStrongPower(state, level, pos, side);
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
				BlockUtils.updateIndirectNeighbors(level, pos, this);
		}

		super.breakBlock(level, pos, state);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(SENDER) ? 1 : 0;
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(SENDER, meta == 1);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new SecureRedstoneInterfaceBlockEntity();
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, SENDER);
	}

	public static class DoorActivator implements IDoorActivator {
		private final List<Block> blocks = Arrays.asList(SCContent.secureRedstoneInterface);

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
