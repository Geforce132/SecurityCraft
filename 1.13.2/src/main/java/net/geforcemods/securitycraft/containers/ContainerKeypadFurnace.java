package net.geforcemods.securitycraft.containers;

import net.geforcemods.securitycraft.tileentity.TileEntityKeypadFurnace;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerFurnace;

public class ContainerKeypadFurnace extends ContainerFurnace{

	public ContainerKeypadFurnace(InventoryPlayer player, TileEntityKeypadFurnace te) {
		super(player, te);
	}

	@Override
	public boolean canInteractWith(EntityPlayer player){
		return true;
	}

}