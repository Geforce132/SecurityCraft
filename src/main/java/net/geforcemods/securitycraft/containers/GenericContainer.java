package net.geforcemods.securitycraft.containers;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

public class GenericContainer extends AbstractContainerMenu {

	public GenericContainer(MenuType<GenericContainer> type, int windowId)
	{
		super(type, windowId);
	}

	@Override
	public boolean stillValid(Player player) {
		return true;
	}
}
