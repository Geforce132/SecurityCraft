package net.geforcemods.securitycraft.inventory;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.items.BriefcaseItem;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class BriefcaseMenu extends Container {
	public static final int CONTAINER_SIZE = 12;
	private final ItemContainer briefcaseInventory;

	public BriefcaseMenu(InventoryPlayer playerInventory, ItemContainer briefcaseInventory) {
		this.briefcaseInventory = briefcaseInventory;

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 4; j++) {
				addSlotToContainer(new Slot(briefcaseInventory, j + (i * 4), 53 + (j * 18), 17 + (i * 18)) {
					@Override
					public boolean isItemValid(ItemStack stack) {
						return stack.getItem() != SCContent.briefcase;
					}
				});
			}
		}

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				addSlotToContainer(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}

		for (int i = 0; i < 9; i++) {
			addSlotToContainer(new Slot(playerInventory, i, 8 + i * 18, 142));
		}
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int index) {
		ItemStack slotStackCopy = ItemStack.EMPTY;
		Slot slot = inventorySlots.get(index);

		if (slot != null && slot.getHasStack()) {
			ItemStack slotStack = slot.getStack();
			slotStackCopy = slotStack.copy();

			if (index < CONTAINER_SIZE) {
				if (!mergeItemStack(slotStack, CONTAINER_SIZE, 48, true))
					return ItemStack.EMPTY;

				slot.onSlotChange(slotStack, slotStackCopy);
			}
			else if (!mergeItemStack(slotStack, 0, CONTAINER_SIZE, false))
				return ItemStack.EMPTY;

			if (slotStack.getCount() == 0)
				slot.putStack(ItemStack.EMPTY);
			else
				slot.onSlotChanged();

			if (slotStack.getCount() == slotStackCopy.getCount())
				return ItemStack.EMPTY;

			slot.onTake(player, slotStack);
		}

		return slotStackCopy;
	}

	@Override
	public ItemStack slotClick(int slot, int dragType, ClickType clickType, EntityPlayer player) {
		if (slot >= 0 && getSlot(slot) != null && getSlot(slot).getStack().getItem() instanceof BriefcaseItem)
			return ItemStack.EMPTY;

		return super.slotClick(slot, dragType, clickType, player);
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return PlayerUtils.getItemStackFromAnyHand(player, SCContent.briefcase) == briefcaseInventory.getContainerStack();
	}
}
