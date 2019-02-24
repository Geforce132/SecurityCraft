package net.geforcemods.securitycraft.entity;

import net.geforcemods.securitycraft.SCContent;
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

	public EntityTaserBullet(World world){
		super(SCContent.eTypeTaserBullet, world);
		setSize(0.01F, 0.01F);
	}

	public EntityTaserBullet(World world, EntityLivingBase shooter, boolean isPowered){
		super(SCContent.eTypeTaserBullet, shooter, world);
		setSize(0.01F, 0.01F);
		powered = isPowered;
		this.shoot(shooter, shooter.rotationPitch, shooter.rotationYaw, 0.0F, 6.0F, 0.0F);
	}

	@Override
	protected float getGravityVelocity(){
		return 0.00F;
	}

	@Override
	public void tick()
	{
		super.tick();

		deathTime--;

		if(deathTime <= 0)
			remove();
	}

	@Override
	protected void onImpact(RayTraceResult result)
	{
		if(!world.isRemote)
		{
			if(result.type== Type.ENTITY)
			{
				if(result.entity instanceof EntityPlayer)
				{
					if(((EntityPlayer)result.entity).abilities.isCreativeMode || (EntityLivingBase)result.entity == getThrower() || !FMLServerHandler.instance().getServer().isPVPEnabled())
						return;
				}

				if(result.entity instanceof EntityLivingBase)
				{
					if(((EntityLivingBase) result.entity).attackEntityFrom(DamageSource.GENERIC, 1F))
					{
						int strength = powered ? 4 : 1;
						int length = powered ? 400 : 200;

						((EntityLivingBase) result.entity).addPotionEffect(new PotionEffect(Potion.getPotionById(18), length, strength));
						((EntityLivingBase) result.entity).addPotionEffect(new PotionEffect(Potion.getPotionById(9), length, strength));
						((EntityLivingBase) result.entity).addPotionEffect(new PotionEffect(Potion.getPotionById(2), length, strength));
					}

					remove();
				}
			}
		}
	}
}
