package org.freeforums.geforce.securitycraft.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;

import org.freeforums.geforce.securitycraft.tileentity.TileEntityRAM;

public class ContainerRemoteAccessMine extends Container{

	public ContainerRemoteAccessMine(InventoryPlayer par1Inventory, TileEntityRAM par2TileEntityRAD) {
		
	}

	public boolean canInteractWith(EntityPlayer entityplayer) {
		return true;
	}

}
