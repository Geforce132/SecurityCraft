package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.network.packets.PacketUpdateLogger;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class TileEntityLogger extends TileEntityOwnable {
	
	public String[] players = new String[100];
	
	public boolean attackEntity(Entity entity) {		
		if (!this.worldObj.isRemote) {		
			if (entity instanceof EntityPlayer) {
		        	addPlayerName(((EntityPlayer) entity).getCommandSenderName());
		        	sendChangeToClient();
			}
		}
		
		return true;
	}
	
	public boolean canAttack() {
                if (worldObj != null) {
		    return worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);
                } else {
                    return false;
                }
	}
	
	private void addPlayerName(String username){
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

	private boolean hasPlayerName(String username){
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
    public void writeToNBT(NBTTagCompound par1NBTTagCompound){
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
    public void readFromNBT(NBTTagCompound par1NBTTagCompound){
        super.readFromNBT(par1NBTTagCompound);
        
        for(int i = 0; i < this.players.length; i++){
        	if (par1NBTTagCompound.hasKey("player" + i))
        	{
        		this.players[i] = par1NBTTagCompound.getString("player" + i);
        	}
        }
    }
	
	public void sendChangeToClient(){
        for(int i = 0; i < this.players.length; i++){
        	if(this.players[i] != null){
        		mod_SecurityCraft.network.sendToAll(new PacketUpdateLogger(this.xCoord, this.yCoord, this.zCoord, i, this.players[i]));
        		
        	}
	    }
    }

}
