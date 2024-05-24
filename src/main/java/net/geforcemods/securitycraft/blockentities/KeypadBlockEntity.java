package net.geforcemods.securitycraft.blockentities;

import java.util.UUID;

import net.geforcemods.securitycraft.api.ILockable;
import net.geforcemods.securitycraft.api.IPasscodeProtected;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.BooleanOption;
import net.geforcemods.securitycraft.api.Option.DisabledOption;
import net.geforcemods.securitycraft.api.Option.IntOption;
import net.geforcemods.securitycraft.api.Option.SendAllowlistMessageOption;
import net.geforcemods.securitycraft.api.Option.SendDenylistMessageOption;
import net.geforcemods.securitycraft.api.Option.SignalLengthOption;
import net.geforcemods.securitycraft.api.Option.SmartModuleCooldownOption;
import net.geforcemods.securitycraft.blocks.KeypadBlock;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.PasscodeUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class KeypadBlockEntity extends DisguisableBlockEntity implements IPasscodeProtected, ILockable {
	private byte[] passcode;
	private UUID saltKey;
	private BooleanOption sendAllowlistMessage = new SendAllowlistMessageOption(false);
	private BooleanOption sendDenylistMessage = new SendDenylistMessageOption(true);
	private IntOption signalLength = new SignalLengthOption(this::getPos, 60);
	private DisabledOption disabled = new DisabledOption(false);
	private SmartModuleCooldownOption smartModuleCooldown = new SmartModuleCooldownOption(this::getPos);
	private long cooldownEnd = 0;

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);

		if (saltKey != null)
			tag.setUniqueId("saltKey", saltKey);

		if (passcode != null)
			tag.setString("passcode", PasscodeUtils.bytesToString(passcode));

		long cooldownLeft = getCooldownEnd() - System.currentTimeMillis();

		tag.setLong("cooldownLeft", cooldownLeft <= 0 ? -1 : cooldownLeft);
		return tag;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		loadSaltKey(tag);
		loadPasscode(tag);
		cooldownEnd = System.currentTimeMillis() + tag.getLong("cooldownLeft");

		if (tag.hasKey("sendMessage") && !tag.getBoolean("sendMessage")) {
			sendAllowlistMessage.setValue(false);
			sendDenylistMessage.setValue(false);
		}
	}

	@Override
	public void activate(EntityPlayer player) {
		if (!world.isRemote)
			((KeypadBlock) getBlockType()).activate(world.getBlockState(pos), world, pos, signalLength.get());
	}

	@Override
	public boolean shouldAttemptCodebreak(EntityPlayer player) {
		return !world.getBlockState(pos).getValue(KeypadBlock.POWERED) && IPasscodeProtected.super.shouldAttemptCodebreak(player);
	}

	@Override
	public <T> void onOptionChanged(Option<T> option) {
		if (option == disabled && ((BooleanOption) option).get() || option == signalLength) {
			IBlockState state = world.getBlockState(pos);

			world.setBlockState(pos, state.withProperty(KeypadBlock.POWERED, false));
			BlockUtils.updateIndirectNeighbors(world, pos, state.getBlock());
		}
	}

	@Override
	public byte[] getPasscode() {
		return passcode == null || passcode.length == 0 ? null : passcode;
	}

	@Override
	public void setPasscode(byte[] passcode) {
		this.passcode = passcode;
	}

	@Override
	public UUID getSaltKey() {
		return saltKey;
	}

	@Override
	public void setSaltKey(UUID saltKey) {
		this.saltKey = saltKey;
	}

	@Override
	public void startCooldown() {
		if (!isOnCooldown()) {
			IBlockState state = world.getBlockState(pos);

			cooldownEnd = System.currentTimeMillis() + smartModuleCooldown.get() * 50;
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
	public ModuleType[] acceptedModules() {
		return new ModuleType[] {
				ModuleType.ALLOWLIST, ModuleType.DENYLIST, ModuleType.DISGUISE, ModuleType.SMART, ModuleType.HARMING
		};
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[] {
				sendAllowlistMessage, sendDenylistMessage, signalLength, disabled, smartModuleCooldown
		};
	}

	public boolean sendsAllowlistMessage() {
		return sendAllowlistMessage.get();
	}

	public boolean sendsDenylistMessage() {
		return sendDenylistMessage.get();
	}

	public int getSignalLength() {
		return signalLength.get();
	}

	public boolean isDisabled() {
		return disabled.get();
	}
}
