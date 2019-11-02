package org.freeforums.geforce.securitycraft.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;

import org.freeforums.geforce.securitycraft.tileentity.TileEntityKeycardReader;

public class ContainerKeycardSetup extends Container{

	public ContainerKeycardSetup(InventoryPlayer inventory, TileEntityKeycardReader tile_entity) {}

	public boolean canInteractWith(EntityPlayer entityplayer) {
		return true;
	}

}
