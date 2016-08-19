package net.geforcemods.securitycraft.containers;

import net.geforcemods.securitycraft.main.mod_SecurityCraft;
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
		
		if (!briefcase.hasTagCompound()) {
			briefcase.setTagCompound(new NBTTagCompound());
		}
		
		readFromNBT(briefcase.getTagCompound());
	}

	public int getSizeInventory() {
		return SIZE;
	}

	public ItemStack getStackInSlot(int index) {
		return briefcaseInventory[index];
	}
	
	public void readFromNBT(NBTTagCompound compound) {
		NBTTagList items = compound.getTagList("ItemInventory", Constants.NBT.TAG_COMPOUND);

		for(int i = 0; i < items.tagCount(); i++) {
			NBTTagCompound item = items.getCompoundTagAt(i);
			int slot = item.getInteger("Slot");

			if(slot < getSizeInventory()) {
				briefcaseInventory[slot] = ItemStack.loadItemStackFromNBT(item);
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
		mod_SecurityCraft.network.sendToServer(new PacketSUpdateNBTTag(briefcase));
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
		briefcaseInventory[index] = itemstack;

		if(itemstack != null && itemstack.stackSize > getInventoryStackLimit()) {
			itemstack.stackSize = getInventoryStackLimit();
		}

		markDirty();
	}

	public String getInventoryName() {
		return "Briefcase";
	}

	public boolean hasCustomInventoryName() {
		return true;
	}

	public int getInventoryStackLimit() {
		return 64;
	}

	public void markDirty() {
		for(int i = 0; i < getSizeInventory(); i++) {
			if(getStackInSlot(i) != null && getStackInSlot(i).stackSize == 0) {
				briefcaseInventory[i] = null;
			}
		}
		
		writeToNBT(briefcase.getTagCompound());
	}

	public boolean isUseableByPlayer(EntityPlayer player) {
		return true;
	}

	public void openInventory() {}

	public void closeInventory() {}

	public boolean isItemValidForSlot(int index, ItemStack itemstack) {
		return true;
	}

}
