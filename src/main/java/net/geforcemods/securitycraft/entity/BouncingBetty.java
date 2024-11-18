package net.geforcemods.securitycraft.entity;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class BouncingBetty extends Entity {
	/** How many ticks until the explosion */
	private int fuse;

	public BouncingBetty(EntityType<? extends BouncingBetty> type, World level) {
		super(type, level);
	}

	public BouncingBetty(World level) {
		this(SCContent.BOUNCING_BETTY_ENTITY.get(), level);
	}

	public BouncingBetty(World level, double x, double y, double z) {
		this(level);
		setPos(x, y, z);
		float f = (float) (Math.random() * Math.PI * 2.0D);
		setDeltaMovement(-((float) Math.sin(f)) * 0.02F, 0.20000000298023224D, -((float) Math.cos(f)) * 0.02F);
		setFuse(80);
		xo = x;
		yo = y;
		zo = z;
	}

	@Override
	protected void defineSynchedData() {}

	@Override
	protected boolean isMovementNoisy() {
		return false;
	}

	@Override
	public boolean isPickable() {
		return !removed;
	}

	@Override
	public void tick() {
		xo = getX();
		yo = getY();
		zo = getZ();
		setDeltaMovement(getDeltaMovement().add(0, -0.03999999910593033D, 0));
		move(MoverType.SELF, getDeltaMovement());
		setDeltaMovement(getDeltaMovement().multiply(0.9800000190734863D, 0.9800000190734863D, 0.9800000190734863D));

		if (onGround)
			setDeltaMovement(getDeltaMovement().multiply(0.699999988079071D, 0.699999988079071D, -0.5D));

		if (fuse-- <= 0 && !level.isClientSide) {
			remove();
			explode();
		}
		else if (level.isClientSide)
			level.addParticle(ParticleTypes.SMOKE, false, getX(), getY() + 0.5D, getZ(), 0.0D, 0.0D, 0.0D);
	}

	private void explode() {
		level.explode(this, getX(), getY(), getZ(), ConfigHandler.SERVER.smallerMineExplosion.get() ? 3.0F : 6.0F, ConfigHandler.SERVER.shouldSpawnFire.get(), BlockUtils.getExplosionMode());
	}

	@Override
	protected void addAdditionalSaveData(CompoundNBT tag) {
		tag.putByte("Fuse", (byte) getFuse());
	}

	@Override
	protected void readAdditionalSaveData(CompoundNBT tag) {
		setFuse(tag.getByte("Fuse"));
	}

	@Override
	public IPacket<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	public int getFuse() {
		return fuse;
	}

	public void setFuse(int fuse) {
		this.fuse = fuse;
	}
}
