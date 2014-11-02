package org.freeforums.geforce.securitycraft.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;

import org.freeforums.geforce.securitycraft.tileentity.CustomizableSCTE;

public class ContainerCustomizeBlock extends Container{
	
	private CustomizableSCTE tileEntity;

	public ContainerCustomizeBlock(InventoryPlayer inventory, CustomizableSCTE tileEntity) {
		this.tileEntity = tileEntity;
		
		for(int i = 0; i < tileEntity.getNumberOfCustomizableOptions(); i++){
			this.addSlotToContainer(new Slot(tileEntity, i, 20 + (i * 20), 20));
		}
		
	}
	
	public boolean canInteractWith(EntityPlayer par1EntityPlayer) {
		return true;
	}

}
