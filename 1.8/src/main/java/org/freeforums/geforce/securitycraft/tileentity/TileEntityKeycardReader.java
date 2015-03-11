package org.freeforums.geforce.securitycraft.tileentity;

import org.freeforums.geforce.securitycraft.enums.EnumCustomModules;

import net.minecraft.nbt.NBTTagCompound;

public class TileEntityKeycardReader extends CustomizableSCTE{

	private int passLV = 0;
	private boolean isGivingPower = false;
	private boolean requiresExactKeycard = false;
	
	public int getPassLV(){
    	return passLV;
    }
    
    public void setPassLV(int par1){
    	passLV = par1;
    }
    
    public boolean getIsProvidingPower(){
    	return isGivingPower;
    }
    
    public void setIsProvidingPower(boolean par1){
    	isGivingPower = par1;
    }
    
    public void setRequiresExactKeycard(boolean par1) {
    	requiresExactKeycard = par1;
	}
    
    public boolean doesRequireExactKeycard() {
    	return requiresExactKeycard;
	}
    
    
    
    /**
     * Writes a tile entity to NBT.
     */
    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setInteger("passLV", this.passLV);
        par1NBTTagCompound.setBoolean("requiresExactKeycard", this.requiresExactKeycard);
    }

    /**
     * Reads a tile entity from NBT.
     */
    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
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

	protected EnumCustomModules[] getCustomizableOptions() {
		return new EnumCustomModules[]{EnumCustomModules.WHITELIST, EnumCustomModules.BLACKLIST};
	}
    
}
