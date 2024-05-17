package net.geforcemods.securitycraft.blockentities;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.BooleanOption;
import net.geforcemods.securitycraft.api.Option.IgnoreOwnerOption;
import net.geforcemods.securitycraft.api.Option.IntOption;
import net.geforcemods.securitycraft.api.Option.TargetingModeOption;
import net.geforcemods.securitycraft.blocks.FloorTrapBlock;
import net.geforcemods.securitycraft.blocks.SometimesVisibleBlock;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.misc.TargetingMode;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;

public class FloorTrapBlockEntity extends DisguisableBlockEntity implements ITickableTileEntity {
	private IgnoreOwnerOption ignoreOwner = new IgnoreOwnerOption(true);
	private BooleanOption disappearInstantlyInChains = new BooleanOption("disappearInstantlyInChains", true);
	private IntOption disappearDelay = new IntOption(this::getBlockPos, "disappearDelay", 5, 0, 200, 1);
	private IntOption reappearDelay = new IntOption(this::getBlockPos, "reappearDelay", 20, 5, 200, 1);
	private TargetingModeOption targetingMode = new TargetingModeOption(TargetingMode.PLAYERS);
	private boolean shouldDisappear = false, shouldReappear = false;
	private int ticksUntilDisappearing = -1, ticksUntilReappearing = -1;

	public FloorTrapBlockEntity() {
		super(SCContent.FLOOR_TRAP_BLOCK_ENTITY.get());
	}

	@Override
	public void tick() {
		if (level.isClientSide) {
			BlockState state = level.getBlockState(worldPosition);

			if (state.getValue(FloorTrapBlock.INVISIBLE))
				level.addParticle(SCContent.FLOOR_TRAP_CLOUD.get(), false, worldPosition.getX() + 0.5D, worldPosition.getY() + 0.5D, worldPosition.getZ() + 0.5D, 0.0D, 0.0D, 0.0D);

			return;
		}


		if (!shouldReappear && shouldDisappear) {
			if (ticksUntilDisappearing-- <= 0)
				disappear();

			return;
		}

		TargetingMode mode = targetingMode.get();
		AxisAlignedBB area = new AxisAlignedBB(worldPosition.getX(), worldPosition.getY() + 1.0D, worldPosition.getZ(), worldPosition.getX() + 1.0D, worldPosition.getY() + 1.1666D, worldPosition.getZ() + 1.0D);

		//@formatter:off
		shouldDisappear = level.getEntitiesOfClass(LivingEntity.class, area, e -> !e.isSpectator() && !allowsOwnableEntity(e) && !isAllowed(e) && !(isOwnedBy(e) && ignoresOwner()))
			.stream()
			.anyMatch(entity -> mode.allowsPlayers() && entity instanceof PlayerEntity || mode.allowsMobs());
		//@formatter:on

		if (shouldReappear && ticksUntilReappearing-- <= 0 && !shouldDisappear)
			reappear();

		scheduleDisappear(false);
	}

	public void scheduleDisappear(boolean force) {
		scheduleDisappear(disappearDelay.get(), force);
	}

	public void scheduleDisappear(int delay, boolean force) {
		if (force)
			shouldDisappear = true;

		if (shouldDisappear)
			ticksUntilDisappearing = delay;
	}

	public void disappear() {
		level.setBlockAndUpdate(worldPosition, getBlockState().setValue(SometimesVisibleBlock.INVISIBLE, true));
		level.playSound(null, worldPosition, SoundEvents.ENDER_DRAGON_FLAP, SoundCategory.BLOCKS, 1.0F, 2.0F);
		shouldDisappear = false;
		scheduleReappear();
	}

	public void scheduleReappear() {
		scheduleReappear(reappearDelay.get());
	}

	public void scheduleReappear(int delay) {
		shouldReappear = true;
		ticksUntilReappearing = delay;
	}

	public void reappear() {
		level.setBlockAndUpdate(worldPosition, getBlockState().setValue(SometimesVisibleBlock.INVISIBLE, false));
		shouldReappear = false;
	}

	@Override
	public CompoundNBT save(CompoundNBT tag) {
		super.save(tag);
		tag.putBoolean("should_disappear", shouldDisappear);
		tag.putBoolean("should_reappear", shouldReappear);
		tag.putInt("ticks_until_disappearing", ticksUntilDisappearing);
		tag.putInt("ticks_until_reappearing", ticksUntilReappearing);
		return tag;
	}

	@Override
	public void load(BlockState state, CompoundNBT tag) {
		super.load(state, tag);
		shouldDisappear = tag.getBoolean("should_disappear");
		shouldReappear = tag.getBoolean("should_reappear");
		ticksUntilDisappearing = tag.getInt("ticks_until_disappearing");
		ticksUntilReappearing = tag.getInt("ticks_until_reappearing");
	}

	@Override
	public ModuleType[] acceptedModules() {
		return new ModuleType[] {
				ModuleType.DISGUISE, ModuleType.ALLOWLIST, ModuleType.SMART
		};
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[] {
				ignoreOwner, targetingMode, disappearInstantlyInChains, disappearDelay, reappearDelay
		};
	}

	@Override
	public boolean ignoresOwner() {
		return ignoreOwner.get();
	}

	public boolean shouldDisappearInstantlyInChains() {
		return disappearInstantlyInChains.get();
	}
}
