package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.api.IOwnable;
import net.minecraft.nbt.NBTTagCompound;

public class TileEntityOwnable extends TileEntitySCTE implements IOwnable {
	
	private String ownerUUID = "ownerUUID";
	private String owner = "owner";

	
	public String getOwnerName(){
    	return owner;
    }
	
	public String getOwnerUUID(){
    	return ownerUUID;
    }
	
	/**
	 * 
	 * @param par1 The owner's UUID.
	 * @param par2 The owner's name.
	 */ 
    public void setOwner(String par1, String par2){
    	ownerUUID = par1;
    	owner = par2;
    }
    
    /**
     * Writes a tile entity to NBT.
     */
    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeToNBT(par1NBTTagCompound);
        if(this.owner != null && this.owner != ""){
        	par1NBTTagCompound.setString("owner", this.owner);
        }
        
        if(this.ownerUUID != null && this.ownerUUID != ""){
        	par1NBTTagCompound.setString("ownerUUID", this.ownerUUID);
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
            this.owner = par1NBTTagCompound.getString("owner");
        }
        
        if (par1NBTTagCompound.hasKey("ownerUUID"))
        {
            this.ownerUUID = par1NBTTagCompound.getString("ownerUUID");
        }
    }

}
