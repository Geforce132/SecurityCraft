package net.geforcemods.securitycraft.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class BlockEntityInventoryWrapper<T extends BlockEntity & Container> implements Container {
	private final T wrapped;
	private final AbstractContainerMenu menu;

	public BlockEntityInventoryWrapper(T wrapped, AbstractContainerMenu menu) {
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
	public boolean stillValid(Player player) {
		return wrapped.stillValid(player);
	}
}
