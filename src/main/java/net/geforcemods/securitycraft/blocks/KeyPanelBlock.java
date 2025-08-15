package net.geforcemods.securitycraft.blocks;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IPasscodeProtected;
import net.geforcemods.securitycraft.blockentities.KeyPanelBlockEntity;
import net.geforcemods.securitycraft.misc.SaltData;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class KeyPanelBlock extends OwnableBlock {
	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	public static final PropertyBool POWERED = PropertyBool.create("powered");

	protected KeyPanelBlock(Material material) {
		super(material);
		setSoundType(SoundType.METAL);
		destroyTimeForOwner = 3.5F;
		setHarvestLevel("pickaxe", 1);
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) {
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
		return NULL_AABB;
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		KeyPanelBlockEntity be = (KeyPanelBlockEntity) world.getTileEntity(pos);

		if (state.getValue(POWERED) && be.getSignalLength() > 0)
			return false;
		else if (!world.isRemote) {
			if (be.isDisabled())
				player.sendStatusMessage(Utils.localize("gui.securitycraft:scManual.disabled"), true);
			else if (be.verifyPasscodeSet(world, pos, be, player)) {
				if (be.isDenied(player)) {
					if (be.sendsDenylistMessage())
						PlayerUtils.sendMessageToPlayer(player, Utils.localize(getTranslationKey() + ".name"), Utils.localize("messages.securitycraft:module.onDenylist"), TextFormatting.RED);
				}
				else if (be.isAllowed(player)) {
					if (be.sendsAllowlistMessage())
						PlayerUtils.sendMessageToPlayer(player, Utils.localize(getTranslationKey() + ".name"), Utils.localize("messages.securitycraft:module.onAllowlist"), TextFormatting.GREEN);

					activate(state, world, pos, be.getSignalLength());
				}
				else if (player.getHeldItem(hand).getItem() != SCContent.codebreaker)
					be.openPasscodeGUI(world, pos, player);
			}
		}

		return true;
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random random) {
		if (state.getValue(POWERED)) {
			world.setBlockState(pos, state.withProperty(POWERED, false));
			BlockUtils.updateIndirectNeighbors(world, pos, this, getConnectedDirection(state).getOpposite());
		}
	}

	public void activate(IBlockState state, World world, BlockPos pos, int signalLength) {
		world.setBlockState(pos, state.cycleProperty(POWERED));
		BlockUtils.updateIndirectNeighbors(world, pos, this, getConnectedDirection(state).getOpposite());

		if (signalLength > 0)
			world.scheduleUpdate(pos, this, signalLength);
	}

	public abstract EnumFacing getConnectedDirection(IBlockState state);

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

		if (state.getValue(POWERED)) {
			world.notifyNeighborsOfStateChange(pos, this, false);
			BlockUtils.updateIndirectNeighbors(world, pos, this);
		}

		if (te instanceof IPasscodeProtected)
			SaltData.removeSalt(((IPasscodeProtected) te).getSaltKey());

		super.breakBlock(world, pos, state);
	}

	@Override
	public boolean canProvidePower(IBlockState state) {
		return true;
	}

	@Override
	public int getWeakPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		return state.getValue(POWERED) ? 15 : 0;
	}

	@Override
	public int getStrongPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		return state.getValue(POWERED) && getConnectedDirection(state) == side ? 15 : 0;
	}

	@Override
	public boolean canPlaceBlockAt(World world, BlockPos pos) {
		return true;
	}

	@Override
	public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side) {
		return world.getBlockState(pos).isSideSolid(world, pos, side);
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
		EnumFacing connectedDirection = getConnectedDirection(state);

		if (!canPlaceBlockOnSide(world, pos.offset(connectedDirection.getOpposite()), connectedDirection)) {
			dropBlockAsItem(world, pos, state, 0);
			world.setBlockToAir(pos);
		}
	}

	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		return Arrays.asList(new ItemStack(SCContent.keyPanel));
	}

	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		return new ItemStack(SCContent.keyPanel);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new KeyPanelBlockEntity();
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
