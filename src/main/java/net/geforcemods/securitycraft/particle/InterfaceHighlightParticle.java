package net.geforcemods.securitycraft.particle;

import org.joml.Vector3f;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.DustParticleBase;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class InterfaceHighlightParticle extends DustParticleBase<InterfaceHighlightParticleOptions> {
	private final TextureAtlasSprite sprite;

	protected InterfaceHighlightParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, InterfaceHighlightParticleOptions options, SpriteSet sprites) {
		super(level, x, y, z, xSpeed, ySpeed, zSpeed, options, sprites);
		float colorChangeMultiplier = random.nextFloat() * 0.4F + 0.6F;
		Vector3f color = randomizeColor(options.getColor(), colorChangeMultiplier);

		hasPhysics = false;
		setColor(color.x, color.y, color.z);
		setParticleSpeed(options.getDirection().x, options.getDirection().y, options.getDirection().z);
		sprite = sprites.get(random);
		setSprite(sprite);
		age = 5;
	}

	private Vector3f randomizeColor(Vector3f vector, float multiplier) {
		return new Vector3f(randomizeColor(vector.x(), multiplier), randomizeColor(vector.y(), multiplier), randomizeColor(vector.z(), multiplier));
	}

	@Override
	public void tick() {
		super.tick();
		setSprite(sprite);
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
