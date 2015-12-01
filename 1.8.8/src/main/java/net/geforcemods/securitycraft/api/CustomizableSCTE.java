package net.geforcemods.securitycraft.api;

import java.util.ArrayList;

import net.geforcemods.securitycraft.gui.GuiCustomizeBlock;
import net.geforcemods.securitycraft.items.ItemModule;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;

/**
 * Extend this class in your TileEntity to make it customizable. You will
 * be able to modify it with the various modules in SecurityCraft, and
 * have your block do different functions based on what modules are
 * inserted.
 * 
 * @author Geforce
 */
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

	public IChatComponent getDisplayName() {
		return new ChatComponentTranslation(getCommandSenderName());
	}
	
	public String getCommandSenderName(){
		return "Customize";
	}

	public boolean hasCustomName() {
		return true;
	}

	public int getInventoryStackLimit() {
		return 1;
	}

	public boolean isUseableByPlayer(EntityPlayer p_70300_1_) {
		return true;
	}

	public void openInventory(EntityPlayer player) {}

	public void closeInventory(EntityPlayer player) {}

	public boolean isItemValidForSlot(int par1, ItemStack par2ItemStack) {
		return par2ItemStack.getItem() instanceof ItemModule ? true : false;
	}
	
	public int getField(int id) {
		return 0;
	}

	public void setField(int id, int value) {
		
	}

	public int getFieldCount() {
		return 0;
	}
	
	public void clear() {
		for(int i = 0; i < itemStacks.length; i++){
			itemStacks[i] = null;
		}
	}
	
	////////////////////////
	   // MODULE STUFF //
	////////////////////////

	/**
	 * Called whenever a module is inserted into a slot in the "Customize" GUI.
	 * 
	 * @param stack The raw ItemStack being inserted.
	 * @param module The EnumCustomModules variant of stack.
	 */	
	public void onModuleInserted(ItemStack stack, EnumCustomModules module) {}
	
	/**
	 * Called whenever a module is removed from a slot in the "Customize" GUI.
	 * 
	 * @param stack The raw ItemStack being removed.
	 * @param module The EnumCustomModules variant of stack.
     */	
	public void onModuleRemoved(ItemStack stack, EnumCustomModules module) {}
	
	/**
	 * @return An ArrayList of all EnumCustomModules currently inserted in the TileEntity.
     */	
	public ArrayList<EnumCustomModules> getModules(){
		ArrayList<EnumCustomModules> modules = new ArrayList<EnumCustomModules>();
		
		for(ItemStack stack : this.itemStacks){
			if(stack != null && stack.getItem() instanceof ItemModule){
				modules.add(((ItemModule) stack.getItem()).getModule());
			}
		}
		
		return modules;
	}
	
	/**
	 * @return The ItemStack for the given EnumCustomModules type.
	 * If there is no ItemStack for that type, returns null.
     */
	public ItemStack getModule(EnumCustomModules module){
		for(int i = 0; i < this.itemStacks.length; i++){
			if(this.itemStacks[i] != null && this.itemStacks[i].getItem() instanceof ItemModule && ((ItemModule) this.itemStacks[i].getItem()).getModule() == module){
				return this.itemStacks[i];
			}
		}
		
		return null;
	}
	
	/**
	 * Inserts a generic copy of the given module type into the Customization inventory.
	 */
	public void insertModule(EnumCustomModules module){
		for(int i = 0; i < this.itemStacks.length; i++){
			if(this.itemStacks[i] == null && module != null){
				this.itemStacks[i] = new ItemStack(module.getItem()); 
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
		return this.acceptedModules().length;
	}
	
	public ArrayList<EnumCustomModules> getOptions(){
		ArrayList<EnumCustomModules> list = new ArrayList<EnumCustomModules>();
		
		for(EnumCustomModules module : acceptedModules()){
			list.add(module);
		}
		
		return list;
	}
	
	/**
	 * Return an array of what {@link EnumCustomModules} can be inserted
	 * into this tile entity.
	 */
	public abstract EnumCustomModules[] acceptedModules();
	
	/**
	 *  Set the descriptions for each module that gets shown on the info buttons in 
	 *  the Universal Block Modifier's "Customize" GUI. ({@link GuiCustomizeBlock})
	 */
	public abstract String[] getOptionDescriptions();
	
}
