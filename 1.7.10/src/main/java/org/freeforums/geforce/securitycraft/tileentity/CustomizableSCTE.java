package org.freeforums.geforce.securitycraft.tileentity;

import org.freeforums.geforce.securitycraft.items.ItemModule;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public abstract class CustomizableSCTE extends TileEntitySCTE implements IInventory{
	
	private ItemStack[] itemStacks = new ItemStack[getNumberOfCustomizableOptions()];
	
	public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
		super.readFromNBT(par1NBTTagCompound);
		
		NBTTagList nbttaglist = par1NBTTagCompound.getTagList("Items", 10);
        this.itemStacks = new ItemStack[getNumberOfCustomizableOptions()];

        for (int i = 0; i < nbttaglist.tagCount(); ++i)
        {
            NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
            byte b0 = nbttagcompound1.getByte("Slot");

            if (b0 >= 0 && b0 < this.itemStacks.length)
            {
                this.itemStacks[b0] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
            }
        }
    }
	
	public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
		super.writeToNBT(par1NBTTagCompound);
		
		NBTTagList nbttaglist = new NBTTagList();

        for(int i = 0; i < this.itemStacks.length; i++){
            if (this.itemStacks[i] != null)
            {
                NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                nbttagcompound1.setByte("Slot", (byte)i);
                this.itemStacks[i].writeToNBT(nbttagcompound1);
                nbttaglist.appendTag(nbttagcompound1);
            }
        }

        par1NBTTagCompound.setTag("Items", nbttaglist);
    }
	
	public int getSizeInventory() {
		return getNumberOfCustomizableOptions();
	}

	public ItemStack getStackInSlot(int par1) {
		return this.itemStacks[par1];
	}

	public ItemStack decrStackSize(int par1, int par2)
    {
        if (this.itemStacks[par1] != null)
        {
            ItemStack itemstack;

            if (this.itemStacks[par1].stackSize <= par2)
            {
                itemstack = this.itemStacks[par1];
                this.itemStacks[par1] = null;
                return itemstack;
            }
            else
            {
                itemstack = this.itemStacks[par1].splitStack(par2);

                if (this.itemStacks[par1].stackSize == 0)
                {
                    this.itemStacks[par1] = null;
                }

                return itemstack;
            }
        }
        else
        {
            return null;
        }
    }

	public ItemStack getStackInSlotOnClosing(int par1)
    {
        if (this.itemStacks[par1] != null)
        {
            ItemStack itemstack = this.itemStacks[par1];
            this.itemStacks[par1] = null;
            return itemstack;
        }
        else
        {
            return null;
        }
    }

	/**
     * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
     */
    public void setInventorySlotContents(int par1, ItemStack par2)
    {
        this.itemStacks[par1] = par2;

        if (par2 != null && par2.stackSize > this.getInventoryStackLimit())
        {
        	par2.stackSize = this.getInventoryStackLimit();
        }
    }

	public String getInventoryName() {
		return "Customize";
	}

	public boolean hasCustomInventoryName() {
		return true;
	}

	public int getInventoryStackLimit() {
		return 1;
	}

	public boolean isUseableByPlayer(EntityPlayer p_70300_1_) {
		return true;
	}

	public void openInventory() {}

	public void closeInventory() {}

	public boolean isItemValidForSlot(int par1, ItemStack par2ItemStack) {
		return par2ItemStack.getItem() instanceof ItemModule ? true : false;
	}
	
	public abstract int getNumberOfCustomizableOptions();
}
