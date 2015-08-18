package org.freeforums.geforce.securitycraft.containers;

import org.freeforums.geforce.securitycraft.main.Utils;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityInventoryScanner;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerInventoryScanner extends Container {
	
	private final int numRows;
	private final TileEntityInventoryScanner inventoryScannerTE;
	
    public ContainerInventoryScanner(IInventory par1IInventory, TileEntityInventoryScanner par2TileEntityInventoryScanner){
        this.numRows = par2TileEntityInventoryScanner.getSizeInventory() / 9;
    	this.inventoryScannerTE = par2TileEntityInventoryScanner;
        int rows = (numRows - 4) * 18;
        
    	for(int i = 0; i < 10; i++){
            this.addSlotToContainer(new SlotRestricted(par2TileEntityInventoryScanner, i, (4 + (i * 17)), 16));
        }		
    	
    	for(int i = 0; i < 9; i++){
            for(int j = 0; j < 3; j++){
                this.addSlotToContainer(new Slot(par2TileEntityInventoryScanner, 10 + ((i * 3) + j), 177 + (j * 18), 17 + i * 18));
            }
        }
    	
    	for(int i = 0; i < 3; i++){
            for(int j = 0; j < 9; j++){
                this.addSlotToContainer(new Slot(par1IInventory, j + i * 9 + 9, 8 + j * 18, 115 + i * 18));
            }
        }

        for(int i = 0; i < 9; i++){
            this.addSlotToContainer(new Slot(par1IInventory, i, 8 + i * 18, 173));
        }
    }
    
    /**
     * Called when a player shift-clicks on a slot. You must override this or you will crash when someone does that.
     */
    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2)
    {
        ItemStack itemstack = null;
        Slot slot = (Slot)this.inventorySlots.get(par2);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (par2 < this.numRows * 9)
            {
                if (!this.mergeItemStack(itemstack1, this.numRows * 9, this.inventorySlots.size(), true))
                {
                    return null;
                }
            }
            else if (!this.mergeItemStack(itemstack1, 0, this.numRows * 9, false))
            {
                return null;
            }

            if (itemstack1.stackSize == 0)
            {
                slot.putStack((ItemStack)null);
            }
            else
            {
                slot.onSlotChanged();
            }
        }

        return itemstack;
    }
    
    /**
     * Called when the container is closed.
     */
    public void onContainerClosed(EntityPlayer par1EntityPlayer)
    {
        super.onContainerClosed(par1EntityPlayer);
        
        Utils.setISinTEAppropriately(par1EntityPlayer.worldObj, inventoryScannerTE.getPos(), ((TileEntityInventoryScanner) par1EntityPlayer.worldObj.getTileEntity(inventoryScannerTE.getPos())).getContents(), ((TileEntityInventoryScanner) par1EntityPlayer.worldObj.getTileEntity(inventoryScannerTE.getPos())).getType());
    }
	
	public boolean canInteractWith(EntityPlayer entityplayer) {
		return true;
	}
	
}
