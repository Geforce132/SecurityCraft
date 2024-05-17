package net.geforcemods.securitycraft.blockentities;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.BooleanOption;
import net.geforcemods.securitycraft.api.Option.IgnoreOwnerOption;
import net.geforcemods.securitycraft.api.Option.IntOption;
import net.geforcemods.securitycraft.api.Option.TargetingModeOption;
import net.geforcemods.securitycraft.blocks.SometimesVisibleBlock;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.misc.TargetingMode;
import net.geforcemods.securitycraft.util.ITickingBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class FloorTrapBlockEntity extends DisguisableBlockEntity implements ITickingBlockEntity {
	private IgnoreOwnerOption ignoreOwner = new IgnoreOwnerOption(true);
	private BooleanOption disappearInstantlyInChains = new BooleanOption("disappearInstantlyInChains", true);
	private IntOption disappearDelay = new IntOption("disappearDelay", 5, 0, 200, 1);
	private IntOption reappearDelay = new IntOption("reappearDelay", 20, 5, 200, 1);
	private TargetingModeOption targetingMode = new TargetingModeOption(TargetingMode.PLAYERS);
	private boolean shouldDisappear = false, shouldReappear = false;
	private int ticksUntilDisappearing = -1, ticksUntilReappearing = -1;

	public FloorTrapBlockEntity(BlockPos pos, BlockState state) {
		super(SCContent.FLOOR_TRAP_BLOCK_ENTITY.get(), pos, state);
	}

	@Override
	public void tick(Level level, BlockPos pos, BlockState state) { //server only as per FloorTrapBlock
		if (!shouldReappear && shouldDisappear) {
			if (ticksUntilDisappearing-- <= 0)
				disappear();

			return;
		}

		TargetingMode mode = targetingMode.get();

		//@formatter:off
		shouldDisappear = level.getEntitiesOfClass(LivingEntity.class, new AABB(pos.above()).setMaxY(pos.getY() + 1.1666D), e -> e.canBeSeenByAnyone() && !(e instanceof OwnableEntity ownableEntity && allowsOwnableEntity(ownableEntity)) && !isAllowed(e) && !(isOwnedBy(e) && ignoresOwner()))
			.stream()
			.anyMatch(entity -> mode.allowsPlayers() && entity instanceof Player || mode.allowsMobs());
		//@formatter:on

		if (shouldReappear && ticksUntilReappearing-- <= 0 && !shouldDisappear)
			reappear();

		scheduleDisappear(false);
	}

	public static void particleTick(Level level, BlockPos pos, BlockState state, FloorTrapBlockEntity blockEntity) { //client only, and is only called when invisible
		level.addParticle(SCContent.FLOOR_TRAP_CLOUD.get(), false, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 0.0D, 0.0D, 0.0D);
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
		level.playSound(null, worldPosition, SoundEvents.ENDER_DRAGON_FLAP, SoundSource.BLOCKS, 1.0F, 2.0F);
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
	public void saveAdditional(CompoundTag tag, HolderLookup.Provider lookupProvider) {
		super.saveAdditional(tag, lookupProvider);
		tag.putBoolean("should_disappear", shouldDisappear);
		tag.putBoolean("should_reappear", shouldReappear);
		tag.putInt("ticks_until_disappearing", ticksUntilDisappearing);
		tag.putInt("ticks_until_reappearing", ticksUntilReappearing);
	}

	@Override
	public void loadAdditional(CompoundTag tag, HolderLookup.Provider lookupProvider) {
		super.loadAdditional(tag, lookupProvider);
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
