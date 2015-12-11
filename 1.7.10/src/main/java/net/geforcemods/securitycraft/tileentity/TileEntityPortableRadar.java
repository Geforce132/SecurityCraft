package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class TileEntityPortableRadar extends CustomizableSCTE {
	
	private String customName;
		
	//Using TileEntitySCTE.attacks() and the attackEntity() method to check for players. :3
    public boolean attackEntity(Entity entity) {
    	if (entity instanceof EntityPlayer) {
    		worldObj.scheduleBlockUpdate(xCoord, yCoord, zCoord, mod_SecurityCraft.portableRadar, 1);
    		return true;
    	} else {
    		return false;
    	}
    }
    
    public boolean canAttack() {
    	return true;
    }
    
    /**
     * Reads a tile entity from NBT.
     */
    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readFromNBT(par1NBTTagCompound);
         
        if (par1NBTTagCompound.hasKey("customName"))
        {
        	this.customName = par1NBTTagCompound.getString("customName");
        }      
    }

    /**
     * Writes a tile entity to NBT.
     */
    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeToNBT(par1NBTTagCompound);
        
        if (this.customName != null && !this.customName.isEmpty()) {
        	par1NBTTagCompound.setString("customName", this.customName);
        }

    }
    
	public String getCustomName() {
		return customName;
	}

	public void setCustomName(String customName) {
		this.customName = customName;
	}
	
	public boolean hasCustomName(){
		return (this.customName != null && !this.customName.isEmpty()) ? true : false;
	}

	public EnumCustomModules[] acceptedModules() {
		return new EnumCustomModules[]{EnumCustomModules.REDSTONE, EnumCustomModules.WHITELIST};
	}
	
}
