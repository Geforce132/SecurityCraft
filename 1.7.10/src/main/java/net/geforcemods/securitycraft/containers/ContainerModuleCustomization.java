package net.geforcemods.securitycraft.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerModuleCustomization extends Container {
	
	private ModuleInventory inventory;
	private int moduleInventorySize = 0;
	
	public ContainerModuleCustomization(EntityPlayer par1Player, InventoryPlayer playerInventory, ModuleInventory moduleInventory) {
		this.inventory = moduleInventory;
		moduleInventorySize = moduleInventory.SIZE;
		
		if(moduleInventorySize == 1){
			this.addSlotToContainer(new AddonSlot(inventory, 0, 79, 20));
		}else if(moduleInventorySize == 2){
			this.addSlotToContainer(new AddonSlot(inventory, 0, 70, 20));
			this.addSlotToContainer(new AddonSlot(inventory, 1, 88, 20));
		}else if(moduleInventorySize == 3){
			this.addSlotToContainer(new AddonSlot(inventory, 0, 61, 20));
			this.addSlotToContainer(new AddonSlot(inventory, 1, 79, 20));
			this.addSlotToContainer(new AddonSlot(inventory, 2, 97, 20));
		}
		
		for(int i = 0; i < 3; i++) {
			for(int j = 0; j < 9; j++) {
				this.addSlotToContainer(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}

		for(int i = 0; i < 9; i++) {
			this.addSlotToContainer(new Slot(playerInventory, i, 8 + i * 18, 142));
		}
	}
	
	public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int index) {
		ItemStack itemstack = null;
		Slot slot = (Slot) this.inventorySlots.get(index);

		if(slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			if(index < moduleInventorySize) {
				if(!this.mergeItemStack(itemstack1, moduleInventorySize, 48, true)) {
					return null;
				}

				slot.onSlotChange(itemstack1, itemstack);
			}
			else {
				if(index >= moduleInventorySize) {
					if(!this.mergeItemStack(itemstack1, 0, moduleInventorySize, false)) {
						return null;
					}
				}
			}
			
			if(itemstack1.stackSize == 0) {
				slot.putStack((ItemStack) null);
			}
			else {
				slot.onSlotChanged();
			}

			if(itemstack1.stackSize == itemstack.stackSize) {
				return null;
			}

			slot.onPickupFromSlot(par1EntityPlayer, itemstack1);
		}

		return itemstack;
	}

	public ItemStack slotClick(int slot, int button, int flag, EntityPlayer player) {
		if(slot >= 0 && getSlot(slot) != null && getSlot(slot).getStack() == player.getHeldItem()) {
			return null;
		}

		return super.slotClick(slot, button, flag, player);
	}

	public boolean canInteractWith(EntityPlayer player) {
		return true;
	}
	
	
	public static class AddonSlot extends Slot {
		
		private ModuleInventory inventory;
		
		public AddonSlot(ModuleInventory par1IInventory, int par2, int par3, int par4) {
			super(par1IInventory, par2, par3, par4);
			this.inventory = par1IInventory;
		}
		
	    public boolean isItemValid(ItemStack par1ItemStack) {
	    	int numberOfItems = 0;
			int numberOfBlocks = 0;
			boolean isStackBlock = par1ItemStack.getUnlocalizedName().startsWith("tile.");
			
			for(ItemStack stack : inventory.moduleInventory) {
				if(stack != null && stack.getItem() != null) {
					if(stack.getItem().getUnlocalizedName().startsWith("tile.")) {
						numberOfBlocks++;
					}
					else {
						numberOfItems++;
					}
				}
			}
			
			if(isStackBlock && numberOfBlocks < inventory.maxNumberOfBlocks) {
				return true;
			}
			else if(!isStackBlock && numberOfItems < inventory.maxNumberOfItems) {
				return true;
			}
			
			return false;
        }
        
        public int getSlotStackLimit() {
            return 1;
        }        
	}

}
