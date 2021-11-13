package net.geforcemods.securitycraft.inventory;

import net.geforcemods.securitycraft.items.ModuleItem;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.Constants;

public class ModuleItemContainer implements Container {

	public final int size = 1;
	private final ItemStack module;

	public NonNullList<ItemStack> moduleInventory;

	public ModuleItemContainer(ItemStack moduleStack) {
		module = moduleStack;

		if(!(moduleStack.getItem() instanceof ModuleItem moduleItem))
			return;

		moduleInventory = NonNullList.withSize(size, ItemStack.EMPTY);

		if (!module.hasTag())
			module.setTag(new CompoundTag());

		readFromNBT(module.getTag());
	}

	@Override
	public int getContainerSize() {
		return size;
	}

	@Override
	public ItemStack getItem(int index) {
		return moduleInventory.get(index);
	}

	public void readFromNBT(CompoundTag tag) {
		ListTag items = tag.getList("ItemInventory", Constants.NBT.TAG_COMPOUND);

		for(int i = 0; i < items.size(); i++) {
			CompoundTag item = items.getCompound(i);
			int slot = item.getInt("Slot");

			if(slot < getContainerSize())
				moduleInventory.set(slot, ItemStack.of(item));
		}
	}

	public void writeToNBT(CompoundTag tag) {
		ListTag items = new ListTag();

		for(int i = 0; i < getContainerSize(); i++)
			if(!getItem(i).isEmpty()) {
				CompoundTag item = new CompoundTag();
				item.putInt("Slot", i);
				getItem(i).save(item);

				items.add(item);
			}

		tag.put("ItemInventory", items);
	}

	@Override
	public ItemStack removeItem(int index, int size) {
		ItemStack stack = getItem(index);

		if(!stack.isEmpty())
			if(stack.getCount() > size) {
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
	public void setItem(int index, ItemStack stack) {
		moduleInventory.set(index, stack);

		if(!stack.isEmpty() && stack.getCount() > getMaxStackSize())
			stack.setCount(getMaxStackSize());

		setChanged();
	}

	@Override
	public int getMaxStackSize() {
		return 64;
	}

	@Override
	public void setChanged() {
		for(int i = 0; i < getContainerSize(); i++)
			if(!getItem(i).isEmpty() && getItem(i).getCount() == 0)
				moduleInventory.set(i, ItemStack.EMPTY);

		writeToNBT(module.getTag());
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
	public boolean isEmpty()
	{
		for(ItemStack stack : moduleInventory)
			if(!stack.isEmpty())
				return false;

		return true;
	}
}
