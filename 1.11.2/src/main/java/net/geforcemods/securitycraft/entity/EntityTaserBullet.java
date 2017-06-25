package net.geforcemods.securitycraft.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;

public class EntityTaserBullet extends EntityThrowable {

	private int deathTime = 2; //lives for 0.1 seconds aka 11 blocks range
	
	public EntityTaserBullet(World worldIn){
		super(worldIn);
		this.setSize(0.01F, 0.01F);
	}

	public EntityTaserBullet(World worldIn, EntityLivingBase shooter){
		super(worldIn, shooter);
		this.setSize(0.01F, 0.01F);
		this.setHeadingFromThrower(shooter, shooter.rotationPitch, shooter.rotationYaw, 0.0F, 6.0F, 0.0F);
	}

	public EntityTaserBullet(World worldIn, double x, double y, double z){
		super(worldIn, x, y, z);
		this.setSize(0.01F, 0.01F);
	}

	@Override
	protected float getGravityVelocity(){
		return 0.00F;
	}

	@Override
	public void onUpdate()
	{
		super.onUpdate();
		
		deathTime--;
		
		if(deathTime <= 0)
			setDead();
	}
	
	@Override
	protected void onImpact(RayTraceResult par1RayTraceResult)
	{
		if(!this.worldObj.isRemote)
		{
			if(par1RayTraceResult.typeOfHit == Type.ENTITY)
			{
				if(par1RayTraceResult.entityHit instanceof EntityPlayer)
				{
					if(((EntityPlayer)par1RayTraceResult.entityHit).capabilities.isCreativeMode)
						return;
				}

				if(par1RayTraceResult.entityHit instanceof EntityLivingBase)
				{
					((EntityLivingBase) par1RayTraceResult.entityHit).attackEntityFrom(DamageSource.generic, 1F);
					((EntityLivingBase) par1RayTraceResult.entityHit).addPotionEffect(new PotionEffect(Potion.getPotionFromResourceLocation("weakness"), 500, 2));
					((EntityLivingBase) par1RayTraceResult.entityHit).addPotionEffect(new PotionEffect(Potion.getPotionFromResourceLocation("nausea"), 500, 2));
					((EntityLivingBase) par1RayTraceResult.entityHit).addPotionEffect(new PotionEffect(Potion.getPotionFromResourceLocation("slowness"), 500, 2));
					this.setDead();
				}
			}
		}
	}
}
