package net.geforcemods.securitycraft.entity;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.projectile.AbstractFireballEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.Explosion.Mode;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class IMSBombEntity extends AbstractFireballEntity {

	private int ticksFlying = 0;
	private int launchTime;
	private boolean launching = true;

	public IMSBombEntity(EntityType<IMSBombEntity> type, World world){
		super(SCContent.eTypeImsBomb, world);
	}

	public IMSBombEntity(World world, double x, double y, double z, double accelerationX, double accelerationY, double accelerationZ, int height){
		super(SCContent.eTypeImsBomb, x, y, z, accelerationX, accelerationY, accelerationZ, world);
		launchTime = height * 3; //the ims bomb entity travels upwards by 1/3 blocks per tick
	}

	@Override
	public void tick(){
		if(!launching)
			super.tick();
		else
		{
			if(ticksFlying == 0)
				setMotion(getMotion().x, 0.33F, getMotion().z);

			//move up before homing onto target
			if(ticksFlying++ < launchTime)
				move(MoverType.SELF, getMotion());
			else
			{
				setMotion(0.0D, 0.0D, 0.0D);
				launching = false;
			}
		}
	}

	@Override
	protected void onImpact(RayTraceResult result){
		if(!world.isRemote && result.getType() == Type.BLOCK && BlockUtils.getBlock(world, ((BlockRayTraceResult)result).getPos()) != SCContent.IMS.get()){
			BlockPos impactPos = ((BlockRayTraceResult)result).getPos();

			world.createExplosion(this, impactPos.getX(), impactPos.getY() + 1D, impactPos.getZ(), 7F, ConfigHandler.SERVER.shouldSpawnFire.get(), Mode.BREAK);
			remove();
		}
	}

	@Override
	public void writeAdditional(CompoundNBT tag)
	{
		super.writeAdditional(tag);
		tag.putInt("launchTime", launchTime);
		tag.putInt("ticksFlying", ticksFlying);
		tag.putBoolean("launching", launching);
	}

	@Override
	public void readAdditional(CompoundNBT tag)
	{
		super.readAdditional(tag);
		launchTime = tag.getInt("launchTime");
		ticksFlying = tag.getInt("ticksFlying");
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

	@Override
	public IPacket<?> createSpawnPacket()
	{
		return NetworkHooks.getEntitySpawningPacket(this);
	}
}
