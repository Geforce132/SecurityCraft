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

public class BouncingBettyEntity extends Entity {

	/** How long the fuse is */
	public int fuse;

	public BouncingBettyEntity(EntityType<BouncingBettyEntity> type, World world){
		super(SCContent.eTypeBouncingBetty, world);
	}

	public BouncingBettyEntity(World world, double x, double y, double z){
		this(SCContent.eTypeBouncingBetty, world);
		setPos(x, y, z);
		float f = (float)(Math.random() * Math.PI * 2.0D);
		setDeltaMovement(-((float)Math.sin(f)) * 0.02F, 0.20000000298023224D, -((float)Math.cos(f)) * 0.02F);
		fuse = 80;
		xo = x;
		yo = y;
		zo = z;
	}

	@Override
	protected void defineSynchedData() {}

	/**
	 * returns if this entity triggers Block.onEntityWalking on the blocks they walk on. used for spiders and wolves to
	 * prevent them from trampling crops
	 */
	@Override
	protected boolean isMovementNoisy()
	{
		return false;
	}

	/**
	 * Returns true if other Entities should be prevented from moving through this Entity.
	 */
	@Override
	public boolean isPickable()
	{
		return !removed;
	}

	/**
	 * Called to update the entity's position/logic.
	 */
	@Override
	public void tick()
	{
		xo = getX();
		yo = getY();
		zo = getZ();
		setDeltaMovement(getDeltaMovement().add(0, -0.03999999910593033D, 0));
		move(MoverType.SELF, getDeltaMovement());
		setDeltaMovement(getDeltaMovement().multiply(0.9800000190734863D, 0.9800000190734863D, 0.9800000190734863D));

		if (onGround)
			setDeltaMovement(getDeltaMovement().multiply(0.699999988079071D, 0.699999988079071D, -0.5D));

		if (!level.isClientSide && fuse-- <= 0)
		{
			remove();
			explode();
		}
		else if(level.isClientSide)
			level.addParticle(ParticleTypes.SMOKE, false, getX(), getY() + 0.5D, getZ(), 0.0D, 0.0D, 0.0D);
	}

	private void explode()
	{
		level.explode(this, getX(), getY(), getZ(), ConfigHandler.SERVER.smallerMineExplosion.get() ? 3.0F : 6.0F, ConfigHandler.SERVER.shouldSpawnFire.get(), BlockUtils.getExplosionMode());
	}

	/**
	 * (abstract) Protected helper method to write subclass entity data to NBT.
	 */
	@Override
	protected void addAdditionalSaveData(CompoundNBT tag)
	{
		tag.putByte("Fuse", (byte)fuse);
	}

	/**
	 * (abstract) Protected helper method to read subclass entity data from NBT.
	 */
	@Override
	protected void readAdditionalSaveData(CompoundNBT tag)
	{
		fuse = tag.getByte("Fuse");
	}

	@Override
	public IPacket<?> getAddEntityPacket()
	{
		return NetworkHooks.getEntitySpawningPacket(this);
	}
}
