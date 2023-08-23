package net.geforcemods.securitycraft.inventory;

import net.geforcemods.securitycraft.items.ModuleItem;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ModuleItemContainer implements Container {
	private final ItemStack module;
	private NonNullList<ItemStack> moduleInventory;
	private DisguiseModuleMenu menu;

	public ModuleItemContainer(ItemStack moduleStack) {
		module = moduleStack;

		if (!(moduleStack.getItem() instanceof ModuleItem))
			return;

		moduleInventory = NonNullList.withSize(1, ItemStack.EMPTY);

		if (!module.hasTag())
			module.setTag(new CompoundTag());

		readFromNBT(module.getTag());
	}

	@Override
	public int getContainerSize() {
		return 1;
	}

	@Override
	public ItemStack getItem(int index) {
		return moduleInventory.get(index);
	}

	public void readFromNBT(CompoundTag tag) {
		ListTag items = tag.getList("ItemInventory", Tag.TAG_COMPOUND);

		for (int i = 0; i < items.size(); i++) {
			CompoundTag item = items.getCompound(i);
			int slot = item.getInt("Slot");

			if (slot < getContainerSize())
				moduleInventory.set(slot, ItemStack.of(item));
		}
	}

	public void writeToNBT(CompoundTag tag) {
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
			if (stack.getCount() > size)
				stack = stack.split(size);
			else
				setItem(index, ItemStack.EMPTY);

			setChanged();
		}

		return stack;
	}

	@Override
	public ItemStack removeItemNoUpdate(int index) {
		ItemStack stack = getItem(index);

		setItem(index, ItemStack.EMPTY);
		setChanged();
		return stack;
	}

	@Override
	public void setItem(int index, ItemStack stack) {
		moduleInventory.set(index, stack);

		if (!stack.isEmpty() && stack.getCount() > getMaxStackSize())
			stack.setCount(getMaxStackSize());

		setChanged();
	}

	@Override
	public int getMaxStackSize() {
		return 64;
	}

	@Override
	public void setChanged() {
		for (int i = 0; i < getContainerSize(); i++) {
			if (!getItem(i).isEmpty() && getItem(i).getCount() == 0)
				moduleInventory.set(i, ItemStack.EMPTY);
		}

		writeToNBT(module.getTag());

		if (menu != null)
			menu.slotsChanged(this);
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
	public void clearContent() {}

	@Override
	public boolean isEmpty() {
		for (ItemStack stack : moduleInventory) {
			if (!stack.isEmpty())
				return false;
		}

		return true;
	}

	public void setMenu(DisguiseModuleMenu menu) {
		this.menu = menu;
	}

	public ItemStack getModule() {
		return module;
	}
}
