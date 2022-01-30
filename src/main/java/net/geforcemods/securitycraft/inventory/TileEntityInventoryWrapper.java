package net.geforcemods.securitycraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;

public class TileEntityInventoryWrapper<T extends TileEntity & IInventory> implements IInventory {
	private final T wrapped;
	private final Container container;

	public TileEntityInventoryWrapper(T wrapped, Container container) {
		this.wrapped = wrapped;
		this.container = container;
	}

	@Override
	public String getName() {
		return wrapped.getName();
	}

	@Override
	public boolean hasCustomName() {
		return wrapped.hasCustomName();
	}

	@Override
	public ITextComponent getDisplayName() {
		return wrapped.getDisplayName();
	}

	@Override
	public int getSizeInventory() {
		return wrapped.getSizeInventory();
	}

	@Override
	public boolean isEmpty() {
		return wrapped.isEmpty();
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		return wrapped.getStackInSlot(index);
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		ItemStack toReturn = wrapped.decrStackSize(index, count);

		container.onCraftMatrixChanged(wrapped);
		return toReturn;
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		ItemStack toReturn = wrapped.removeStackFromSlot(index);

		container.onCraftMatrixChanged(wrapped);
		return toReturn;
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		wrapped.setInventorySlotContents(index, stack);
		container.onCraftMatrixChanged(wrapped);
	}

	@Override
	public int getInventoryStackLimit() {
		return wrapped.getInventoryStackLimit();
	}

	@Override
	public void markDirty() {
		wrapped.markDirty();
		container.onCraftMatrixChanged(wrapped);
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		return wrapped.isUsableByPlayer(player);
	}

	@Override
	public void openInventory(EntityPlayer player) {
		wrapped.openInventory(player);
	}

	@Override
	public void closeInventory(EntityPlayer player) {
		wrapped.closeInventory(player);
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return wrapped.isItemValidForSlot(index, stack);
	}

	@Override
	public int getField(int id) {
		return wrapped.getField(id);
	}

	@Override
	public void setField(int id, int value) {
		wrapped.setField(id, value);
	}

	@Override
	public int getFieldCount() {
		return wrapped.getFieldCount();
	}

	@Override
	public void clear() {
		wrapped.clear();
		container.onCraftMatrixChanged(wrapped);
	}
}
