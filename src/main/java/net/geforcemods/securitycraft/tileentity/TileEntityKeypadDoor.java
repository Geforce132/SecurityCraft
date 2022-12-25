package net.geforcemods.securitycraft.tileentity;

import java.util.function.Consumer;

import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.SmartModuleCooldownOption;
import net.geforcemods.securitycraft.blocks.BlockKeypad;
import net.geforcemods.securitycraft.blocks.BlockKeypadDoor;
import net.geforcemods.securitycraft.misc.EnumModuleType;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockDoor.EnumDoorHalf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TileEntityKeypadDoor extends TileEntitySpecialDoor implements IPasswordProtected {
	private SmartModuleCooldownOption smartModuleCooldown = new SmartModuleCooldownOption(this::getPos);
	private long cooldownEnd = 0;
	private String passcode;

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);

		if (passcode != null && !passcode.isEmpty())
			tag.setString("passcode", passcode);

		tag.setLong("cooldownLeft", getCooldownEnd() - System.currentTimeMillis());
		return tag;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		passcode = tag.getString("passcode");
		cooldownEnd = System.currentTimeMillis() + tag.getLong("cooldownLeft");
	}

	@Override
	public void activate(EntityPlayer player) {
		if (!world.isRemote) {
			IBlockState state = world.getBlockState(pos);

			if (state.getBlock() instanceof BlockKeypadDoor) {
				//for some reason calling BlockKeypadDoor#activate if the block is the upper half does not work, so delegate opening to the lower half
				if (state.getValue(BlockDoor.HALF) == EnumDoorHalf.UPPER) {
					pos = pos.down();
					state = world.getBlockState(pos);
				}

				((BlockKeypadDoor) state.getBlock()).activate(state, world, pos, getSignalLength());
			}
		}
	}

	@Override
	public boolean shouldAttemptCodebreak(IBlockState state, EntityPlayer player) {
		return !state.getValue(BlockKeypad.POWERED) && IPasswordProtected.super.shouldAttemptCodebreak(state, player);
	}

	@Override
	public String getPassword() {
		return (passcode != null && !passcode.isEmpty()) ? passcode : null;
	}

	@Override
	public void setPassword(String password) {
		passcode = password;
		runForOtherHalf(otherHalf -> otherHalf.setPasswordExclusively(password));
	}

	//only set the password for this door half
	public void setPasswordExclusively(String password) {
		passcode = password;
	}

	@Override
	public void startCooldown() {
		long start = System.currentTimeMillis();

		startCooldown(start);
		runForOtherHalf(otherHalf -> otherHalf.startCooldown(start));
	}

	public void startCooldown(long start) {
		if (!isOnCooldown()) {
			IBlockState state = world.getBlockState(pos);

			cooldownEnd = start + smartModuleCooldown.get() * 50;
			world.notifyBlockUpdate(pos, state, state, 3);
			markDirty();
		}
	}

	@Override
	public long getCooldownEnd() {
		return cooldownEnd;
	}

	@Override
	public boolean isOnCooldown() {
		return System.currentTimeMillis() < getCooldownEnd();
	}

	@Override
	public EnumModuleType[] acceptedModules() {
		return new EnumModuleType[] {
				EnumModuleType.ALLOWLIST, EnumModuleType.DENYLIST, EnumModuleType.SMART, EnumModuleType.HARMING
		};
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[] {
				sendMessage, signalLength, disabled, smartModuleCooldown
		};
	}

	@Override
	public int defaultSignalLength() {
		return 60;
	}

	public void runForOtherHalf(Consumer<TileEntityKeypadDoor> action) {
		TileEntity te = null;
		IBlockState state = world.getBlockState(pos);

		if (state.getValue(BlockDoor.HALF) == EnumDoorHalf.LOWER)
			te = world.getTileEntity(pos.up());
		else if (state.getValue(BlockDoor.HALF) == EnumDoorHalf.UPPER)
			te = world.getTileEntity(pos.down());

		if (te instanceof TileEntityKeypadDoor)
			action.accept((TileEntityKeypadDoor) te);
	}
}
