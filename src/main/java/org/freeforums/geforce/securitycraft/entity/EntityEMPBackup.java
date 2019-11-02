package org.freeforums.geforce.securitycraft.entity;

import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import org.freeforums.geforce.securitycraft.main.HelpfulMethods;
import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityEMPBackup extends Entity{
	

	public EntityEMPBackup(World par1World) {
		super(par1World);
	}
	
	public EntityEMPBackup(World par1World, double par2, double par4, double par6)
    {
        this(par1World);
        this.setPosition(par2, par4, par6);
        float f = (float)(Math.random() * Math.PI * 2.0D);
        this.motionX = (double)(-((float)Math.sin((double)f)) * 0.02F);
        this.motionY = 0.20000000298023224D;
        this.motionZ = (double)(-((float)Math.cos((double)f)) * 0.02F);
        this.prevPosX = par2;
        this.prevPosY = par4;
        this.prevPosZ = par6;
    }

	protected void entityInit() {}
	
	/**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.motionY = -1.0D;
        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.9800000190734863D;
        this.motionZ *= 0.9800000190734863D;
            
        if (this.isCollidedVertically)
        {
            //this.motionX *= 0.699999988079071D;
            //this.motionZ *= 0.699999988079071D;
            //this.motionY *= -0.5D;
            if(this.worldObj.isRemote){           	
	            this.electrify(this.worldObj, this.posX, this.posY, this.posZ, true);
            	
            }else{
            	this.electrify(this.worldObj, this.posX, this.posY, this.posZ, false);
                
            	
            }
            
            this.setDead();
            
            
        }

        
    }
	
	private void electrify(World worldObj, double par1, double par2, double par3, boolean isClient) {
		HelpfulMethods.disableEMPField(worldObj, par1 - (mod_SecurityCraft.configHandler.empRadius / 2), par2, par3 - (mod_SecurityCraft.configHandler.empRadius / 2), Blocks.stone, mod_SecurityCraft.configHandler.empRadius, mod_SecurityCraft.configHandler.empRadius, isClient);
	}

	/**
     * returns if this entity triggers Block.onEntityWalking on the blocks they walk on. used for spiders and wolves to
     * prevent them from trampling crops
     */
    protected boolean canTriggerWalking()
    {
        return false;
    }

    /**
     * Returns true if other Entities should be prevented from moving through this Entity.
     */
    public boolean canBeCollidedWith()
    {
        return !this.isDead;
    }
    

	protected void readEntityFromNBT(NBTTagCompound nbttagcompound) {}

	protected void writeEntityToNBT(NBTTagCompound nbttagcompound) {}
	
	 @SideOnly(Side.CLIENT)
	 public float getShadowSize(){
		 return 0.0F;
	 }

}
