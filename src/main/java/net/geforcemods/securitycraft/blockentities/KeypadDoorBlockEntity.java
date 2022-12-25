package net.geforcemods.securitycraft.blockentities;

import java.util.function.Consumer;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.SmartModuleCooldownOption;
import net.geforcemods.securitycraft.blocks.KeypadDoorBlock;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.TileEntity;

public class KeypadDoorBlockEntity extends SpecialDoorBlockEntity implements IPasswordProtected {
	private SmartModuleCooldownOption smartModuleCooldown = new SmartModuleCooldownOption(this::getBlockPos);
	private long cooldownEnd = 0;
	private String passcode;

	public KeypadDoorBlockEntity() {
		super(SCContent.KEYPAD_DOOR_BLOCK_ENTITY.get());
	}

	@Override
	public CompoundNBT save(CompoundNBT tag) {
		super.save(tag);

		if (passcode != null && !passcode.isEmpty())
			tag.putString("passcode", passcode);

		tag.putLong("cooldownLeft", getCooldownEnd() - System.currentTimeMillis());
		return tag;
	}

	@Override
	public void load(BlockState state, CompoundNBT tag) {
		super.load(state, tag);

		passcode = tag.getString("passcode");
		cooldownEnd = System.currentTimeMillis() + tag.getLong("cooldownLeft");
	}

	@Override
	public void activate(PlayerEntity player) {
		if (!level.isClientSide && getBlockState().getBlock() instanceof KeypadDoorBlock)
			((KeypadDoorBlock) getBlockState().getBlock()).activate(getBlockState(), level, worldPosition, getSignalLength());
	}

	@Override
	public boolean shouldAttemptCodebreak(BlockState state, PlayerEntity player) {
		if (isDisabled()) {
			player.displayClientMessage(Utils.localize("gui.securitycraft:scManual.disabled"), true);
			return false;
		}

		return !state.getValue(DoorBlock.POWERED) && IPasswordProtected.super.shouldAttemptCodebreak(state, player);
	}

	@Override
	public String getPassword() {
		return (passcode != null && !passcode.isEmpty()) ? passcode : null;
	}

	@Override
	public void setPassword(String password) {
		passcode = password;
		runForOtherHalf(otherHalf -> otherHalf.setPasswordExclusively(password));
		setChanged();
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
			cooldownEnd = start + smartModuleCooldown.get() * 50;
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
				ModuleType.ALLOWLIST, ModuleType.DENYLIST, ModuleType.SMART, ModuleType.HARMING
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

	public void runForOtherHalf(Consumer<KeypadDoorBlockEntity> action) {
		TileEntity te = null;

		if (getBlockState().getValue(DoorBlock.HALF) == DoubleBlockHalf.LOWER)
			te = level.getBlockEntity(worldPosition.above());
		else if (getBlockState().getValue(DoorBlock.HALF) == DoubleBlockHalf.UPPER)
			te = level.getBlockEntity(worldPosition.below());

		if (te instanceof KeypadDoorBlockEntity)
			action.accept((KeypadDoorBlockEntity) te);
	}
}
