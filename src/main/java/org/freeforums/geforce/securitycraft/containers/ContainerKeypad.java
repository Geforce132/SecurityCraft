package org.freeforums.geforce.securitycraft.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;

import org.freeforums.geforce.securitycraft.tileentity.TileEntityKeypad;

public class ContainerKeypad extends Container{

	public ContainerKeypad(InventoryPlayer inventory, TileEntityKeypad tile_entity) {
			
	}

	public boolean canInteractWith(EntityPlayer entityplayer) {
		return true;
	}

}
