package org.freeforums.geforce.securitycraft.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityChest;

public class TileEntityKeypadChest extends TileEntityChest{
	
	private int passcode;
	private String owner;
	
	public TileEntityKeypadChest adjacentChestZNeg;
    public TileEntityKeypadChest adjacentChestXPos;
    public TileEntityKeypadChest adjacentChestXNeg;
    public TileEntityKeypadChest adjacentChestZPos;

	
	public int getKeypadCode(){
    	return passcode;
    }
    
    public void setKeypadCode(int par1){
    	passcode = par1;
    }
    
    public String getOwner(){
    	return owner;
    }
    
    public void setOwner(String par1){
    	owner = par1;
    }
    
    /**
     * Writes a tile entity to NBT.
     */
    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setInteger("passcode", this.passcode);
        
        if(this.owner != null && this.owner != ""){
        	par1NBTTagCompound.setString("owner", this.owner);
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
            this.passcode = par1NBTTagCompound.getInteger("passcode");
        }
        
        if (par1NBTTagCompound.hasKey("owner"))
        {
            this.owner = par1NBTTagCompound.getString("owner");
        }
    }
    
   
    
    /**
     * Returns the name of the inventory
     */
    public String getInventoryName()
    {
        return "Protected chest";
    }
    
//    private void func_145978_a(TileEntityChest p_145978_1_, int p_145978_2_)
//    {
//        if (p_145978_1_.isInvalid())
//        {
//            this.adjacentChestChecked = false;
//        }
//        else if (this.adjacentChestChecked)
//        {
//            switch (p_145978_2_)
//            {
//                case 0:
//                    if (this.adjacentChestZPos != p_145978_1_)
//                    {
//                        this.adjacentChestChecked = false;
//                    }
//
//                    break;
//                case 1:
//                    if (this.adjacentChestXNeg != p_145978_1_)
//                    {
//                        this.adjacentChestChecked = false;
//                    }
//
//                    break;
//                case 2:
//                    if (this.adjacentChestZNeg != p_145978_1_)
//                    {
//                        this.adjacentChestChecked = false;
//                    }
//
//                    break;
//                case 3:
//                    if (this.adjacentChestXPos != p_145978_1_)
//                    {
//                        this.adjacentChestChecked = false;
//                    }
//            }
//        }
//    }
//
//    /**
//     * Performs the check for adjacent chests to determine if this chest is double or not.
//     */
//    public void checkForAdjacentChests()
//    {
//        if (!this.adjacentChestChecked)
//        {
//            this.adjacentChestChecked = true;
//            this.adjacentChestZNeg = null;
//            this.adjacentChestXPos = null;
//            this.adjacentChestXNeg = null;
//            this.adjacentChestZPos = null;
//
//            if (this.func_145977_a(this.xCoord - 1, this.yCoord, this.zCoord))
//            {
//                this.adjacentChestXNeg = (TileEntityKeypadChest)this.worldObj.getTileEntity(this.xCoord - 1, this.yCoord, this.zCoord);
//            }
//
//            if (this.func_145977_a(this.xCoord + 1, this.yCoord, this.zCoord))
//            {
//                this.adjacentChestXPos = (TileEntityKeypadChest)this.worldObj.getTileEntity(this.xCoord + 1, this.yCoord, this.zCoord);
//            }
//
//            if (this.func_145977_a(this.xCoord, this.yCoord, this.zCoord - 1))
//            {
//                this.adjacentChestZNeg = (TileEntityKeypadChest)this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord - 1);
//            }
//
//            if (this.func_145977_a(this.xCoord, this.yCoord, this.zCoord + 1))
//            {
//                this.adjacentChestZPos = (TileEntityKeypadChest)this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord + 1);
//            }
//
//            if (this.adjacentChestZNeg != null)
//            {
//                this.adjacentChestZNeg.func_145978_a(this, 0);
//            }
//
//            if (this.adjacentChestZPos != null)
//            {
//                this.adjacentChestZPos.func_145978_a(this, 2);
//            }
//
//            if (this.adjacentChestXPos != null)
//            {
//                this.adjacentChestXPos.func_145978_a(this, 1);
//            }
//
//            if (this.adjacentChestXNeg != null)
//            {
//                this.adjacentChestXNeg.func_145978_a(this, 3);
//            }
//        }
//    }
//    
//    private boolean func_145977_a(int p_145977_1_, int p_145977_2_, int p_145977_3_)
//    {
//        Block block = this.worldObj.getBlock(p_145977_1_, p_145977_2_, p_145977_3_);
//        return block instanceof BlockKeypadChest && ((BlockKeypadChest)block).field_149956_a == this.func_145980_j();
//    }
 
}
