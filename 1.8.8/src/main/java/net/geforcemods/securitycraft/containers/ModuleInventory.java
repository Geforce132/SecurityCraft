package net.geforcemods.securitycraft.containers;

import net.geforcemods.securitycraft.items.ItemModule;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.network.packets.PacketSUpdateNBTTag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
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
				
		if (!module.hasTagCompound()) {
			module.setTagCompound(new NBTTagCompound());
		}
		
		readFromNBT(module.getTagCompound());
	}

	public int getSizeInventory() {
		return SIZE;
	}

	public ItemStack getStackInSlot(int index) {
		return moduleInventory[index];
	}
	
	public void readFromNBT(NBTTagCompound compound) {
		NBTTagList items = compound.getTagList("ItemInventory", Constants.NBT.TAG_COMPOUND);

		for(int i = 0; i < items.tagCount(); i++) {
			NBTTagCompound item = items.getCompoundTagAt(i);
			int slot = item.getInteger("Slot");

			if(slot < getSizeInventory()) {
				moduleInventory[slot] = ItemStack.loadItemStackFromNBT(item);
			}
		}
	}

	public void writeToNBT(NBTTagCompound tagcompound) {
		NBTTagList items = new NBTTagList();

		for(int i = 0; i < getSizeInventory(); i++) {
			if(getStackInSlot(i) != null) {
				NBTTagCompound item = new NBTTagCompound();
				item.setInteger("Slot", i);
				getStackInSlot(i).writeToNBT(item);
				
				items.appendTag(item);
			}
		}

		tagcompound.setTag("ItemInventory", items);
		mod_SecurityCraft.network.sendToServer(new PacketSUpdateNBTTag(module));
	}

	public ItemStack decrStackSize(int index, int size) {
		ItemStack stack = getStackInSlot(index);
		
		if(stack != null) {
			if(stack.stackSize > size) {
				stack = stack.splitStack(size);
				markDirty();
			}
			else {
				setInventorySlotContents(index, null);
			}
		}
		
		return stack;
	}

	public ItemStack getStackInSlotOnClosing(int index) {
		ItemStack stack = getStackInSlot(index);
		setInventorySlotContents(index, null);
		return stack;
	}

	public void setInventorySlotContents(int index, ItemStack itemstack) {
		moduleInventory[index] = itemstack;

		if(itemstack != null && itemstack.stackSize > getInventoryStackLimit()) {
			itemstack.stackSize = getInventoryStackLimit();
		}

		markDirty();
	}

	public String getName() {
		return "ModuleCustomization";
	}
	
	public String getCommandSenderName() {
		return "ModuleCustomization";
	}
	
	public IChatComponent getDisplayName() {
		return new ChatComponentText(getName());
	}

	public boolean hasCustomName() {
		return true;
	}

	public int getInventoryStackLimit() {
		return 64;
	}

	public void markDirty() {
		for(int i = 0; i < getSizeInventory(); i++) {
			if(getStackInSlot(i) != null && getStackInSlot(i).stackSize == 0) {
				moduleInventory[i] = null;
			}
		}
		
		writeToNBT(module.getTagCompound());
	}

	public boolean isUseableByPlayer(EntityPlayer player) {
		return true;
	}

	public void openInventory(EntityPlayer player) {}

	public void closeInventory(EntityPlayer player) {}

	public boolean isItemValidForSlot(int index, ItemStack itemstack) {
		return true;
	}

	public int getField(int id) {
		return 0;
    }

	public void setField(int id, int value) {}

	public int getFieldCount() {
		return 5;
	}

	public void clear() {}

}
