package net.geforcemods.securitycraft.containers;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;

public class ContainerGeneric extends Container {
	@Override
	public boolean canInteractWith(PlayerEntity player) {
		return true;
	}
}
