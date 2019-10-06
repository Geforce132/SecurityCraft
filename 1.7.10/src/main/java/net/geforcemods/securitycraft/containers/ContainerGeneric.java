package net.geforcemods.securitycraft.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;

public class ContainerGeneric extends Container {

	public ContainerGeneric()
	{}

	public ContainerGeneric(InventoryPlayer inventory, TileEntity te){

	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return true;
	}

}
