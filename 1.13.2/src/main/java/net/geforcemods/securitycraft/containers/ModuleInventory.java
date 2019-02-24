package net.geforcemods.securitycraft.containers;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.items.ItemModule;
import net.geforcemods.securitycraft.network.packets.PacketSUpdateNBTTag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.util.Constants;

public class ModuleInventory implements IInventory {

	public int SIZE = 0;
	private final ItemStack module;

	public NonNullList<ItemStack> moduleInventory;
	public int maxNumberOfItems;
	public int maxNumberOfBlocks;

	public ModuleInventory(ItemStack moduleItem) {
		module = moduleItem;

		if(moduleItem.getItem() == null || !(moduleItem.getItem() instanceof ItemModule)) return;

		SIZE = ((ItemModule) moduleItem.getItem()).getNumberOfAddons();
		maxNumberOfItems = ((ItemModule) moduleItem.getItem()).getNumberOfItemAddons();
		maxNumberOfBlocks = ((ItemModule) moduleItem.getItem()).getNumberOfBlockAddons();
		moduleInventory = NonNullList.withSize(SIZE, ItemStack.EMPTY);

		if (!module.hasTag())
			module.setTag(new NBTTagCompound());

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

	public void readFromNBT(NBTTagCompound tag) {
		NBTTagList items = tag.getList("ItemInventory", Constants.NBT.TAG_COMPOUND);

		for(int i = 0; i < items.size(); i++) {
			NBTTagCompound item = items.getCompound(i);
			int slot = item.getInt("Slot");

			if(slot < getSizeInventory())
				moduleInventory.set(slot, new ItemStack(item));
		}
	}

	public void writeToNBT(NBTTagCompound tag) {
		NBTTagList items = new NBTTagList();

		for(int i = 0; i < getSizeInventory(); i++)
			if(!getStackInSlot(i).isEmpty()) {
				NBTTagCompound item = new NBTTagCompound();
				item.putInt("Slot", i);
				getStackInSlot(i).write(item);

				items.add(item);
			}

		tag.put("ItemInventory", items);
		SecurityCraft.network.sendToServer(new PacketSUpdateNBTTag(module));
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
	public ITextComponent getName() {
		return new TextComponentString("ModuleCustomization");
	}

	@Override
	public ITextComponent getDisplayName() {
		return getName();
	}

	@Override
	public boolean hasCustomName() {
		return true;
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
	public boolean isUsableByPlayer(EntityPlayer player) {
		return true;
	}

	@Override
	public void openInventory(EntityPlayer player) {}

	@Override
	public void closeInventory(EntityPlayer player) {}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return true;
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {}

	@Override
	public int getFieldCount() {
		return 5;
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
