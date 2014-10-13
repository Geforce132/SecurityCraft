package org.freeforums.geforce.securitycraft.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;

import org.freeforums.geforce.securitycraft.tileentity.TileEntityRAM;

public class ContainerRAMDetonate extends Container{

	
	public ContainerRAMDetonate(InventoryPlayer inventory, TileEntityRAM tile_entity) {
	}

	public boolean canInteractWith(EntityPlayer entityplayer) {
		return true;
	}

}
