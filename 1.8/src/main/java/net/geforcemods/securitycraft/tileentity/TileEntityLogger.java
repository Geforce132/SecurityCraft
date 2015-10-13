package net.geforcemods.securitycraft.tileentity;

import java.util.Iterator;
import java.util.List;

import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.network.packets.PacketUpdateLogger;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.util.AxisAlignedBB;

public class TileEntityLogger extends TileEntityOwnable {
	
	public String[] players = new String[100];
	
    public void update(){
        if(!this.worldObj.isRemote && this.worldObj.getTotalWorldTime() % 80L == 0L && this.worldObj.isBlockIndirectlyGettingPowered(this.getPos()) > 0){
        	this.logPlayers();
        }
    }
	
	public void logPlayers(){
		double d0 = (double)(mod_SecurityCraft.configHandler.usernameLoggerSearchRadius);
		
		AxisAlignedBB axisalignedbb = AxisAlignedBB.fromBounds((double) pos.getX(), (double) pos.getY(), (double) pos.getZ(), (double)(pos.getX() + 1), (double)(pos.getY() + 1), (double)(pos.getZ() + 1)).expand(d0, d0, d0);
        List list = this.worldObj.getEntitiesWithinAABB(EntityPlayer.class, axisalignedbb);
        Iterator iterator = list.iterator();
        EntityPlayer entityplayer;
        
        while(iterator.hasNext()){
        	addPlayerName(((EntityPlayer)iterator.next()).getName());
        }
        
    	sendChangeToClient();
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

    public void writeToNBT(NBTTagCompound par1NBTTagCompound){
        super.writeToNBT(par1NBTTagCompound);
        
        for(int i = 0; i < this.players.length; i++){
        	if(this.players[i] != null){
        		par1NBTTagCompound.setString("player" + i, this.players[i]);
        	}
        }
    }

    public void readFromNBT(NBTTagCompound par1NBTTagCompound){
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
    	return new S35PacketUpdateTileEntity(pos, 1, tag);        
    }        
    
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {                
    	readFromNBT(packet.getNbtCompound());        
    }
	
	public void sendChangeToClient(){
        for(int i = 0; i < this.players.length; i++){
        	if(this.players[i] != null){
        		//TODO
        		mod_SecurityCraft.network.sendToAll(new PacketUpdateLogger(pos.getX(), pos.getY(), pos.getZ(), i, this.players[i]));
        		
        	}
	    }
    }

}
