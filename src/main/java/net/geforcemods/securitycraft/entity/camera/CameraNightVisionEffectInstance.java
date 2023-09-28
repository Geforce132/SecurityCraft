package net.geforcemods.securitycraft.entity.camera;

import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;

public final class CameraNightVisionEffectInstance extends EffectInstance {
	public CameraNightVisionEffectInstance() {
		super(Effects.NIGHT_VISION, Integer.MAX_VALUE, 0, false, false);
	}
}