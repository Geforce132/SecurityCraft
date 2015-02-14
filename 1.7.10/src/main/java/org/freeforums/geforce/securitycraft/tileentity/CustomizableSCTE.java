package org.freeforums.geforce.securitycraft.tileentity;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import org.freeforums.geforce.securitycraft.items.ItemModule;
import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;
import org.freeforums.geforce.securitycraft.misc.EnumCustomModules;

public abstract class CustomizableSCTE extends TileEntityOwnable implements IInventory{
	
	public ItemStack[] itemStacks = new ItemStack[getNumberOfCustomizableOptions()];
	
	public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
		super.readFromNBT(par1NBTTagCompound);
		
		NBTTagList nbttaglist = par1NBTTagCompound.getTagList("Modules", 10);
        this.itemStacks = new ItemStack[getNumberOfCustomizableOptions()];

        for (int i = 0; i < nbttaglist.tagCount(); ++i)
        {
            NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
            byte b0 = nbttagcompound1.getByte("ModuleSlot");

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
                nbttagcompound1.setByte("ModuleSlot", (byte)i);
                this.itemStacks[i].writeToNBT(nbttagcompound1);
                nbttaglist.appendTag(nbttagcompound1);
            }
        }

        par1NBTTagCompound.setTag("Modules", nbttaglist);           
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
                this.onModuleRemoved(itemstack, ((ItemModule) itemstack.getItem()).getModule());
                return itemstack;
            }
            else
            {
                itemstack = this.itemStacks[par1].splitStack(par2);

                if (this.itemStacks[par1].stackSize == 0)
                {
                    this.itemStacks[par1] = null;
                }

                this.onModuleRemoved(itemstack, ((ItemModule) itemstack.getItem()).getModule());

                return itemstack;
            }
        }
        else
        {
            return null;
        }
    }
	
	/**
	 * Copy of decrStackSize which can't be overrided by subclasses.
	 */
	
	public ItemStack safeDecrStackSize(int par1, int par2)
    {
        if (this.itemStacks[par1] != null)
        {
            ItemStack itemstack;
            
            if (this.itemStacks[par1].stackSize <= par2)
            {
                itemstack = this.itemStacks[par1];
                this.itemStacks[par1] = null;
                this.onModuleRemoved(itemstack, ((ItemModule) itemstack.getItem()).getModule());
                return itemstack;
            }
            else
            {
                itemstack = this.itemStacks[par1].splitStack(par2);

                if (this.itemStacks[par1].stackSize == 0)
                {
                    this.itemStacks[par1] = null;
                }

                this.onModuleRemoved(itemstack, ((ItemModule) itemstack.getItem()).getModule());

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
        
        if(par2 != null){
        	this.onModuleInserted(par2, ((ItemModule) par2.getItem()).getModule());
        }
    }
    
    /**
     * Copy of setInventorySlotContents which can't be overrided by subclasses.
     */
    public void safeSetInventorySlotContents(int par1, ItemStack par2) {
    	this.itemStacks[par1] = par2;

        if (par2 != null && par2.stackSize > this.getInventoryStackLimit())
        {
        	par2.stackSize = this.getInventoryStackLimit();
        }
        
        if(par2 != null && par2.getItem() != null && par2.getItem() instanceof ItemModule){
        	this.onModuleInserted(par2, ((ItemModule) par2.getItem()).getModule());
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
	
	public void onModuleInserted(ItemStack stack, EnumCustomModules module) {}
	
	public void onModuleRemoved(ItemStack stack, EnumCustomModules module) {}
	
	public ItemStack getModule(EnumCustomModules module){
		for(int i = 0; i < this.itemStacks.length; i++){
			if(this.itemStacks[i] != null && this.itemStacks[i].getItem() instanceof ItemModule && ((ItemModule) this.itemStacks[i].getItem()).getModule() == module){
				return this.itemStacks[i];
			}
		}
		
		return null;
	}
    
    //TODO Be sure to update this whenever a new module is added!
	public static Item getModuleFromType(EnumCustomModules module){
		if(module == EnumCustomModules.WHITELIST){
			return mod_SecurityCraft.whitelistModule;
		}else if(module == EnumCustomModules.BLACKLIST){
			return mod_SecurityCraft.blacklistModule;
		}else if(module == EnumCustomModules.HARMING){
			return mod_SecurityCraft.harmingModule;
		}else if(module == EnumCustomModules.SMART){
			return mod_SecurityCraft.smartModule;
		}else if(module == EnumCustomModules.REDSTONE){
			return mod_SecurityCraft.redstoneModule;
		}else{
			return null;
		}
	}
	
	//TODO Be sure to update this whenever a new module is added!
	public static EnumCustomModules getTypeFromModule(ItemStack module){
		if(module.getItem() == mod_SecurityCraft.whitelistModule){
			return EnumCustomModules.WHITELIST;
		}else if(module.getItem() == mod_SecurityCraft.blacklistModule){
			return EnumCustomModules.BLACKLIST;
		}else if(module.getItem() == mod_SecurityCraft.harmingModule){
			return EnumCustomModules.HARMING;
		}else if(module.getItem() == mod_SecurityCraft.smartModule){
			return EnumCustomModules.SMART;
		}else if(module.getItem() == mod_SecurityCraft.redstoneModule){
			return EnumCustomModules.REDSTONE;
		}else{
			return null;
		}
	}
	
	/**
	 * Inserts a generic copy of the given module type into the Customization inventory.
	 */
	public void insertModule(EnumCustomModules module){
		for(int i = 0; i < this.itemStacks.length; i++){
			if(this.itemStacks[i] == null && module != null){
				this.itemStacks[i] = new ItemStack(getModuleFromType(module)); 
				break;
			}else if(this.itemStacks[i] != null && module == null){
				this.itemStacks[i] = null;
			}else{
				continue;
			}
		}
	}
	
	
	
	/**
	 * Inserts an exact copy of the given item into the Customization inventory.
	 */
	public void insertModule(ItemStack module){
		if(module == null || !(module.getItem() instanceof ItemModule)){ return; }
		
		for(int i = 0; i < this.itemStacks.length; i++){
			if(this.itemStacks[i] == null){
				this.itemStacks[i] = module.copy();
				break;
			}else{
				continue;
			}
		}
	}
	
	/**
	 * Removes the first item with the given module type from the inventory.
	 */
	public void removeModule(EnumCustomModules module){
		for(int i = 0; i < this.itemStacks.length; i++){
			if(this.itemStacks[i] != null && this.itemStacks[i].getItem() instanceof ItemModule && ((ItemModule) this.itemStacks[i].getItem()).getModule() == module){
				this.itemStacks[i] = null;
			}
		}
	}
	
	/**
	 * Does this inventory contain a item with the given module type?
	 */
	public boolean hasModule(EnumCustomModules module){
		if(module == null){
			for(int i = 0; i < this.itemStacks.length; i++){
				if(this.itemStacks[i] == null){
					return true;
				}
			}
		}else{
			for(int i = 0; i < this.itemStacks.length; i++){
				if(this.itemStacks[i] != null && this.itemStacks[i].getItem() instanceof ItemModule && ((ItemModule) this.itemStacks[i].getItem()).getModule() == module){
					return true;
				}
			}
		}
		
		return false;
	}
	
	public int getNumberOfCustomizableOptions(){
		return this.getCustomizableOptions().length;
	}
	
	public ArrayList<EnumCustomModules> getOptions(){
		ArrayList<EnumCustomModules> list = new ArrayList<EnumCustomModules>();
		
		for(EnumCustomModules module : getCustomizableOptions()){
			list.add(module);
		}
		
		return list;
	}
	
	protected abstract EnumCustomModules[] getCustomizableOptions();
	
}
