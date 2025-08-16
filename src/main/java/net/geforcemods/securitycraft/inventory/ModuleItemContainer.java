package net.geforcemods.securitycraft.inventory;

import net.geforcemods.securitycraft.items.ModuleItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.util.Constants;

public class ModuleItemContainer implements IInventory {
	private final ItemStack module;
	private NonNullList<ItemStack> moduleInventory;
	private DisguiseModuleMenu container;

	public ModuleItemContainer(ItemStack moduleItem) {
		module = moduleItem;

		if (!(moduleItem.getItem() instanceof ModuleItem))
			return;

		moduleInventory = NonNullList.withSize(1, ItemStack.EMPTY);

		if (!module.hasTagCompound())
			module.setTagCompound(new NBTTagCompound());

		readFromNBT(module.getTagCompound());
	}

	@Override
	public int getSizeInventory() {
		return 1;
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		return moduleInventory.get(index);
	}

	public void readFromNBT(NBTTagCompound tag) {
		NBTTagList items = tag.getTagList("ItemInventory", Constants.NBT.TAG_COMPOUND);

		for (int i = 0; i < items.tagCount(); i++) {
			NBTTagCompound item = items.getCompoundTagAt(i);
			int slot = item.getInteger("Slot");

			if (slot < getSizeInventory())
				moduleInventory.set(slot, new ItemStack(item));
		}
	}

	public void writeToNBT(NBTTagCompound tag) {
		NBTTagList items = new NBTTagList();

		for (int i = 0; i < getSizeInventory(); i++)
			if (!getStackInSlot(i).isEmpty()) {
				NBTTagCompound item = new NBTTagCompound();
				item.setInteger("Slot", i);
				getStackInSlot(i).writeToNBT(item);

				items.appendTag(item);
			}

		tag.setTag("ItemInventory", items);
	}

	@Override
	public ItemStack decrStackSize(int index, int size) {
		ItemStack stack = getStackInSlot(index);

		if (!stack.isEmpty())
			if (stack.getCount() > size) {
				stack = stack.splitStack(size);
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

		if (!stack.isEmpty() && stack.getCount() > getInventoryStackLimit())
			stack.setCount(getInventoryStackLimit());

		markDirty();
	}

	@Override
	public String getName() {
		return "ModuleCustomization";
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TextComponentString(getName());
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
		for (int i = 0; i < getSizeInventory(); i++) {
			if (!getStackInSlot(i).isEmpty() && getStackInSlot(i).getCount() == 0)
				moduleInventory.set(i, ItemStack.EMPTY);
		}

		if (!module.hasTagCompound())
			module.setTagCompound(new NBTTagCompound());

		writeToNBT(module.getTagCompound());

		if (container != null)
			container.onCraftMatrixChanged(this);
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
	public boolean isEmpty() {
		for (ItemStack stack : moduleInventory) {
			if (!stack.isEmpty())
				return false;
		}

		return true;
	}

	public void setContainer(DisguiseModuleMenu container) {
		this.container = container;
	}

	public ItemStack getModule() {
		return module;
	}
}
