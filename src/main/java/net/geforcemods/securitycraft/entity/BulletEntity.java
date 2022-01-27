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
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class BulletEntity extends AbstractArrowEntity {
	private static final DataParameter<Owner> OWNER = EntityDataManager.<Owner> defineId(BulletEntity.class, Owner.getSerializer());
	private Collection<EffectInstance> potionEffects = Sets.newHashSet();

	public BulletEntity(EntityType<BulletEntity> type, World world) {
		super(SCContent.eTypeBullet, world);
	}

	public BulletEntity(World world, SentryEntity shooter) {
		super(SCContent.eTypeBullet, shooter, world);

		Owner owner = shooter.getOwner();

		this.potionEffects = shooter.getActiveEffects();
		entityData.set(OWNER, new Owner(owner.getName(), owner.getUUID()));
	}

	/**
	 * @return The owner of the sentry which shot this bullet
	 */
	public Owner getSCOwner() {
		return entityData.get(OWNER);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		entityData.define(OWNER, new Owner());
	}

	@Override
	public void addAdditionalSaveData(CompoundNBT compound) {
		super.addAdditionalSaveData(compound);

		if (!this.potionEffects.isEmpty()) {
			ListNBT list = new ListNBT();

			for (EffectInstance effect : this.potionEffects) {
				list.add(effect.save(new CompoundNBT()));
			}

			compound.put("PotionEffects", list);
		}
	}

	@Override
	public void readAdditionalSaveData(CompoundNBT compound) {
		super.readAdditionalSaveData(compound);

		if (compound.contains("PotionEffects", 9)) {
			ListNBT potionList = compound.getList("PotionEffects", 10);

			if (!potionList.isEmpty()) {
				for (int i = 0; i < potionList.size(); ++i) {
					EffectInstance effect = EffectInstance.load(potionList.getCompound(i));

					if (effect != null)
						potionEffects.add(effect);
				}
			}
		}
	}

	@Override
	protected void onHit(RayTraceResult raytraceResult) {
		if (raytraceResult.getType() == Type.ENTITY) {
			Entity target = ((EntityRayTraceResult) raytraceResult).getEntity();

			if (!(target instanceof SentryEntity)) {
				target.hurt(DamageSource.arrow(this, getOwner()), MathHelper.ceil(getDeltaMovement().length()));

				if (target instanceof LivingEntity && !potionEffects.isEmpty()) {
					for (EffectInstance effect : potionEffects) {
						((LivingEntity) target).addEffect(effect);
					}
				}

				remove();
			}
		}
		else if (raytraceResult.getType() == Type.BLOCK)
			remove();
	}

	@Override
	protected void doPostHurtEffects(LivingEntity entity) {
		remove();
	}

	@Override
	protected ItemStack getPickupItem() {
		return ItemStack.EMPTY;
	}

	@Override
	public IPacket<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}
}
