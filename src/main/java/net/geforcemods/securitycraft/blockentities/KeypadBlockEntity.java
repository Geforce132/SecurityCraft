package net.geforcemods.securitycraft.blockentities;

import java.util.UUID;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.ILockable;
import net.geforcemods.securitycraft.api.IPasscodeProtected;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.BooleanOption;
import net.geforcemods.securitycraft.api.Option.DisabledOption;
import net.geforcemods.securitycraft.api.Option.IntOption;
import net.geforcemods.securitycraft.api.Option.SmartModuleCooldownOption;
import net.geforcemods.securitycraft.blocks.KeypadBlock;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.PasscodeUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class KeypadBlockEntity extends DisguisableBlockEntity implements IPasscodeProtected, ILockable {
	private byte[] passcode;
	private UUID saltKey;
	private BooleanOption isAlwaysActive = new BooleanOption("isAlwaysActive", false) {
		@Override
		public void toggle() {
			super.toggle();

			if (!isDisabled()) {
				world.setBlockState(pos, world.getBlockState(pos).withProperty(KeypadBlock.POWERED, get()));
				world.notifyNeighborsOfStateChange(pos, SCContent.keypad, false);
			}
		}
	};
	private BooleanOption sendMessage = new BooleanOption("sendMessage", true);
	private IntOption signalLength = new IntOption(this::getPos, "signalLength", 60, 5, 400, 5, true); //20 seconds max
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

		tag.setLong("cooldownLeft", getCooldownEnd() - System.currentTimeMillis());
		return tag;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		loadSaltKey(tag);
		loadPasscode(tag);
		cooldownEnd = System.currentTimeMillis() + tag.getLong("cooldownLeft");
	}

	@Override
	public void activate(EntityPlayer player) {
		if (!world.isRemote)
			((KeypadBlock) getBlockType()).activate(world.getBlockState(pos), world, pos, signalLength.get());
	}

	@Override
	public boolean shouldAttemptCodebreak(IBlockState state, EntityPlayer player) {
		return !state.getValue(KeypadBlock.POWERED) && IPasscodeProtected.super.shouldAttemptCodebreak(state, player);
	}

	@Override
	public void onOptionChanged(Option<?> option) {
		if (option.getName().equals("disabled")) {
			boolean isDisabled = ((BooleanOption) option).get();
			IBlockState state = world.getBlockState(pos);

			if (isDisabled && state.getValue(KeypadBlock.POWERED))
				world.setBlockState(pos, state.withProperty(KeypadBlock.POWERED, false));
			else if (!isDisabled && isAlwaysActive.get())
				world.setBlockState(pos, state.withProperty(KeypadBlock.POWERED, true));
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
				isAlwaysActive, sendMessage, signalLength, disabled, smartModuleCooldown
		};
	}

	public boolean sendsMessages() {
		return sendMessage.get();
	}

	public int getSignalLength() {
		return signalLength.get();
	}

	public boolean isDisabled() {
		return disabled.get();
	}
}
