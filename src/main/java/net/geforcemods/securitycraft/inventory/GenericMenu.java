package net.geforcemods.securitycraft.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;

public class GenericMenu extends Container {
	public GenericMenu(ContainerType<GenericMenu> type, int windowId) {
		super(type, windowId);
	}

	@Override
	public boolean stillValid(PlayerEntity player) {
		return true;
	}
}
