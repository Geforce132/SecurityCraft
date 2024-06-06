package net.geforcemods.securitycraft.particle;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class InterfaceHighlightParticleType extends ParticleType<InterfaceHighlightParticleOptions> {
	public InterfaceHighlightParticleType(boolean overrideLimiter) {
		super(overrideLimiter);
	}

	@Override
	public MapCodec<InterfaceHighlightParticleOptions> codec() {
		return InterfaceHighlightParticleOptions.CODEC;
	}

	@Override
	public StreamCodec<? super RegistryFriendlyByteBuf, InterfaceHighlightParticleOptions> streamCodec() {
		return InterfaceHighlightParticleOptions.STREAM_CODEC;
	}
}
