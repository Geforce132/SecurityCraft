package net.geforcemods.securitycraft.inventory;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blockentities.InventoryScannerBlockEntity;
import net.geforcemods.securitycraft.blocks.InventoryScannerBlock;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class InventoryScannerMenu extends Container {
	private final int numRows;
	public final InventoryScannerBlockEntity te;

	public InventoryScannerMenu(InventoryPlayer inventory, InventoryScannerBlockEntity te) {
		numRows = te.getSizeInventory() / 9;
		this.te = te;

		//prohibited items
		for (int i = 0; i < 10; i++) {
			addSlotToContainer(new OwnerRestrictedSlot(te, te, i, (6 + (i * 18)), 16, true));
		}

		//inventory scanner storage
		if (te.isOwnedBy(inventory.player) && te.isModuleEnabled(ModuleType.STORAGE)) {
			for (int i = 0; i < 9; i++) {
				for (int j = 0; j < 3; j++) {
					addSlotToContainer(new Slot(te, 10 + ((i * 3) + j), 188 + (j * 18), 29 + i * 18));
				}
			}
		}

		//inventory
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				addSlotToContainer(new Slot(inventory, j + i * 9 + 9, 15 + j * 18, 115 + i * 18));
			}
		}

		//hotbar
		for (int i = 0; i < 9; i++) {
			addSlotToContainer(new Slot(inventory, i, 15 + i * 18, 173));
		}
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int index) {
		ItemStack slotStackCopy = ItemStack.EMPTY;
		Slot slot = inventorySlots.get(index);

		if (slot != null && slot.getHasStack()) {
			ItemStack slotStack = slot.getStack();
			slotStackCopy = slotStack.copy();

			if (index < numRows * 9) {
				if (!mergeItemStack(slotStack, numRows * 9, inventorySlots.size(), true))
					return ItemStack.EMPTY;
			}
			else if (!mergeItemStack(slotStack, 0, numRows * 9, false))
				return ItemStack.EMPTY;

			if (slotStack.getCount() == 0)
				slot.putStack(ItemStack.EMPTY);
			else
				slot.onSlotChanged();
		}

		return slotStackCopy;
	}

	@Override
	public void onContainerClosed(EntityPlayer player) {
		super.onContainerClosed(player);

		InventoryScannerBlockEntity connectedScanner = InventoryScannerBlock.getConnectedInventoryScanner(player.world, te.getPos());

		if (connectedScanner == null)
			return;

		connectedScanner.setContents(te.getContents());
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return BlockUtils.isWithinUsableDistance(te.getWorld(), te.getPos(), player, SCContent.inventoryScanner);
	}

	@Override
	public ItemStack slotClick(int slotId, int dragType, ClickType clickType, EntityPlayer player) {
		if (slotId >= 0 && slotId < 10 && getSlot(slotId) instanceof OwnerRestrictedSlot && ((OwnerRestrictedSlot) getSlot(slotId)).isGhostSlot()) {
			if (te.isOwnedBy(player)) {
				ItemStack pickedUpStack = player.inventory.getItemStack().copy();

				pickedUpStack.setCount(1);
				te.getContents().set(slotId, pickedUpStack);
			}

			return ItemStack.EMPTY;
		}
		else
			return super.slotClick(slotId, dragType, clickType, player);
	}
}
