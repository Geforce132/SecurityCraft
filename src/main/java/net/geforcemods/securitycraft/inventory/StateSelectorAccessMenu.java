package net.geforcemods.securitycraft.inventory;

import net.geforcemods.securitycraft.util.StandingOrWallType;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public abstract class StateSelectorAccessMenu extends AbstractContainerMenu {
	protected StateSelectorAccessMenu(MenuType<?> menuType, int containerId) {
		super(menuType, containerId);
	}

	public abstract ItemStack getStateStack();

	public abstract BlockState getSavedState();

	public abstract StandingOrWallType getStandingOrWallType();

	public void onStateChange(BlockState state) {}
}
