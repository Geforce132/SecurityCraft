package net.geforcemods.securitycraft.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import net.geforcemods.securitycraft.blocks.InventoryScannerFieldBlock;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.Container;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.ItemStack;

public class InventoryUtils {
	public static ItemStack addItemToStorage(Container container, int start, int endInclusive, ItemStack stack) {
		ItemStack remainder = stack;

		for (int i = start; i <= endInclusive; i++) {
			remainder = InventoryUtils.insertItem(container, i, remainder);

			if (remainder.isEmpty())
				break;
		}

		return remainder;
	}

	public static ItemStack insertItem(Container container, int slot, ItemStack stackToInsert) {
		if (stackToInsert.isEmpty())
			return stackToInsert;

		ItemStack slotStack = container.getItem(slot);
		int limit = stackToInsert.getItem().getMaxStackSize(stackToInsert);

		if (slotStack.isEmpty()) {
			container.setItem(slot, stackToInsert);
			container.setChanged();
			return ItemStack.EMPTY;
		}
		else if (InventoryScannerFieldBlock.areItemStacksEqual(slotStack, stackToInsert) && slotStack.getCount() < limit) {
			if (limit - slotStack.getCount() >= stackToInsert.getCount()) {
				slotStack.setCount(slotStack.getCount() + stackToInsert.getCount());
				container.setChanged();
				return ItemStack.EMPTY;
			}
			else {
				ItemStack toInsert = stackToInsert.copy();
				ItemStack toReturn = toInsert.split((slotStack.getCount() + stackToInsert.getCount()) - limit); //this is the remaining stack that could not be inserted

				slotStack.setCount(slotStack.getCount() + toInsert.getCount());
				container.setChanged();
				return toReturn;
			}
		}

		return stackToInsert;
	}

	public static int checkInventoryForItem(List<ItemStack> inventory, ItemStack stackToMatch, int itemsLeftToFind, boolean exactStackCheck, boolean shouldRemoveItems, Consumer<ItemStack> handleRemovedItem, BiConsumer<Integer, ItemStack> handleRemainingItemInSlot) {
		return checkInventoryForItem(inventory, 0, inventory.size() - 1, stackToMatch, itemsLeftToFind, exactStackCheck, shouldRemoveItems, handleRemovedItem, handleRemainingItemInSlot);
	}

	public static int checkInventoryForItem(List<ItemStack> inventory, int startSlot, int endSlotInclusive, ItemStack stackToMatch, int itemsLeftToFind, boolean exactStackCheck, boolean shouldRemoveItems, Consumer<ItemStack> handleRemovedItem, BiConsumer<Integer, ItemStack> handleRemainingItemInSlot) {
		if (itemsLeftToFind > 0) {
			for (int i = endSlotInclusive; i >= startSlot; i--) { //Iteration in backwards order, so slot numbers still match when an entry is removed from the inventory
				itemsLeftToFind = checkItemsInInventorySlot(inventory.get(i), i, stackToMatch, itemsLeftToFind, exactStackCheck, shouldRemoveItems, handleRemovedItem, handleRemainingItemInSlot);

				if (itemsLeftToFind == 0)
					break;
			}
		}

		return itemsLeftToFind;
	}

	public static int checkItemsInInventorySlot(ItemStack stackInSlot, int positionInInv, ItemStack stackToMatch, int itemsLeftToFind, boolean exactStackCheck, boolean shouldRemoveItems, Consumer<ItemStack> handleRemovedItem, BiConsumer<Integer, ItemStack> handleRemainingItemInSlot) {
		if (itemsLeftToFind > 0) {
			if (BlockUtils.areItemsEqual(stackInSlot, stackToMatch, exactStackCheck)) {
				if (shouldRemoveItems) {
					ItemStack splitStack = stackInSlot.split(itemsLeftToFind); //After this operation, stackInSlot may be an empty stack

					itemsLeftToFind -= splitStack.getCount();
					handleRemovedItem.accept(splitStack);
					handleRemainingItemInSlot.accept(positionInInv, stackInSlot);
				}
				else
					itemsLeftToFind = Math.max(itemsLeftToFind - stackInSlot.getCount(), 0);

				if (itemsLeftToFind == 0)
					return 0;
			}

			itemsLeftToFind = checkItemsInItemContainer(stackInSlot, stackToMatch, itemsLeftToFind, exactStackCheck, shouldRemoveItems, handleRemovedItem);
			itemsLeftToFind = checkItemsInBundle(stackInSlot, stackToMatch, itemsLeftToFind, exactStackCheck, shouldRemoveItems, handleRemovedItem);
		}

		return itemsLeftToFind;
	}

	public static int checkItemsInItemContainer(ItemStack itemContainer, ItemStack stackToMatch, int itemsLeftToFind, boolean exactStackCheck, boolean shouldRemoveItems, Consumer<ItemStack> handleRemovedItem) {
		if (itemsLeftToFind > 0 && itemContainer != null && itemContainer.hasTag()) {
			CompoundTag blockEntityTag = itemContainer.getTag().getCompound("BlockEntityTag");
			ListTag contents = blockEntityTag.getList("Items", Tag.TAG_COMPOUND);
			NonNullList<ItemStack> containerItems = NonNullList.withSize(contents.size(), ItemStack.EMPTY);

			contents.stream().map(tag -> ItemStack.of((CompoundTag) tag)).forEach(containerItems::add);
			itemsLeftToFind = checkInventoryForItem(containerItems, stackToMatch, itemsLeftToFind, exactStackCheck, shouldRemoveItems, handleRemovedItem, containerItems::set);

			if (shouldRemoveItems) {
				ListTag newContents = new ListTag();

				containerItems.stream().map(ItemStack::serializeNBT).forEach(newContents::add);
				blockEntityTag.put("Items", newContents);
			}
		}

		return itemsLeftToFind;
	}

	public static int checkItemsInBundle(ItemStack bundle, ItemStack stackToMatch, int itemsLeftToFind, boolean exactStackCheck, boolean shouldRemoveItems, Consumer<ItemStack> handleRemovedItem) {
		if (itemsLeftToFind > 0 && bundle != null && bundle.getItem() instanceof BundleItem) {
			List<ItemStack> bundleItems = BundleItem.getContents(bundle).collect(Collectors.toList());

			itemsLeftToFind = checkInventoryForItem(new ArrayList<>(bundleItems), stackToMatch, itemsLeftToFind, exactStackCheck, shouldRemoveItems, handleRemovedItem, (i, stack) -> {
				if (stack.isEmpty())
					bundleItems.remove((int) i);
				else
					bundleItems.set(i, stack);
			});

			if (shouldRemoveItems)
				bundle.getTag().put("Items", bundleItems.stream().map(stack -> stack.save(new CompoundTag())).collect(ListTag::new, ListTag::add, ListTag::addAll));
		}

		return itemsLeftToFind;
	}

	public static int countItemsInSlotRange(Container container, ItemStack stackToMatch, int start, int endInclusive, boolean hasSmartModule) {
		int itemsLeftToFind = Integer.MAX_VALUE;

		for (int i = start; i <= endInclusive; i++) {
			itemsLeftToFind = checkItemsInInventorySlot(container.getItem(i), i, stackToMatch, itemsLeftToFind, hasSmartModule, false, stack -> {}, (slot, stack) -> {});
		}

		return Integer.MAX_VALUE - itemsLeftToFind;
	}
}
