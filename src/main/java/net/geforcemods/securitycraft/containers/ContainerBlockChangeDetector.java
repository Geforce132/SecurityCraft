package net.geforcemods.securitycraft.containers;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.inventory.TileEntityInventoryWrapper;
import net.geforcemods.securitycraft.misc.EnumModuleType;
import net.geforcemods.securitycraft.tileentity.TileEntityBlockChangeDetector;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ContainerBlockChangeDetector extends ContainerGeneric {
	public final TileEntityBlockChangeDetector te;

	public ContainerBlockChangeDetector(InventoryPlayer inventory, TileEntityBlockChangeDetector te) {
		this.te = te;

		if (te.isOwnedBy(inventory.player)) {
			addSlotToContainer(new Slot(new TileEntityInventoryWrapper<>(te, this), 36, 175, 44) {
				@Override
				public boolean isItemValid(ItemStack stack) {
					return te.isModuleEnabled(EnumModuleType.SMART) && stack.getItem() instanceof ItemBlock;
				}

				@Override
				public int getSlotStackLimit() {
					return 1;
				}
			});
		}

		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 9; ++x) {
				addSlotToContainer(new Slot(inventory, x + y * 9 + 9, 8 + x * 18, 84 + y * 18 + 90));
			}
		}

		for (int x = 0; x < 9; x++) {
			addSlotToContainer(new Slot(inventory, x, 8 + x * 18, 142 + 90));
		}
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int index) {
		ItemStack slotStackCopy = ItemStack.EMPTY;
		Slot slot = inventorySlots.get(index);

		if (slot != null && slot.getHasStack()) {
			ItemStack slotStack = slot.getStack();
			slotStackCopy = slotStack.copy();

			if (index < 1) {
				if (!mergeItemStack(slotStack, 1, 37, true))
					return ItemStack.EMPTY;
			}
			else if (index >= 1) {
				if (!mergeItemStack(slotStack, 0, 1, false))
					return ItemStack.EMPTY;
			}

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
	public boolean canInteractWith(EntityPlayer player) {
		return BlockUtils.isWithinUsableDistance(te.getWorld(), te.getPos(), player, SCContent.blockChangeDetector);
	}
}
