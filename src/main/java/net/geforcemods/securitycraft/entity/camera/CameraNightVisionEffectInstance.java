package net.geforcemods.securitycraft.entity.camera;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public final class CameraNightVisionEffectInstance extends MobEffectInstance {
	public CameraNightVisionEffectInstance() {
		super(MobEffects.NIGHT_VISION, -1, 0, false, false);
	}
}