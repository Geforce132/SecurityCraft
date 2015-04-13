package org.freeforums.geforce.securitycraft.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;

public class ContainerGeneric extends Container {
	
	/**
	 * Blank, empty Container for use with GUIs that don't have any inventory slots.
	 * 
	 */
	public ContainerGeneric(InventoryPlayer inventory, TileEntity te){
		
	}

	public boolean canInteractWith(EntityPlayer playerIn) {
		return true;
	}

}
