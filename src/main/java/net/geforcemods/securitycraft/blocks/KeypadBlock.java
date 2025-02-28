package net.geforcemods.securitycraft.blocks;

import java.util.Random;
import java.util.function.Function;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasscodeConvertible;
import net.geforcemods.securitycraft.api.IPasscodeProtected;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blockentities.FrameBlockEntity;
import net.geforcemods.securitycraft.blockentities.KeypadBlockEntity;
import net.geforcemods.securitycraft.misc.SaltData;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
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
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class KeypadBlock extends DisguisableBlock {
	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	public static final PropertyBool POWERED = PropertyBool.create("powered");

	public KeypadBlock(Material material) {
		super(material);
		setSoundType(SoundType.METAL);
		setHardness(5.0F);
		setHarvestLevel("pickaxe", 1);
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		KeypadBlockEntity be = (KeypadBlockEntity) world.getTileEntity(pos);

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

	public void activate(IBlockState state, World world, BlockPos pos, int signalLength) {
		world.setBlockState(pos, state.cycleProperty(POWERED));
		BlockUtils.updateIndirectNeighbors(world, pos, SCContent.keypad);

		if (signalLength > 0)
			world.scheduleUpdate(pos, this, signalLength);
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random random) {
		if (state.getValue(POWERED)) {
			world.setBlockState(pos, state.withProperty(POWERED, false));
			BlockUtils.updateIndirectNeighbors(world, pos, SCContent.keypad);
		}
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		TileEntity te = world.getTileEntity(pos);

		if (state.getValue(POWERED))
			BlockUtils.updateIndirectNeighbors(world, pos, this);

		if (te instanceof IPasscodeProtected)
			SaltData.removeSalt(((IPasscodeProtected) te).getSaltKey());

		super.breakBlock(world, pos, state);
	}

	@Override
	public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
		setDefaultFacing(world, pos, state);
	}

	private void setDefaultFacing(World world, BlockPos pos, IBlockState state) {
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
	public int getStrongPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		if (blockState.getValue(POWERED))
			return 15;
		else
			return 0;
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		return getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite()).withProperty(POWERED, false);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		if (meta == 15)
			return getDefaultState();

		if (meta <= 5)
			return getDefaultState().withProperty(FACING, EnumFacing.values()[meta].getAxis() == EnumFacing.Axis.Y ? EnumFacing.NORTH : EnumFacing.values()[meta]).withProperty(POWERED, false);
		else
			return getDefaultState().withProperty(FACING, EnumFacing.values()[meta - 6]).withProperty(POWERED, true);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		if (state.getProperties().containsKey(POWERED) && state.getValue(POWERED))
			return (state.getValue(FACING).getIndex() + 6);
		else {
			if (!state.getProperties().containsKey(FACING))
				return 15;

			return state.getValue(FACING).getIndex();
		}
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING, POWERED);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new KeypadBlockEntity();
	}

	@Override
	public IBlockState withRotation(IBlockState state, Rotation rot) {
		return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
	}

	@Override
	public IBlockState withMirror(IBlockState state, Mirror mirror) {
		return state.withRotation(mirror.toRotation(state.getValue(FACING)));
	}

	public static class Convertible implements Function<Object, IPasscodeConvertible>, IPasscodeConvertible {
		@Override
		public IPasscodeConvertible apply(Object o) {
			return this;
		}

		@Override
		public boolean isUnprotectedBlock(IBlockState state) {
			return state.getBlock() == SCContent.frame;
		}

		@Override
		public boolean isProtectedBlock(IBlockState state) {
			return state.getBlock() == SCContent.keypad;
		}

		@Override
		public boolean protect(EntityPlayer player, World world, BlockPos pos) {
			FrameBlockEntity be = (FrameBlockEntity) world.getTileEntity(pos);

			if (be.getCurrentCamera() != null) {
				PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.frame), Utils.localize("messages.securitycraft:frame.cannotConvert"), TextFormatting.RED);
				return false;
			}

			Owner owner = be.getOwner();

			be.dropAllModules();
			world.setBlockState(pos, SCContent.keypad.getDefaultState().withProperty(KeypadBlock.FACING, world.getBlockState(pos).getValue(FrameBlock.FACING)).withProperty(KeypadBlock.POWERED, false));
			((IOwnable) world.getTileEntity(pos)).setOwner(owner.getUUID(), owner.getName());
			return true;
		}

		@Override
		public boolean unprotect(EntityPlayer player, World world, BlockPos pos) {
			KeypadBlockEntity be = (KeypadBlockEntity) world.getTileEntity(pos);
			Owner owner = be.getOwner();

			be.dropAllModules();
			world.setBlockState(pos, SCContent.frame.getDefaultState().withProperty(FrameBlock.FACING, world.getBlockState(pos).getValue(KeypadBlock.FACING)));
			((IOwnable) world.getTileEntity(pos)).setOwner(owner.getUUID(), owner.getName());
			return true;
		}
	}
}
