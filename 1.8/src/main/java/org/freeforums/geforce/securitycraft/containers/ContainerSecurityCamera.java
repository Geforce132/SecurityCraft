package org.freeforums.geforce.securitycraft.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;

import org.freeforums.geforce.securitycraft.tileentity.TileEntitySecurityCamera;

public class ContainerSecurityCamera extends Container{

	public ContainerSecurityCamera(InventoryPlayer inventory, TileEntitySecurityCamera tile_entity) {
	}

	public boolean canInteractWith(EntityPlayer entityplayer) {
		return true;
	}

}
