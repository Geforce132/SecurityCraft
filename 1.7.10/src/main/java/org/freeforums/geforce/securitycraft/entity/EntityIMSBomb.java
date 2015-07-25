package org.freeforums.geforce.securitycraft.entity;

import org.freeforums.geforce.securitycraft.main.Utils.PlayerUtils;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;

public class EntityIMSBomb extends EntityFireball {

	private String playerName = null;
	public int ticksFlying = 0;
	public boolean launching = true;

	public EntityIMSBomb(World worldIn){
		super(worldIn);
		this.setSize(0.375F, 0.5F);
	}

	public EntityIMSBomb(World worldIn, double x, double y, double z){
		this(worldIn);
		this.setSize(0.375F, 0.5F);
		this.setPosition(x, y, z);
	}
	
	public EntityIMSBomb(World worldIn, double x, double y, double z, double targetX, double targetY, double targetZ){
		super(worldIn, x, y, z, targetX, targetY, targetZ);
		this.setSize(0.375F, 0.5F);
	}
	
	public EntityIMSBomb(World worldIn, EntityLivingBase targetEntity, double x, double y, double z){
        super(worldIn, targetEntity, x, y, z);
        this.playerName = targetEntity.getCommandSenderName();
        this.setSize(0.375F, 0.5F);
    }

	public void onUpdate(){	
		if(!launching){
			super.onUpdate();
			System.out.println("Updating: " + posX + " " + posY + " " + posZ + " " + FMLCommonHandler.instance().getEffectiveSide());
			return;
		}
		
		if(ticksFlying < 40 && launching){
			this.motionY = 0.25F;
			this.ticksFlying++;
			this.moveEntity(this.motionX, this.motionY, this.motionZ);
		}else if(ticksFlying >= 40 && launching){
			//this.motionY = 0.25F;
			//this.ticksFlying++;
			//this.moveEntity(this.motionX, this.motionY, this.motionZ);
			this.setTarget(playerName);
		}
	}

	public void setTarget(String player) {
		if(player != null && !this.worldObj.isRemote && PlayerUtils.isPlayerOnline(player)){
			System.out.println("Setting target pos to: " + posX + " " + posY + " " + posZ);
			EntityPlayer target = PlayerUtils.getPlayerFromName(player);
			
			double d5 = target.posX - posX;
            double d6 = target.boundingBox.minY + (double)(target.height / 2.0F) - ((double) posY + 1.25D);
            double d7 = target.posZ - posZ;
			
			//EntityIMSBomb entitylargefireball = new EntityIMSBomb(worldObj, target, d5, d6, d7);
			EntityIMSBomb entitylargefireball = new EntityIMSBomb(worldObj, posX, posY, posZ, d5, d6, d7);
            entitylargefireball.launching = false;
            worldObj.spawnEntityInWorld(entitylargefireball);
            System.out.println("Spawning new IMSBomb at pos: " + entitylargefireball.posX + " " + entitylargefireball.posY + " " + entitylargefireball.posZ);
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

}
