package net.geforcemods.securitycraft.entity;

import net.geforcemods.securitycraft.SCContent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.network.IPacket;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class EntityTaserBullet extends ThrowableEntity {

	private int deathTime = 2; //lives for 0.1 seconds aka 11 blocks range
	private boolean powered;

	public EntityTaserBullet(EntityType<EntityTaserBullet> type, World world){
		super(SCContent.eTypeTaserBullet, world);
	}

	public EntityTaserBullet(World world, LivingEntity shooter, boolean isPowered){
		super(SCContent.eTypeTaserBullet, shooter, world);
		powered = isPowered;
		this.shoot(shooter, shooter.rotationPitch, shooter.rotationYaw, 0.0F, 6.0F, 0.0F);
	}

	@Override
	protected void registerData() {}

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
	protected void onImpact(RayTraceResult baseResult)
	{
		if(!world.isRemote)
		{
			if(baseResult.getType() == Type.ENTITY)
			{
				EntityRayTraceResult result = (EntityRayTraceResult)baseResult;

				if(result.getEntity() instanceof PlayerEntity)
				{
					if(((PlayerEntity)result.getEntity()).abilities.isCreativeMode || (LivingEntity)result.getEntity() == getThrower() || !ServerLifecycleHooks.getCurrentServer().isPVPEnabled())
						return;
				}

				if(result.getEntity() instanceof LivingEntity)
				{
					if(((LivingEntity) result.getEntity()).attackEntityFrom(DamageSource.GENERIC, 1F))
					{
						int strength = powered ? 4 : 1;
						int length = powered ? 400 : 200;

						((LivingEntity) result.getEntity()).addPotionEffect(new EffectInstance(Effect.getPotionById(18), length, strength));
						((LivingEntity) result.getEntity()).addPotionEffect(new EffectInstance(Effect.getPotionById(9), length, strength));
						((LivingEntity) result.getEntity()).addPotionEffect(new EffectInstance(Effect.getPotionById(2), length, strength));
					}

					remove();
				}
			}
		}
	}

	@Override
	public IPacket<?> createSpawnPacket()
	{
		return NetworkHooks.getEntitySpawningPacket(this);
	}
}
