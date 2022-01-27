package net.geforcemods.securitycraft.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class BlockEntityInventoryWrapper<T extends TileEntity & IInventory> implements IInventory {
	private final T wrapped;
	private final Container menu;

	public BlockEntityInventoryWrapper(T wrapped, Container menu) {
		this.wrapped = wrapped;
		this.menu = menu;
	}

	@Override
	public void clearContent() {
		wrapped.clearContent();
		menu.slotsChanged(wrapped);
	}

	@Override
	public int getContainerSize() {
		return wrapped.getContainerSize();
	}

	@Override
	public boolean isEmpty() {
		return wrapped.isEmpty();
	}

	@Override
	public ItemStack getItem(int index) {
		return wrapped.getItem(index);
	}

	@Override
	public ItemStack removeItem(int index, int count) {
		ItemStack toReturn = wrapped.removeItem(index, count);

		menu.slotsChanged(wrapped);
		return toReturn;
	}

	@Override
	public ItemStack removeItemNoUpdate(int index) {
		ItemStack toReturn = wrapped.removeItemNoUpdate(index);

		menu.slotsChanged(wrapped);
		return toReturn;
	}

	@Override
	public void setItem(int index, ItemStack stack) {
		wrapped.setItem(index, stack);
		menu.slotsChanged(wrapped);
	}

	@Override
	public void setChanged() {
		wrapped.setChanged();
		menu.slotsChanged(wrapped);
	}

	@Override
	public boolean stillValid(PlayerEntity player) {
		return wrapped.stillValid(player);
	}
}
