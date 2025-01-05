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
	public final InventoryScannerBlockEntity be;
	private ContainerLevelAccess worldPosCallable;

	public InventoryScannerMenu(int windowId, Level level, BlockPos pos, Inventory inventory) {
		super(SCContent.INVENTORY_SCANNER_MENU.get(), windowId);
		be = (InventoryScannerBlockEntity) level.getBlockEntity(pos);
		worldPosCallable = ContainerLevelAccess.create(level, pos);
		BlockEntityInventoryWrapper<InventoryScannerBlockEntity> wrapper = new BlockEntityInventoryWrapper<>(be, this);

		//prohibited items 0-9
		for (int i = 0; i < 10; i++) {
			addSlot(new OwnerRestrictedSlot(wrapper, be, i, (6 + (i * 18)), 16, true));
		}

		//inventory scanner storage 10-36
		if (be.isOwnedBy(inventory.player) && be.isModuleEnabled(ModuleType.STORAGE)) {
			for (int i = 0; i < 9; i++) {
				for (int j = 0; j < 3; j++) {
					addSlot(new Slot(wrapper, 10 + ((i * 3) + j), 188 + (j * 18), 29 + i * 18));
				}
			}
		}

		//37
		addSlot(new LensSlot(be.getLensContainer(), 0, 159, 89) {
			@Override
			public boolean mayPickup(Player player) {
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
	public ItemStack quickMoveStack(Player player, int index) {
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
				slot.setByPlayer(ItemStack.EMPTY);
			else
				slot.setChanged();
		}

		return toReturn;
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
