package net.geforcemods.securitycraft.containers;

import net.geforcemods.securitycraft.api.IOwnable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotOwnerRestricted extends Slot {

	private final IInventory inventory;
	private final IOwnable tileEntity;

	public SlotOwnerRestricted(IInventory inv, IOwnable tileEntity, int slotIndex, int xPos, int yPos) {
		super(inv, slotIndex, xPos, yPos);
		inventory = inv;
		this.tileEntity = tileEntity;
	}

	/**
	 * Return whether this slot's stack can be taken from this slot.
	 */
	@Override
	public boolean canTakeStack(EntityPlayer player){
		return (tileEntity.getOwner().isOwner(player));
	}

	@Override
	public void putStack(ItemStack stack){
		inventory.setInventorySlotContents(getSlotIndex(), stack);
		onSlotChanged();
	}

	@Override
	public int getSlotStackLimit(){
		return 1;
	}

}
