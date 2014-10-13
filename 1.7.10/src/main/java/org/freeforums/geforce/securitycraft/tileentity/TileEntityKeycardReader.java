package org.freeforums.geforce.securitycraft.tileentity;

import net.minecraft.nbt.NBTTagCompound;

public class TileEntityKeycardReader extends TileEntityOwnable{

	private int passLV = 0;
	private boolean isGivingPower = false;
	
	
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
    
    
    
    
    /**
     * Writes a tile entity to NBT.
     */
    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setInteger("passLV", this.passLV);

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
        
        
    }
    
}
