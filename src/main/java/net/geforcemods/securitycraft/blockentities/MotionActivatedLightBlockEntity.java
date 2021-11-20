package net.geforcemods.securitycraft.blockentities;

import java.util.List;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableBlockEntity;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.DoubleOption;
import net.geforcemods.securitycraft.blocks.MotionActivatedLightBlock;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.EntityUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class MotionActivatedLightBlockEntity extends CustomizableBlockEntity {
	private static final int TICKS_BETWEEN_ATTACKS = 5;
	private DoubleOption searchRadiusOption = new DoubleOption(this::getBlockPos, "searchRadius", 5.0D, 5.0D, 20.0D, 1.0D, true);
	private int cooldown = TICKS_BETWEEN_ATTACKS;

	public MotionActivatedLightBlockEntity(BlockPos pos, BlockState state)
	{
		super(SCContent.beTypeMotionLight, pos, state);
	}

	@Override
	public void tick(Level level, BlockPos pos, BlockState state) {
		super.tick(level, pos, state);

		if(cooldown-- > 0)
			return;

		List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, new AABB(pos).inflate(searchRadiusOption.get()), e -> !PlayerUtils.isPlayerMountedOnCamera(e) && !EntityUtils.isInvisible(e));
		boolean shouldBeOn = !entities.isEmpty();

		if(state.getValue(MotionActivatedLightBlock.LIT) != shouldBeOn)
			level.setBlockAndUpdate(pos, state.setValue(MotionActivatedLightBlock.LIT, shouldBeOn));

		cooldown = TICKS_BETWEEN_ATTACKS;
	}

	@Override
	public ModuleType[] acceptedModules() {
		return new ModuleType[] {};
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option<?>[] {searchRadiusOption};
	}
}
