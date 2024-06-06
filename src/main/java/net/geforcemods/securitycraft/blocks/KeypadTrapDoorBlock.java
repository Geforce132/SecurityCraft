package net.geforcemods.securitycraft.blocks;

import java.util.function.Function;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IPasscodeConvertible;
import net.geforcemods.securitycraft.api.IPasscodeProtected;
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
					if (be.sendsDenylistMessage())
						PlayerUtils.sendMessageToPlayer(player, Utils.localize(this), Utils.localize("messages.securitycraft:module.onDenylist"), TextFormatting.RED);
				}
				else if (be.isAllowed(player)) {
					if (be.sendsAllowlistMessage())
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
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		TileEntity be = world.getTileEntity(pos);

		if (be instanceof IPasscodeProtected)
			SaltData.removeSalt(((IPasscodeProtected) be).getSaltKey());

		super.breakBlock(world, pos, state);
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos neighbor) {}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		return super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer).withProperty(OPEN, false);
	}

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
		public boolean isUnprotectedBlock(IBlockState state) {
			return state.getBlock() == SCContent.reinforcedIronTrapdoor;
		}

		@Override
		public boolean isProtectedBlock(IBlockState state) {
			return state.getBlock() == SCContent.keypadTrapdoor;
		}

		@Override
		public boolean protect(EntityPlayer player, World level, BlockPos pos) {
			return convert(level, pos, SCContent.keypadTrapdoor);
		}

		@Override
		public boolean unprotect(EntityPlayer player, World level, BlockPos pos) {
			return convert(level, pos, SCContent.reinforcedIronTrapdoor);
		}

		public boolean convert(World level, BlockPos pos, Block convertedBlock) {
			IBlockState state = level.getBlockState(pos);
			EnumFacing facing = state.getValue(FACING);
			DoorHalf half = state.getValue(HALF);
			TileEntity be = level.getTileEntity(pos);
			NBTTagCompound tag;

			if (be instanceof IModuleInventory)
				((IModuleInventory) be).dropAllModules();

			tag = be.writeToNBT(new NBTTagCompound());
			level.setBlockState(pos, convertedBlock.getDefaultState().withProperty(FACING, facing).withProperty(OPEN, false).withProperty(HALF, half));
			level.getTileEntity(pos).readFromNBT(tag);
			return true;
		}
	}
}
