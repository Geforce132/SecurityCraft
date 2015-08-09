package org.freeforums.geforce.securitycraft.tileentity;

import org.freeforums.geforce.securitycraft.api.CustomizableSCTE;
import org.freeforums.geforce.securitycraft.api.IPasswordProtected;
import org.freeforums.geforce.securitycraft.blocks.BlockKeypad;
import org.freeforums.geforce.securitycraft.misc.EnumCustomModules;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;

public class TileEntityKeyPanel extends CustomizableSCTE implements IPasswordProtected
{
	private String passcode;
    
    /**
     * Writes a tile entity to NBT.
     */
    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeToNBT(par1NBTTagCompound);
        
        if(this.passcode != null && !this.passcode.isEmpty()){
        	par1NBTTagCompound.setString("passcode", this.passcode);
        }
    }

    /**
     * Reads a tile entity from NBT.
     */
    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readFromNBT(par1NBTTagCompound);

        if (par1NBTTagCompound.hasKey("passcode"))
        {
        	if(par1NBTTagCompound.getInteger("passcode") != 0){
        		this.passcode = String.valueOf(par1NBTTagCompound.getInteger("passcode"));
        	}else{
        		this.passcode = par1NBTTagCompound.getString("passcode");
        	}
        }
    }
    
    public void activate(EntityPlayer player) {
    	if(!worldObj.isRemote && worldObj.getBlock(xCoord, yCoord, zCoord) instanceof BlockKeypad){
    		BlockKeypad.activate(worldObj, xCoord, yCoord, zCoord);
    	}
	}
    
	public String getPassword() {
		return this.passcode;
	}
	
	public void setPassword(String password) {
		passcode = password;
	}

	public EnumCustomModules[] getCustomizableOptions() {
		return new EnumCustomModules[]{EnumCustomModules.WHITELIST, EnumCustomModules.BLACKLIST};
	}
	
	public String[] getOptionDescriptions() {
		return new String[]{EnumChatFormatting.UNDERLINE + "Whitelist module:" + EnumChatFormatting.RESET + "\n\nAdding a whitelist module to a keypad will allow players to use the block without knowing the code.", EnumChatFormatting.UNDERLINE + "Blacklist module:" + EnumChatFormatting.RESET + "\n\nAdding a blacklist module to a keypad will ban players from interacting with the block."};
	}
}
