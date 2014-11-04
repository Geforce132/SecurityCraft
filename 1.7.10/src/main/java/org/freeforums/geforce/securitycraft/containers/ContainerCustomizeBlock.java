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
		
		if(tileEntity.getNumberOfCustomizableOptions() == 1){
			this.addSlotToContainer(new Slot(tileEntity, 0, 79, 20));
		}else if(tileEntity.getNumberOfCustomizableOptions() == 2){
			this.addSlotToContainer(new Slot(tileEntity, 0, 70, 20));
			this.addSlotToContainer(new Slot(tileEntity, 1, 88, 20));
		}
		
		for(int i = 0; i < 3; i++){
            for(int j = 0; j < 9; ++j){
                this.addSlotToContainer(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for(int i = 0; i < 9; i++){
            this.addSlotToContainer(new Slot(inventory, i, 8 + i * 18, 142));
        }
	}
	
	public boolean canInteractWith(EntityPlayer par1EntityPlayer) {
		return true;
	}

}
