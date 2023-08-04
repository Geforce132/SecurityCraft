package net.geforcemods.securitycraft.inventory;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blockentities.InventoryScannerBlockEntity;
import net.geforcemods.securitycraft.blocks.InventoryScannerBlock;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class InventoryScannerMenu extends AbstractContainerMenu {
	private final int numRows;
	public final InventoryScannerBlockEntity be;
	private ContainerLevelAccess worldPosCallable;

	public InventoryScannerMenu(int windowId, Level level, BlockPos pos, Inventory inventory) {
		super(SCContent.INVENTORY_SCANNER_MENU.get(), windowId);
		be = (InventoryScannerBlockEntity) level.getBlockEntity(pos);
		numRows = be.getContainerSize() / 9;
		worldPosCallable = ContainerLevelAccess.create(level, pos);

		//prohibited items
		for (int i = 0; i < 10; i++) {
			addSlot(new OwnerRestrictedSlot(be, be, i, (6 + (i * 18)), 16, true));
		}

		//inventory scanner storage
		if (be.isOwnedBy(inventory.player) && be.isModuleEnabled(ModuleType.STORAGE)) {
			for (int i = 0; i < 9; i++) {
				for (int j = 0; j < 3; j++) {
					addSlot(new Slot(be, 10 + ((i * 3) + j), 188 + (j * 18), 29 + i * 18));
				}
			}
		}

		addSlot(new LensSlot(be.getLensContainer(), 0, 159, 89) {
			@Override
			public boolean mayPickup(Player player) {
				return be.isOwnedBy(player);
			}
		});

		//inventory
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				addSlot(new Slot(inventory, j + i * 9 + 9, 15 + j * 18, 115 + i * 18));
			}
		}

		//hotbar
		for (int i = 0; i < 9; i++) {
			addSlot(new Slot(inventory, i, 15 + i * 18, 173));
		}
	}

	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		ItemStack slotStackCopy = ItemStack.EMPTY;
		Slot slot = slots.get(index);

		if (slot.hasItem()) {
			ItemStack slotStack = slot.getItem();
			slotStackCopy = slotStack.copy();

			if (index < numRows * 9) {
				if (!moveItemStackTo(slotStack, numRows * 9, slots.size(), true))
					return ItemStack.EMPTY;
			}
			else if (!moveItemStackTo(slotStack, 0, numRows * 9, false))
				return ItemStack.EMPTY;

			if (slotStack.getCount() == 0)
				slot.set(ItemStack.EMPTY);
			else
				slot.setChanged();
		}

		return slotStackCopy;
	}

	@Override
	public void removed(Player player) {
		super.removed(player);

		InventoryScannerBlockEntity connectedScanner = InventoryScannerBlock.getConnectedInventoryScanner(be.getLevel(), be.getBlockPos());

		if (connectedScanner != null)
			connectedScanner.setContents(be.getContents());
	}

	@Override
	public boolean stillValid(Player player) {
		return stillValid(worldPosCallable, player, SCContent.INVENTORY_SCANNER.get());
	}

	@Override
	public void clicked(int slotId, int dragType, ClickType clickType, Player player) {
		if (slotId >= 0 && slotId < 10 && getSlot(slotId) instanceof OwnerRestrictedSlot slot && slot.isGhostSlot()) {
			if (be.isOwnedBy(player)) {
				ItemStack pickedUpStack = getCarried().copy();

				pickedUpStack.setCount(1);
				be.getContents().set(slotId, pickedUpStack);
				be.setChanged();
			}
		}
		else
			super.clicked(slotId, dragType, clickType, player);
	}
}
