package net.geforcemods.securitycraft.particle;

import net.geforcemods.securitycraft.SecurityCraft;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.world.World;

public class FloorTrapCloudParticle extends Particle {
	public FloorTrapCloudParticle(World level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
		super(level, x, y, z);
		setSize(0.01F, 0.01F);
		setParticleTexture(Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(SecurityCraft.MODID + ":particle/floor_trap_cloud"));
		particleScale *= level.rand.nextFloat() * 0.6F + 0.4F;
		canCollide = false;
		motionX = xSpeed;
		motionY = ySpeed;
		motionZ = zSpeed;
	}

	@Override
	public int getFXLayer() {
		return 1;
	}

	public static FloorTrapCloudParticle createParticle(int particleID, World level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, int... parameter) {
		FloorTrapCloudParticle particle = new FloorTrapCloudParticle(level, x, y, z, level.rand.nextGaussian() * 0.0075D, 0.005D, level.rand.nextGaussian() * 0.0075D);

		particle.particleMaxAge = level.rand.nextInt(40 - 10 + 1) + 10;
		particle.particleGravity = 0.0F;
		return particle;
	}
}
