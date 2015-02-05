package org.freeforums.geforce.securitycraft.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import org.freeforums.geforce.securitycraft.tileentity.TileEntityInventoryScanner;

public class SlotRestricted extends Slot {
	
	private final TileEntityInventoryScanner inventoryScannerTE;

	public SlotRestricted(TileEntityInventoryScanner par1iInventory, int par2, int par3, int par4) {
		super(par1iInventory, par2, par3, par4);
		this.inventoryScannerTE = par1iInventory;
	}
	
	/**
     * Return whether this slot's stack can be taken from this slot.
     */
    public boolean canTakeStack(EntityPlayer par1EntityPlayer)
    {
    	return (inventoryScannerTE.getOwnerUUID() != null && inventoryScannerTE.getOwnerUUID().matches(par1EntityPlayer.getGameProfile().getId().toString()));
    }
    
    public void putStack(ItemStack p_75215_1_)
    {
        this.inventoryScannerTE.setInventorySlotContents(getSlotIndex(), p_75215_1_);
        this.onSlotChanged();
    }

}
