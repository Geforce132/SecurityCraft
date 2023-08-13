package net.geforcemods.securitycraft.blocks;

import java.util.Random;
import java.util.function.Function;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.INameSetter;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasscodeConvertible;
import net.geforcemods.securitycraft.api.IPasscodeProtected;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blockentities.KeypadTrapdoorBlockEntity;
import net.geforcemods.securitycraft.blocks.reinforced.BaseIronTrapDoorBlock;
import net.geforcemods.securitycraft.misc.SaltData;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class KeypadTrapDoorBlock extends BaseIronTrapDoorBlock {
	public KeypadTrapDoorBlock(Material mat) {
		super(mat);
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (state.getValue(OPEN))
			return false;
		else if (!world.isRemote) {
			KeypadTrapdoorBlockEntity be = (KeypadTrapdoorBlockEntity) world.getTileEntity(pos);

			if (be.isDisabled())
				player.sendStatusMessage(Utils.localize("gui.securitycraft:scManual.disabled"), true);
			else if (be.verifyPasscodeSet(world, pos, be, player)) {
				if (be.isDenied(player)) {
					if (be.sendsMessages())
						PlayerUtils.sendMessageToPlayer(player, Utils.localize(this), Utils.localize("messages.securitycraft:module.onDenylist"), TextFormatting.RED);
				}
				else if (be.isAllowed(player)) {
					if (be.sendsMessages())
						PlayerUtils.sendMessageToPlayer(player, Utils.localize(this), Utils.localize("messages.securitycraft:module.onAllowlist"), TextFormatting.GREEN);

					activate(state, world, pos, be.getSignalLength());
				}
				else if (player.getHeldItem(hand).getItem() != SCContent.codebreaker)
					be.openPasscodeGUI(world, pos, player);
			}
		}

		return true;
	}

	public void activate(IBlockState state, World world, BlockPos pos, int signalLength) {
		world.setBlockState(pos, state.withProperty(OPEN, true));
		world.scheduleUpdate(pos, this, signalLength);
		playSound(null, world, pos, true);
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random random) {
		if (state.getValue(OPEN)) {
			world.setBlockState(pos, state.withProperty(OPEN, false));
			playSound(null, world, pos, false);
		}
	}

	//here for making it accessible without AT
	@Override
	public void playSound(EntityPlayer player, World level, BlockPos pos, boolean isOpened) {
		super.playSound(player, level, pos, isOpened);
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		TileEntity be = world.getTileEntity(pos);

		if (be instanceof IPasscodeProtected)
			SaltData.removeSalt(((IPasscodeProtected) be).getSaltKey());

		super.breakBlock(world, pos, state);
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		super.onBlockPlacedBy(world, pos, state, placer, stack);

		if (!world.isRemote && stack.hasDisplayName()) {
			TileEntity te = world.getTileEntity(pos);

			if (te instanceof INameSetter)
				((INameSetter) te).setCustomName(stack.getDisplayName());
		}
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos neighbor) {}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new KeypadTrapdoorBlockEntity();
	}

	public static class Convertible implements Function<Object, IPasscodeConvertible>, IPasscodeConvertible {
		@Override
		public IPasscodeConvertible apply(Object o) {
			return this;
		}

		@Override
		public boolean isValidStateForConversion(IBlockState state) {
			return state.getBlock() == SCContent.frame;
		}

		@Override
		public boolean convert(EntityPlayer player, World level, BlockPos pos) {
			IBlockState state = level.getBlockState(pos);
			EnumFacing facing = state.getValue(FACING);
			boolean open = state.getValue(OPEN);
			DoorHalf half = state.getValue(HALF);
			TileEntity trapdoor = level.getTileEntity(pos);
			Owner oldOwner = ((IOwnable) trapdoor).getOwner();
			NBTTagCompound tag = trapdoor.writeToNBT(new NBTTagCompound());

			level.setBlockState(pos, SCContent.keypadTrapdoor.getDefaultState().withProperty(FACING, facing).withProperty(OPEN, open).withProperty(HALF, half));
			trapdoor = level.getTileEntity(pos);
			trapdoor.readFromNBT(tag);
			((IOwnable) trapdoor).setOwner(oldOwner.getUUID(), oldOwner.getName());
			return true;
		}
	}
}
