package net.geforcemods.securitycraft.inventory;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.items.KeycardItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

public class KeycardHolderMenu extends Container {
	public static final int CONTAINER_SIZE = 5;

	public KeycardHolderMenu(int id, PlayerInventory playerInventory, ItemContainer keycardHolderInv) {
		super(SCContent.KEYCARD_HOLDER_MENU.get(), id);

		for (int i = 0; i < CONTAINER_SIZE; i++) {
			addSlot(new ItemRestrictedSlot(keycardHolderInv, i, 44 + (i * 18), 20, stack -> stack.getItem() instanceof KeycardItem));
		}

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, i * 18 + 51));
			}
		}

		for (int i = 0; i < 9; i++) {
			addSlot(new Slot(playerInventory, i, 8 + i * 18, 109));
		}
	}

	@Override
	public ItemStack quickMoveStack(PlayerEntity player, int index) {
		ItemStack slotStackCopy = ItemStack.EMPTY;
		Slot slot = slots.get(index);

		if (slot != null && slot.hasItem()) {
			ItemStack slotStack = slot.getItem();
			slotStackCopy = slotStack.copy();

			if (index < CONTAINER_SIZE) {
				if (!moveItemStackTo(slotStack, CONTAINER_SIZE, 36 + CONTAINER_SIZE, true))
					return ItemStack.EMPTY;

				slot.onQuickCraft(slotStack, slotStackCopy);
			}
			else if (index >= CONTAINER_SIZE) {
				if (!moveItemStackTo(slotStack, 0, CONTAINER_SIZE, false))
					return ItemStack.EMPTY;
			}

			if (slotStack.getCount() == 0)
				slot.set(ItemStack.EMPTY);
			else
				slot.setChanged();

			if (slotStack.getCount() == slotStackCopy.getCount())
				return ItemStack.EMPTY;

			slot.onTake(player, slotStack);
		}

		return slotStackCopy;
	}

	@Override
	public boolean stillValid(PlayerEntity player) {
		return true;
	}
}