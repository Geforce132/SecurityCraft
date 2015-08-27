package net.breakinbad.securitycraft.tileentity;

import net.breakinbad.securitycraft.api.CustomizableSCTE;
import net.breakinbad.securitycraft.api.IPasswordProtected;
import net.breakinbad.securitycraft.blocks.BlockKeycardReader;
import net.breakinbad.securitycraft.main.Utils.BlockUtils;
import net.breakinbad.securitycraft.misc.EnumCustomModules;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;

public class TileEntityKeycardReader extends CustomizableSCTE implements IPasswordProtected {

	private int passLV = 0;
	private boolean requiresExactKeycard = false;
	
	/**
     * Writes a tile entity to NBT.
     */
    public void writeToNBT(NBTTagCompound par1NBTTagCompound){
        super.writeToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setInteger("passLV", this.passLV);
        par1NBTTagCompound.setBoolean("requiresExactKeycard", this.requiresExactKeycard);
    }

    /**
     * Reads a tile entity from NBT.
     */
    public void readFromNBT(NBTTagCompound par1NBTTagCompound){
        super.readFromNBT(par1NBTTagCompound);

        if (par1NBTTagCompound.hasKey("passLV"))
        {
            this.passLV = par1NBTTagCompound.getInteger("passLV");
        }
        
        if (par1NBTTagCompound.hasKey("requiresExactKeycard"))
        {
            this.requiresExactKeycard = par1NBTTagCompound.getBoolean("requiresExactKeycard");
        }    
        
    }
   
    public void setRequiresExactKeycard(boolean par1) {
    	requiresExactKeycard = par1;
	}
    
    public boolean doesRequireExactKeycard() {
    	return requiresExactKeycard;
	}
    
    public void activate(EntityPlayer player) {
    	if(!worldObj.isRemote && BlockUtils.getBlock(getWorld(), getPos()) instanceof BlockKeycardReader){
    		BlockKeycardReader.activate(worldObj, getPos());
    	}
	}
      
    public String getPassword() {
		return passLV == 0 ? null : String.valueOf(passLV);
	}
    
    public void setPassword(String password) {
		passLV = Integer.parseInt(password);
	}

    public EnumCustomModules[] getCustomizableOptions() {
		return new EnumCustomModules[]{EnumCustomModules.WHITELIST, EnumCustomModules.BLACKLIST};
	}

	public String[] getOptionDescriptions() {
		return new String[]{EnumChatFormatting.UNDERLINE + "Whitelist module:" + EnumChatFormatting.RESET + "\n\nAdding a whitelist module to a keycard reader will allow players to use the reader using any keycard.", EnumChatFormatting.UNDERLINE + "Blacklist module:" + EnumChatFormatting.RESET + "\n\nAdding a blacklist module to a keycard reader will ban players from interacting with the reader."};
	}
    
}
