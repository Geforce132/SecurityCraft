package org.freeforums.geforce.securitycraft.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;

import org.freeforums.geforce.securitycraft.tileentity.TileEntityLogger;

public class ContainerLogger extends Container {

	public ContainerLogger(InventoryPlayer par1InventoryPlayer, TileEntityLogger par2TileEntityFurnace) {
	}

	public boolean canInteractWith(EntityPlayer entityplayer) {
		return true;
	}

}
