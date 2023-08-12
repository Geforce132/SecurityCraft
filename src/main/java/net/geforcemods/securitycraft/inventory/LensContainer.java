package net.geforcemods.securitycraft.inventory;

import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;

public class LensContainer extends InventoryBasic {
	public LensContainer(String title, boolean customName, int slotCount) {
		super(title, customName, slotCount);
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
