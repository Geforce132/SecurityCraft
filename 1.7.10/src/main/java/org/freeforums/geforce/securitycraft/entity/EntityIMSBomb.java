package org.freeforums.geforce.securitycraft.entity;

import org.freeforums.geforce.securitycraft.main.Utils.PlayerUtils;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;

public class EntityIMSBomb extends EntityFireball {

	private String playerName = null;
	private EntityCreature targetMob = null;
	
	public int ticksFlying = 0;
	public boolean launching = true;

	public EntityIMSBomb(World worldIn){
		super(worldIn);
		this.setSize(0.25F, 0.3F);
	}
	
	public EntityIMSBomb(World worldIn, EntityPlayer targetEntity, double x, double y, double z, double targetX, double targetY, double targetZ){
		super(worldIn, x, y, z, targetX, targetY, targetZ);
		this.playerName = targetEntity.getCommandSenderName();
		this.setSize(0.25F, 0.3F);
	}
	
	public EntityIMSBomb(World worldIn, EntityCreature targetEntity, double x, double y, double z, double targetX, double targetY, double targetZ){
		super(worldIn, x, y, z, targetX, targetY, targetZ);
		this.targetMob = targetEntity;
		this.setSize(0.25F, 0.3F);
	}

	public void onUpdate(){	
		if(!launching){
			super.onUpdate();
			return;
		}
		
		if(ticksFlying < 30 && launching){
			this.motionY = 0.35F;
			this.ticksFlying++;
			this.moveEntity(this.motionX, this.motionY, this.motionZ);
		}else if(ticksFlying >= 30 && launching){
			this.setTarget();
		}
	}

	public void setTarget() {
		if(playerName != null && PlayerUtils.isPlayerOnline(playerName)){
			EntityPlayer target = PlayerUtils.getPlayerFromName(playerName);
			
			double d5 = target.posX - posX;
            double d6 = target.boundingBox.minY + (double)(target.height / 2.0F) - ((double) posY + 1.25D);
            double d7 = target.posZ - posZ;
			
			EntityIMSBomb entitylargefireball = new EntityIMSBomb(worldObj, target, posX, posY, posZ, d5, d6, d7);
            entitylargefireball.launching = false;
            worldObj.spawnEntityInWorld(entitylargefireball);
            this.setDead();
		}else if(targetMob != null && !targetMob.isDead){			
			double d5 = targetMob.posX - posX;
            double d6 = targetMob.boundingBox.minY + (double)(targetMob.height / 2.0F) - ((double) posY + 1.25D);
            double d7 = targetMob.posZ - posZ;
			
			EntityIMSBomb entitylargefireball = new EntityIMSBomb(worldObj, targetMob, posX, posY, posZ, d5, d6, d7);
            entitylargefireball.launching = false;
            worldObj.spawnEntityInWorld(entitylargefireball);
            this.setDead();
		}else{
			this.setDead();
		}
	}

	protected void onImpact(MovingObjectPosition par1MovingObjectPosition){
		if(!this.worldObj.isRemote){
			if(par1MovingObjectPosition.typeOfHit == MovingObjectType.BLOCK){
				this.worldObj.createExplosion(this, par1MovingObjectPosition.blockX, par1MovingObjectPosition.blockY + 1D, par1MovingObjectPosition.blockZ, 10F, true);
				this.setDead();
			}
		}
	}
	
	protected float getMotionFactor(){
        return 1F;
    }

	protected boolean canTriggerWalking(){
		return false;
	}
	
	public boolean canBeCollidedWith(){
        return false;
    }

    public float getCollisionBorderSize(){
        return 0.3F;
    }

}
