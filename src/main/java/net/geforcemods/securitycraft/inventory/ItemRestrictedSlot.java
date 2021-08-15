package net.geforcemods.securitycraft.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ItemRestrictedSlot extends Slot {

	private final Container inventory;
	private final Item[] prohibitedItems;

	public ItemRestrictedSlot(Container inventory, int index, int xPos, int yPos, Item... prohibitedItems) {
		super(inventory, index, xPos, yPos);
		this.inventory = inventory;
		this.prohibitedItems = prohibitedItems;
	}

	@Override
	public boolean mayPlace(ItemStack stack) {
		if(stack.getItem() == null) return false;

		// Only allows items not in prohibitedItems[] to be placed in the slot.
		for(Item prohibitedItem : prohibitedItems)
			if(stack.getItem() == prohibitedItem)
				return false;

		return true;
	}

	@Override
	public void set(ItemStack stack) {
		inventory.setItem(getSlotIndex(), stack);
		setChanged();
	}
}
