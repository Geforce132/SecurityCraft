package net.geforcemods.securitycraft.entity.camera;

import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;

public final class CameraNightVisionEffectInstance extends PotionEffect {
	public CameraNightVisionEffectInstance() {
		super(MobEffects.NIGHT_VISION, Integer.MAX_VALUE, 0, false, false);
	}
}