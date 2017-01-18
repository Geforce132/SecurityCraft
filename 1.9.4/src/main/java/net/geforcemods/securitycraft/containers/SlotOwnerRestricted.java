package net.geforcemods.securitycraft.containers;

import net.geforcemods.securitycraft.api.IOwnable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotOwnerRestricted extends Slot {
	
	private final IInventory inventory;
	private final IOwnable tileEntity;

	public SlotOwnerRestricted(IInventory par1iInventory, IOwnable tileEntity, int par2, int par3, int par4) {
		super(par1iInventory, par2, par3, par4);
		this.inventory = par1iInventory;
		this.tileEntity = tileEntity;
	}
	
	/**
     * Return whether this slot's stack can be taken from this slot.
     */
    @Override
	public boolean canTakeStack(EntityPlayer par1EntityPlayer){
    	return (tileEntity.getOwner().isOwner(par1EntityPlayer));
    }
    
    @Override
	public void putStack(ItemStack p_75215_1_){
        this.inventory.setInventorySlotContents(getSlotIndex(), p_75215_1_);
        this.onSlotChanged();
    }

    @Override
	public int getSlotStackLimit(){
        return 1;
    }
    
}
