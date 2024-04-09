package net.geforcemods.securitycraft.inventory;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blockentities.BlockPocketManagerBlockEntity;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class BlockPocketManagerMenu extends Container {
	private final BlockPocketManagerBlockEntity te;
	public final boolean hasStorageModule;
	public final boolean isOwner;

	public BlockPocketManagerMenu(InventoryPlayer inventory, BlockPocketManagerBlockEntity te) {
		this.te = te;
		isOwner = te != null && te.isOwnedBy(inventory.player);
		hasStorageModule = te != null && te.isModuleEnabled(ModuleType.STORAGE) && isOwner;

		if (hasStorageModule) {
			for (int y = 0; y < 3; y++) {
				for (int x = 0; x < 9; ++x) {
					addSlotToContainer(new Slot(inventory, x + y * 9 + 9, 8 + x * 18, 84 + y * 18 + 74));
				}
			}

			for (int x = 0; x < 9; x++) {
				addSlotToContainer(new Slot(inventory, x, 8 + x * 18, 142 + 74));
			}

			IItemHandler handler = te.getStorageHandler();
			int slotId = 0;

			for (int y = 0; y < 8; y++) {
				for (int x = 0; x < 7; x++) {
					addSlotToContainer(new SlotItemHandler(handler, slotId++, 124 + x * 18, 8 + y * 18));
				}
			}
		}
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int index) {
		ItemStack copy = ItemStack.EMPTY;
		Slot slot = inventorySlots.get(index);

		if (slot != null && slot.getHasStack()) {
			ItemStack slotStack = slot.getStack();

			copy = slotStack.copy();

			if (index >= 36) { //block pocket manager slots
				if (!mergeItemStack(slotStack, 0, 36, true))
					return ItemStack.EMPTY;
			}
			else if (!mergeItemStack(slotStack, 36, inventorySlots.size(), false)) //main inventory and hotbar
				return ItemStack.EMPTY;

			if (slotStack.isEmpty())
				slot.putStack(ItemStack.EMPTY);
			else
				slot.onSlotChanged();
		}

		return copy;
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return BlockUtils.isWithinUsableDistance(te.getWorld(), te.getPos(), player, SCContent.blockPocketManager);
	}
}
