package net.geforcemods.securitycraft.entity;

import java.util.Collection;

import com.google.common.collect.Sets;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.Owner;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class BulletEntity extends AbstractArrowEntity
{
	private static final DataParameter<Owner> OWNER = EntityDataManager.<Owner>createKey(BulletEntity.class, Owner.getSerializer());
	private Collection<EffectInstance> potionEffects = Sets.newHashSet();

	public BulletEntity(EntityType<BulletEntity> type, World world)
	{
		super(SCContent.eTypeBullet, world);
	}

	public BulletEntity(World world, SentryEntity shooter)
	{
		super(SCContent.eTypeBullet, shooter, world);

		Owner owner =  shooter.getOwner();

		this.potionEffects = shooter.getActivePotionEffects();
		dataManager.set(OWNER, new Owner(owner.getName(), owner.getUUID()));
	}

	/**
	 * @return The owner of the sentry which shot this bullet
	 */
	public Owner getOwner()
	{
		return dataManager.get(OWNER);
	}

	@Override
	protected void registerData()
	{
		super.registerData();
		dataManager.register(OWNER, new Owner());
	}

	@Override
	protected void onEntityHit(EntityRayTraceResult raytraceResult)
	{
		Entity target = raytraceResult.getEntity();

		if(!(target instanceof SentryEntity))
		{
			target.attackEntityFrom(DamageSource.causeArrowDamage(this, getShooter()), MathHelper.ceil(getMotion().length()));

			if (target instanceof LivingEntity && !potionEffects.isEmpty()) {
				for (EffectInstance effect : potionEffects) {
					((LivingEntity)target).addPotionEffect(effect);
				}
			}

			remove();
		}
	}

	@Override
	protected void func_230299_a_(BlockRayTraceResult raytraceResult) //onBlockHit
	{
		remove();
	}

	@Override
	protected void arrowHit(LivingEntity entity)
	{
		remove();
	}

	@Override
	protected ItemStack getArrowStack()
	{
		return ItemStack.EMPTY;
	}

	@Override
	public IPacket<?> createSpawnPacket()
	{
		return NetworkHooks.getEntitySpawningPacket(this);
	}
}
