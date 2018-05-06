package net.geforcemods.securitycraft.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerFurnace;
import net.minecraft.tileentity.TileEntityFurnace;

public class ContainerKeypadFurnace extends ContainerFurnace{

	public ContainerKeypadFurnace(InventoryPlayer player, TileEntityFurnace te) {
		super(player, te);
	}

	@Override
	public boolean canInteractWith(EntityPlayer player){
		return true;
	}

}