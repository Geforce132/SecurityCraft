package net.geforcemods.securitycraft.containers;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.network.server.UpdateNBTTag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.util.Constants;

public class BriefcaseInventory implements IInventory {

	public static final int SIZE = 12;
	private final ItemStack briefcase;

	private NonNullList<ItemStack> briefcaseInventory = NonNullList.<ItemStack>withSize(SIZE, ItemStack.EMPTY);

	public BriefcaseInventory(ItemStack briefcaseItem) {
		briefcase = briefcaseItem;

		if (!briefcase.hasTag())
			briefcase.setTag(new NBTTagCompound());

		readFromNBT(briefcase.getTag());
	}

	@Override
	public int getSizeInventory() {
		return SIZE;
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		return briefcaseInventory.get(index);
	}

	public void readFromNBT(NBTTagCompound tag) {
		NBTTagList items = tag.getList("ItemInventory", Constants.NBT.TAG_COMPOUND);

		for(int i = 0; i < items.size(); i++) {
			NBTTagCompound item = items.getCompound(i);
			int slot = item.getInt("Slot");

			if(slot < getSizeInventory())
				briefcaseInventory.set(slot, new ItemStack(item));
		}
	}

	public void writeToNBT(NBTTagCompound tag) {
		NBTTagList items = new NBTTagList();

		for(int i = 0; i < getSizeInventory(); i++)
			if(getStackInSlot(i) != null) {
				NBTTagCompound item = new NBTTagCompound();
				item.putInt("Slot", i);
				getStackInSlot(i).write(item);

				items.add(item);
			}

		tag.put("ItemInventory", items);
		SecurityCraft.channel.sendToServer(new UpdateNBTTag(briefcase));
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
	public void setInventorySlotContents(int index, ItemStack itemStack) {
		briefcaseInventory.set(index, itemStack);

		if(!itemStack.isEmpty() && itemStack.getCount() > getInventoryStackLimit())
			itemStack.setCount(getInventoryStackLimit());

		markDirty();
	}

	@Override
	public ITextComponent getName() {
		return new TextComponentString("Briefcase");
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
				briefcaseInventory.set(i, ItemStack.EMPTY);

		writeToNBT(briefcase.getTag());
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
		return getName();
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
