package net.geforcemods.securitycraft.containers;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;

public class GenericContainer extends Container {

	public GenericContainer(ContainerType<GenericContainer> type, int windowId)
	{
		super(type, windowId);
	}

	@Override
	public boolean canInteractWith(PlayerEntity player) {
		return true;
	}
}
