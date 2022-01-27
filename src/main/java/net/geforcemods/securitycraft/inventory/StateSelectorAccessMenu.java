package net.geforcemods.securitycraft.inventory;

import net.geforcemods.securitycraft.util.StandingOrWallType;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.ItemStack;

public abstract class StateSelectorAccessMenu extends Container {
	protected StateSelectorAccessMenu(ContainerType<?> menuType, int containerId) {
		super(menuType, containerId);
	}

	public abstract ItemStack getStateStack();

	public abstract BlockState getSavedState();

	public abstract StandingOrWallType getStandingOrWallType();

	public void onStateChange(BlockState state) {}
}
