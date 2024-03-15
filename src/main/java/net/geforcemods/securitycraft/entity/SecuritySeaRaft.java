package net.geforcemods.securitycraft.entity;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.entity.sentry.Sentry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.ChestBoat;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class SecuritySeaRaft extends ChestBoat implements IOwnable {
	private static final EntityDataAccessor<Owner> OWNER = SynchedEntityData.<Owner>defineId(Sentry.class, Owner.getSerializer());

	public SecuritySeaRaft(EntityType<? extends Boat> type, Level level) {
		super(SCContent.SECURITY_SEA_RAFT_ENTITY.get(), level);
	}

	public SecuritySeaRaft(Level level, double x, double y, double z) {
		super(SCContent.SECURITY_SEA_RAFT_ENTITY.get(), level);
		setPos(x, y, z);
		xo = y;
		yo = y;
		zo = z;
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		entityData.define(OWNER, new Owner());
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		Entity entity = source.getEntity();

		if (!(entity instanceof Player player) || isOwnedBy(player))
			return super.hurt(source, amount);
		else
			return false;
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag tag) {
		CompoundTag ownerTag = new CompoundTag();

		super.addAdditionalSaveData(tag);
		getOwner().save(ownerTag, needsValidation());
		tag.put("owner", ownerTag);
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		entityData.set(OWNER, Owner.fromCompound(tag.getCompound("owner")));
	}

	public void setOwner(Player player) {
		setOwner(player.getGameProfile().getId().toString(), player.getName().getString());
	}

	@Override
	public void setOwner(String uuid, String name) {
		entityData.set(OWNER, new Owner(name, uuid));
	}

	@Override
	public Owner getOwner() {
		return entityData.get(OWNER);
	}

	@Override
	public void onOwnerChanged(BlockState state, Level level, BlockPos pos, Player player) {}

	@Override
	public Item getDropItem() {
		return SCContent.SECURITY_SEA_RAFT_ITEM.get();
	}
}
