package org.freeforums.geforce.securitycraft.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;

public class EntityTaserBullet extends EntityThrowable {

	public EntityTaserBullet(World worldIn){
		super(worldIn);
		this.setSize(0.01F, 0.01F);
	}

	public EntityTaserBullet(World worldIn, EntityLivingBase shooter){
		super(worldIn, shooter);
		this.setSize(0.01F, 0.01F);
	}

	public EntityTaserBullet(World worldIn, double x, double y, double z){
		super(worldIn, x, y, z);
		this.setSize(0.01F, 0.01F);
	}

	protected float func_70182_d(){
		return 6F;
	}

	protected float getGravityVelocity(){
		return 0.00F;
	}

	protected void onImpact(MovingObjectPosition par1MovingObjectPosition)
	{
		if(!this.worldObj.isRemote)
		{
			if(par1MovingObjectPosition.typeOfHit == MovingObjectType.ENTITY)
			{
				if(par1MovingObjectPosition.entityHit instanceof EntityPlayer)
				{
					if(((EntityPlayer)par1MovingObjectPosition.entityHit).capabilities.isCreativeMode)
						return;
				}

				if(par1MovingObjectPosition.entityHit instanceof EntityLivingBase)
				{
					((EntityLivingBase) par1MovingObjectPosition.entityHit).attackEntityFrom(DamageSource.generic, 1F);
					((EntityLivingBase) par1MovingObjectPosition.entityHit).addPotionEffect(new PotionEffect(Potion.weakness.id, 500, 2));
					((EntityLivingBase) par1MovingObjectPosition.entityHit).addPotionEffect(new PotionEffect(Potion.confusion.id, 500, 2));
					((EntityLivingBase) par1MovingObjectPosition.entityHit).addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 500, 2));
					this.setDead();
				}
			}
		}
	}
}
