package net.geforcemods.securitycraft.blockentities;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.ILockable;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.DisabledOption;
import net.geforcemods.securitycraft.api.Option.OptionBoolean;
import net.geforcemods.securitycraft.api.Option.OptionInt;
import net.geforcemods.securitycraft.api.Option.SmartModuleCooldownOption;
import net.geforcemods.securitycraft.blocks.KeypadBlock;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class KeypadBlockEntity extends DisguisableBlockEntity implements IPasswordProtected, ILockable {
	private String passcode;
	private OptionBoolean isAlwaysActive = new OptionBoolean("isAlwaysActive", false) {
		@Override
		public void toggle() {
			super.toggle();

			if (!isDisabled()) {
				world.setBlockState(pos, world.getBlockState(pos).withProperty(KeypadBlock.POWERED, get()));
				world.notifyNeighborsOfStateChange(pos, SCContent.keypad, false);
			}
		}
	};
	private OptionBoolean sendMessage = new OptionBoolean("sendMessage", true);
	private OptionInt signalLength = new OptionInt(this::getPos, "signalLength", 60, 5, 400, 5, true); //20 seconds max
	private DisabledOption disabled = new DisabledOption(false);
	private SmartModuleCooldownOption smartModuleCooldown = new SmartModuleCooldownOption(this::getPos);
	private long cooldownEnd = 0;

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
		if (!world.isRemote)
			((KeypadBlock) getBlockType()).activate(world.getBlockState(pos), world, pos, signalLength.get());
	}

	@Override
	public boolean shouldAttemptCodebreak(IBlockState state, EntityPlayer player) {
		return !state.getValue(KeypadBlock.POWERED) && IPasswordProtected.super.shouldAttemptCodebreak(state, player);
	}

	@Override
	public void onOptionChanged(Option<?> option) {
		if (option.getName().equals("disabled")) {
			boolean isDisabled = ((OptionBoolean) option).get();
			IBlockState state = world.getBlockState(pos);

			if (isDisabled && state.getValue(KeypadBlock.POWERED))
				world.setBlockState(pos, state.withProperty(KeypadBlock.POWERED, false));
			else if (!isDisabled && isAlwaysActive.get())
				world.setBlockState(pos, state.withProperty(KeypadBlock.POWERED, true));
		}
	}

	@Override
	public String getPassword() {
		return (passcode != null && !passcode.isEmpty()) ? passcode : null;
	}

	@Override
	public void setPassword(String password) {
		passcode = password;
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
