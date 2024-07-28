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
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;

public class IMSBomb extends Fireball {
	private static final EntityDataAccessor<Owner> OWNER = SynchedEntityData.defineId(IMSBomb.class, Owner.getSerializer());
	private static final EntityDataAccessor<Integer> LAUNCH_TIME = SynchedEntityData.defineId(IMSBomb.class, EntityDataSerializers.INT);
	private int ticksFlying = 0;
	private boolean launching = true;
	private boolean isFast;
	private Vec3 upwardsSpeed;

	public IMSBomb(EntityType<IMSBomb> type, Level level) {
		super(SCContent.IMS_BOMB_ENTITY.get(), level);
	}

	public IMSBomb(Level level, double x, double y, double z, Vec3 acceleration, int height, IMSBlockEntity be) {
		super(SCContent.IMS_BOMB_ENTITY.get(), x, y, z, acceleration, level);

		Owner owner = be.getOwner();

		entityData.set(OWNER, new Owner(owner.getName(), owner.getUUID()));
		entityData.set(LAUNCH_TIME, height * 3); //the ims bomb entity travels upwards by 1/3 blocks per tick
		isFast = be.isModuleEnabled(ModuleType.SPEED);
	}

	@Override
	public void tick() {
		if (!launching)
 			super.tick();
		else {
			//move up before homing onto target
			if (ticksFlying < getLaunchTime()) {
				if (upwardsSpeed == null)
					upwardsSpeed = new Vec3(0, isFast ? 0.66F : 0.33F, 0);

				ticksFlying += isFast ? 2 : 1;
				move(MoverType.SELF, upwardsSpeed);
			}
			else
				launching = false;
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
		tag.putInt("ticksFlying", ticksFlying);
		tag.putBoolean("launching", launching);
		tag.putBoolean("isFast", isFast);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);
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

	public int getLaunchTime() {
		return entityData.get(LAUNCH_TIME);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(OWNER, new Owner());
		builder.define(LAUNCH_TIME, 0);
	}

	@Override
	protected float getInertia() {
		return isFast ? 1.5F : 1.0F;
	}

	@Override
	public boolean ignoreExplosion(Explosion explosion) {
		return true;
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
	public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity serverEntity) {
		return new ClientboundAddEntityPacket(this, serverEntity);
	}
}
