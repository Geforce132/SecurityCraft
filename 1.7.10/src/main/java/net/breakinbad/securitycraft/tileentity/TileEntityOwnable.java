package net.breakinbad.securitycraft.tileentity;

import net.breakinbad.securitycraft.api.IOwnable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;

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
    
    public boolean isOwner(EntityPlayer player){
    	if(!ownerUUID.matches("ownerUUID")){
    		return ownerUUID.matches(player.getGameProfile().getId().toString());
    	}else{
    		return owner.matches(player.getCommandSenderName());
    	}
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
    
    public Packet getDescriptionPacket() {                
    	NBTTagCompound tag = new NBTTagCompound();                
    	this.writeToNBT(tag);                
    	return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, tag);        
    }        
    
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {                
    	readFromNBT(packet.func_148857_g());        
    }

}
