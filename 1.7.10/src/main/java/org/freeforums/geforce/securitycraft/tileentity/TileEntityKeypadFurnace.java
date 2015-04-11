package org.freeforums.geforce.securitycraft.tileentity;

import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityFurnace;

import org.freeforums.geforce.securitycraft.blocks.BlockKeypadFurnace;
import org.freeforums.geforce.securitycraft.interfaces.IPasswordProtected;

import cpw.mods.fml.common.ObfuscationReflectionHelper;

public class TileEntityKeypadFurnace extends TileEntityFurnace implements ISidedInventory, IPasswordProtected {

	private String passcode;
	private String ownerUUID = "ownerUUID";
	private String owner = "owner";

	public void writeToNBT(NBTTagCompound par1NBTTagCompound){
		super.writeToNBT(par1NBTTagCompound);    
		
		if(this.passcode != null && !this.passcode.isEmpty()){
        	par1NBTTagCompound.setString("passcode", this.passcode);
        }
		
		if(this.owner != null && this.owner != ""){
        	par1NBTTagCompound.setString("owner", this.owner);
        }
        
        if(this.ownerUUID != null && this.ownerUUID != ""){
        	par1NBTTagCompound.setString("ownerUUID", this.ownerUUID);
        }
	}
	
	public void readFromNBT(NBTTagCompound par1NBTTagCompound){
		super.readFromNBT(par1NBTTagCompound);   
		
		if (par1NBTTagCompound.hasKey("passcode"))
        {
        	if(par1NBTTagCompound.getInteger("passcode") != 0){
        		this.passcode = String.valueOf(par1NBTTagCompound.getInteger("passcode"));
        	}else{
        		this.passcode = par1NBTTagCompound.getString("passcode");
        	}
        }
		
		if (par1NBTTagCompound.hasKey("owner"))
        {
            this.owner = par1NBTTagCompound.getString("owner");
        }
        
        if (par1NBTTagCompound.hasKey("ownerUUID"))
        {
            this.ownerUUID = par1NBTTagCompound.getString("ownerUUID");
        }
	}
	
	public void updateEntity(){
        boolean flag = this.furnaceBurnTime > 0;
        boolean flag1 = false;

        if (this.furnaceBurnTime > 0){
            --this.furnaceBurnTime;
        }

        if (!this.worldObj.isRemote){
            if (this.furnaceBurnTime != 0 || this.getStackInSlot(1) != null && this.getStackInSlot(0) != null){
                if (this.furnaceBurnTime == 0 && this.canSmelt()){
                    this.currentItemBurnTime = this.furnaceBurnTime = getItemBurnTime(this.getStackInSlot(1));

                    if (this.furnaceBurnTime > 0){
                        flag1 = true;

                        if (this.getStackInSlot(1) != null){
                            --this.getStackInSlot(1).stackSize;

                            if (this.getStackInSlot(1).stackSize == 0){
                            	ItemStack[] array = ObfuscationReflectionHelper.getPrivateValue(TileEntityFurnace.class, this, 3);
                            	array[1] = array[1].getItem().getContainerItem(array[1]);
                            	ObfuscationReflectionHelper.setPrivateValue(TileEntityFurnace.class, this, array, 3);
                            }
                        }
                    }
                }

                if (this.isBurning() && this.canSmelt()){
                    ++this.furnaceCookTime;

                    if (this.furnaceCookTime == 200){
                        this.furnaceCookTime = 0;
                        this.smeltItem();
                        flag1 = true;
                    }
                }else{
                    this.furnaceCookTime = 0;
                }
            }

            if (flag != this.furnaceBurnTime > 0){
                flag1 = true;
            }
        }

        if (flag1){
            this.markDirty();
        }
    }
	
	private boolean canSmelt(){
        if (this.getStackInSlot(0) == null){
            return false;
        }else{
            ItemStack itemstack = FurnaceRecipes.smelting().getSmeltingResult(this.getStackInSlot(0));
            if (itemstack == null) return false;
            if (this.getStackInSlot(2) == null) return true;
            if (!this.getStackInSlot(2).isItemEqual(itemstack)) return false;
            int result = this.getStackInSlot(2).stackSize + itemstack.stackSize;
            return result <= getInventoryStackLimit() && result <= this.getStackInSlot(2).getMaxStackSize(); 
        }
    }
	
	public String getOwnerName(){
    	return owner;
    }
	
	public String getOwnerUUID(){
    	return ownerUUID;
    }
    
    public void setOwner(String par1, String par2){
    	ownerUUID = par1;
    	owner = par2;
    }
	
    public String getKeypadCode(){
    	return passcode;
    }
    
    public void setKeypadCode(String par1){
    	passcode = par1;
    }

	public String getPassword() {
		return passcode;
	}

}