package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;

public class TileEntityPortableRadar extends CustomizableSCTE {
	
	private String username;
	private String customName;
	
	private int cooldown = 0;
	
	
	/**
     * Allows the entity to update its state. Overridden in most subclasses, e.g. the mob spawner uses this to count
     * ticks and creates a new spawn inside its implementation.
     */
    public void updateEntity()
    {
    	this.cooldown++;
        
    	if(cooldown == mod_SecurityCraft.configHandler.portableRadarDelay){
    		this.worldObj.scheduleBlockUpdate(xCoord, yCoord, zCoord, mod_SecurityCraft.portableRadar, 1);
    		this.cooldown = 0;
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
            this.username = par1NBTTagCompound.getString("owner");
        }
        
        if (par1NBTTagCompound.hasKey("customName")){
        	this.customName = par1NBTTagCompound.getString("customName");
        }
        
        if (par1NBTTagCompound.hasKey("cooldown")){
        	this.cooldown = par1NBTTagCompound.getInteger("cooldown");
        }
    }

    /**
     * Writes a tile entity to NBT.
     */
    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setString("owner", this.username);
        par1NBTTagCompound.setInteger("cooldown", this.cooldown);
        
        if(this.customName != null && !this.customName.isEmpty()){
        	par1NBTTagCompound.setString("customName", this.customName);
        }

    }

	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getUsername(){
		return this.username;
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

	public EnumCustomModules[] getCustomizableOptions() {
		return new EnumCustomModules[]{EnumCustomModules.REDSTONE, EnumCustomModules.WHITELIST};
	}

	public String[] getOptionDescriptions() {
		return new String[]{EnumChatFormatting.UNDERLINE + "Redstone module:" + EnumChatFormatting.RESET + "\n\nAdding a redstone module to a portable radar will cause the radar to emit a redstone signal whenever a player is within range.", EnumChatFormatting.UNDERLINE + "Whitelist module:" + EnumChatFormatting.RESET + "\n\nAdding a whitelist module to a portable radar will remove the whitelisted players from the radar's search."};
	}
	
}
