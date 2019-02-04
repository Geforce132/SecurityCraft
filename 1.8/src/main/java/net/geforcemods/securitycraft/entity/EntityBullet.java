package net.geforcemods.securitycraft.entity;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityBullet extends Entity implements IProjectile
{
	public Entity shootingEntity;
	private int ticksInAir;
	private double damage = 2.0D;
	private int knockbackStrength;

	public EntityBullet(World world)
	{
		super(world);
		renderDistanceWeight = 10.0D;
		setSize(0.15F, 0.1F);
	}

	public EntityBullet(World world, EntityLivingBase shooter)
	{
		super(world);
		renderDistanceWeight = 10.0D;
		shootingEntity = shooter;
		setSize(0.15F, 0.1F);
		setLocationAndAngles(shooter.posX, shooter.posY + shooter.getEyeHeight(), shooter.posZ, shooter.rotationYaw, shooter.rotationPitch);
		posX -= MathHelper.cos(rotationYaw / 180.0F * (float)Math.PI) * 0.16F;
		posY -= 0.10000000149011612D;
		posZ -= MathHelper.sin(rotationYaw / 180.0F * (float)Math.PI) * 0.16F;
		setPosition(posX, posY, posZ);
		motionX = -MathHelper.sin(rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(rotationPitch / 180.0F * (float)Math.PI);
		motionZ = MathHelper.cos(rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(rotationPitch / 180.0F * (float)Math.PI);
		motionY = (-MathHelper.sin(rotationPitch / 180.0F * (float)Math.PI));
		setThrowableHeading(motionX, motionY, motionZ, 1.6F * 1.5F, 1.0F);
	}

	@Override
	protected void entityInit() {}

	/**
	 * Similar to setArrowHeading, it's point the throwable entity to a x, y, z direction.
	 */
	public void setThrowableHeading(double x, double y, double z, float velocity, float inaccuracy)
	{
		float f = MathHelper.sqrt_double(x * x + y * y + z * z);

		x = x / f;
		y = y / f;
		z = z / f;
		x = x + rand.nextGaussian() * (rand.nextBoolean() ? -1 : 1) * 0.007499999832361937D * inaccuracy;
		y = y + rand.nextGaussian() * (rand.nextBoolean() ? -1 : 1) * 0.007499999832361937D * inaccuracy;
		z = z + rand.nextGaussian() * (rand.nextBoolean() ? -1 : 1) * 0.007499999832361937D * inaccuracy;
		x = x * velocity;
		y = y * velocity;
		z = z * velocity;
		motionX = x;
		motionY = y;
		motionZ = z;
		float f1 = MathHelper.sqrt_double(x * x + z * z);
		prevRotationYaw = rotationYaw = (float)(Math.atan2(x, z) * 180.0D / Math.PI);
		prevRotationPitch = rotationPitch = (float)(Math.atan2(y, f1) * 180.0D / Math.PI);
	}

	@SideOnly(Side.CLIENT)
	public void setPositionAndRotation2(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean p_180426_10_)
	{
		setPosition(x, y, z);
		setRotation(yaw, pitch);
	}

	/**
	 * Sets the velocity to the args. Args: x, y, z
	 */
	@SideOnly(Side.CLIENT)
	public void setVelocity(double x, double y, double z)
	{
		motionX = x;
		motionY = y;
		motionZ = z;

		if(prevRotationPitch == 0.0F && prevRotationYaw == 0.0F)
		{
			float f = MathHelper.sqrt_double(x * x + z * z);

			prevRotationYaw = rotationYaw = (float)(Math.atan2(x, z) * 180.0D / Math.PI);
			prevRotationPitch = rotationPitch = (float)(Math.atan2(y, f) * 180.0D / Math.PI);
			prevRotationPitch = rotationPitch;
			prevRotationYaw = rotationYaw;
			setLocationAndAngles(posX, posY, posZ, rotationYaw, rotationPitch);
		}
	}

	/**
	 * Called to update the entity's position/logic.
	 */
	public void onUpdate()
	{
		super.onUpdate();

		if(prevRotationPitch == 0.0F && prevRotationYaw == 0.0F)
		{
			float f = MathHelper.sqrt_double(motionX * motionX + motionZ * motionZ);

			prevRotationYaw = rotationYaw = (float)(Math.atan2(motionX, motionZ) * 180.0D / Math.PI);
			prevRotationPitch = rotationPitch = (float)(Math.atan2(motionY, f) * 180.0D / Math.PI);
		}

		++ticksInAir;
		Vec3 vec31 = new Vec3(posX, posY, posZ);
		Vec3 vec3 = new Vec3(posX + motionX, posY + motionY, posZ + motionZ);
		MovingObjectPosition movingobjectposition = worldObj.rayTraceBlocks(vec31, vec3, false, true, false);
		vec31 = new Vec3(posX, posY, posZ);
		vec3 = new Vec3(posX + motionX, posY + motionY, posZ + motionZ);

		if(movingobjectposition != null)
			vec3 = new Vec3(movingobjectposition.hitVec.xCoord, movingobjectposition.hitVec.yCoord, movingobjectposition.hitVec.zCoord);

		Entity entity = null;
		List<Entity> list = worldObj.getEntitiesWithinAABBExcludingEntity(this, getEntityBoundingBox().addCoord(motionX, motionY, motionZ).expand(1.0D, 1.0D, 1.0D));
		double d0 = 0.0D;

		for(int i = 0; i < list.size(); ++i)
		{
			Entity entity1 = list.get(i);

			if(entity1.canBeCollidedWith() && (entity1 != shootingEntity || ticksInAir >= 5))
			{
				float f1 = 0.3F;
				AxisAlignedBB axisalignedbb1 = entity1.getEntityBoundingBox().expand(f1, f1, f1);
				MovingObjectPosition movingobjectposition1 = axisalignedbb1.calculateIntercept(vec31, vec3);

				if(movingobjectposition1 != null)
				{
					double d1 = vec31.squareDistanceTo(movingobjectposition1.hitVec);

					if(d1 < d0 || d0 == 0.0D)
					{
						entity = entity1;
						d0 = d1;
					}
				}
			}
		}

		if(entity != null)
			movingobjectposition = new MovingObjectPosition(entity);

		if(movingobjectposition != null && movingobjectposition.entityHit != null && movingobjectposition.entityHit instanceof EntityPlayer)
		{
			EntityPlayer entityplayer = (EntityPlayer)movingobjectposition.entityHit;

			if(entityplayer.capabilities.disableDamage || shootingEntity instanceof EntityPlayer && !((EntityPlayer)shootingEntity).canAttackPlayer(entityplayer))
				movingobjectposition = null;
		}

		if(movingobjectposition != null)
		{
			if(movingobjectposition.entityHit != null)
			{
				float f2 = MathHelper.sqrt_double(motionX * motionX + motionY * motionY + motionZ * motionZ);
				int l = MathHelper.ceiling_double_int(f2 * damage);

				DamageSource damagesource;

				if(shootingEntity == null)
					damagesource = DamageSource.causeThrownDamage(this, this);
				else
					damagesource = DamageSource.causeThrownDamage(this, shootingEntity);

				if(movingobjectposition.entityHit.attackEntityFrom(damagesource, l))
				{
					if(movingobjectposition.entityHit instanceof EntityLivingBase)
					{
						if(knockbackStrength > 0)
						{
							float f7 = MathHelper.sqrt_double(motionX * motionX + motionZ * motionZ);

							if(f7 > 0.0F)
								movingobjectposition.entityHit.addVelocity(motionX * knockbackStrength * 0.6000000238418579D / f7, 0.1D, motionZ * knockbackStrength * 0.6000000238418579D / f7);
						}

						if(shootingEntity != null && movingobjectposition.entityHit != shootingEntity && movingobjectposition.entityHit instanceof EntityPlayer && shootingEntity instanceof EntityPlayerMP)
							((EntityPlayerMP)shootingEntity).playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(6, 0.0F));
					}

					if(!(movingobjectposition.entityHit instanceof EntityEnderman))
						setDead();
				}
				else
					setDead();
			}
		}

		float f3 = MathHelper.sqrt_double(motionX * motionX + motionZ * motionZ);
		this.rotationYaw = (float)(Math.atan2(motionX, motionZ) * 180.0D / Math.PI);

		posX += motionX;
		posY += motionY;
		posZ += motionZ;

		for(rotationPitch = (float)(Math.atan2(motionY, f3) * 180.0D / Math.PI); rotationPitch - prevRotationPitch < -180.0F; prevRotationPitch -= 360.0F)
		{
			;
		}

		while(rotationPitch - prevRotationPitch >= 180.0F)
		{
			prevRotationPitch += 360.0F;
		}

		while(rotationYaw - prevRotationYaw < -180.0F)
		{
			prevRotationYaw -= 360.0F;
		}

		while (rotationYaw - prevRotationYaw >= 180.0F)
		{
			prevRotationYaw += 360.0F;
		}

		float f4 = 0.99F;
		float f6 = 0.05F;

		rotationPitch = prevRotationPitch + (rotationPitch - prevRotationPitch) * 0.2F;
		rotationYaw = prevRotationYaw + (rotationYaw - prevRotationYaw) * 0.2F;

		if(isInWater())
		{
			for(int i1 = 0; i1 < 4; ++i1)
			{
				float f8 = 0.25F;

				worldObj.spawnParticle(EnumParticleTypes.WATER_BUBBLE, posX - motionX * f8, posY - motionY * f8, posZ - motionZ * f8, motionX, motionY, motionZ, new int[0]);
			}

			f4 = 0.6F;
		}

		if(isWet())
			extinguish();

		motionX *= f4;
		motionY *= f4;
		motionZ *= f4;
		motionY -= f6;
		setPosition(posX, posY, posZ);
		doBlockCollisions();
	}

	/**
	 * (abstract) Protected helper method to write subclass entity data to NBT.
	 */
	public void writeEntityToNBT(NBTTagCompound tagCompound)
	{
		tagCompound.setDouble("damage", damage);
	}

	/**
	 * (abstract) Protected helper method to read subclass entity data from NBT.
	 */
	public void readEntityFromNBT(NBTTagCompound tagCompund)
	{
		if(tagCompund.hasKey("damage", 99))
			damage = tagCompund.getDouble("damage");
	}

	@Override
	protected boolean canTriggerWalking()
	{
		return false;
	}

	public void setDamage(double damage)
	{
		this.damage = damage;
	}

	public double getDamage()
	{
		return damage;
	}

	public void setKnockbackStrength(int knockbackStrength)
	{
		this.knockbackStrength = knockbackStrength;
	}

	public boolean canAttackWithItem()
	{
		return false;
	}

	public float getEyeHeight()
	{
		return 0.0F;
	}
}