package net.geforcemods.securitycraft.particle;

import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;

public class FloorTrapCloudParticle extends SpriteTexturedParticle {
	public FloorTrapCloudParticle(IAnimatedSprite spriteSet, ClientWorld level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
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
	public IParticleRenderType getRenderType() {
		return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
	}

	public static class Provider implements IParticleFactory<BasicParticleType> {
		private final IAnimatedSprite spriteSet;

		public Provider(IAnimatedSprite spriteSet) {
			this.spriteSet = spriteSet;
		}

		@Override
		public Particle createParticle(BasicParticleType type, ClientWorld level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			FloorTrapCloudParticle particle = new FloorTrapCloudParticle(spriteSet, level, x, y, z, level.random.nextGaussian() * 0.0075D, 0.005D, level.random.nextGaussian() * 0.0075D);

			particle.lifetime = level.random.nextInt(40 - 10 + 1) + 10;
			particle.gravity = 0.0F;
			return particle;
		}
	}
}
