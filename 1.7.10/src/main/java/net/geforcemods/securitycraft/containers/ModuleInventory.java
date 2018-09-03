package net.geforcemods.securitycraft.containers;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.items.ItemModule;
import net.geforcemods.securitycraft.network.packets.PacketSUpdateNBTTag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

public class ModuleInventory implements IInventory {

	public int SIZE = 0;
	private final ItemStack module;

	public ItemStack[] moduleInventory;
	public int maxNumberOfItems;
	public int maxNumberOfBlocks;

	public ModuleInventory(ItemStack moduleItem) {
		module = moduleItem;

		if(moduleItem.getItem() == null || !(moduleItem.getItem() instanceof ItemModule)) return;

		SIZE = ((ItemModule) moduleItem.getItem()).getNumberOfAddons();
		maxNumberOfItems = ((ItemModule) moduleItem.getItem()).getNumberOfItemAddons();
		maxNumberOfBlocks = ((ItemModule) moduleItem.getItem()).getNumberOfBlockAddons();
		moduleInventory = new ItemStack[SIZE];

		if (!module.hasTagCompound())
			module.setTagCompound(new NBTTagCompound());

		readFromNBT(module.getTagCompound());
	}

	@Override
	public int getSizeInventory() {
		return SIZE;
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		return moduleInventory[index];
	}

	public void readFromNBT(NBTTagCompound taf) {
		NBTTagList items = taf.getTagList("ItemInventory", Constants.NBT.TAG_COMPOUND);

		for(int i = 0; i < items.tagCount(); i++) {
			NBTTagCompound item = items.getCompoundTagAt(i);
			int slot = item.getInteger("Slot");

			if(slot < getSizeInventory())
				moduleInventory[slot] = ItemStack.loadItemStackFromNBT(item);
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
		SecurityCraft.network.sendToServer(new PacketSUpdateNBTTag(module));
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
		moduleInventory[index] = stack;

		if(stack != null && stack.stackSize > getInventoryStackLimit())
			stack.stackSize = getInventoryStackLimit();

		markDirty();
	}

	@Override
	public String getInventoryName() {
		return "ModuleCustomization";
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
				moduleInventory[i] = null;

		writeToNBT(module.getTagCompound());
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
