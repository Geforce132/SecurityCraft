package net.geforcemods.securitycraft.particle;

import com.mojang.serialization.Codec;

import net.minecraft.core.particles.ParticleType;

public class InterfaceHighlightParticleType extends ParticleType<InterfaceHighlightParticleOptions> {
	public InterfaceHighlightParticleType(boolean overrideLimiter) {
		super(overrideLimiter, InterfaceHighlightParticleOptions.DESERIALIZER);
	}

	@Override
	public Codec<InterfaceHighlightParticleOptions> codec() {
		return InterfaceHighlightParticleOptions.CODEC;
	}
}
