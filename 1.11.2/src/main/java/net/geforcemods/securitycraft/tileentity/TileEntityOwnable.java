package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.api.TileEntitySCTE;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.network.packets.PacketCRequestTEOwnableUpdate;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;

public class TileEntityOwnable extends TileEntitySCTE implements IOwnable {
	
	private Owner owner = new Owner();
    
    /**
     * Writes a tile entity to NBT.
     * @return 
     */
    @Override
	public NBTTagCompound writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeToNBT(par1NBTTagCompound);
        
        if(this.owner != null){
        	par1NBTTagCompound.setString("owner", this.owner.getName());
        	par1NBTTagCompound.setString("ownerUUID", this.owner.getUUID());
        }
        
        return par1NBTTagCompound;
    }

    /**
     * Reads a tile entity from NBT.
     */
    @Override
	public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readFromNBT(par1NBTTagCompound);

        if (par1NBTTagCompound.hasKey("owner"))
        {
            this.owner.setOwnerName(par1NBTTagCompound.getString("owner"));
        }
        
        if (par1NBTTagCompound.hasKey("ownerUUID"))
        {
            this.owner.setOwnerUUID(par1NBTTagCompound.getString("ownerUUID"));
        }      
    }
    
    @Override
	public SPacketUpdateTileEntity getUpdatePacket() {                
    	NBTTagCompound tag = new NBTTagCompound();                
    	this.writeToNBT(tag);                
    	return new SPacketUpdateTileEntity(pos, 1, tag);        
    }        
    
    @Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {                
    	readFromNBT(packet.getNbtCompound());        
    }  
    
    @Override
	public TileEntityOwnable intersectsEntities(){
        intersectsEntities = true;
        return this;
    }
    
    @Override
	public Owner getOwner(){
    	return owner;
    }

	@Override
	public void setOwner(String uuid, String name) {
		owner.set(uuid, name);
	}
	
	@Override
	public void onLoad()
	{
		if(worldObj.isRemote)
			mod_SecurityCraft.network.sendToServer(new PacketCRequestTEOwnableUpdate(this));
	}
}
