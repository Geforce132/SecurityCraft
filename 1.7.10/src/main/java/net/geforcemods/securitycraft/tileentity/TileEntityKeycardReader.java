package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.blocks.BlockKeycardReader;
import net.geforcemods.securitycraft.gui.GuiHandler;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

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
    	if(!worldObj.isRemote && worldObj.getBlock(xCoord, yCoord, zCoord) instanceof BlockKeycardReader){
    		BlockKeycardReader.activate(worldObj, xCoord, yCoord, zCoord);
    	}
	}
    
    public void openPasswordGUI(EntityPlayer player) {
		if(getPassword() == null) {    	
	    	player.openGui(mod_SecurityCraft.instance, GuiHandler.SETUP_KEYCARD_READER_ID, worldObj, xCoord, yCoord, zCoord);
		}
	}
    
    public boolean onCodebreakerUsed(int meta, EntityPlayer player, boolean isCodebreakerDisabled) {
		return false;
	}
    
    public String getPassword() {
		return passLV == 0 ? null : String.valueOf(passLV);
	}
    
    public void setPassword(String password) {
		passLV = Integer.parseInt(password);
	}

    public EnumCustomModules[] acceptedModules() {
		return new EnumCustomModules[]{EnumCustomModules.WHITELIST, EnumCustomModules.BLACKLIST};
	}
    
}
