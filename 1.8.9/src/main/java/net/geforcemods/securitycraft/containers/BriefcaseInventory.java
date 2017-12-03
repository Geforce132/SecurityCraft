package net.geforcemods.securitycraft.containers;

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

	public void readFromNBT(NBTTagCompound compound) {
		NBTTagList items = compound.getTagList("ItemInventory", Constants.NBT.TAG_COMPOUND);

		for(int i = 0; i < items.tagCount(); i++) {
			NBTTagCompound item = items.getCompoundTagAt(i);
			int slot = item.getInteger("Slot");

			if(slot < getSizeInventory())
				briefcaseInventory[slot] = ItemStack.loadItemStackFromNBT(item);
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
		mod_SecurityCraft.network.sendToServer(new PacketSUpdateNBTTag(briefcase));
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
		briefcaseInventory[index] = itemstack;

		if(itemstack != null && itemstack.stackSize > getInventoryStackLimit())
			itemstack.stackSize = getInventoryStackLimit();

		markDirty();
	}

	@Override
	public String getName() {
		return "Briefcase";
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
				briefcaseInventory[i] = null;

		writeToNBT(briefcase.getTagCompound());
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
	public IChatComponent getDisplayName() {
		return new ChatComponentText(getName());
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
			briefcaseInventory[i] = null;
	}
}
