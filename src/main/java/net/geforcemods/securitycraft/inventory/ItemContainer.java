package net.geforcemods.securitycraft.inventory;

import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.Container;
import net.minecraft.world.entity.ContainerUser;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;

public class ItemContainer implements Container {
	private final ItemStack containerStack;
	private final NonNullList<ItemStack> inventory;
	private final int maxStackSize;

	private ItemContainer(ItemStack containerStack, int inventorySize, int maxStackSize) {
		this.containerStack = containerStack;
		this.maxStackSize = maxStackSize;
		inventory = NonNullList.<ItemStack>withSize(inventorySize, ItemStack.EMPTY);
		load();
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

	public void load() {
		containerStack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).copyInto(inventory);
	}

	public void save() {
		containerStack.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(inventory));
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

		save();
	}

	@Override
	public boolean stillValid(Player player) {
		return true;
	}

	@Override
	public void startOpen(ContainerUser player) {}

	@Override
	public void stopOpen(ContainerUser player) {
		save();
	}

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
