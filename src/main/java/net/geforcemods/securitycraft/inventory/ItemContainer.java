package net.geforcemods.securitycraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.util.Constants;

public class ItemContainer implements IInventory {
	private final ItemStack containerStack;
	private final NonNullList<ItemStack> inventory;
	private final int maxStackSize;

	private ItemContainer(ItemStack containerStack, int inventorySize, int maxStackSize) {
		this.containerStack = containerStack;
		this.maxStackSize = maxStackSize;
		inventory = NonNullList.<ItemStack>withSize(inventorySize, ItemStack.EMPTY);

		if (!containerStack.hasTagCompound())
			containerStack.setTagCompound(new NBTTagCompound());

		load(containerStack.getTagCompound());
	}

	public static ItemContainer briefcase(ItemStack briefcase) {
		return new ItemContainer(briefcase, BriefcaseMenu.CONTAINER_SIZE, 64);
	}

	public static ItemContainer keycardHolder(ItemStack keycardHolder) {
		return new ItemContainer(keycardHolder, KeycardHolderMenu.CONTAINER_SIZE, 1);
	}

	@Override
	public int getSizeInventory() {
		return inventory.size();
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		return inventory.get(index);
	}

	public void load(NBTTagCompound tag) {
		NBTTagList items = tag.getTagList("ItemInventory", Constants.NBT.TAG_COMPOUND);

		for (int i = 0; i < items.tagCount(); i++) {
			NBTTagCompound item = items.getCompoundTagAt(i);
			int slot = item.getInteger("Slot");

			if (slot < getSizeInventory())
				inventory.set(slot, new ItemStack(item));
		}
	}

	public void writeToNBT(NBTTagCompound tag) {
		NBTTagList items = new NBTTagList();

		for (int i = 0; i < getSizeInventory(); i++) {
			if (!getStackInSlot(i).isEmpty()) {
				NBTTagCompound item = new NBTTagCompound();
				item.setInteger("Slot", i);
				getStackInSlot(i).writeToNBT(item);

				items.appendTag(item);
			}
		}

		tag.setTag("ItemInventory", items);
	}

	@Override
	public ItemStack decrStackSize(int index, int size) {
		ItemStack stack = getStackInSlot(index);

		if (!stack.isEmpty()) {
			if (stack.getCount() > size) {
				stack = stack.splitStack(size);
				markDirty();
			}
			else
				setInventorySlotContents(index, ItemStack.EMPTY);
		}

		return stack;
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		ItemStack stack = getStackInSlot(index);
		setInventorySlotContents(index, ItemStack.EMPTY);
		return stack;
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack itemStack) {
		inventory.set(index, itemStack);

		if (!itemStack.isEmpty() && itemStack.getCount() > getInventoryStackLimit())
			itemStack.setCount(getInventoryStackLimit());

		markDirty();
	}

	@Override
	public String getName() {
		return containerStack.getDisplayName();
	}

	@Override
	public boolean hasCustomName() {
		return true;
	}

	@Override
	public int getInventoryStackLimit() {
		return maxStackSize;
	}

	@Override
	public void markDirty() {
		for (int i = 0; i < getSizeInventory(); i++) {
			if (!getStackInSlot(i).isEmpty() && getStackInSlot(i).getCount() == 0)
				inventory.set(i, ItemStack.EMPTY);
		}

		if (!containerStack.hasTagCompound())
			containerStack.setTagCompound(new NBTTagCompound());

		writeToNBT(containerStack.getTagCompound());
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
	public boolean isItemValidForSlot(int index, ItemStack itemStack) {
		return true;
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TextComponentString(getName());
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {
		for (int i = 0; i < getSizeInventory(); i++) {
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
