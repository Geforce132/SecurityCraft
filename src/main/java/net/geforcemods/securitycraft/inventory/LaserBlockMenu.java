package net.geforcemods.securitycraft.inventory;

import java.util.Map;

import net.geforcemods.securitycraft.blockentities.LaserBlockBlockEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class LaserBlockMenu extends Container {
	public final LaserBlockBlockEntity be;
	public final Map<EnumFacing, Boolean> sideConfig;

	public LaserBlockMenu(LaserBlockBlockEntity te, InventoryPlayer inventory) {
		be = te;
		this.sideConfig = te.getSideConfig();

		if (be.isOwnedBy(inventory.player)) {
			InventoryBasic container = be.getLensContainer();

			for (int i = 0; i < 6; i++) {
				addSlotToContainer(new LensSlot(container, i, 15, i * 22 + 27));
			}
		}

		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 9; ++x) {
				addSlotToContainer(new Slot(inventory, x + y * 9 + 9, 8 + x * 18, 174 + y * 18));
			}
		}

		for (int x = 0; x < 9; x++) {
			addSlotToContainer(new Slot(inventory, x, 8 + x * 18, 232));
		}
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int index) {
		ItemStack slotStackCopy = ItemStack.EMPTY;
		Slot slot = inventorySlots.get(index);

		if (slot != null && slot.getHasStack()) {
			ItemStack slotStack = slot.getStack();
			slotStackCopy = slotStack.copy();

			if (index < 6) {
				if (!mergeItemStack(slotStack, 6, 42, true))
					return ItemStack.EMPTY;

				slot.onSlotChange(slotStack, slotStackCopy);
			}
			else if (!mergeItemStack(slotStack, 0, 6, false))
				return ItemStack.EMPTY;

			if (slotStack.getCount() == 0)
				slot.putStack(ItemStack.EMPTY);
			else
				slot.onSlotChanged();

			if (slotStack.getCount() == slotStackCopy.getCount())
				return ItemStack.EMPTY;

			slot.onTake(player, slotStack);
			detectAndSendChanges();
		}

		return slotStackCopy;
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		BlockPos pos = be.getPos();

		return player.getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D;
	}
}
