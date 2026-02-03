package net.geforcemods.securitycraft.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public class FloorTrapCloudParticle extends SingleQuadParticle {
	public FloorTrapCloudParticle(TextureAtlasSprite sprite, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
		super(level, x, y, z, sprite);
		setSize(0.01F, 0.01F);
		quadSize *= random.nextFloat() * 0.6F + 0.4F;
		hasPhysics = false;
		xd = xSpeed;
		yd = ySpeed;
		zd = zSpeed;
	}

	@Override
	public SingleQuadParticle.Layer getLayer() {
		return SingleQuadParticle.Layer.OPAQUE;
	}

	public static class Provider implements ParticleProvider<SimpleParticleType> {
		private final SpriteSet spriteSet;

		public Provider(SpriteSet spriteSet) {
			this.spriteSet = spriteSet;
		}

		@Override
		public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, RandomSource random) {
			FloorTrapCloudParticle particle = new FloorTrapCloudParticle(spriteSet.get(random), level, x, y, z, random.nextGaussian() * 0.0075D, 0.005D, random.nextGaussian() * 0.0075D);

			particle.lifetime = Mth.randomBetweenInclusive(level.random, 10, 40);
			particle.gravity = 0.0F;
			return particle;
		}
	}
}
