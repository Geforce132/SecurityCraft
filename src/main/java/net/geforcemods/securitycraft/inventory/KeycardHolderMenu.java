package net.geforcemods.securitycraft.inventory;

import net.geforcemods.securitycraft.items.KeycardItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class KeycardHolderMenu extends Container {
	public static final int CONTAINER_SIZE = 5;

	public KeycardHolderMenu(InventoryPlayer playerInventory, ItemContainer keycardHolderInv) {
		for (int i = 0; i < CONTAINER_SIZE; i++) {
			addSlotToContainer(new ItemRestrictedSlot(keycardHolderInv, i, 44 + (i * 18), 20, stack -> stack.getItem() instanceof KeycardItem));
		}

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				addSlotToContainer(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, i * 18 + 51));
			}
		}

		for (int i = 0; i < 9; i++) {
			addSlotToContainer(new Slot(playerInventory, i, 8 + i * 18, 109));
		}
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int index) {
		ItemStack slotStackCopy = ItemStack.EMPTY;
		Slot slot = inventorySlots.get(index);

		if (slot != null && slot.getHasStack()) {
			ItemStack slotStack = slot.getStack();
			slotStackCopy = slotStack.copy();

			if (index < CONTAINER_SIZE) {
				if (!mergeItemStack(slotStack, CONTAINER_SIZE, 36 + CONTAINER_SIZE, true))
					return ItemStack.EMPTY;

				slot.onSlotChange(slotStack, slotStackCopy);
			}
			else if (!mergeItemStack(slotStack, 0, CONTAINER_SIZE, false))
				return ItemStack.EMPTY;

			if (slotStack.getCount() == 0)
				slot.putStack(ItemStack.EMPTY);
			else
				slot.onSlotChanged();

			if (slotStack.getCount() == slotStackCopy.getCount())
				return ItemStack.EMPTY;

			slot.onTake(player, slotStack);
		}

		return slotStackCopy;
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return true;
	}
}
