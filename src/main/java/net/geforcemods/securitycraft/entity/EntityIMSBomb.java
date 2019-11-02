package net.geforcemods.securitycraft.entity;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.WorldUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
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

	public EntityIMSBomb(World world){
		super(SCContent.eTypeImsBomb, world, 0.25F, 0.3F);
	}

	public EntityIMSBomb(World world, EntityPlayer targetEntity, double x, double y, double z, double targetX, double targetY, double targetZ, int height){
		super(EntityType.FIREBALL, x, y, z, targetX, targetY, targetZ, world, 0.25F, 0.3F);
		playerName = targetEntity.getName().getFormattedText();
		launchHeight = height;
	}

	public EntityIMSBomb(World world, EntityLivingBase targetEntity, double x, double y, double z, double targetX, double targetY, double targetZ, int height){
		super(EntityType.FIREBALL, targetEntity, targetX, targetY, targetZ, world, 0.25F, 0.3F);
		targetMob = targetEntity;
		launchHeight = height;
	}

	@Override
	public void tick(){
		if(!launching){
			super.tick();
			return;
		}

		if(ticksFlying < launchHeight && launching){
			motionY = 0.35F;
			ticksFlying++;
			move(MoverType.SELF, motionX, motionY, motionZ);
		}else if(ticksFlying >= launchHeight && launching)
			setTarget();
	}

	public void setTarget() {
		if(playerName != null && PlayerUtils.isPlayerOnline(playerName)){
			EntityPlayer target = PlayerUtils.getPlayerFromName(playerName);

			double targetX = target.posX - posX;
			double targetY = target.getBoundingBox().minY + target.height / 2.0F - (posY + 1.25D);
			double targetZ = target.posZ - posZ;
			EntityIMSBomb imsBomb = new EntityIMSBomb(world, target, posX, posY, posZ, targetX, targetY, targetZ, 0);

			imsBomb.launching = false;
			WorldUtils.addScheduledTask(world, () -> world.spawnEntity(imsBomb));
			remove();
		}else if(targetMob != null && !targetMob.removed){
			double targetX = targetMob.posX - posX;
			double targetY = targetMob.getBoundingBox().minY + targetMob.height / 2.0F - (posY + 1.25D);
			double targetZ = targetMob.posZ - posZ;
			EntityIMSBomb imsBomb = new EntityIMSBomb(world, targetMob, posX, posY, posZ, targetX, targetY, targetZ, 0);

			imsBomb.launching = false;
			WorldUtils.addScheduledTask(world, () -> world.spawnEntity(imsBomb));
			remove();
		}
		else
			remove();
	}

	@Override
	protected void onImpact(RayTraceResult result){
		if(!world.isRemote)
			if(result.type == Type.BLOCK && BlockUtils.getBlock(world, result.getBlockPos()) != SCContent.ims){
				world.createExplosion(this, result.getBlockPos().getX(), result.getBlockPos().getY() + 1D, result.getBlockPos().getZ(), 7F, true);
				remove();
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
