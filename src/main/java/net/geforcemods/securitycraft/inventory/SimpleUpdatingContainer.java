package net.geforcemods.securitycraft.inventory;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class SimpleUpdatingContainer extends SimpleContainer {
	private final AbstractContainerMenu menu;

	public SimpleUpdatingContainer(int size, AbstractContainerMenu menu) {
		super(size);
		this.menu = menu;
	}

	@Override
	public void setChanged() {
		super.setChanged();
		menu.slotsChanged(this);
	}
}
