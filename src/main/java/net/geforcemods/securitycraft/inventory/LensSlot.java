package net.geforcemods.securitycraft.inventory;

import net.geforcemods.securitycraft.SCContent;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.IDyeableArmorItem;
import net.minecraft.item.ItemStack;

public class LensSlot extends Slot {
	public LensSlot(IInventory container, int slot, int x, int y) {
		super(container, slot, x, y);
	}

	@Override
	public boolean mayPlace(ItemStack stack) {
		return stack.getItem() == SCContent.LENS.get() && ((IDyeableArmorItem) stack.getItem()).hasCustomColor(stack);
	}

	@Override
	public int getMaxStackSize() {
		return 1;
	}
}
