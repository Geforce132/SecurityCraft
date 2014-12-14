package org.freeforums.geforce.securitycraft.tileentity;


public class TileEntityReinforcedDoor extends TileEntityOwnable{
	
//	private String doorOwner;
//	
//	public Packet getDescriptionPacket() {                
//    	NBTTagCompound tag = new NBTTagCompound();                
//    	this.writeToNBT(tag);                
//    	return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, tag);        
//    }        
//    
//    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {                
//    	readFromNBT(packet.func_148857_g());        
//    }
//
//	/**
//     * Writes a tile entity to NBT.
//     */
//    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
//    {
//        super.writeToNBT(par1NBTTagCompound);
//        
//        if(this.doorOwner != null && this.doorOwner != ""){
//        	par1NBTTagCompound.setString("doorOwner", this.doorOwner);
//        }
//    }
//
//    /**
//     * Reads a tile entity from NBT.
//     */
//    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
//    {
//        super.readFromNBT(par1NBTTagCompound);
//
//        if (par1NBTTagCompound.hasKey("doorOwner"))
//        {
//            this.doorOwner = par1NBTTagCompound.getString("doorOwner");
//        }
//    }
//
//	public void setDoorOwner(String username) {
//		this.doorOwner = username;
//	}
//	
//	public String getDoorOwner(){
//		return this.doorOwner;
//	}

}
