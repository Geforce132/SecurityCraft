package net.geforcemods.securitycraft.blockentities;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.ILockable;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.BooleanOption;
import net.geforcemods.securitycraft.api.Option.DisabledOption;
import net.geforcemods.securitycraft.api.Option.IntOption;
import net.geforcemods.securitycraft.api.Option.SmartModuleCooldownOption;
import net.geforcemods.securitycraft.blocks.KeypadBlock;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;

public class KeypadBlockEntity extends DisguisableBlockEntity implements IPasswordProtected, ILockable {
	private String passcode;
	private BooleanOption isAlwaysActive = new BooleanOption("isAlwaysActive", false) {
		@Override
		public void toggle() {
			super.toggle();

			if (!isDisabled()) {
				level.setBlockAndUpdate(worldPosition, getBlockState().setValue(KeypadBlock.POWERED, get()));
				level.updateNeighborsAt(worldPosition, SCContent.KEYPAD.get());
			}
		}
	};
	private BooleanOption sendMessage = new BooleanOption("sendMessage", true);
	private IntOption signalLength = new IntOption(this::getBlockPos, "signalLength", 60, 5, 400, 5, true); //20 seconds max
	private DisabledOption disabled = new DisabledOption(false);
	private SmartModuleCooldownOption smartModuleCooldown = new SmartModuleCooldownOption(this::getBlockPos);
	private long cooldownEnd = 0;

	public KeypadBlockEntity(BlockPos pos, BlockState state) {
		super(SCContent.KEYPAD_BLOCK_ENTITY.get(), pos, state);
	}

	@Override
	public void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);

		if (passcode != null && !passcode.isEmpty())
			tag.putString("passcode", passcode);

		tag.putLong("cooldownLeft", getCooldownEnd() - System.currentTimeMillis());
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);

		passcode = tag.getString("passcode");
		cooldownEnd = System.currentTimeMillis() + tag.getLong("cooldownLeft");
	}

	@Override
	public void activate(Player player) {
		if (!level.isClientSide && getBlockState().getBlock() instanceof KeypadBlock block)
			block.activate(getBlockState(), level, worldPosition, signalLength.get());
	}

	@Override
	public boolean shouldAttemptCodebreak(BlockState state, Player player) {
		if (isDisabled()) {
			player.displayClientMessage(Utils.localize("gui.securitycraft:scManual.disabled"), true);
			return false;
		}

		return !state.getValue(KeypadBlock.POWERED) && IPasswordProtected.super.shouldAttemptCodebreak(state, player);
	}

	@Override
	public void onOptionChanged(Option<?> option) {
		if (option.getName().equals("disabled")) {
			boolean isDisabled = ((BooleanOption) option).get();

			if (isDisabled && getBlockState().getValue(KeypadBlock.POWERED))
				level.setBlockAndUpdate(worldPosition, getBlockState().setValue(KeypadBlock.POWERED, false));
			else if (!isDisabled && isAlwaysActive.get())
				level.setBlockAndUpdate(worldPosition, getBlockState().setValue(KeypadBlock.POWERED, true));
		}
	}

	@Override
	public String getPassword() {
		return (passcode != null && !passcode.isEmpty()) ? passcode : null;
	}

	@Override
	public void setPassword(String password) {
		passcode = password;
		setChanged();
	}

	@Override
	public void startCooldown() {
		if (!isOnCooldown()) {
			cooldownEnd = System.currentTimeMillis() + smartModuleCooldown.get() * 50;
			level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2);
			setChanged();
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
