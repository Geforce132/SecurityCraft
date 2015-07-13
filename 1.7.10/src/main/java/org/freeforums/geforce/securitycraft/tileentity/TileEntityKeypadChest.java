package org.freeforums.geforce.securitycraft.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntityChest;

import org.freeforums.geforce.securitycraft.api.IOwnable;
import org.freeforums.geforce.securitycraft.api.IPasswordProtected;
import org.freeforums.geforce.securitycraft.blocks.BlockKeypadChest;

public class TileEntityKeypadChest extends TileEntityChest implements IOwnable, IPasswordProtected {
	
	private String passcode;
    private String ownerUUID;
    private String ownerName;

	public TileEntityKeypadChest adjacentChestZNeg;
    public TileEntityKeypadChest adjacentChestXPos;
    public TileEntityKeypadChest adjacentChestXNeg;
    public TileEntityKeypadChest adjacentChestZPos;
  
	
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
    
    public Packet getDescriptionPacket() {                
    	NBTTagCompound tag = new NBTTagCompound();                
    	this.writeToNBT(tag);                
    	return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, tag);        
    }        
    
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {                
    	readFromNBT(packet.func_148857_g());        
    }
    
    public String getInventoryName(){
        return "Protected chest";
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

	public void activate(EntityPlayer player) {
		if(!worldObj.isRemote && worldObj.getBlock(xCoord, yCoord, zCoord) instanceof BlockKeypadChest){
			BlockKeypadChest.activate(worldObj, xCoord, yCoord, zCoord, player);
    	}
	}

	public String getPassword() {
		return (this.passcode != null && !this.passcode.isEmpty()) ? this.passcode : null;
	}

	public void setPassword(String password) {
		passcode = password;
	}
	
}
