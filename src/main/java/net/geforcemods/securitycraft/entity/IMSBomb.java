package net.geforcemods.securitycraft.entity;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blockentities.IMSBlockEntity;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;

public class IMSBomb extends EntityFireball {
	private static final DataParameter<Owner> OWNER = EntityDataManager.createKey(IMSBomb.class, Owner.getSerializer());
	private int ticksFlying = 0;
	private int launchTime;
	private boolean launching = true;
	private boolean isFast;

	public IMSBomb(World world) {
		super(world);
		setSize(0.25F, 0.3F);
	}

	public IMSBomb(World world, double x, double y, double z, double accelerationX, double accelerationY, double accelerationZ, int height, IMSBlockEntity te) {
		super(world, x, y, z, accelerationX, accelerationY, accelerationZ);
		setSize(0.25F, 0.3F);
		launchTime = height * 3; //the ims bomb entity travels upwards by 1/3 blocks per tick

		Owner owner = te.getOwner();

		dataManager.set(OWNER, new Owner(owner.getName(), owner.getUUID()));
		isFast = te.isModuleEnabled(ModuleType.SPEED);
	}

	@Override
	public void onUpdate() {
		if (!launching)
			super.onUpdate();
		else {
			if (ticksFlying == 0)
				motionY = isFast ? 0.66F : 0.33F;

			//move up before homing onto target
			if (ticksFlying < launchTime) {
				ticksFlying += isFast ? 2 : 1;
				move(MoverType.SELF, motionX, motionY, motionZ);
			}
			else {
				motionX = motionY = motionZ = 0.0F;
				launching = false;
			}
		}
	}

	@Override
	protected void onImpact(RayTraceResult result) {
		if (!world.isRemote && result.typeOfHit == Type.BLOCK && world.getBlockState(result.getBlockPos()).getBlock() != SCContent.ims) {
			world.newExplosion(this, result.getBlockPos().getX(), result.getBlockPos().getY() + 1D, result.getBlockPos().getZ(), ConfigHandler.smallerMineExplosion ? 3.5F : 7F, ConfigHandler.shouldSpawnFire, ConfigHandler.mineExplosionsBreakBlocks);
			setDead();
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setInteger("launchTime", launchTime);
		tag.setInteger("ticksFlying", ticksFlying);
		tag.setBoolean("launching", launching);
		tag.setBoolean("isFast", isFast);
		return tag;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		launchTime = tag.getInteger("launchTime");
		ticksFlying = tag.getInteger("ticksFlying");
		launching = tag.getBoolean("launching");
		isFast = tag.getBoolean("isFast");
	}

	/**
	 * @return The owner of the IMS which shot this bullet
	 */
	public Owner getOwner() {
		return dataManager.get(OWNER);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(OWNER, new Owner());
	}

	@Override
	protected float getMotionFactor() {
		return isFast ? 1.5F : 1.0F;
	}

	@Override
	public boolean isImmuneToExplosions() {
		return true;
	}

	@Override
	protected boolean canTriggerWalking() {
		return false;
	}

	@Override
	public boolean canBeCollidedWith() {
		return false;
	}

	@Override
	public float getCollisionBorderSize() {
		return 0.3F;
	}
}
