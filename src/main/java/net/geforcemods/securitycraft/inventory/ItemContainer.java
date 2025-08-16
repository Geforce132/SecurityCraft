package net.geforcemods.securitycraft.inventory;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ItemContainer implements Container {
	private final ItemStack containerStack;
	private final NonNullList<ItemStack> inventory;
	private final int maxStackSize;

	private ItemContainer(ItemStack containerStack, int inventorySize, int maxStackSize) {
		this.containerStack = containerStack;
		this.maxStackSize = maxStackSize;
		inventory = NonNullList.<ItemStack>withSize(inventorySize, ItemStack.EMPTY);
		load(containerStack.getOrCreateTag());
	}

	public static ItemContainer briefcase(ItemStack briefcase) {
		return new ItemContainer(briefcase, BriefcaseMenu.CONTAINER_SIZE, 64);
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

	public void load(CompoundTag tag) {
		ListTag items = tag.getList("ItemInventory", Tag.TAG_COMPOUND);

		for (int i = 0; i < items.size(); i++) {
			CompoundTag item = items.getCompound(i);
			int slot = item.getInt("Slot");

			if (slot < getContainerSize())
				inventory.set(slot, ItemStack.of(item));
		}
	}

	public void save(CompoundTag tag) {
		ListTag items = new ListTag();

		for (int i = 0; i < getContainerSize(); i++) {
			if (!getItem(i).isEmpty()) {
				CompoundTag item = new CompoundTag();

				item.putInt("Slot", i);
				getItem(i).save(item);
				items.add(item);
			}
		}

		tag.put("ItemInventory", items);
	}

	@Override
	public ItemStack removeItem(int index, int size) {
		ItemStack stack = getItem(index);

		if (!stack.isEmpty()) {
			if (stack.getCount() > size) {
				stack = stack.split(size);
				setChanged();
			}
			else
				setItem(index, ItemStack.EMPTY);
		}

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

		save(containerStack.getOrCreateTag());
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

	public ItemStack getContainerStack() {
		return containerStack;
	}
}
