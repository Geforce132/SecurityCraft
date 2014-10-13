package org.freeforums.geforce.securitycraft.tileentity;

import java.util.Iterator;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.util.AxisAlignedBB;

import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;
import org.freeforums.geforce.securitycraft.network.packets.PacketUpdateLogger;

public class TileEntityLogger extends TileEntitySCTE {
	
	public String[] players = new String[100];
	
	public void logPlayers(){
		if(this.worldObj.isRemote){
			return;
		}else{
			
		double d0 = (double)(mod_SecurityCraft.configHandler.usernameLoggerSearchRadius);
		
		AxisAlignedBB axisalignedbb = AxisAlignedBB.getAABBPool().getAABB((double)this.xCoord, (double)this.yCoord, (double)this.zCoord, (double)(this.xCoord + 1), (double)(this.yCoord + 1), (double)(this.zCoord + 1)).expand(d0, d0, d0);
        List list = this.worldObj.getEntitiesWithinAABB(EntityPlayer.class, axisalignedbb);
        Iterator iterator = list.iterator();
        EntityPlayer entityplayer;
        
        while(iterator.hasNext()){
        	addPlayerName(((EntityPlayer)iterator.next()).getCommandSenderName());
        }
        
    	sendChangeToClient();

		}
	}
    
	
	private void addPlayerName(String username) {
		if(!hasPlayerName(username)){
	        for(int i = 0; i < this.players.length; i++){
	        	if(this.players[i] == "" || this.players[i] == null){
	        		this.players[i] = username;
	        		break;
	        	}else{
	        		continue;
	        	}
	        }
		}
	}

	private boolean hasPlayerName(String username) {
        for(int i = 0; i < this.players.length; i++){
        	if(this.players[i] == username){
        		return true;
        	}else{
        		continue;
        	}
        }

		return false;
	}

	/**
     * Writes a tile entity to NBT.
     */
    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeToNBT(par1NBTTagCompound);
        
        for(int i = 0; i < this.players.length; i++){
        	if(this.players[i] != null){
        		par1NBTTagCompound.setString("player" + i, this.players[i]);
        	}
        }
    }

    /**
     * Reads a tile entity from NBT.
     */
    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readFromNBT(par1NBTTagCompound);
        
        for(int i = 0; i < this.players.length; i++){
        	if (par1NBTTagCompound.hasKey("player" + i))
        	{
        		this.players[i] = par1NBTTagCompound.getString("player" + i);
        	}
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
	
	
	public void sendChangeToClient()
    {
        for(int i = 0; i < this.players.length; i++){
        	if(this.players[i] != null){
        		//TODO
        		mod_SecurityCraft.network.sendToAll(new PacketUpdateLogger(this.xCoord, this.yCoord, this.zCoord, i, this.players[i]));
        		
        	}
	    }
    }

}
