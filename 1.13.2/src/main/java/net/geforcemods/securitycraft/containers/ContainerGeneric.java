package net.geforcemods.securitycraft.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

public class ContainerGeneric extends Container {
	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return true;
	}
}
