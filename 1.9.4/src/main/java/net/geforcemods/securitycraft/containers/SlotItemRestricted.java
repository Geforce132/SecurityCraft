package net.geforcemods.securitycraft.containers;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class SlotItemRestricted extends Slot {
	
	private final IInventory inventory;
	private final Item[] prohibitedItems;

	public SlotItemRestricted(IInventory par1IInventory, int par2, int par3, int par4, Item... prohibitedItems) {
		super(par1IInventory, par2, par3, par4);
		this.inventory = par1IInventory;
		this.prohibitedItems = prohibitedItems;
	}
	
	@Override
	public boolean isItemValid(ItemStack stack) {
		if(stack.getItem() == null) return false;
		
		// Only allows items not in prohibitedItems[] to be placed in the slot.
		for(Item prohibitedItem : prohibitedItems) {
			if(stack.getItem() == prohibitedItem) {
				return false;
			}
		}
		
        return true;
    }
    
    @Override
	public void putStack(ItemStack stack) {
        this.inventory.setInventorySlotContents(getSlotIndex(), stack);
        onSlotChanged();
    }
}
