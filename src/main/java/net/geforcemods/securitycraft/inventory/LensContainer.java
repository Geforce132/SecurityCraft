package net.geforcemods.securitycraft.inventory;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.items.LensItem;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;

public class LensContainer extends InventoryBasic {
	public LensContainer(int slotCount) {
		super("", false, slotCount);
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		return stack.getItem() == SCContent.lens && ((LensItem) stack.getItem()).hasColor(stack);
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	public void setItemExclusively(int index, ItemStack stack) {
		inventoryContents.set(index, stack);

		if (!stack.isEmpty() && stack.getCount() > getInventoryStackLimit())
			stack.setCount(getInventoryStackLimit());
	}
}
