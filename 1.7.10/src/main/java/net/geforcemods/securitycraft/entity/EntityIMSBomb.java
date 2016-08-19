package net.geforcemods.securitycraft.entity;

import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;

public class EntityIMSBomb extends EntityFireball {

	private String playerName = null;
	private EntityLivingBase targetMob = null;
	
	public int ticksFlying = 0;
	private int launchHeight;
	public boolean launching = true;

	public EntityIMSBomb(World worldIn){
		super(worldIn);
		this.setSize(0.25F, 0.3F);
	}
	
	public EntityIMSBomb(World worldIn, EntityPlayer targetEntity, double x, double y, double z, double targetX, double targetY, double targetZ, int height){
		super(worldIn, x, y, z, targetX, targetY, targetZ);
		this.playerName = targetEntity.getCommandSenderName();
		this.launchHeight = height;
		this.setSize(0.25F, 0.3F);
	}
	
	public EntityIMSBomb(World worldIn, EntityLivingBase targetEntity, double x, double y, double z, double targetX, double targetY, double targetZ, int height){
		super(worldIn, x, y, z, targetX, targetY, targetZ);
		this.targetMob = targetEntity;
		this.launchHeight = height;
		this.setSize(0.25F, 0.3F);
	}

	public void onUpdate(){	
		if(!launching){
			super.onUpdate();
			return;
		}

		if(ticksFlying < launchHeight && launching){
			this.motionY = 0.35F;
			this.ticksFlying++;
			this.moveEntity(this.motionX, this.motionY, this.motionZ);
		}else if(ticksFlying >= launchHeight && launching){
			this.setTarget();
		}
	}

	public void setTarget() {
		if(playerName != null && PlayerUtils.isPlayerOnline(playerName)){
			EntityPlayer target = PlayerUtils.getPlayerFromName(playerName);
						
			double d5 = target.posX - posX;
            double d6 = target.boundingBox.minY + target.height / 2.0F - (posY + 1.25D);
            double d7 = target.posZ - posZ;
			
			EntityIMSBomb entitylargefireball = new EntityIMSBomb(worldObj, target, posX, posY, posZ, d5, d6, d7, 0);
            entitylargefireball.launching = false;
            worldObj.spawnEntityInWorld(entitylargefireball);
            this.setDead();
		}else if(targetMob != null && !targetMob.isDead){	
			double d5 = targetMob.posX - posX;
            double d6 = targetMob.boundingBox.minY + targetMob.height / 2.0F - (posY + 1.25D);
            double d7 = targetMob.posZ - posZ;
			
			EntityIMSBomb entitylargefireball = new EntityIMSBomb(worldObj, targetMob, posX, posY, posZ, d5, d6, d7, 0);
            entitylargefireball.launching = false;
            worldObj.spawnEntityInWorld(entitylargefireball);
            this.setDead();
		}else{
			this.setDead();
		}
	}

	protected void onImpact(MovingObjectPosition par1MovingObjectPosition){
		if(!this.worldObj.isRemote){
			if(par1MovingObjectPosition.typeOfHit == MovingObjectType.BLOCK && worldObj.getBlock(par1MovingObjectPosition.blockX, par1MovingObjectPosition.blockY, par1MovingObjectPosition.blockZ) != mod_SecurityCraft.ims){
				this.worldObj.createExplosion(this, par1MovingObjectPosition.blockX, par1MovingObjectPosition.blockY + 1D, par1MovingObjectPosition.blockZ, 7F, true);
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
