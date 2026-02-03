package net.geforcemods.securitycraft.particle;

import org.joml.Vector3f;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.geforcemods.securitycraft.SCContent;
import net.minecraft.core.particles.ScalableParticleOptionsBase;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;

public class InterfaceHighlightParticleOptions extends ScalableParticleOptionsBase {
	//@formatter:off
	public static final MapCodec<InterfaceHighlightParticleOptions> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
					ExtraCodecs.VECTOR3F.fieldOf("color").forGetter(InterfaceHighlightParticleOptions::getColor),
					ExtraCodecs.VECTOR3F.fieldOf("direction").forGetter(InterfaceHighlightParticleOptions::getDirection),
					SCALE.fieldOf("scale").forGetter(ScalableParticleOptionsBase::getScale))
			.apply(instance, InterfaceHighlightParticleOptions::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, InterfaceHighlightParticleOptions> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VECTOR3F, InterfaceHighlightParticleOptions::getColor,
			ByteBufCodecs.VECTOR3F, InterfaceHighlightParticleOptions::getDirection,
			ByteBufCodecs.FLOAT, ScalableParticleOptionsBase::getScale,
			InterfaceHighlightParticleOptions::new);
	//@formatter:on
	private final Vector3f color;
	private final Vector3f direction;

	public InterfaceHighlightParticleOptions(Vector3f color, Vector3f direction, float scale) {
		super(scale);
		this.color = color;
		this.direction = direction;
	}

	public Vector3f getColor() {
		return color;
	}

	public Vector3f getDirection() {
		return direction;
	}

	@Override
	public InterfaceHighlightParticleType getType() {
		return SCContent.INTERFACE_HIGHLIGHT.get();
	}
}
