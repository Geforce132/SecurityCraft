package net.geforcemods.securitycraft.blockentities;

import java.util.List;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableBlockEntity;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.BooleanOption;
import net.geforcemods.securitycraft.api.Option.DisabledOption;
import net.geforcemods.securitycraft.api.Option.DoubleOption;
import net.geforcemods.securitycraft.blocks.MotionActivatedLightBlock;
import net.geforcemods.securitycraft.entity.sentry.Sentry;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.ITickingBlockEntity;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class MotionActivatedLightBlockEntity extends CustomizableBlockEntity implements ITickingBlockEntity {
	private static final int TICKS_BETWEEN_ATTACKS = 5;
	private DoubleOption searchRadiusOption = new DoubleOption("searchRadius", 5.0D, 5.0D, 20.0D, 1.0D);
	private DisabledOption disabled = new DisabledOption(false);
	private int cooldown = TICKS_BETWEEN_ATTACKS;

	public MotionActivatedLightBlockEntity(BlockPos pos, BlockState state) {
		super(SCContent.MOTION_LIGHT_BLOCK_ENTITY.get(), pos, state);
	}

	@Override
	public void tick(Level level, BlockPos pos, BlockState state) {
		if (isDisabled() || cooldown-- > 0)
			return;

		List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, new AABB(pos).inflate(searchRadiusOption.get()), e -> !Utils.isEntityInvisible(e) && e.canBeSeenByAnyone() && !(e instanceof Sentry || e instanceof ArmorStand));
		boolean shouldBeOn = !entities.isEmpty();

		if (state.getValue(MotionActivatedLightBlock.LIT) != shouldBeOn)
			level.setBlockAndUpdate(pos, state.setValue(MotionActivatedLightBlock.LIT, shouldBeOn));

		cooldown = TICKS_BETWEEN_ATTACKS;
	}

	@Override
	public <T> void onOptionChanged(Option<T> option) {
		//turn off the light when it's disabled
		if (option == disabled && ((BooleanOption) option).get() && getBlockState().getValue(MotionActivatedLightBlock.LIT))
			level.setBlockAndUpdate(worldPosition, getBlockState().setValue(MotionActivatedLightBlock.LIT, false));

		super.onOptionChanged(option);
	}

	@Override
	public ModuleType[] acceptedModules() {
		return new ModuleType[] {};
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option<?>[] {
				searchRadiusOption, disabled
		};
	}

	public boolean isDisabled() {
		return disabled.get();
	}
}
