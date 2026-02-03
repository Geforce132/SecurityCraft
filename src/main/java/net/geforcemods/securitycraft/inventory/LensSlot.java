package net.geforcemods.securitycraft.inventory;

import net.geforcemods.securitycraft.SCContent;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class LensSlot extends Slot {
	public LensSlot(Container container, int slot, int x, int y) {
		super(container, slot, x, y);
	}

	@Override
	public boolean mayPlace(ItemStack stack) {
		return stack.is(SCContent.LENS.get()) && stack.has(DataComponents.DYED_COLOR);
	}

	@Override
	public int getMaxStackSize() {
		return 1;
	}
}
