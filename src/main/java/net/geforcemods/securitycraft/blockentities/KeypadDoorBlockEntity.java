package net.geforcemods.securitycraft.blockentities;

import java.util.UUID;
import java.util.function.Consumer;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.INameSetter;
import net.geforcemods.securitycraft.api.IPasscodeProtected;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.BooleanOption;
import net.geforcemods.securitycraft.api.Option.SendAllowlistMessageOption;
import net.geforcemods.securitycraft.api.Option.SendDenylistMessageOption;
import net.geforcemods.securitycraft.api.Option.SmartModuleCooldownOption;
import net.geforcemods.securitycraft.blocks.KeypadDoorBlock;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;

public class KeypadDoorBlockEntity extends SpecialDoorBlockEntity implements IPasscodeProtected {
	private BooleanOption sendAllowlistMessage = new SendAllowlistMessageOption(false);
	private BooleanOption sendDenylistMessage = new SendDenylistMessageOption(true);
	private SmartModuleCooldownOption smartModuleCooldown = new SmartModuleCooldownOption(this::getBlockPos);
	private long cooldownEnd = 0;
	private byte[] passcode;
	private UUID saltKey;

	public KeypadDoorBlockEntity() {
		super(SCContent.KEYPAD_DOOR_BLOCK_ENTITY.get());
	}

	@Override
	public CompoundNBT save(CompoundNBT tag) {
		super.save(tag);

		long cooldownLeft = getCooldownEnd() - System.currentTimeMillis();

		savePasscodeAndSalt(tag);
		tag.putLong("cooldownLeft", cooldownLeft <= 0 ? -1 : cooldownLeft);
		return tag;
	}

	@Override
	public void load(BlockState state, CompoundNBT tag) {
		super.load(state, tag);

		loadSaltKey(tag);
		loadPasscode(tag);
		cooldownEnd = System.currentTimeMillis() + tag.getLong("cooldownLeft");

		if (tag.contains("sendMessage") && !tag.getBoolean("sendMessage")) {
			sendAllowlistMessage.setValue(false);
			sendDenylistMessage.setValue(false);
		}
	}

	@Override
	public void activate(PlayerEntity player) {
		if (!level.isClientSide && getBlockState().getBlock() instanceof KeypadDoorBlock)
			((KeypadDoorBlock) getBlockState().getBlock()).activate(getBlockState(), level, worldPosition, getSignalLength());
	}

	@Override
	public boolean shouldAttemptCodebreak(PlayerEntity player) {
		if (isDisabled()) {
			player.displayClientMessage(Utils.localize("gui.securitycraft:scManual.disabled"), true);
			return false;
		}

		return !getBlockState().getValue(DoorBlock.OPEN) && IPasscodeProtected.super.shouldAttemptCodebreak(player);
	}

	@Override
	public byte[] getPasscode() {
		return passcode == null || passcode.length == 0 ? null : passcode;
	}

	@Override
	public void setPasscode(byte[] passcode) {
		this.passcode = passcode;
		setChanged();
	}

	@Override
	public UUID getSaltKey() {
		return saltKey;
	}

	@Override
	public void setSaltKey(UUID saltKey) {
		this.saltKey = saltKey;
		setChanged();
	}

	@Override
	public void setPasscodeInAdjacentBlock(String codeToSet) {
		runForOtherHalf(otherBe -> {
			if (getOwner().owns(otherBe)) {
				otherBe.hashAndSetPasscode(codeToSet, getSalt());
				level.sendBlockUpdated(otherBe.getBlockPos(), otherBe.getBlockState(), otherBe.getBlockState(), 2);
			}
		});
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
				ModuleType.ALLOWLIST, ModuleType.DENYLIST, ModuleType.SMART, ModuleType.HARMING, ModuleType.DISGUISE
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

	@Override
	public int defaultSignalLength() {
		return 60;
	}

	@Override
	public void setCustomName(ITextComponent customName) {
		super.setCustomName(customName);

		if (getBlockState().getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER)
			((INameSetter) level.getBlockEntity(worldPosition.above())).setCustomName(customName);
	}

	public void runForOtherHalf(Consumer<KeypadDoorBlockEntity> action) {
		TileEntity be = null;

		if (level == null) //Happens when loading the BE, in that case running the same code for the other half is unnecessary
			return;

		if (getBlockState().getValue(DoorBlock.HALF) == DoubleBlockHalf.LOWER)
			be = level.getBlockEntity(worldPosition.above());
		else if (getBlockState().getValue(DoorBlock.HALF) == DoubleBlockHalf.UPPER)
			be = level.getBlockEntity(worldPosition.below());

		if (be instanceof KeypadDoorBlockEntity)
			action.accept((KeypadDoorBlockEntity) be);
	}
}
