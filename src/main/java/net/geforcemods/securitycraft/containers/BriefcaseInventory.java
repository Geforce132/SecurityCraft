package net.geforcemods.securitycraft.containers;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.network.packets.PacketSUpdateNBTTag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

public class BriefcaseInventory implements IInventory {

	public static final int SIZE = 12;
	private final ItemStack briefcase;

	private ItemStack[] briefcaseInventory = new ItemStack[SIZE];

	public BriefcaseInventory(ItemStack briefcaseItem) {
		briefcase = briefcaseItem;

		if (!briefcase.hasTagCompound())
			briefcase.setTagCompound(new NBTTagCompound());

		readFromNBT(briefcase.getTagCompound());
	}

	@Override
	public int getSizeInventory() {
		return SIZE;
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		return briefcaseInventory[index];
	}

	public void readFromNBT(NBTTagCompound tag) {
		NBTTagList items = tag.getTagList("ItemInventory", Constants.NBT.TAG_COMPOUND);

		for(int i = 0; i < items.tagCount(); i++) {
			NBTTagCompound item = items.getCompoundTagAt(i);
			int slot = item.getInteger("Slot");

			if(slot < getSizeInventory())
				briefcaseInventory[slot] = ItemStack.loadItemStackFromNBT(item);
		}
	}

	public void writeToNBT(NBTTagCompound tag) {
		NBTTagList items = new NBTTagList();

		for(int i = 0; i < getSizeInventory(); i++)
			if(getStackInSlot(i) != null) {
				NBTTagCompound item = new NBTTagCompound();
				item.setInteger("Slot", i);
				getStackInSlot(i).writeToNBT(item);

				items.appendTag(item);
			}

		tag.setTag("ItemInventory", items);
		SecurityCraft.network.sendToServer(new PacketSUpdateNBTTag(briefcase));
	}

	@Override
	public ItemStack decrStackSize(int index, int size) {
		ItemStack stack = getStackInSlot(index);

		if(stack != null)
			if(stack.stackSize > size) {
				stack = stack.splitStack(size);
				markDirty();
			}
			else
				setInventorySlotContents(index, null);

		return stack;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int index) {
		ItemStack stack = getStackInSlot(index);
		setInventorySlotContents(index, null);
		return stack;
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		briefcaseInventory[index] = stack;

		if(stack != null && stack.stackSize > getInventoryStackLimit())
			stack.stackSize = getInventoryStackLimit();

		markDirty();
	}

	@Override
	public String getInventoryName() {
		return "Briefcase";
	}

	@Override
	public boolean isCustomInventoryName() {
		return true;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public void markDirty() {
		for(int i = 0; i < getSizeInventory(); i++)
			if(getStackInSlot(i) != null && getStackInSlot(i).stackSize == 0)
				briefcaseInventory[i] = null;

		writeToNBT(briefcase.getTagCompound());
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return true;
	}

	@Override
	public void openChest() {}

	@Override
	public void closeChest() {}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return true;
	}

}
