package net.geforcemods.securitycraft.particle;

import net.geforcemods.securitycraft.SecurityCraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;

public class FloorTrapCloudParticle extends TextureSheetParticle {
	public FloorTrapCloudParticle(SpriteSet spriteSet, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
		super(level, x, y, z);
		setSize(0.01F, 0.01F);
		pickSprite(spriteSet);
		quadSize *= random.nextFloat() * 0.6F + 0.4F;
		hasPhysics = false;
		xd = xSpeed;
		yd = ySpeed;
		zd = zSpeed;
	}

	@Override
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
	}

	public static class Provider implements ParticleProvider<SimpleParticleType> {
		private final SpriteSet spriteSet;

		public Provider(SpriteSet spriteSet) {
			this.spriteSet = spriteSet;
		}

		@Override
		public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			FloorTrapCloudParticle particle = new FloorTrapCloudParticle(spriteSet, level, x, y, z, SecurityCraft.RANDOM.nextGaussian() * 0.0075D, 0.005D, SecurityCraft.RANDOM.nextGaussian() * 0.0075D);

			particle.lifetime = Mth.randomBetweenInclusive(level.random, 10, 40);
			particle.gravity = 0.0F;
			return particle;
		}
	}
}
