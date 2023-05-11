package net.geforcemods.securitycraft.blockentities;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableBlockEntity;
import net.geforcemods.securitycraft.api.ILockable;
import net.geforcemods.securitycraft.api.IPasscodeProtected;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.BooleanOption;
import net.geforcemods.securitycraft.api.Option.DisabledOption;
import net.geforcemods.securitycraft.api.Option.SmartModuleCooldownOption;
import net.geforcemods.securitycraft.blocks.DisplayCaseBlock;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.geforcemods.securitycraft.util.ITickingBlockEntity;
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
	private BooleanOption sendMessage = new BooleanOption("sendMessage", true);
	private DisabledOption disabled = new DisabledOption(false);
	private SmartModuleCooldownOption smartModuleCooldown = new SmartModuleCooldownOption(this::getBlockPos);
	private long cooldownEnd = 0;
	private ItemStack displayedStack = ItemStack.EMPTY;
	private boolean shouldBeOpen;
	private float openness;
	private float oOpenness;
	private String passcode;

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
	public String getPasscode() {
		return (passcode != null && !passcode.isEmpty()) ? passcode : null;
	}

	@Override
	public void setPasscode(String passcode) {
		this.passcode = passcode;
		setChanged();
	}

	@Override
	public void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.put("DisplayedStack", getDisplayedStack().save(new CompoundTag()));
		tag.putBoolean("ShouldBeOpen", shouldBeOpen);
		tag.putLong("cooldownLeft", getCooldownEnd() - System.currentTimeMillis());

		if (passcode != null && !passcode.isEmpty())
			tag.putString("Passcode", passcode);
	}

	@Override
	public void load(CompoundTag tag) {
		load(tag, true);
	}

	public void load(CompoundTag tag, boolean forceOpenness) {
		super.load(tag);
		setDisplayedStack(ItemStack.of((CompoundTag) tag.get("DisplayedStack")));
		shouldBeOpen = tag.getBoolean("ShouldBeOpen");
		cooldownEnd = System.currentTimeMillis() + tag.getLong("cooldownLeft");
		passcode = tag.getString("Passcode");

		if (forceOpenness)
			forceOpen(shouldBeOpen);
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
				sendMessage, disabled, smartModuleCooldown
		};
	}

	public boolean sendsMessages() {
		return sendMessage.get();
	}

	public boolean isDisabled() {
		return disabled.get();
	}

	@Override
	public void handleUpdateTag(CompoundTag tag) {
		load(tag, false);
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
