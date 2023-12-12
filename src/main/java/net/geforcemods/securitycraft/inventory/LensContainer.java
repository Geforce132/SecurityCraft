package net.geforcemods.securitycraft.inventory;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.items.LensItem;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

public class LensContainer extends SimpleContainer {
	public LensContainer(int size) {
		super(size);
	}

	@Override
	public boolean canAddItem(ItemStack stack) {
		return stack.is(SCContent.LENS.get()) && ((LensItem) stack.getItem()).hasCustomColor(stack);
	}

	@Override
	public boolean canPlaceItem(int slot, ItemStack stack) {
		return canAddItem(stack);
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