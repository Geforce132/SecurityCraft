package net.geforcemods.securitycraft.blockentities;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.EnumOption;
import net.geforcemods.securitycraft.api.Option.IgnoreOwnerOption;
import net.geforcemods.securitycraft.api.Option.IntOption;
import net.geforcemods.securitycraft.blocks.SometimesVisibleBlock;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.misc.TargetingMode;
import net.geforcemods.securitycraft.util.EntityUtils;
import net.geforcemods.securitycraft.util.ITickingBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class FloorTrapBlockEntity extends DisguisableBlockEntity implements ITickingBlockEntity {
	private IgnoreOwnerOption ignoreOwner = new IgnoreOwnerOption(true);
	private IntOption disappearDelay = new IntOption("disappearDelay", 5, 0, 200, 1, true);
	private IntOption reappearDelay = new IntOption("reappearDelay", 20, 5, 200, 1, true);
	private EnumOption<TargetingMode> targetingMode = new EnumOption<>("targetingMode", TargetingMode.PLAYERS, TargetingMode.class) {
		@Override
		public Component getValueName() {
			return value.translate();
		}
	};
	private boolean shouldDisappear = false, shouldReappear = false;
	private int ticksUntilDisappearing = -1, ticksUntilReappearing = -1;

	public FloorTrapBlockEntity(BlockPos pos, BlockState state) {
		super(SCContent.FLOOR_TRAP_BLOCK_ENTITY.get(), pos, state);
	}

	@Override
	public void tick(Level level, BlockPos pos, BlockState state) { //server only as per FloorTrapBlock
		if (!shouldReappear && shouldDisappear) {
			if (ticksUntilDisappearing-- <= 0) {
				level.setBlockAndUpdate(pos, getBlockState().setValue(SometimesVisibleBlock.INVISIBLE, true));
				level.playSound(null, pos, SoundEvents.ENDER_DRAGON_FLAP, SoundSource.BLOCKS, 1.0F, 2.0F);
				((ServerLevel) level).sendParticles(ParticleTypes.SMOKE, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 5, 0.0D, 0.0D, 0.0D, 0.0D);
				shouldDisappear = false;
				shouldReappear = true;
				ticksUntilReappearing = reappearDelay.get();
			}

			return;
		}

		TargetingMode mode = targetingMode.get();

		//@formatter:off
		shouldDisappear = level.getEntitiesOfClass(LivingEntity.class, new AABB(pos.above()), e -> e.canBeSeenByAnyone() && !isAllowed(e) && !(EntityUtils.doesEntityOwn(e, level, pos) && ignoresOwner()))
			.stream()
			.anyMatch(entity -> mode.allowsPlayers() && entity instanceof Player || mode.allowsMobs());
		//@formatter:on

		if (shouldReappear) {
			if (!shouldDisappear && ticksUntilReappearing-- <= 0) {
				level.setBlockAndUpdate(pos, getBlockState().setValue(SometimesVisibleBlock.INVISIBLE, false));
				shouldReappear = false;
			}
		}
		else if (shouldDisappear)
			ticksUntilDisappearing = disappearDelay.get();
	}

	@Override
	public void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.putBoolean("should_disappear", shouldDisappear);
		tag.putBoolean("should_reappear", shouldReappear);
		tag.putInt("ticks_until_disappearing", ticksUntilDisappearing);
		tag.putInt("ticks_until_reappearing", ticksUntilReappearing);
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		shouldDisappear = tag.getBoolean("should_disappear");
		shouldReappear = tag.getBoolean("should_reappear");
		ticksUntilDisappearing = tag.getInt("ticks_until_disappearing");
		ticksUntilReappearing = tag.getInt("ticks_until_reappearing");
	}

	@Override
	public ModuleType[] acceptedModules() {
		return new ModuleType[] {
				ModuleType.DISGUISE, ModuleType.ALLOWLIST
		};
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[] {
				ignoreOwner, targetingMode, disappearDelay, reappearDelay
		};
	}

	public boolean ignoresOwner() {
		return ignoreOwner.get();
	}
}
