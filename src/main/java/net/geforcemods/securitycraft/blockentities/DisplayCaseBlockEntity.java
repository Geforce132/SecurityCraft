package net.geforcemods.securitycraft.blockentities;

import java.util.UUID;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableBlockEntity;
import net.geforcemods.securitycraft.api.ILockable;
import net.geforcemods.securitycraft.api.IPasscodeProtected;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.BooleanOption;
import net.geforcemods.securitycraft.api.Option.DisabledOption;
import net.geforcemods.securitycraft.api.Option.SendAllowlistMessageOption;
import net.geforcemods.securitycraft.api.Option.SendDenylistMessageOption;
import net.geforcemods.securitycraft.api.Option.SmartModuleCooldownOption;
import net.geforcemods.securitycraft.blocks.DisplayCaseBlock;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.geforcemods.securitycraft.util.ITickingBlockEntity;
import net.geforcemods.securitycraft.util.PasscodeUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class DisplayCaseBlockEntity extends CustomizableBlockEntity implements ITickingBlockEntity, IPasscodeProtected, ILockable {
	private final AABB renderBoundingBox;
	private BooleanOption sendAllowlistMessage = new SendAllowlistMessageOption(false);
	private BooleanOption sendDenylistMessage = new SendDenylistMessageOption(true);
	private DisabledOption disabled = new DisabledOption(false);
	private SmartModuleCooldownOption smartModuleCooldown = new SmartModuleCooldownOption();
	private long cooldownEnd = 0;
	private ItemStack displayedStack = ItemStack.EMPTY;
	private boolean hasReceivedData = false;
	private boolean shouldBeOpen;
	private float openness;
	private float oOpenness;
	private byte[] passcode;
	private UUID saltKey;

	public DisplayCaseBlockEntity(BlockPos pos, BlockState state) {
		this(SCContent.DISPLAY_CASE_BLOCK_ENTITY.get(), pos, state);
	}

	public DisplayCaseBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		renderBoundingBox = new AABB(pos);
	}

	@Override
	public void tick(Level level, BlockPos pos, BlockState state) {
		oOpenness = openness;

		if (!shouldBeOpen && openness > 0.0F)
			openness = Math.max(openness - 0.1F, 0.0F);
		else if (shouldBeOpen && openness < 1.0F)
			openness = Math.min(openness + 0.1F, 1.0F);
	}

	@Override
	public void activate(Player player) {
		if (!level.isClientSide && getBlockState().getBlock() instanceof DisplayCaseBlock block)
			block.activate(this);
	}

	@Override
	public boolean shouldAttemptCodebreak(BlockState state, Player player) {
		if (isDisabled()) {
			player.displayClientMessage(Utils.localize("gui.securitycraft:scManual.disabled"), true);
			return false;
		}

		return !isOpen() && IPasscodeProtected.super.shouldAttemptCodebreak(state, player);
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
	}

	@Override
	public void saveAdditional(CompoundTag tag) {
		long cooldownLeft;

		super.saveAdditional(tag);
		tag.put("DisplayedStack", getDisplayedStack().save(new CompoundTag()));
		tag.putBoolean("ShouldBeOpen", shouldBeOpen);
		cooldownLeft = getCooldownEnd() - System.currentTimeMillis();
		tag.putLong("cooldownLeft", cooldownLeft <= 0 ? -1 : cooldownLeft);

		if (saltKey != null)
			tag.putUUID("saltKey", saltKey);

		if (passcode != null)
			tag.putString("passcode", PasscodeUtils.bytesToString(passcode));
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		setDisplayedStack(ItemStack.of((CompoundTag) tag.get("DisplayedStack")));
		shouldBeOpen = tag.getBoolean("ShouldBeOpen");
		cooldownEnd = System.currentTimeMillis() + tag.getLong("cooldownLeft");
		loadSaltKey(tag);
		loadPasscode(tag);

		if (level != null && level.isClientSide && !hasReceivedData) {
			forceOpen(shouldBeOpen);
			hasReceivedData = true;
		}

		if (tag.contains("sendMessage") && !tag.getBoolean("sendMessage")) {
			sendAllowlistMessage.setValue(false);
			sendDenylistMessage.setValue(false);
		}
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
				ModuleType.ALLOWLIST, ModuleType.DENYLIST, ModuleType.SMART, ModuleType.HARMING
		};
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[] {
				sendAllowlistMessage, sendDenylistMessage, disabled, smartModuleCooldown
		};
	}

	public boolean sendsAllowlistMessage() {
		return sendAllowlistMessage.get();
	}

	public boolean sendsDenylistMessage() {
		return sendDenylistMessage.get();
	}

	public boolean isDisabled() {
		return disabled.get();
	}

	public void setDisplayedStack(ItemStack displayedStack) {
		this.displayedStack = displayedStack;
		sync();
	}

	public ItemStack getDisplayedStack() {
		return displayedStack;
	}

	public void setOpen(boolean shouldBeOpen) {
		level.playSound(null, worldPosition, shouldBeOpen ? SCSounds.DISPLAY_CASE_OPEN.event : SCSounds.DISPLAY_CASE_CLOSE.event, SoundSource.BLOCKS, 1.0F, 1.0F);
		this.shouldBeOpen = shouldBeOpen;
		sync();
	}

	public void forceOpen(boolean open) {
		shouldBeOpen = open;
		oOpenness = openness = open ? 1.0F : 0.0F;
		sync();
	}

	public float getOpenness(float partialTicks) {
		return Mth.lerp(partialTicks, oOpenness, openness);
	}

	public boolean isOpen() {
		return shouldBeOpen;
	}

	private void sync() {
		if (level != null && !level.isClientSide) {
			setChanged();
			level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2);
		}
	}

	@Override
	public AABB getRenderBoundingBox() {
		return renderBoundingBox;
	}
}
