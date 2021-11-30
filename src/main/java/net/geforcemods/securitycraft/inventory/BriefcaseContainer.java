package net.geforcemods.securitycraft.inventory;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class BriefcaseContainer implements Container {

	public static final int SIZE = 12;
	private final ItemStack briefcase;
	private NonNullList<ItemStack> briefcaseInventory = NonNullList.<ItemStack>withSize(SIZE, ItemStack.EMPTY);

	public BriefcaseContainer(ItemStack briefcaseItem) {
		briefcase = briefcaseItem;

		if (!briefcase.hasTag())
			briefcase.setTag(new CompoundTag());

		readFromNBT(briefcase.getTag());
	}

	@Override
	public int getContainerSize() {
		return SIZE;
	}

	@Override
	public ItemStack getItem(int index) {
		return briefcaseInventory.get(index);
	}

	public void readFromNBT(CompoundTag tag) {
		ListTag items = tag.getList("ItemInventory", Tag.TAG_COMPOUND);

		for(int i = 0; i < items.size(); i++) {
			CompoundTag item = items.getCompound(i);
			int slot = item.getInt("Slot");

			if(slot < getContainerSize())
				briefcaseInventory.set(slot, ItemStack.of(item));
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
	public void setItem(int index, ItemStack itemStack) {
		briefcaseInventory.set(index, itemStack);

		if(!itemStack.isEmpty() && itemStack.getCount() > getMaxStackSize())
			itemStack.setCount(getMaxStackSize());

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
				briefcaseInventory.set(i, ItemStack.EMPTY);

		writeToNBT(briefcase.getTag());
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
	public boolean canPlaceItem(int index, ItemStack itemStack) {
		return true;
	}

	@Override
	public void clearContent() {
		for(int i = 0; i < SIZE; i++)
			briefcaseInventory.set(i, ItemStack.EMPTY);
	}

	@Override
	public boolean isEmpty()
	{
		for(ItemStack stack : briefcaseInventory)
			if(!stack.isEmpty())
				return false;

		return true;
	}
}
