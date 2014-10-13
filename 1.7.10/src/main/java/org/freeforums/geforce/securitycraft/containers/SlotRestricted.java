package org.freeforums.geforce.securitycraft.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;

import org.freeforums.geforce.securitycraft.tileentity.TileEntityInventoryScanner;

public class SlotRestricted extends Slot {
	
	private final TileEntityInventoryScanner inventoryScannerTE;

	public SlotRestricted(TileEntityInventoryScanner par1TileEntityInventoryScanner, int par2, int par3, int par4) {
		super(par1TileEntityInventoryScanner, par2, par3, par4);
		this.inventoryScannerTE = par1TileEntityInventoryScanner;
	}
	
	/**
     * Return whether this slot's stack can be taken from this slot.
     */
    public boolean canTakeStack(EntityPlayer par1EntityPlayer)
    {
        return (inventoryScannerTE.getOwner() != null && inventoryScannerTE.getOwner().matches(par1EntityPlayer.getCommandSenderName()));
    }

}
