package net.geforcemods.securitycraft.particle;

import java.util.Locale;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.geforcemods.securitycraft.SCContent;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;
import net.minecraft.util.registry.Registry;

public class InterfaceHighlightParticleOptions implements IParticleData {
	//@formatter:off
	public static final Codec<InterfaceHighlightParticleOptions> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
					Codec.FLOAT.fieldOf("dir_x").forGetter(InterfaceHighlightParticleOptions::getDirX),
					Codec.FLOAT.fieldOf("dir_y").forGetter(InterfaceHighlightParticleOptions::getDirY),
					Codec.FLOAT.fieldOf("dir_z").forGetter(InterfaceHighlightParticleOptions::getDirZ),
					Codec.FLOAT.fieldOf("r").forGetter(InterfaceHighlightParticleOptions::getR),
					Codec.FLOAT.fieldOf("g").forGetter(InterfaceHighlightParticleOptions::getG),
					Codec.FLOAT.fieldOf("b").forGetter(InterfaceHighlightParticleOptions::getB),
					Codec.FLOAT.fieldOf("scale").forGetter(InterfaceHighlightParticleOptions::getScale))
			.apply(instance, InterfaceHighlightParticleOptions::new));
	//@formatter:on
	public static final IParticleData.IDeserializer<InterfaceHighlightParticleOptions> DESERIALIZER = new IParticleData.IDeserializer<InterfaceHighlightParticleOptions>() {
		@Override
		public InterfaceHighlightParticleOptions fromCommand(ParticleType<InterfaceHighlightParticleOptions> type, StringReader reader) throws CommandSyntaxException {
			float dirX, dirY, dirZ, r, g, b, scale;

			reader.expect(' ');
			dirX = (float) reader.readDouble();
			reader.expect(' ');
			dirY = (float) reader.readDouble();
			reader.expect(' ');
			dirZ = (float) reader.readDouble();
			reader.expect(' ');
			r = (float) reader.readDouble();
			reader.expect(' ');
			g = (float) reader.readDouble();
			reader.expect(' ');
			b = (float) reader.readDouble();
			reader.expect(' ');
			scale = (float) reader.readDouble();
			return new InterfaceHighlightParticleOptions(dirX, dirY, dirZ, r, g, b, scale);
		}

		@Override
		public InterfaceHighlightParticleOptions fromNetwork(ParticleType<InterfaceHighlightParticleOptions> type, PacketBuffer buf) {
			return new InterfaceHighlightParticleOptions(buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat());
		}
	};
	private final float r, g, b, dirX, dirY, dirZ, scale;

	public InterfaceHighlightParticleOptions(float r, float g, float b, float dirX, float dirY, float dirZ, float scale) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.dirX = dirX;
		this.dirY = dirY;
		this.dirZ = dirZ;
		this.scale = scale;
	}

	@Override
	public void writeToNetwork(PacketBuffer buf) {
		buf.writeFloat(dirX);
		buf.writeFloat(dirY);
		buf.writeFloat(dirZ);
		buf.writeFloat(r);
		buf.writeFloat(g);
		buf.writeFloat(b);
		buf.writeFloat(scale);
	}

	@Override
	public String writeToString() {
		//@formatter:off
		return String.format(Locale.ROOT,
				"%s %.2f %.2f %.2f %.2f %.2f %.2f %.2f",
				Registry.PARTICLE_TYPE.getKey(getType()),
				dirX,
				dirY,
				dirZ,
				r,
				g,
				b,
				scale);
		//@formatter:on
	}

	public float getR() {
		return r;
	}

	public float getG() {
		return g;
	}

	public float getB() {
		return b;
	}

	public float getDirX() {
		return dirX;
	}

	public float getDirY() {
		return dirY;
	}

	public float getDirZ() {
		return dirZ;
	}

	public float getScale() {
		return scale;
	}

	@Override
	public InterfaceHighlightParticleType getType() {
		return SCContent.INTERFACE_HIGHLIGHT.get();
	}
}
