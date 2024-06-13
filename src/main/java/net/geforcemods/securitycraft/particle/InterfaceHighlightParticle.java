package net.geforcemods.securitycraft.particle;

import org.joml.Vector3f;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.DustParticleBase;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class InterfaceHighlightParticle extends DustParticleBase<InterfaceHighlightParticleOptions> {
	private final TextureAtlasSprite permanentSprite;

	protected InterfaceHighlightParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, InterfaceHighlightParticleOptions options, SpriteSet sprites) {
		super(level, x, y, z, xSpeed, ySpeed, zSpeed, options, sprites);
		float colorChangeMultiplier = random.nextFloat() * 0.4F + 0.6F;
		Vector3f color = options.getColor().mul(randomizeColor(1.0F, colorChangeMultiplier));

		hasPhysics = false;
		setColor(color.x, color.y, color.z);
		setParticleSpeed(options.getDirection().x, options.getDirection().y, options.getDirection().z);
		permanentSprite = sprites.get(random);
		setSprite(permanentSprite);
		lifetime = (int) (20.0D / (random.nextDouble() * 0.3D + 0.7D)) - 5;
	}

	@Override
	public void tick() {
		super.tick();
		setSprite(permanentSprite);
	}

	@Override
	protected int getLightColor(float partialTicks) {
		return LightTexture.FULL_BRIGHT;
	}

	public static class Provider implements ParticleProvider<InterfaceHighlightParticleOptions> {
		private final SpriteSet sprites;

		public Provider(SpriteSet sprites) {
			this.sprites = sprites;
		}

		@Override
		public Particle createParticle(InterfaceHighlightParticleOptions options, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			return new InterfaceHighlightParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, options, sprites);
		}
	}
}
