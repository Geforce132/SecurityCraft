package net.geforcemods.securitycraft.inventory;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SCTags;
import net.geforcemods.securitycraft.items.KeycardHolderItem;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class KeycardHolderMenu extends AbstractContainerMenu {
	public static final int CONTAINER_SIZE = 5;
	private final ItemContainer keycardHolderInv;

	public KeycardHolderMenu(int id, Inventory playerInventory, ItemContainer keycardHolderInv) {
		super(SCContent.KEYCARD_HOLDER_MENU.get(), id);

		this.keycardHolderInv = keycardHolderInv;

		for (int i = 0; i < CONTAINER_SIZE; i++) {
			addSlot(new Slot(keycardHolderInv, i, 44 + (i * 18), 20) {
				@Override
				public boolean mayPlace(ItemStack stack) {
					return stack.is(SCTags.Items.KEYCARD_HOLDER_CAN_HOLD);
				}
			});
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
	public ItemStack quickMoveStack(Player player, int index) {
		ItemStack slotStackCopy = ItemStack.EMPTY;
		Slot slot = slots.get(index);

		if (slot.hasItem()) {
			ItemStack slotStack = slot.getItem();
			slotStackCopy = slotStack.copy();

			if (index < CONTAINER_SIZE) {
				if (!moveItemStackTo(slotStack, CONTAINER_SIZE, 36 + CONTAINER_SIZE, true))
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
		if (!(slot >= 0 && getSlot(slot) != null && getSlot(slot).getItem().getItem() instanceof KeycardHolderItem))
			super.clicked(slot, dragType, clickType, player);
	}

	@Override
	public boolean stillValid(Player player) {
		return PlayerUtils.getItemStackFromAnyHand(player, SCContent.KEYCARD_HOLDER.get()) == keycardHolderInv.getContainerStack();
	}

	@Override
	public void removed(Player player) {
		super.removed(player);
		keycardHolderInv.stopOpen(player);
	}
}
