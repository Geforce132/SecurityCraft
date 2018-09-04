package net.geforcemods.securitycraft.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerDisguiseModule extends Container {

	private ModuleInventory inventory;

	public ContainerDisguiseModule(EntityPlayer player, InventoryPlayer playerInventory, ModuleInventory moduleInventory) {
		inventory = moduleInventory;
		addSlotToContainer(new AddonSlot(inventory, 0, 79, 20));

		for(int i = 0; i < 3; i++)
			for(int j = 0; j < 9; j++)
				addSlotToContainer(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));

		for(int i = 0; i < 9; i++)
			addSlotToContainer(new Slot(playerInventory, i, 8 + i * 18, 142));
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int index) {
		ItemStack slotStackCopy = null;
		Slot slot = (Slot) inventorySlots.get(index);

		if(slot != null && slot.getHasStack()) {
			ItemStack slotStack = slot.getStack();
			slotStackCopy = slotStack.copy();

			if(index < inventory.SIZE) {
				if(!mergeItemStack(slotStack, inventory.SIZE, 37, true))
					return null;

				slot.onSlotChange(slotStack, slotStackCopy);
			}
			else if(index >= inventory.SIZE)
				if(!mergeItemStack(slotStack, 0, inventory.SIZE, false))
					return null;

			if(slotStack.stackSize == 0)
				slot.putStack((ItemStack) null);
			else
				slot.onSlotChanged();

			if(slotStack.stackSize == slotStackCopy.stackSize)
				return null;

			slot.onPickupFromSlot(player, slotStack);
		}

		return slotStackCopy;
	}

	@Override
	public ItemStack slotClick(int slot, int button, int flag, EntityPlayer player) {
		if(slot >= 0 && getSlot(slot) != null && getSlot(slot).getStack() == player.getHeldItem())
			return null;

		return super.slotClick(slot, button, flag, player);
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return true;
	}


	public static class AddonSlot extends Slot {

		private ModuleInventory inventory;

		public AddonSlot(ModuleInventory inventory, int slotIndex, int xPos, int yPos) {
			super(inventory, slotIndex, xPos, yPos);
			this.inventory = inventory;
		}

		@Override
		public boolean isItemValid(ItemStack itemStack) {
			int numberOfItems = 0;
			int numberOfBlocks = 0;
			boolean isStackBlock = itemStack.getUnlocalizedName().startsWith("tile.");

			for(ItemStack stack : inventory.moduleInventory)
				if(stack != null && stack.getItem() != null)
					if(stack.getItem().getUnlocalizedName().startsWith("tile."))
						numberOfBlocks++;
					else
						numberOfItems++;

			if(isStackBlock && numberOfBlocks < inventory.maxNumberOfBlocks)
				return true;
			else if(!isStackBlock && numberOfItems < inventory.maxNumberOfItems)
				return true;

			return false;
		}

		@Override
		public int getSlotStackLimit() {
			return 1;
		}
	}

}
