package net.geforcemods.securitycraft.containers;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.network.server.UpdateNBTTagOnServer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.util.Constants;

public class BriefcaseInventory implements IInventory {

	public static final int SIZE = 12;
	private final ItemStack briefcase;

	private NonNullList<ItemStack> briefcaseInventory = NonNullList.<ItemStack>withSize(SIZE, ItemStack.EMPTY);

	public BriefcaseInventory(ItemStack briefcaseItem) {
		briefcase = briefcaseItem;

		if (!briefcase.hasTag())
			briefcase.setTag(new CompoundNBT());

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

	public void readFromNBT(CompoundNBT tag) {
		ListNBT items = tag.getList("ItemInventory", Constants.NBT.TAG_COMPOUND);

		for(int i = 0; i < items.size(); i++) {
			CompoundNBT item = items.getCompound(i);
			int slot = item.getInt("Slot");

			if(slot < getSizeInventory())
				briefcaseInventory.set(slot, ItemStack.read(item));
		}
	}

	public void writeToNBT(CompoundNBT tag) {
		ListNBT items = new ListNBT();

		for(int i = 0; i < getSizeInventory(); i++)
			if(getStackInSlot(i) != null) {
				CompoundNBT item = new CompoundNBT();
				item.putInt("Slot", i);
				getStackInSlot(i).write(item);

				items.add(item);
			}

		tag.put("ItemInventory", items);
		SecurityCraft.channel.sendToServer(new UpdateNBTTagOnServer(briefcase));
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
	public boolean isUsableByPlayer(PlayerEntity player) {
		return true;
	}

	@Override
	public void openInventory(PlayerEntity player) {}

	@Override
	public void closeInventory(PlayerEntity player) {}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack itemStack) {
		return true;
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
