package net.geforcemods.securitycraft.inventory;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ItemContainer implements Container {
	private final ItemStack containerStack;
	private final NonNullList<ItemStack> inventory;
	private final int maxStackSize;

	private ItemContainer(ItemStack containerStack, int inventorySize, int maxStackSize) {
		this.containerStack = containerStack;
		this.maxStackSize = maxStackSize;
		inventory = NonNullList.<ItemStack>withSize(inventorySize, ItemStack.EMPTY);
		//TODO:
		//load(Utils.getTag(containerStack).getUnsafe());
	}

	public static ItemContainer briefcase(ItemStack briefcase) {
		return new ItemContainer(briefcase, BriefcaseMenu.CONTAINER_SIZE, Item.ABSOLUTE_MAX_STACK_SIZE);
	}

	public static ItemContainer keycardHolder(ItemStack keycardHolder) {
		return new ItemContainer(keycardHolder, KeycardHolderMenu.CONTAINER_SIZE, 1);
	}

	@Override
	public int getContainerSize() {
		return inventory.size();
	}

	@Override
	public ItemStack getItem(int index) {
		return inventory.get(index);
	}

	public void load(CompoundTag tag, HolderLookup.Provider lookupProvider) {
		ListTag items = tag.getList("ItemInventory", Tag.TAG_COMPOUND);

		for (int i = 0; i < items.size(); i++) {
			CompoundTag item = items.getCompound(i);
			int slot = item.getInt("Slot");

			if (slot < getContainerSize())
				inventory.set(slot, ItemStack.parseOptional(lookupProvider, item));
		}
	}

	public void save(CompoundTag tag, HolderLookup.Provider lookupProvider) {
		ListTag items = new ListTag();

		for (int i = 0; i < getContainerSize(); i++) {
			if (!getItem(i).isEmpty()) {
				CompoundTag item = new CompoundTag();

				item.putInt("Slot", i);
				items.add(getItem(i).save(lookupProvider, items));
			}
		}

		tag.put("ItemInventory", items);
	}

	@Override
	public ItemStack removeItem(int index, int size) {
		ItemStack stack = getItem(index);

		if (!stack.isEmpty())
			if (stack.getCount() > size) {
				stack = stack.split(size);
				setChanged();
			}
			else
				setItem(index, ItemStack.EMPTY);

		return stack;
	}

	@Override
	public ItemStack removeItemNoUpdate(int index) {
		ItemStack stack = getItem(index);

		setItem(index, ItemStack.EMPTY);
		return stack;
	}

	@Override
	public void setItem(int index, ItemStack itemStack) {
		inventory.set(index, itemStack);

		if (!itemStack.isEmpty() && itemStack.getCount() > getMaxStackSize())
			itemStack.setCount(getMaxStackSize());

		setChanged();
	}

	@Override
	public int getMaxStackSize() {
		return maxStackSize;
	}

	@Override
	public void setChanged() {
		for (int i = 0; i < getContainerSize(); i++) {
			if (!getItem(i).isEmpty() && getItem(i).getCount() == 0)
				inventory.set(i, ItemStack.EMPTY);
		}

		//TODO:
		//save(containerStack.getTag());
	}

	@Override
	public boolean stillValid(Player player) {
		return true;
	}

	@Override
	public void startOpen(Player player) {}

	@Override
	public void stopOpen(Player player) {}

	@Override
	public boolean canPlaceItem(int index, ItemStack stack) {
		return true;
	}

	@Override
	public void clearContent() {
		for (int i = 0; i < inventory.size(); i++) {
			inventory.set(i, ItemStack.EMPTY);
		}
	}

	@Override
	public boolean isEmpty() {
		for (ItemStack stack : inventory) {
			if (!stack.isEmpty())
				return false;
		}

		return true;
	}
}
