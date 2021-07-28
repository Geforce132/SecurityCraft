package net.geforcemods.securitycraft.entity;

import java.util.Collection;

import com.google.common.collect.Sets;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.Owner;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

public class BulletEntity extends AbstractArrow
{
	private static final EntityDataAccessor<Owner> OWNER = SynchedEntityData.<Owner>defineId(BulletEntity.class, Owner.getSerializer());
	private Collection<MobEffectInstance> potionEffects = Sets.newHashSet();

	public BulletEntity(EntityType<BulletEntity> type, Level world)
	{
		super(SCContent.eTypeBullet, world);
	}

	public BulletEntity(Level world, SentryEntity shooter)
	{
		super(SCContent.eTypeBullet, shooter, world);

		Owner owner =  shooter.getOwner();

		this.potionEffects = shooter.getActiveEffects();
		entityData.set(OWNER, new Owner(owner.getName(), owner.getUUID()));
	}

	/**
	 * @return The owner of the sentry which shot this bullet
	 */
	public Owner getSCOwner()
	{
		return entityData.get(OWNER);
	}

	@Override
	protected void defineSynchedData()
	{
		super.defineSynchedData();
		entityData.define(OWNER, new Owner());
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);

		if (!this.potionEffects.isEmpty()) {
			ListTag list = new ListTag();

			for(MobEffectInstance effect : this.potionEffects) {
				list.add(effect.save(new CompoundTag()));
			}

			compound.put("PotionEffects", list);
		}
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);

		if (compound.contains("PotionEffects", 9)) {
			ListTag potionList = compound.getList("PotionEffects", 10);

			if (!potionList.isEmpty()) {
				for (int i = 0; i < potionList.size(); ++i) {
					MobEffectInstance effect = MobEffectInstance.load(potionList.getCompound(i));

					if (effect != null) {
						potionEffects.add(effect);
					}
				}
			}
		}
	}

	@Override
	protected void onHitEntity(EntityHitResult raytraceResult)
	{
		Entity target = raytraceResult.getEntity();

		if(!(target instanceof SentryEntity))
		{
			target.hurt(DamageSource.arrow(this, getOwner()), Mth.ceil(getDeltaMovement().length()));

			if (target instanceof LivingEntity lEntity && !potionEffects.isEmpty()) {
				for (MobEffectInstance effect : potionEffects) {
					lEntity.addEffect(effect);
				}
			}

			discard();
		}
	}

	@Override
	protected void onHitBlock(BlockHitResult raytraceResult) //onBlockHit
	{
		discard();
	}

	@Override
	protected ItemStack getPickupItem()
	{
		return ItemStack.EMPTY;
	}

	@Override
	public Packet<?> getAddEntityPacket()
	{
		return new ClientboundAddEntityPacket(this);
	}
}
