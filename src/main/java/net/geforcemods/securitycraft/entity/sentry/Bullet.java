package net.geforcemods.securitycraft.entity.sentry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.Sets;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.Owner;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

public class Bullet extends AbstractArrow {
	private static final EntityDataAccessor<Owner> OWNER = SynchedEntityData.<Owner>defineId(Bullet.class, Owner.getSerializer());
	private Collection<MobEffectInstance> potionEffects = Sets.newHashSet();

	public Bullet(EntityType<Bullet> type, Level level) {
		super(SCContent.BULLET_ENTITY.get(), level);
		pickup = Pickup.DISALLOWED;
	}

	public Bullet(Level level, Sentry shooter) {
		super(SCContent.BULLET_ENTITY.get(), shooter, level, new ItemStack(Items.STICK), null);

		Owner owner = shooter.getOwner();

		potionEffects = shooter.getActiveEffects();
		entityData.set(OWNER, new Owner(owner.getName(), owner.getUUID()));
		pickup = Pickup.DISALLOWED;
	}

	/**
	 * @return The owner of the sentry which shot this bullet
	 */
	public Owner getSCOwner() {
		return entityData.get(OWNER);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(OWNER, new Owner());
	}

	@Override
	public void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);

		if (!potionEffects.isEmpty())
			tag.store("PotionEffects", MobEffectInstance.CODEC.listOf(), registryAccess().createSerializationContext(NbtOps.INSTANCE), new ArrayList<>(potionEffects));
	}

	@Override
	public void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);

		if (tag.contains("PotionEffects")) {
			List<MobEffectInstance> mobEffectList = tag.read("PotionEffects", MobEffectInstance.CODEC.listOf(), registryAccess().createSerializationContext(NbtOps.INSTANCE)).orElse(List.of());

			potionEffects = new ArrayList<>(mobEffectList);
		}
	}

	@Override
	protected void onHitEntity(EntityHitResult raytraceResult) {
		Entity target = raytraceResult.getEntity();

		if (!(target instanceof Sentry) && !(target instanceof ItemFrame)) {
			target.hurt(damageSources().arrow(this, getOwner()), ConfigHandler.SERVER.sentryBulletDamage.get());

			if (target instanceof LivingEntity lEntity && !potionEffects.isEmpty()) {
				for (MobEffectInstance effect : potionEffects) {
					lEntity.addEffect(effect);
				}
			}
		}

		discard();
	}

	@Override
	protected void onHitBlock(BlockHitResult raytraceResult) {
		discard();
	}

	@Override
	protected ItemStack getDefaultPickupItem() {
		//can't encode an empty stack; pickup is disallowed just in case
		return new ItemStack(Items.STICK);
	}

	@Override
	public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity serverEntity) {
		return new ClientboundAddEntityPacket(this, serverEntity);
	}
}
