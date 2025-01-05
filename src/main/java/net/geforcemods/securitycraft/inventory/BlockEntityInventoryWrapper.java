package net.geforcemods.securitycraft.inventory;

import net.geforcemods.securitycraft.api.IModuleInventoryWithContainer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class BlockEntityInventoryWrapper<T extends BlockEntity & IModuleInventoryWithContainer> implements Container {
	private final T wrapped;
	private final AbstractContainerMenu menu;

	public BlockEntityInventoryWrapper(T wrapped, AbstractContainerMenu menu) {
		this.wrapped = wrapped;
		this.menu = menu;
	}

	@Override
	public void clearContent() {
		for (int i = 0; i < wrapped.getContainerSize(); i++) {
			wrapped.setContainerItem(i, ItemStack.EMPTY);
		}

		menu.slotsChanged(this);
	}

	@Override
	public int getContainerSize() {
		return wrapped.getContainerSize();
	}

	@Override
	public boolean isEmpty() {
		return wrapped.isContainerEmpty();
	}

	@Override
	public ItemStack getItem(int index) {
		return wrapped.getStackInContainer(index);
	}

	@Override
	public ItemStack removeItem(int index, int count) {
		ItemStack toReturn = wrapped.removeContainerItem(index, count, false);

		menu.slotsChanged(this);
		return toReturn;
	}

	@Override
	public ItemStack removeItemNoUpdate(int index) {
		ItemStack toReturn = wrapped.getStackInContainer(index);

		wrapped.setStackInSlot(index, ItemStack.EMPTY);
		menu.slotsChanged(this);
		return toReturn;
	}

	@Override
	public void setItem(int index, ItemStack stack) {
		wrapped.setContainerItem(index, stack);
		menu.slotsChanged(this);
	}

	@Override
	public void setChanged() {
		wrapped.setChanged();
		menu.slotsChanged(this);
	}

	@Override
	public boolean stillValid(Player player) {
		return true;
	}
}
