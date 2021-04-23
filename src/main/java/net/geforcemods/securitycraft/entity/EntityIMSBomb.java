package net.geforcemods.securitycraft.entity;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;

public class EntityIMSBomb extends EntityFireball {

	private int ticksFlying = 0;
	private int launchTime;
	private boolean launching = true;

	public EntityIMSBomb(World world){
		super(world);
		setSize(0.25F, 0.3F);
	}

	public EntityIMSBomb(World world, double x, double y, double z, double accelerationX, double accelerationY, double accelerationZ, int height){
		super(world, x, y, z, accelerationX, accelerationY, accelerationZ);
		setSize(0.25F, 0.3F);
		launchTime = height * 3; //the ims bomb entity travels upwards by 1/3 blocks per tick
	}

	@Override
	public void onUpdate(){
		if(!launching){
			super.onUpdate();
			return;
		}
		else
		{
			if(ticksFlying == 0)
				motionY = 0.35F;

			//move up before homing onto target
			if(ticksFlying++ < launchTime)
				move(MoverType.SELF, motionX, motionY, motionZ);
			else
			{
				motionX = motionY = motionZ = 0.0F;
				launching = false;
			}
		}
	}

	@Override
	protected void onImpact(RayTraceResult result){
		if(!world.isRemote && result.typeOfHit == Type.BLOCK && BlockUtils.getBlock(world, result.getBlockPos()) != SCContent.ims){
			world.newExplosion(this, result.getBlockPos().getX(), result.getBlockPos().getY() + 1D, result.getBlockPos().getZ(), ConfigHandler.smallerMineExplosion ? 3.5F : 7F, ConfigHandler.shouldSpawnFire, ConfigHandler.mineExplosionsBreakBlocks);
			setDead();
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag)
	{
		super.writeToNBT(tag);
		tag.setInteger("launchTime", launchTime);
		tag.setInteger("ticksFlying", ticksFlying);
		tag.setBoolean("launching", launching);
		return tag;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);
		launchTime = tag.getInteger("launchTime");
		ticksFlying = tag.getInteger("ticksFlying");
		launching = tag.getBoolean("launching");
	}

	@Override
	protected float getMotionFactor(){
		return 1F;
	}

	@Override
	protected boolean canTriggerWalking(){
		return false;
	}

	@Override
	public boolean canBeCollidedWith(){
		return false;
	}

	@Override
	public float getCollisionBorderSize(){
		return 0.3F;
	}

}
