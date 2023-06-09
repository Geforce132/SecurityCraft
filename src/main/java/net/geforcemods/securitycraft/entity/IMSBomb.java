package net.geforcemods.securitycraft.entity;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blockentities.IMSBlockEntity;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.HitResult.Type;

public class IMSBomb extends Fireball {
	private static final EntityDataAccessor<Owner> OWNER = SynchedEntityData.defineId(IMSBomb.class, Owner.getSerializer());
	private int ticksFlying = 0;
	private int launchTime;
	private boolean launching = true;
	private boolean isFast;

	public IMSBomb(EntityType<IMSBomb> type, Level level) {
		super(SCContent.IMS_BOMB_ENTITY.get(), level);
	}

	public IMSBomb(Level level, double x, double y, double z, double accelerationX, double accelerationY, double accelerationZ, int height, IMSBlockEntity be) {
		super(SCContent.IMS_BOMB_ENTITY.get(), x, y, z, accelerationX, accelerationY, accelerationZ, level);

		Owner owner = be.getOwner();

		launchTime = height * 3; //the ims bomb entity travels upwards by 1/3 blocks per tick
		entityData.set(OWNER, new Owner(owner.getName(), owner.getUUID()));
		isFast = be.isModuleEnabled(ModuleType.SPEED);
	}

	@Override
	public void tick() {
		if (!launching)
			super.tick();
		else {
			if (ticksFlying == 0)
				setDeltaMovement(getDeltaMovement().x, isFast ? 0.66F : 0.33F, getDeltaMovement().z);

			//move up before homing onto target
			if (ticksFlying < launchTime) {
				ticksFlying += isFast ? 2 : 1;
				move(MoverType.SELF, getDeltaMovement());
			}
			else {
				setDeltaMovement(0.0D, 0.0D, 0.0D);
				launching = false;
			}
		}
	}

	@Override
	protected void onHit(HitResult result) {
		if (!level().isClientSide && result.getType() == Type.BLOCK && level().getBlockState(((BlockHitResult) result).getBlockPos()).getBlock() != SCContent.IMS.get()) {
			BlockPos impactPos = ((BlockHitResult) result).getBlockPos();

			level().explode(this, impactPos.getX(), impactPos.getY() + 1D, impactPos.getZ(), ConfigHandler.SERVER.smallerMineExplosion.get() ? 3.5F : 7F, ConfigHandler.SERVER.shouldSpawnFire.get(), BlockUtils.getExplosionInteraction());
			discard();
		}
	}

	@Override
	public void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		tag.putInt("launchTime", launchTime);
		tag.putInt("ticksFlying", ticksFlying);
		tag.putBoolean("launching", launching);
		tag.putBoolean("isFast", isFast);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		launchTime = tag.getInt("launchTime");
		ticksFlying = tag.getInt("ticksFlying");
		launching = tag.getBoolean("launching");
		isFast = tag.getBoolean("isFast");
	}

	/**
	 * @return The owner of the IMS which shot this bullet
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
	protected float getInertia() {
		return isFast ? 1.5F : 1.0F;
	}

	@Override
	protected MovementEmission getMovementEmission() {
		return MovementEmission.NONE;
	}

	@Override
	public boolean isPickable() {
		return false;
	}

	@Override
	public float getPickRadius() {
		return 0.3F;
	}

	@Override
	public Packet<ClientGamePacketListener> getAddEntityPacket() {
		return new ClientboundAddEntityPacket(this);
	}
}
