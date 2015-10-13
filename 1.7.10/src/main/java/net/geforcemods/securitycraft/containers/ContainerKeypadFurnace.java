package net.geforcemods.securitycraft.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerFurnace;
import net.minecraft.tileentity.TileEntityFurnace;

public class ContainerKeypadFurnace extends ContainerFurnace{

	public ContainerKeypadFurnace(InventoryPlayer player, TileEntityFurnace par2TileEntity) {
		super(player, par2TileEntity);
	}
	
	public boolean canInteractWith(EntityPlayer p_75145_1_){
        return true;
    }
    
}