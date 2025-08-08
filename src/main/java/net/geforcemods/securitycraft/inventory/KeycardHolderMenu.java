package net.geforcemods.securitycraft.inventory;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SCTags;
import net.geforcemods.securitycraft.items.KeycardHolderItem;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

public class KeycardHolderMenu extends Container {
	public static final int CONTAINER_SIZE = 5;
	private final ItemContainer keycardHolderInv;

	public KeycardHolderMenu(int id, PlayerInventory playerInventory, ItemContainer keycardHolderInv) {
		super(SCContent.KEYCARD_HOLDER_MENU.get(), id);
		this.keycardHolderInv = keycardHolderInv;

		for (int i = 0; i < CONTAINER_SIZE; i++) {
			addSlot(new Slot(keycardHolderInv, i, 44 + (i * 18), 20) {
				@Override
				public boolean mayPlace(ItemStack stack) {
					return stack.getItem().is(SCTags.Items.KEYCARD_HOLDER_CAN_HOLD);
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
	public ItemStack clicked(int slot, int dragType, ClickType clickType, PlayerEntity player) {
		if (slot >= 0 && getSlot(slot) != null && getSlot(slot).getItem().getItem() instanceof KeycardHolderItem)
			return ItemStack.EMPTY;

		return super.clicked(slot, dragType, clickType, player);
	}

	@Override
	public boolean stillValid(PlayerEntity player) {
		return PlayerUtils.getItemStackFromAnyHand(player, SCContent.KEYCARD_HOLDER.get()) == keycardHolderInv.getContainerStack();
	}
}
