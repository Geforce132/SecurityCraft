package net.geforcemods.securitycraft.containers;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.items.ItemModule;
import net.geforcemods.securitycraft.network.packets.PacketSUpdateNBTTag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
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

	public void readFromNBT(NBTTagCompound compound) {
		NBTTagList items = compound.getTagList("ItemInventory", Constants.NBT.TAG_COMPOUND);

		for(int i = 0; i < items.tagCount(); i++) {
			NBTTagCompound item = items.getCompoundTagAt(i);
			int slot = item.getInteger("Slot");

			if(slot < getSizeInventory())
				moduleInventory[slot] = ItemStack.loadItemStackFromNBT(item);
		}
	}

	public void writeToNBT(NBTTagCompound tagcompound) {
		NBTTagList items = new NBTTagList();

		for(int i = 0; i < getSizeInventory(); i++)
			if(getStackInSlot(i) != null) {
				NBTTagCompound item = new NBTTagCompound();
				item.setInteger("Slot", i);
				getStackInSlot(i).writeToNBT(item);

				items.appendTag(item);
			}

		tagcompound.setTag("ItemInventory", items);
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
	public ItemStack removeStackFromSlot(int index) {
		ItemStack stack = getStackInSlot(index);
		setInventorySlotContents(index, null);
		return stack;
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack itemstack) {
		moduleInventory[index] = itemstack;

		if(itemstack != null && itemstack.stackSize > getInventoryStackLimit())
			itemstack.stackSize = getInventoryStackLimit();

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
	public void openInventory(EntityPlayer player) {}

	@Override
	public void closeInventory(EntityPlayer player) {}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack itemstack) {
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
}
