package net.geforcemods.securitycraft.entity;

import net.geforcemods.securitycraft.ConfigHandler.CommonConfig;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.util.EntityUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.world.Explosion.Mode;
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
		setPosition(x, y, z);
		float f = (float)(Math.random() * Math.PI * 2.0D);
		setMotion(-((float)Math.sin(f)) * 0.02F, 0.20000000298023224D, -((float)Math.cos(f)) * 0.02F);
		fuse = 80;
		prevPosX = x;
		prevPosY = y;
		prevPosZ = z;
	}

	@Override
	protected void registerData() {}

	/**
	 * returns if this entity triggers Block.onEntityWalking on the blocks they walk on. used for spiders and wolves to
	 * prevent them from trampling crops
	 */
	@Override
	protected boolean func_225502_at_() //canTriggerWalking
	{
		return false;
	}

	/**
	 * Returns true if other Entities should be prevented from moving through this Entity.
	 */
	@Override
	public boolean canBeCollidedWith()
	{
		return !removed;
	}

	/**
	 * Called to update the entity's position/logic.
	 */
	@Override
	public void tick()
	{
		prevPosX = func_226277_ct_();
		prevPosY = func_226278_cu_();
		prevPosZ = func_226281_cx_();
		EntityUtils.moveY(this, 0.03999999910593033D);
		move(MoverType.SELF, getMotion());
		setMotion(getMotion().mul(0.9800000190734863D, 0.9800000190734863D, 0.9800000190734863D));

		if (onGround)
			setMotion(getMotion().mul(0.699999988079071D, 0.699999988079071D, -0.5D));

		if (fuse-- <= 0)
		{
			remove();

			if (!world.isRemote)
				explode();
		}
		else if(world.isRemote)
			world.addParticle(ParticleTypes.SMOKE, false, func_226277_ct_(), func_226278_cu_() + 0.5D, func_226281_cx_(), 0.0D, 0.0D, 0.0D);
	}

	private void explode()
	{
		float f = 6.0F;

		if(CommonConfig.CONFIG.smallerMineExplosion.get())
			world.createExplosion(this, func_226277_ct_(), func_226278_cu_(), func_226281_cx_(), (f / 2), CommonConfig.CONFIG.shouldSpawnFire.get(), Mode.BREAK);
		else
			world.createExplosion(this, func_226277_ct_(), func_226278_cu_(), func_226281_cx_(), f, CommonConfig.CONFIG.shouldSpawnFire.get(), Mode.BREAK);
	}

	/**
	 * (abstract) Protected helper method to write subclass entity data to NBT.
	 */
	@Override
	protected void writeAdditional(CompoundNBT tag)
	{
		tag.putByte("Fuse", (byte)fuse);
	}

	/**
	 * (abstract) Protected helper method to read subclass entity data from NBT.
	 */
	@Override
	protected void readAdditional(CompoundNBT tag)
	{
		fuse = tag.getByte("Fuse");
	}

	public float getShadowSize()
	{
		return 0.0F;
	}

	@Override
	public IPacket<?> createSpawnPacket()
	{
		return NetworkHooks.getEntitySpawningPacket(this);
	}
}
