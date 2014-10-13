package org.freeforums.geforce.securitycraft.containers;

import org.freeforums.geforce.securitycraft.tileentity.TileEntityOwnable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;

public class ContainerRetinalScannerSetup extends Container{

	public ContainerRetinalScannerSetup(InventoryPlayer inventory, TileEntityOwnable tile_entity) {
	}

	public boolean canInteractWith(EntityPlayer entityplayer) {
		return true;
	}

}
