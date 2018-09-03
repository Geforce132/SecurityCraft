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
		setHeadingFromThrower(shooter, shooter.rotationPitch, shooter.rotationYaw, 0.0F, 6.0F, 0.0F);
		powered = isPowered;
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
	protected void onImpact(RayTraceResult rayTrace)
	{
		if(!worldObj.isRemote)
		{
			if(rayTrace.typeOfHit == Type.ENTITY)
			{
				if(rayTrace.entityHit instanceof EntityPlayer)
				{
					if(((EntityPlayer)rayTrace.entityHit).capabilities.isCreativeMode || (EntityLivingBase)rayTrace.entityHit == getThrower() || !FMLServerHandler.instance().getServer().isPVPEnabled())
						return;
				}

				if(rayTrace.entityHit instanceof EntityLivingBase)
				{
					if(((EntityLivingBase) rayTrace.entityHit).attackEntityFrom(DamageSource.generic, 1F))
					{
						int strength = powered ? 4 : 1;
						int length = powered ? 400 : 200;

						((EntityLivingBase) rayTrace.entityHit).addPotionEffect(new PotionEffect(Potion.getPotionFromResourceLocation("weakness"), length, strength));
						((EntityLivingBase) rayTrace.entityHit).addPotionEffect(new PotionEffect(Potion.getPotionFromResourceLocation("nausea"), length, strength));
						((EntityLivingBase) rayTrace.entityHit).addPotionEffect(new PotionEffect(Potion.getPotionFromResourceLocation("slowness"), length, strength));
					}

					setDead();
				}
			}
		}
	}
}
