package net.geforcemods.securitycraft.containers;

import net.geforcemods.securitycraft.main.Utils.BlockUtils;
import net.geforcemods.securitycraft.tileentity.TileEntityInventoryScanner;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotRestricted extends Slot {
	
	private final TileEntityInventoryScanner inventoryScannerTE;

	public SlotRestricted(TileEntityInventoryScanner par1iInventory, int par2, int par3, int par4) {
		super(par1iInventory, par2, par3, par4);
		this.inventoryScannerTE = par1iInventory;
	}
	
	/**
     * Return whether this slot's stack can be taken from this slot.
     */
    public boolean canTakeStack(EntityPlayer par1EntityPlayer){
    	return (BlockUtils.isOwnerOfBlock(inventoryScannerTE, par1EntityPlayer));
    }
    
    public void putStack(ItemStack p_75215_1_){
        this.inventoryScannerTE.setInventorySlotContents(getSlotIndex(), p_75215_1_);
        this.onSlotChanged();
    }

    public int getSlotStackLimit(){
        return 1;
    }
    
}
