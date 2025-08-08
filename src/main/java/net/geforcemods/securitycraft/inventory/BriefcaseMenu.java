package net.geforcemods.securitycraft.inventory;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.items.BriefcaseItem;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class BriefcaseMenu extends AbstractContainerMenu {
	public static final int CONTAINER_SIZE = 12;
	private final ItemContainer briefcaseInventory;

	public BriefcaseMenu(int windowId, Inventory playerInventory, ItemContainer briefcaseInventory) {
		super(SCContent.BRIEFCASE_INVENTORY_MENU.get(), windowId);
		this.briefcaseInventory = briefcaseInventory;

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 4; j++) {
				addSlot(new Slot(briefcaseInventory, j + (i * 4), 53 + (j * 18), 17 + (i * 18)) {
					@Override
					public boolean mayPlace(ItemStack stack) {
						return stack.getItem() != SCContent.BRIEFCASE.get();
					}
				});
			}
		}

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}

		for (int i = 0; i < 9; i++) {
			addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
		}
	}

	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		ItemStack slotStackCopy = ItemStack.EMPTY;
		Slot slot = slots.get(index);

		if (slot.hasItem()) {
			ItemStack slotStack = slot.getItem();
			slotStackCopy = slotStack.copy();

			if (index < CONTAINER_SIZE) {
				if (!moveItemStackTo(slotStack, CONTAINER_SIZE, 48, true))
					return ItemStack.EMPTY;

				slot.onQuickCraft(slotStack, slotStackCopy);
			}
			else if (!moveItemStackTo(slotStack, 0, CONTAINER_SIZE, false))
				return ItemStack.EMPTY;

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
	public void clicked(int slot, int dragType, ClickType clickType, Player player) {
		if (!(slot >= 0 && getSlot(slot) != null && getSlot(slot).getItem().getItem() instanceof BriefcaseItem))
			super.clicked(slot, dragType, clickType, player);
	}

	@Override
	public boolean stillValid(Player player) {
		return PlayerUtils.getItemStackFromAnyHand(player, SCContent.BRIEFCASE.get()) == briefcaseInventory.getContainerStack();
	}
}
