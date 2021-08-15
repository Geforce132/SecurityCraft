package net.geforcemods.securitycraft.inventory;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

public class GenericMenu extends AbstractContainerMenu {

	public GenericMenu(MenuType<GenericMenu> type, int windowId)
	{
		super(type, windowId);
	}

	@Override
	public boolean stillValid(Player player) {
		return true;
	}
}
