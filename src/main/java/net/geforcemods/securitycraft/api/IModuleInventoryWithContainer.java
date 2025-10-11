package net.geforcemods.securitycraft.api;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ItemResource;

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
	default int size() {
		return getContainerSize();
	}

	@Override
	default ItemResource getResource(int slot) {
		return isContainer(slot) ? ItemResource.of(getStackInContainer(slot)) : IModuleInventory.super.getResource(slot);
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

			if (!isValid(slot, ItemResource.of(stack)))
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
	default long getCapacityAsLong(int slot, ItemResource resource) {
		return isContainer(slot) ? getContainerStackSize() : IModuleInventory.super.getCapacityAsLong(slot, resource);
	}

	@Override
	default boolean isValid(int slot, ItemResource resource) {
		return isContainer(slot) ? isItemValidForContainer(slot, resource.toStack()) : IModuleInventory.super.isValid(slot, resource);
	}
}
