package net.geforcemods.securitycraft.inventory;

import net.geforcemods.securitycraft.api.ILinkedAction;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.LinkableBlockEntity;
import net.geforcemods.securitycraft.items.ModuleItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class CustomizeBlockMenu extends Container {
	private IModuleInventory moduleInv;
	public final int maxSlots;

	public CustomizeBlockMenu(InventoryPlayer inventory, IModuleInventory tileEntity) {
		this.moduleInv = tileEntity;

		int slotId = 0;

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; ++j) {
				addSlotToContainer(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}

		for (int i = 0; i < 9; i++) {
			addSlotToContainer(new Slot(inventory, i, 8 + i * 18, 142));
		}

		if (moduleInv.enableHack())
			slotId = 100;

		if (moduleInv.getMaxNumberOfModules() == 1)
			addSlotToContainer(new CustomSlotItemHandler(moduleInv, slotId, 80, 20));
		else if (moduleInv.getMaxNumberOfModules() == 2) {
			addSlotToContainer(new CustomSlotItemHandler(moduleInv, slotId++, 70, 20));
			addSlotToContainer(new CustomSlotItemHandler(moduleInv, slotId++, 88, 20));
		}
		else if (moduleInv.getMaxNumberOfModules() == 3) {
			addSlotToContainer(new CustomSlotItemHandler(moduleInv, slotId++, 62, 20));
			addSlotToContainer(new CustomSlotItemHandler(moduleInv, slotId++, 80, 20));
			addSlotToContainer(new CustomSlotItemHandler(moduleInv, slotId++, 98, 20));
		}
		else if (moduleInv.getMaxNumberOfModules() == 4) {
			addSlotToContainer(new CustomSlotItemHandler(moduleInv, slotId++, 52, 20));
			addSlotToContainer(new CustomSlotItemHandler(moduleInv, slotId++, 70, 20));
			addSlotToContainer(new CustomSlotItemHandler(moduleInv, slotId++, 88, 20));
			addSlotToContainer(new CustomSlotItemHandler(moduleInv, slotId++, 106, 20));
		}
		else if (moduleInv.getMaxNumberOfModules() == 5) {
			addSlotToContainer(new CustomSlotItemHandler(moduleInv, slotId++, 34, 20));
			addSlotToContainer(new CustomSlotItemHandler(moduleInv, slotId++, 52, 20));
			addSlotToContainer(new CustomSlotItemHandler(moduleInv, slotId++, 70, 20));
			addSlotToContainer(new CustomSlotItemHandler(moduleInv, slotId++, 88, 20));
			addSlotToContainer(new CustomSlotItemHandler(moduleInv, slotId++, 106, 20));
		}

		maxSlots = 36 + moduleInv.getMaxNumberOfModules();
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int index) {
		ItemStack copy = ItemStack.EMPTY;
		Slot slot = inventorySlots.get(index);

		if (slot != null && slot.getHasStack()) {
			ItemStack slotStack = slot.getStack();
			boolean isModule = slotStack.getItem() instanceof ModuleItem;

			copy = slotStack.copy();

			if (index >= 36 && index <= maxSlots) { //module slots
				if (!mergeItemStack(slotStack, 0, 36, true)) //main inventory + hotbar
					return ItemStack.EMPTY;
			}
			else if (index >= 27 && index <= 35) { //hotbar
				if (isModule && !mergeItemStack(slotStack, 36, maxSlots, false)) //module slots
					return ItemStack.EMPTY;
				else if (!mergeItemStack(slotStack, 0, 27, false)) //main inventory
					return ItemStack.EMPTY;
			}
			else if (index <= 26) { //main inventory
				if (isModule && !mergeItemStack(slotStack, 36, maxSlots, false)) //module slots
					return ItemStack.EMPTY;
				else if (!mergeItemStack(slotStack, 27, 36, false)) //hotbar
					return ItemStack.EMPTY;
			}

			slot.onSlotChange(slotStack, copy);

			if (slotStack.isEmpty())
				slot.putStack(ItemStack.EMPTY);
			else
				slot.onSlotChanged();
		}

		return copy;
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		BlockPos pos = moduleInv.myPos();

		if (moduleInv instanceof TileEntity) {
			TileEntity te = (TileEntity) moduleInv;

			if (te.getWorld().getBlockState(te.getPos()).getBlock() != te.getBlockType())
				return false;
		}

		return player.getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D;
	}

	private class CustomSlotItemHandler extends SlotItemHandler {
		public CustomSlotItemHandler(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
			super(itemHandler, index, xPosition, yPosition);
		}

		@Override
		public void onSlotChange(ItemStack newStack, ItemStack oldStack) {
			if ((slotNumber >= 36 || slotNumber < maxSlots) && oldStack.getItem() instanceof ModuleItem) {
				moduleInv.onModuleRemoved(oldStack, ((ModuleItem) oldStack.getItem()).getModuleType(), false);

				if (moduleInv instanceof LinkableBlockEntity) {
					LinkableBlockEntity linkable = (LinkableBlockEntity) moduleInv;

					linkable.propagate(new ILinkedAction.ModuleRemoved(((ModuleItem) oldStack.getItem()).getModuleType(), false), linkable);
				}

				detectAndSendChanges();
			}
		}

		@Override
		public void putStack(ItemStack stack) {
			super.putStack(stack);
			detectAndSendChanges();
		}

		@Override
		public ItemStack decrStackSize(int amount) {
			ItemStack stack = super.decrStackSize(amount);

			if (!stack.isEmpty())
				detectAndSendChanges();

			return stack;
		}
	}
}
