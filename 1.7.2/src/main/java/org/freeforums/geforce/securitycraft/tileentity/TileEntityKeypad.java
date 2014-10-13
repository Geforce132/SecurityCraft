package org.freeforums.geforce.securitycraft.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

@SuppressWarnings("unused")
public class TileEntityKeypad extends TileEntityOwnable{
	
	
	private int passcode;
	private boolean isGettingHacked = false;
	private int currentlyHackedLetterPos = 0;
	
    public int getKeypadCode(){
    	return passcode;
    }
    
    public void setKeypadCode(int par1){
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
        par1NBTTagCompound.setInteger("passcode", this.passcode);
    }

    /**
     * Reads a tile entity from NBT.
     */
    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readFromNBT(par1NBTTagCompound);

        if (par1NBTTagCompound.hasKey("passcode"))
        {
            this.passcode = par1NBTTagCompound.getInteger("passcode");
        }
    }
  
    
    
    
    

}
