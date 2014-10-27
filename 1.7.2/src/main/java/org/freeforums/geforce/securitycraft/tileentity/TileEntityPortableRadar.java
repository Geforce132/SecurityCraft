package org.freeforums.geforce.securitycraft.tileentity;

import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;

import net.minecraft.nbt.NBTTagCompound;

public class TileEntityPortableRadar extends TileEntitySCTE {
	
	private String username;
	private String customName;
	private boolean EMPed = false;
	
	private int cooldown = 0;

    
	/**
     * Allows the entity to update its state. Overridden in most subclasses, e.g. the mob spawner uses this to count
     * ticks and creates a new spawn inside its implementation.
     */
    public void updateEntity()
    {
        this.cooldown++;
        
    	if(cooldown == mod_SecurityCraft.configHandler.portableRadarDelay){
    		this.worldObj.scheduleBlockUpdate(xCoord, yCoord, zCoord, mod_SecurityCraft.portableRadar, 1);
    		this.cooldown = 0;
    	}
    }
    
    /**
     * Reads a tile entity from NBT.
     */
    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readFromNBT(par1NBTTagCompound);
       
        if (par1NBTTagCompound.hasKey("owner"))
        {
            this.username = par1NBTTagCompound.getString("owner");
        }
        
        if (par1NBTTagCompound.hasKey("emped"))
        {
            this.EMPed = par1NBTTagCompound.getBoolean("emped");
        }
        
        if (par1NBTTagCompound.hasKey("customName")){
        	this.customName = par1NBTTagCompound.getString("customName");
        }
        
        if (par1NBTTagCompound.hasKey("cooldown")){
        	this.cooldown = par1NBTTagCompound.getInteger("cooldown");
        }
    }

    /**
     * Writes a tile entity to NBT.
     */
    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setString("owner", this.username);
        par1NBTTagCompound.setBoolean("emped", this.EMPed);
        par1NBTTagCompound.setInteger("cooldown", this.cooldown);

        if(this.customName != null && !this.customName.isEmpty()){
        	par1NBTTagCompound.setString("customName", this.customName);
        }

    }

	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getUsername(){
		return this.username;
	}
	
	public void setEmped(boolean emped) {
		this.EMPed = emped;
	}
	
	public boolean isEmped(){
		return this.EMPed;
	}

	public String getCustomName() {
		return customName;
	}

	public void setCustomName(String customName) {
		this.customName = customName;
	}
	
	public boolean hasCustomName(){
		return (this.customName != null && !this.customName.isEmpty()) ? true : false;
	}

}
