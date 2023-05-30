package net.geforcemods.securitycraft.inventory;

import java.util.function.Predicate;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ItemRestrictedSlot extends Slot {
	private final Container inventory;
	private final Predicate<ItemStack> stackAllowed;

	public ItemRestrictedSlot(Container inventory, int index, int xPos, int yPos, Predicate<ItemStack> stackAllowed) {
		super(inventory, index, xPos, yPos);
		this.inventory = inventory;
		this.stackAllowed = stackAllowed;
	}

	@Override
	public boolean mayPlace(ItemStack stack) {
		if (stack.getItem() == null)
			return false;

		return stackAllowed.test(stack);
	}

	@Override
	public void set(ItemStack stack) {
		inventory.setItem(getSlotIndex(), stack);
		setChanged();
	}
}
