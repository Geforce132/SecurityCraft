package org.freeforums.geforce.securitycraft.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityChest;

import org.freeforums.geforce.securitycraft.interfaces.IOwnable;
import org.freeforums.geforce.securitycraft.interfaces.IPasswordProtected;

public class TileEntityKeypadChest extends TileEntityChest implements IOwnable, IPasswordProtected {
	
	private String passcode;
    private String ownerUUID;
    private String ownerName;

	public TileEntityKeypadChest adjacentChestZNeg;
    public TileEntityKeypadChest adjacentChestXPos;
    public TileEntityKeypadChest adjacentChestXNeg;
    public TileEntityKeypadChest adjacentChestZPos;

	
	public String getKeypadCode(){
    	return passcode;
    }
    
    public void setKeypadCode(String par1){
    	passcode = par1;
    }
    
    public String getOwnerUUID(){
    	return ownerUUID;
    }
    
    public String getOwnerName(){
    	return ownerName;
    }
    
    public void setOwner(String par1, String par2){
    	ownerUUID = par1;
    	ownerName = par2;
    }
    
    /**
     * Writes a tile entity to NBT.
     */
    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeToNBT(par1NBTTagCompound);
        
        if(this.passcode != null && !this.passcode.isEmpty()){
        	par1NBTTagCompound.setString("passcode", this.passcode);
        }
        
        if(this.ownerName != null && this.ownerName != ""){
        	par1NBTTagCompound.setString("owner", this.ownerName);
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

        if (par1NBTTagCompound.hasKey("passcode"))
        {
        	if(par1NBTTagCompound.getInteger("passcode") != 0){
        		this.passcode = String.valueOf(par1NBTTagCompound.getInteger("passcode"));
        	}else{
        		this.passcode = par1NBTTagCompound.getString("passcode");
        	}
        }
        
        if (par1NBTTagCompound.hasKey("owner"))
        {
            this.ownerName = par1NBTTagCompound.getString("owner");
        }
        
        if (par1NBTTagCompound.hasKey("ownerUUID"))
        {
            this.ownerUUID = par1NBTTagCompound.getString("ownerUUID");
        }
    }
    
    /**
     * Returns the name of the inventory
     */
    public String getInventoryName()
    {
        return "Protected chest";
    }

	public String getPassword() {
		return (this.passcode != null && !this.passcode.isEmpty()) ? this.passcode : null;
	}
    
}
