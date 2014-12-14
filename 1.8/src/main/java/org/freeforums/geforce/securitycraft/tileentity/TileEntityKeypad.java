package org.freeforums.geforce.securitycraft.tileentity;

import net.minecraft.nbt.NBTTagCompound;

import org.freeforums.geforce.securitycraft.misc.EnumCustomModules;

@SuppressWarnings("unused")
public class TileEntityKeypad extends CustomizableSCTE{
	
	
	private String passcode;
	private boolean isGettingHacked = false;
	private int currentlyHackedLetterPos = 0;
	
    public String getKeypadCode(){
    	return passcode;
    }
    
    public void setKeypadCode(String par1){
    	passcode = par1;
    }
    
    public boolean isGettingHacked(){
    	return isGettingHacked;
    }
    
    public void setIsGettingHacked(boolean par1){
    	isGettingHacked = par1;
    }
    
    
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

	public EnumCustomModules[] getCustomizableOptions() {
		return new EnumCustomModules[]{EnumCustomModules.WHITELIST, EnumCustomModules.BLACKLIST};
	}
	

}
