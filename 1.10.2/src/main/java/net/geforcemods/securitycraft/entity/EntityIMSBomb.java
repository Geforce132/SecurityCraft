package net.geforcemods.securitycraft.entity;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.WorldUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;

public class EntityIMSBomb extends EntityFireball {

	private String playerName = null;
	private EntityLivingBase targetMob = null;

	public int ticksFlying = 0;
	private int launchHeight;
	public boolean launching = true;

	public EntityIMSBomb(World worldIn){
		super(worldIn);
		setSize(0.25F, 0.3F);
	}

	public EntityIMSBomb(World worldIn, EntityPlayer targetEntity, double x, double y, double z, double targetX, double targetY, double targetZ, int height){
		super(worldIn, x, y, z, targetX, targetY, targetZ);
		playerName = targetEntity.getName();
		launchHeight = height;
		setSize(0.25F, 0.3F);
	}

	public EntityIMSBomb(World worldIn, EntityLivingBase targetEntity, double x, double y, double z, double targetX, double targetY, double targetZ, int height){
		super(worldIn, x, y, z, targetX, targetY, targetZ);
		targetMob = targetEntity;
		launchHeight = height;
		setSize(0.25F, 0.3F);
	}

	@Override
	public void onUpdate(){
		if(!launching){
			super.onUpdate();
			return;
		}

		if(ticksFlying < launchHeight && launching){
			motionY = 0.35F;
			ticksFlying++;
			move(motionX, motionY, motionZ);
		}else if(ticksFlying >= launchHeight && launching)
			setTarget();
	}

	public void setTarget() {
		if(playerName != null && PlayerUtils.isPlayerOnline(playerName)){
			EntityPlayer target = PlayerUtils.getPlayerFromName(playerName);

			double d5 = target.posX - posX;
			double d6 = target.getEntityBoundingBox().minY + target.height / 2.0F - (posY + 1.25D);
			double d7 = target.posZ - posZ;

			EntityIMSBomb entitylargefireball = new EntityIMSBomb(world, target, posX, posY, posZ, d5, d6, d7, 0);
			entitylargefireball.launching = false;
			WorldUtils.addScheduledTask(world, () -> world.spawnEntity(entitylargefireball));
			setDead();
		}else if(targetMob != null && !targetMob.isDead){
			double d5 = targetMob.posX - posX;
			double d6 = targetMob.getEntityBoundingBox().minY + targetMob.height / 2.0F - (posY + 1.25D);
			double d7 = targetMob.posZ - posZ;

			EntityIMSBomb entitylargefireball = new EntityIMSBomb(world, targetMob, posX, posY, posZ, d5, d6, d7, 0);
			entitylargefireball.launching = false;
			WorldUtils.addScheduledTask(world, () -> world.spawnEntity(entitylargefireball));
			setDead();
		}
		else
			setDead();
	}

	@Override
	protected void onImpact(RayTraceResult par1RayTraceResult){
		if(!world.isRemote)
			if(par1RayTraceResult.typeOfHit == Type.BLOCK && BlockUtils.getBlock(world, par1RayTraceResult.getBlockPos()) != SCContent.ims){
				world.createExplosion(this, par1RayTraceResult.getBlockPos().getX(), par1RayTraceResult.getBlockPos().getY() + 1D, par1RayTraceResult.getBlockPos().getZ(), 7F, true);
				setDead();
			}
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
