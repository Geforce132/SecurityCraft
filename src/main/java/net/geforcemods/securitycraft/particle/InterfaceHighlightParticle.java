package net.geforcemods.securitycraft.particle;

import net.geforcemods.securitycraft.SecurityCraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.world.World;

public class InterfaceHighlightParticle extends Particle {
	public InterfaceHighlightParticle(World level, double x, double y, double z, double r, double g, double b, double dirX, double dirY, double dirZ) {
		super(level, x, y, z, dirX, dirY, dirZ);
		float colorMultiplier = (float) ((Math.random() * 0.2F + 0.8F) * (Math.random() * 0.4D + 0.6D));

		canCollide = false;
		particleRed = (float) (r * colorMultiplier);
		particleGreen = (float) (g * colorMultiplier);
		particleBlue = (float) (b * colorMultiplier);
		motionX = dirX;
		motionY = dirY;
		motionZ = dirZ;
		setParticleTextureIndex(SecurityCraft.RANDOM.nextInt(3) + 3);
		particleMaxAge = (int) (20.0D / (Math.random() * 0.3D + 0.7D)) - 5;
	}

	@Override
	public void onUpdate() {
		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;

		if (particleAge++ >= particleMaxAge)
			setExpired();
		else
			move(motionX, motionY, motionZ);
	}

	@Override
	public int getBrightnessForRender(float partialTicks) {
		return 0xF000F0;
	}
}
