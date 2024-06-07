package net.geforcemods.securitycraft.particle;

import java.util.Locale;

import org.joml.Vector3f;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.geforcemods.securitycraft.SCContent;
import net.minecraft.core.particles.DustParticleOptionsBase;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.ExtraCodecs;

public class InterfaceHighlightParticleOptions extends DustParticleOptionsBase {
	//@formatter:off
	public static final Codec<InterfaceHighlightParticleOptions> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
					ExtraCodecs.VECTOR3F.fieldOf("direction").forGetter(InterfaceHighlightParticleOptions::getDirection),
					ExtraCodecs.VECTOR3F.fieldOf("color").forGetter(DustParticleOptionsBase::getColor),
					Codec.FLOAT.fieldOf("scale").forGetter(DustParticleOptionsBase::getScale))
			.apply(instance, InterfaceHighlightParticleOptions::new));
	//@formatter:on
	public static final ParticleOptions.Deserializer<InterfaceHighlightParticleOptions> DESERIALIZER = new ParticleOptions.Deserializer<>() {
		@Override
		public InterfaceHighlightParticleOptions fromCommand(ParticleType<InterfaceHighlightParticleOptions> type, StringReader reader) throws CommandSyntaxException {
			Vector3f direction = DustParticleOptionsBase.readVector3f(reader);
			Vector3f color = DustParticleOptionsBase.readVector3f(reader);
			float scale;

			reader.expect(' ');
			scale = reader.readFloat();
			return new InterfaceHighlightParticleOptions(color, direction, scale);
		}

		@Override
		public InterfaceHighlightParticleOptions fromNetwork(ParticleType<InterfaceHighlightParticleOptions> type, FriendlyByteBuf buf) {
			Vector3f direction = DustParticleOptionsBase.readVector3f(buf);
			Vector3f color = DustParticleOptionsBase.readVector3f(buf);
			float scale = buf.readFloat();

			return new InterfaceHighlightParticleOptions(color, direction, scale);
		}
	};
	private final Vector3f direction;

	public InterfaceHighlightParticleOptions(Vector3f color, Vector3f direction, float scale) {
		super(color, scale);
		this.direction = direction;
	}

	@Override
	public void writeToNetwork(FriendlyByteBuf buf) {
		buf.writeFloat(direction.x());
		buf.writeFloat(direction.y());
		buf.writeFloat(direction.z());
		super.writeToNetwork(buf);
	}

	@Override
	public String writeToString() {
		//@formatter:off
		return String.format(Locale.ROOT,
				"%s %.2f %.2f %.2f %.2f %.2f %.2f %.2f",
				BuiltInRegistries.PARTICLE_TYPE.getKey(getType()),
				direction.x(),
				direction.y(),
				direction.z(),
				color.x(),
				color.y(),
				color.z(),
				scale);
		//@formatter:on
	}

	public Vector3f getDirection() {
		return direction;
	}

	@Override
	public ParticleType<InterfaceHighlightParticleOptions> getType() {
		return SCContent.INTERFACE_HIGHLIGHT.get();
	}
}
