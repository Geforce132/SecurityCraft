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
	public final InventoryScannerBlockEntity te;

	public InventoryScannerMenu(InventoryPlayer inventory, InventoryScannerBlockEntity te) {
		this.te = te;

		//prohibited items 0-9
		for (int i = 0; i < 10; i++) {
			addSlotToContainer(new OwnerRestrictedSlot(te, te, i, (6 + (i * 18)), 16, true));
		}

		//inventory scanner storage 10-36
		if (te.isOwnedBy(inventory.player) && te.isModuleEnabled(ModuleType.STORAGE)) {
			for (int i = 0; i < 9; i++) {
				for (int j = 0; j < 3; j++) {
					addSlotToContainer(new Slot(te, 10 + ((i * 3) + j), 188 + (j * 18), 29 + i * 18));
				}
			}
		}

		//37
		addSlotToContainer(new LensSlot(te.getLensContainer(), 0, 159, 89) {
			@Override
			public boolean canTakeStack(EntityPlayer player) {
				return te.isOwnedBy(player);
			}
		});

		//inventory 38-64
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				addSlotToContainer(new Slot(inventory, j + i * 9 + 9, 15 + j * 18, 115 + i * 18));
			}
		}

		//hotbar 65-73
		for (int i = 0; i < 9; i++) {
			addSlotToContainer(new Slot(inventory, i, 15 + i * 18, 173));
		}
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int index) {
		ItemStack toReturn = ItemStack.EMPTY;
		Slot slot = inventorySlots.get(index);

		if (slot.getHasStack()) {
			ItemStack slotStack = slot.getStack();
			toReturn = slotStack.copy();

			//inventory scanner storage
			if (index >= 10 && index <= 36) {
				//try to move it to the player's inventory, or lens slot
				if (!mergeItemStack(slotStack, 38, inventorySlots.size(), true) && !mergeItemStack(slotStack, 37, 38, true))
					return ItemStack.EMPTY;
			}
			//lens slot
			else if (index == 37) {
				//try to move it to the player's inventory
				if (!mergeItemStack(slotStack, 38, inventorySlots.size(), true))
					return ItemStack.EMPTY;
			}
			//player's main inventory (minus hotbar)
			else if (index <= 64) {
				//try to move it to the hotbar
				if (!mergeItemStack(slotStack, 65, inventorySlots.size(), true))
					return ItemStack.EMPTY;
			}
			//hotbar; try to move it to the main inventory
			else if (index < inventorySlots.size() && !mergeItemStack(slotStack, 38, 65, true))
				return ItemStack.EMPTY;

			if (slotStack.isEmpty())
				slot.putStack(ItemStack.EMPTY);
			else
				slot.onSlotChanged();
		}

		return toReturn;
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
