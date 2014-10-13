package org.freeforums.geforce.securitycraft.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;

import org.freeforums.geforce.securitycraft.tileentity.TileEntityKeypadChest;

public class ContainerKeypadChestSetup extends Container {

	public ContainerKeypadChestSetup(InventoryPlayer par1InventoryPlayer, TileEntityKeypadChest tile_entity){
	}

	public boolean canInteractWith(EntityPlayer var1) {
		return true;
	}

}
