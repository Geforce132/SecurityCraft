package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.api.TileEntitySCTE;
import net.minecraft.nbt.NBTTagCompound;

public class TileEntityOwnable extends TileEntitySCTE implements IOwnable {
	
	private Owner owner = new Owner();

    /**
     * Writes a tile entity to NBT.
     */
    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeToNBT(par1NBTTagCompound);
        
        if(this.owner != null){
        	par1NBTTagCompound.setString("owner", this.owner.getName());
        	par1NBTTagCompound.setString("ownerUUID", this.owner.getUUID());
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
            this.owner.setOwnerName(par1NBTTagCompound.getString("owner"));
        }
        
        if (par1NBTTagCompound.hasKey("ownerUUID"))
        {
            this.owner.setOwnerUUID(par1NBTTagCompound.getString("ownerUUID"));
        }
    }
    
    public Owner getOwner(){
    	return owner;
    }

	public void setOwner(String uuid, String name) {
		owner.set(uuid, name);
	}
    
}
