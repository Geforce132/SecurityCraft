package net.geforcemods.securitycraft.entity;

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

	private int deathTime = 2; //lives for 0.1 seconds aka 11 blocks range
	private boolean powered;

	public EntityTaserBullet(World world){
		super(world);
		setSize(0.01F, 0.01F);
	}

	public EntityTaserBullet(World world, EntityLivingBase shooter, boolean isPowered){
		super(world, shooter);
		setSize(0.01F, 0.01F);
		powered = isPowered;
	}

	@Override
	protected float func_70182_d(){
		return 6F;
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
	protected void onImpact(MovingObjectPosition mop)
	{
		if(!worldObj.isRemote)
			if(mop.typeOfHit == MovingObjectType.ENTITY)
			{
				if(mop.entityHit instanceof EntityPlayer)
					if(((EntityPlayer)mop.entityHit).capabilities.isCreativeMode || (EntityLivingBase)mop.entityHit == getThrower())
						return;

				if(mop.entityHit instanceof EntityLivingBase)
				{
					int strength = powered ? 4 : 1;
					int length = powered ? 400 : 200;

					((EntityLivingBase) mop.entityHit).attackEntityFrom(DamageSource.generic, 1F);
					((EntityLivingBase) mop.entityHit).addPotionEffect(new PotionEffect(Potion.weakness.id, length, strength));
					((EntityLivingBase) mop.entityHit).addPotionEffect(new PotionEffect(Potion.confusion.id, length, strength));
					((EntityLivingBase) mop.entityHit).addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, length, strength));
					setDead();
				}
			}
	}
}
