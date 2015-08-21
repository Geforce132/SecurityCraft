package org.freeforums.geforce.securitycraft.containers;

import org.freeforums.geforce.securitycraft.tileentity.TileEntityKeypadFurnace;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerFurnace;

public class ContainerKeypadFurnace extends ContainerFurnace{

	public ContainerKeypadFurnace(InventoryPlayer player, TileEntityKeypadFurnace par2TileEntity) {
		super(player, par2TileEntity);
	}
	
	public boolean canInteractWith(EntityPlayer p_75145_1_){
        return true;
    }
    
}