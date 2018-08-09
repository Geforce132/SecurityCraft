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
import net.minecraftforge.fml.server.FMLServerHandler;

public class EntityTaserBullet extends EntityThrowable {

	private int deathTime = 2; //lives for 0.1 seconds aka 11 blocks range
	private boolean powered;

	public EntityTaserBullet(World worldIn){
		super(worldIn);
		setSize(0.01F, 0.01F);
	}

	public EntityTaserBullet(World worldIn, EntityLivingBase shooter, boolean isPowered){
		super(worldIn, shooter);
		setSize(0.01F, 0.01F);
		powered = isPowered;
	}

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
	protected void onImpact(MovingObjectPosition par1MovingObjectPosition)
	{
		if(!worldObj.isRemote)
		{
			if(par1MovingObjectPosition.typeOfHit == MovingObjectType.ENTITY)
			{
				if(par1MovingObjectPosition.entityHit instanceof EntityPlayer)
				{
					if(((EntityPlayer)par1MovingObjectPosition.entityHit).capabilities.isCreativeMode || (EntityLivingBase)par1MovingObjectPosition.entityHit == getThrower() || !FMLServerHandler.instance().getServer().isPVPEnabled())
						return;
				}

				if(par1MovingObjectPosition.entityHit instanceof EntityLivingBase)
				{
					if(((EntityLivingBase) par1MovingObjectPosition.entityHit).attackEntityFrom(DamageSource.generic, 1F))
					{
						int strength = powered ? 4 : 1;
						int length = powered ? 400 : 200;

						((EntityLivingBase) par1MovingObjectPosition.entityHit).addPotionEffect(new PotionEffect(Potion.weakness.id, length, strength));
						((EntityLivingBase) par1MovingObjectPosition.entityHit).addPotionEffect(new PotionEffect(Potion.confusion.id, length, strength));
						((EntityLivingBase) par1MovingObjectPosition.entityHit).addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, length, strength));
					}

					setDead();
				}
			}
		}
	}
}
