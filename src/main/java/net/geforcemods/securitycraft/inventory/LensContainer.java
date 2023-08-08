package net.geforcemods.securitycraft.inventory;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public class LensContainer extends Inventory {
	public LensContainer(int size) {
		super(size);
	}

	@Override
	public int getMaxStackSize() {
		return 1;
	}

	public void setItemExclusively(int index, ItemStack stack) {
		items.set(index, stack);

		if (!stack.isEmpty() && stack.getCount() > getMaxStackSize())
			stack.setCount(getMaxStackSize());
	}
}