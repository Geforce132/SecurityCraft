package net.geforcemods.securitycraft.api;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

/**
 * @see Container
 */
public interface IModuleInventoryWithContainer extends IModuleInventory {
	int getContainerSize();

	boolean isItemValidForContainer(int slot, ItemStack stack);

	ItemStack getStackInContainer(int slot);

	ItemStack removeContainerItem(int index, int count, boolean simulate);

	void setContainerItem(int index, ItemStack stack);

	boolean isContainerEmpty();

	default int getContainerStackSize() {
		return 99;
	}

	default boolean isContainer(int slot) {
		return slot < 100;
	}

	@Override
	default int getSlots() {
		//TODO: side effects?
		return IModuleInventory.super.getSlots() + getContainerSize();
	}

	@Override
	default ItemStack getStackInSlot(int slot) {
		return isContainer(slot) ? getStackInContainer(slot) : IModuleInventory.super.getStackInSlot(slot);
	}

	@Override
	default ItemStack extractItem(int slot, int amount, boolean simulate) {
		return isContainer(slot) ? removeContainerItem(slot, amount, simulate) : IModuleInventory.super.extractItem(slot, amount, simulate);
	}

	@Override
	default ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		if (isContainer(slot)) {
			if (stack.isEmpty())
				return ItemStack.EMPTY;

			if (!isItemValid(slot, stack))
				return stack;

			ItemStack existing = getStackInContainer(slot);

			if (!existing.isEmpty() && !ItemStack.isSameItemSameComponents(stack, existing))
				return stack;

			if (!simulate) {
				if (existing.isEmpty())
					setContainerItem(slot, stack);
				else
					existing.grow(stack.getCount());
			}

			return ItemStack.EMPTY;
		}

		return IModuleInventory.super.insertItem(slot, stack, simulate);
	}

	@Override
	default void setStackInSlot(int slot, ItemStack stack) {
		if (isContainer(slot))
			setContainerItem(slot, stack);
		else
			IModuleInventory.super.setStackInSlot(slot, stack);
	}

	@Override
	default int getSlotLimit(int slot) {
		return isContainer(slot) ? getContainerStackSize() : IModuleInventory.super.getSlotLimit(slot);
	}

	@Override
	default boolean isItemValid(int slot, ItemStack stack) {
		return isContainer(slot) ? isItemValidForContainer(slot, stack) : IModuleInventory.super.isItemValid(slot, stack);
	}
}
