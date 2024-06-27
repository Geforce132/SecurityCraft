package net.geforcemods.securitycraft.entity;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blockentities.IMSBlockEntity;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.projectile.AbstractFireballEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class IMSBomb extends AbstractFireballEntity {
	private static final DataParameter<Owner> OWNER = EntityDataManager.defineId(IMSBomb.class, Owner.getSerializer());
	private int ticksFlying = 0;
	private int launchTime;
	private boolean launching = true;
	private boolean isFast;

	public IMSBomb(EntityType<? extends IMSBomb> type, World world) {
		super(type, world);
	}

	public IMSBomb(World world) {
		this(SCContent.IMS_BOMB_ENTITY.get(), world);
	}

	public IMSBomb(World world, double x, double y, double z, double accelerationX, double accelerationY, double accelerationZ, int height, IMSBlockEntity te) {
		super(SCContent.IMS_BOMB_ENTITY.get(), x, y, z, accelerationX, accelerationY, accelerationZ, world);

		Owner owner = te.getOwner();

		launchTime = height * 3; //the ims bomb entity travels upwards by 1/3 blocks per tick
		entityData.set(OWNER, new Owner(owner.getName(), owner.getUUID()));
		isFast = te.isModuleEnabled(ModuleType.SPEED);
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
	protected void onHit(RayTraceResult result) {
		if (!level.isClientSide && result.getType() == Type.BLOCK && level.getBlockState(((BlockRayTraceResult) result).getBlockPos()).getBlock() != SCContent.IMS.get()) {
			BlockPos impactPos = ((BlockRayTraceResult) result).getBlockPos();

			level.explode(this, impactPos.getX(), impactPos.getY() + 1D, impactPos.getZ(), ConfigHandler.SERVER.smallerMineExplosion.get() ? 3.5F : 7F, ConfigHandler.SERVER.shouldSpawnFire.get(), BlockUtils.getExplosionMode());
			remove();
		}
	}

	@Override
	public void addAdditionalSaveData(CompoundNBT tag) {
		super.addAdditionalSaveData(tag);
		tag.putInt("launchTime", launchTime);
		tag.putInt("ticksFlying", ticksFlying);
		tag.putBoolean("launching", launching);
		tag.putBoolean("isFast", isFast);
	}

	@Override
	public void readAdditionalSaveData(CompoundNBT tag) {
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
	public boolean ignoreExplosion() {
		return true;
	}

	@Override
	protected boolean isMovementNoisy() {
		return false;
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
	public IPacket<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}
}
