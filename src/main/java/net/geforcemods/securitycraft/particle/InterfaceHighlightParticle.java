package net.geforcemods.securitycraft.particle;

import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.client.world.ClientWorld;

public class InterfaceHighlightParticle extends SpriteTexturedParticle {
	protected InterfaceHighlightParticle(ClientWorld level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, InterfaceHighlightParticleOptions options, IAnimatedSprite sprites) {
		super(level, x, y, z, xSpeed, ySpeed, zSpeed);
		float colorMultiplier = (float) ((Math.random() * 0.2F + 0.8F) * (Math.random() * 0.4D + 0.6D));

		hasPhysics = false;
		setColor(options.getR() * colorMultiplier, options.getG() * colorMultiplier, options.getB() * colorMultiplier);
		xd = options.getDirX();
		yd = options.getDirY();
		zd = options.getDirZ();
		setSprite(sprites.get(random));
		lifetime = (int) (20.0D / (Math.random() * 0.3D + 0.7D)) - 5;
	}

	@Override
	public void tick() {
		xo = x;
		yo = y;
		zo = z;

		if (age++ >= lifetime)
			remove();
		else
			move(xd, yd, zd);
	}

	@Override
	public IParticleRenderType getRenderType() {
		return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
	}

	@Override
	protected int getLightColor(float partialTicks) {
		return 0xF000F0;
	}

	public static class Provider implements IParticleFactory<InterfaceHighlightParticleOptions> {
		private final IAnimatedSprite sprites;

		public Provider(IAnimatedSprite sprites) {
			this.sprites = sprites;
		}

		@Override
		public Particle createParticle(InterfaceHighlightParticleOptions options, ClientWorld level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			return new InterfaceHighlightParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, options, sprites);
		}
	}
}
