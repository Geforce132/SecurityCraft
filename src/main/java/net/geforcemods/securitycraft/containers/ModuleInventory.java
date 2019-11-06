package net.geforcemods.securitycraft.containers;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.items.ModuleItem;
import net.geforcemods.securitycraft.network.server.UpdateNBTTagOnServer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.thread.EffectiveSide;

public class ModuleInventory implements IInventory {

	public int SIZE = 0;
	private final ItemStack module;

	public NonNullList<ItemStack> moduleInventory;
	public int maxNumberOfItems;
	public int maxNumberOfBlocks;

	public ModuleInventory(ItemStack moduleItem) {
		module = moduleItem;

		if(moduleItem.getItem() == null || !(moduleItem.getItem() instanceof ModuleItem)) return;

		SIZE = ((ModuleItem) moduleItem.getItem()).getNumberOfAddons();
		maxNumberOfItems = ((ModuleItem) moduleItem.getItem()).getNumberOfItemAddons();
		maxNumberOfBlocks = ((ModuleItem) moduleItem.getItem()).getNumberOfBlockAddons();
		moduleInventory = NonNullList.withSize(SIZE, ItemStack.EMPTY);

		if (!module.hasTag())
			module.setTag(new CompoundNBT());

		readFromNBT(module.getTag());
	}

	@Override
	public int getSizeInventory() {
		return SIZE;
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		return moduleInventory.get(index);
	}

	public void readFromNBT(CompoundNBT tag) {
		ListNBT items = tag.getList("ItemInventory", Constants.NBT.TAG_COMPOUND);

		for(int i = 0; i < items.size(); i++) {
			CompoundNBT item = items.getCompound(i);
			int slot = item.getInt("Slot");

			if(slot < getSizeInventory())
				moduleInventory.set(slot, ItemStack.read(item));
		}
	}

	public void writeToNBT(CompoundNBT tag) {
		ListNBT items = new ListNBT();

		for(int i = 0; i < getSizeInventory(); i++)
			if(!getStackInSlot(i).isEmpty()) {
				CompoundNBT item = new CompoundNBT();
				item.putInt("Slot", i);
				getStackInSlot(i).write(item);

				items.add(item);
			}

		tag.put("ItemInventory", items);

		if(EffectiveSide.get() == LogicalSide.CLIENT)
			SecurityCraft.channel.sendToServer(new UpdateNBTTagOnServer(module));
	}

	@Override
	public ItemStack decrStackSize(int index, int size) {
		ItemStack stack = getStackInSlot(index);

		if(!stack.isEmpty())
			if(stack.getCount() > size) {
				stack = stack.split(size);
				markDirty();
			}
			else
				setInventorySlotContents(index, ItemStack.EMPTY);

		return stack;
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		ItemStack stack = getStackInSlot(index);
		setInventorySlotContents(index, ItemStack.EMPTY);
		return stack;
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		moduleInventory.set(index, stack);

		if(!stack.isEmpty() && stack.getCount() > getInventoryStackLimit())
			stack.setCount(getInventoryStackLimit());

		markDirty();
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public void markDirty() {
		for(int i = 0; i < getSizeInventory(); i++)
			if(!getStackInSlot(i).isEmpty() && getStackInSlot(i).getCount() == 0)
				moduleInventory.set(i, ItemStack.EMPTY);

		writeToNBT(module.getTag());
	}

	@Override
	public boolean isUsableByPlayer(PlayerEntity player) {
		return true;
	}

	@Override
	public void openInventory(PlayerEntity player) {}

	@Override
	public void closeInventory(PlayerEntity player) {}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return true;
	}

	@Override
	public void clear() {}

	@Override
	public boolean isEmpty()
	{
		for(ItemStack stack : moduleInventory)
			if(!stack.isEmpty())
				return false;

		return true;
	}
}
