package org.freeforums.geforce.securitycraft.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;

import org.freeforums.geforce.securitycraft.tileentity.TileEntityOwnable;

public class ContainerRetinalScanner extends Container{

	public ContainerRetinalScanner(InventoryPlayer inventory, TileEntityOwnable tile_entity) {
	}

	public boolean canInteractWith(EntityPlayer entityplayer) {
		return true;
	}

}
