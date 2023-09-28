package net.geforcemods.securitycraft.inventory;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blockentities.InventoryScannerBlockEntity;
import net.geforcemods.securitycraft.blocks.InventoryScannerBlock;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class InventoryScannerMenu extends Container {
	public final InventoryScannerBlockEntity be;
	private IWorldPosCallable worldPosCallable;

	public InventoryScannerMenu(int windowId, World level, BlockPos pos, PlayerInventory inventory) {
		super(SCContent.INVENTORY_SCANNER_MENU.get(), windowId);
		be = (InventoryScannerBlockEntity) level.getBlockEntity(pos);
		worldPosCallable = IWorldPosCallable.create(level, pos);

		//prohibited items 0-9
		for (int i = 0; i < 10; i++) {
			addSlot(new OwnerRestrictedSlot(be, be, i, (6 + (i * 18)), 16, true));
		}

		//inventory scanner storage 10-36
		if (be.isOwnedBy(inventory.player) && be.isModuleEnabled(ModuleType.STORAGE)) {
			for (int i = 0; i < 9; i++) {
				for (int j = 0; j < 3; j++) {
					addSlot(new Slot(be, 10 + ((i * 3) + j), 188 + (j * 18), 29 + i * 18));
				}
			}
		}

		//37
		addSlot(new LensSlot(be.getLensContainer(), 0, 159, 89) {
			@Override
			public boolean mayPickup(PlayerEntity player) {
				return be.isOwnedBy(player);
			}
		});

		//inventory 38-64
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				addSlot(new Slot(inventory, j + i * 9 + 9, 15 + j * 18, 115 + i * 18));
			}
		}

		//hotbar 65-73
		for (int i = 0; i < 9; i++) {
			addSlot(new Slot(inventory, i, 15 + i * 18, 173));
		}
	}

	@Override
	public ItemStack quickMoveStack(PlayerEntity player, int index) {
		ItemStack toReturn = ItemStack.EMPTY;
		Slot slot = slots.get(index);

		if (slot.hasItem()) {
			ItemStack slotStack = slot.getItem();
			toReturn = slotStack.copy();

			//inventory scanner storage
			if (index >= 10 && index <= 36) {
				//try to move it to the player's inventory, or lens slot
				if (!moveItemStackTo(slotStack, 38, slots.size(), true) && !moveItemStackTo(slotStack, 37, 38, true))
					return ItemStack.EMPTY;
			}
			//lens slot
			else if (index == 37) {
				//try to move it to the player's inventory
				if (!moveItemStackTo(slotStack, 38, slots.size(), true))
					return ItemStack.EMPTY;
			}
			//player's main inventory (minus hotbar)
			else if (index <= 64) {
				//try to move it to the hotbar
				if (!moveItemStackTo(slotStack, 65, slots.size(), true))
					return ItemStack.EMPTY;
			}
			//hotbar; try to move it to the main inventory
			else if (index < slots.size() && !moveItemStackTo(slotStack, 38, 65, true))
				return ItemStack.EMPTY;

			if (slotStack.isEmpty())
				slot.set(ItemStack.EMPTY);
			else
				slot.setChanged();
		}

		return toReturn;
	}

	@Override
	public void removed(PlayerEntity player) {
		super.removed(player);

		InventoryScannerBlockEntity connectedScanner = InventoryScannerBlock.getConnectedInventoryScanner(player.level, be.getBlockPos());

		if (connectedScanner == null)
			return;

		connectedScanner.setContents(be.getContents());
	}

	@Override
	public boolean stillValid(PlayerEntity player) {
		return stillValid(worldPosCallable, player, SCContent.INVENTORY_SCANNER.get());
	}

	@Override
	public ItemStack clicked(int slotId, int dragType, ClickType clickType, PlayerEntity player) {
		if (slotId >= 0 && slotId < 10 && getSlot(slotId) instanceof OwnerRestrictedSlot && ((OwnerRestrictedSlot) getSlot(slotId)).isGhostSlot()) {
			if (be.isOwnedBy(player)) {
				ItemStack pickedUpStack = player.inventory.getCarried().copy();

				pickedUpStack.setCount(1);
				be.getContents().set(slotId, pickedUpStack);
			}

			return ItemStack.EMPTY;
		}
		else
			return super.clicked(slotId, dragType, clickType, player);
	}
}
