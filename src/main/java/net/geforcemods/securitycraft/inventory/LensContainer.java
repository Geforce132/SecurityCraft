package net.geforcemods.securitycraft.inventory;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

public class LensContainer extends SimpleContainer {
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