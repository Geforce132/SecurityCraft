package net.geforcemods.securitycraft.entity;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;

public class BouncingBetty extends Entity {
	/** How many ticks until the explosion */
	private int fuse;

	public BouncingBetty(EntityType<BouncingBetty> type, Level level) {
		super(SCContent.BOUNCING_BETTY_ENTITY.get(), level);
	}

	public BouncingBetty(Level level, double x, double y, double z) {
		super(SCContent.BOUNCING_BETTY_ENTITY.get(), level);
		setPos(x, y, z);
		float f = (float) (Math.random() * Math.PI * 2.0D);
		setDeltaMovement(-((float) Math.sin(f)) * 0.02F, 0.20000000298023224D, -((float) Math.cos(f)) * 0.02F);
		setFuse(80);
		xo = x;
		yo = y;
		zo = z;
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {}

	@Override
	protected MovementEmission getMovementEmission() {
		return MovementEmission.NONE;
	}

	@Override
	public boolean isPickable() {
		return !isRemoved();
	}

	@Override
	public void tick() {
		xo = getX();
		yo = getY();
		zo = getZ();
		setDeltaMovement(getDeltaMovement().add(0, -0.03999999910593033D, 0));
		move(MoverType.SELF, getDeltaMovement());
		setDeltaMovement(getDeltaMovement().multiply(0.9800000190734863D, 0.9800000190734863D, 0.9800000190734863D));

		if (onGround())
			setDeltaMovement(getDeltaMovement().multiply(0.699999988079071D, 0.699999988079071D, -0.5D));

		if (fuse-- <= 0 && !level().isClientSide) {
			discard();
			explode();
		}
		else if (level().isClientSide)
			level().addParticle(ParticleTypes.SMOKE, false, getX(), getY() + 0.5D, getZ(), 0.0D, 0.0D, 0.0D);
	}

	private void explode() {
		level().explode(this, getX(), getY(), getZ(), ConfigHandler.SERVER.smallerMineExplosion.get() ? 3.0F : 6.0F, ConfigHandler.SERVER.shouldSpawnFire.get(), BlockUtils.getExplosionInteraction());
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag tag) {
		tag.putByte("Fuse", (byte) getFuse());
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag tag) {
		setFuse(tag.getByte("Fuse"));
	}

	@Override
	public Packet<ClientGamePacketListener> getAddEntityPacket() {
		return new ClientboundAddEntityPacket(this);
	}

	public void setFuse(int fuse) {
		this.fuse = fuse;
	}

	public int getFuse() {
		return fuse;
	}
}
