package net.geforcemods.securitycraft.entity;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.init.Particles;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityBouncingBetty extends Entity {

	/** How long the fuse is */
	public int fuse;

	public EntityBouncingBetty(World world){
		super(SCContent.eTypeBouncingBetty, world);
		preventEntitySpawning = true;
		setSize(0.500F, 0.200F);
	}

	public EntityBouncingBetty(World world, double x, double y, double z){
		this(world);
		setPosition(x, y, z);
		float f = (float)(Math.random() * Math.PI * 2.0D);
		motionX = -((float)Math.sin(f)) * 0.02F;
		motionY = 0.20000000298023224D;
		motionZ = -((float)Math.cos(f)) * 0.02F;
		fuse = 80;
		prevPosX = x;
		prevPosY = y;
		prevPosZ = z;
	}

	@Override
	protected void registerData() {}

	/**
	 * returns if this entity triggers Block.onEntityWalking on the blocks they walk on. used for spiders and wolves to
	 * prevent them from trampling crops
	 */
	@Override
	protected boolean canTriggerWalking()
	{
		return false;
	}

	/**
	 * Returns true if other Entities should be prevented from moving through this Entity.
	 */
	@Override
	public boolean canBeCollidedWith()
	{
		return !removed;
	}

	/**
	 * Called to update the entity's position/logic.
	 */
	@Override
	public void tick()
	{
		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;
		motionY -= 0.03999999910593033D;
		move(MoverType.SELF, motionX, motionY, motionZ);
		motionX *= 0.9800000190734863D;
		motionY *= 0.9800000190734863D;
		motionZ *= 0.9800000190734863D;

		if (onGround)
		{
			motionX *= 0.699999988079071D;
			motionZ *= 0.699999988079071D;
			motionY *= -0.5D;
		}

		if (fuse-- <= 0)
		{
			remove();

			if (!world.isRemote)
				explode();
		}
		else
			world.spawnParticle(Particles.SMOKE, posX, posY + 0.5D, posZ, 0.0D, 0.0D, 0.0D);
	}

	private void explode()
	{
		float f = 6.0F;

		if(ConfigHandler.smallerMineExplosion)
			world.createExplosion(this, posX, posY, posZ, (f / 2), true);
		else
			world.createExplosion(this, posX, posY, posZ, f, true);
	}

	/**
	 * (abstract) Protected helper method to write subclass entity data to NBT.
	 */
	@Override
	protected void writeAdditional(NBTTagCompound tag)
	{
		tag.setByte("Fuse", (byte)fuse);
	}

	/**
	 * (abstract) Protected helper method to read subclass entity data from NBT.
	 */
	@Override
	protected void readAdditional(NBTTagCompound tag)
	{
		fuse = tag.getByte("Fuse");
	}

	@OnlyIn(Dist.CLIENT)
	public float getShadowSize()
	{
		return 0.0F;
	}

}
