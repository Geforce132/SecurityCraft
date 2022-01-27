package net.geforcemods.securitycraft.blockentity;

import java.util.List;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableBlockEntity;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.DoubleOption;
import net.geforcemods.securitycraft.blocks.MotionActivatedLightBlock;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.EntityUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.math.AxisAlignedBB;

public class MotionActivatedLightBlockEntity extends CustomizableBlockEntity implements ITickableTileEntity {
	private static final int TICKS_BETWEEN_ATTACKS = 5;
	private DoubleOption searchRadiusOption = new DoubleOption(this::getBlockPos, "searchRadius", 5.0D, 5.0D, 20.0D, 1.0D, true);
	private int cooldown = TICKS_BETWEEN_ATTACKS;

	public MotionActivatedLightBlockEntity() {
		super(SCContent.beTypeMotionLight);
	}

	@Override
	public void tick() {
		if (cooldown-- > 0)
			return;

		List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, new AxisAlignedBB(worldPosition).inflate(searchRadiusOption.get()), e -> !EntityUtils.isInvisible(e) && !e.isSpectator());
		boolean shouldBeOn = !entities.isEmpty();

		if (getBlockState().getValue(MotionActivatedLightBlock.LIT) != shouldBeOn)
			level.setBlockAndUpdate(worldPosition, getBlockState().setValue(MotionActivatedLightBlock.LIT, shouldBeOn));

		cooldown = TICKS_BETWEEN_ATTACKS;
	}

	@Override
	public ModuleType[] acceptedModules() {
		return new ModuleType[] {};
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option<?>[] {
			searchRadiusOption
		};
	}
}
