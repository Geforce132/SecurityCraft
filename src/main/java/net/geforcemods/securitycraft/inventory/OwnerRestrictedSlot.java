package net.geforcemods.securitycraft.inventory;

import net.geforcemods.securitycraft.api.IOwnable;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class OwnerRestrictedSlot extends Slot {

	private final IOwnable tileEntity;
	private final boolean isGhostSlot;

	public OwnerRestrictedSlot(Container inventory, IOwnable tileEntity, int index, int xPos, int yPos, boolean ghostSlot) {
		super(inventory, index, xPos, yPos);
		this.tileEntity = tileEntity;
		isGhostSlot = ghostSlot;
	}

	/**
	 * Return whether this slot's stack can be taken from this slot.
	 */
	@Override
	public boolean mayPickup(Player player){
		return tileEntity.getOwner().isOwner(player) && !isGhostSlot; //the !isGhostSlot check helps to prevent double clicking a stack to pull all items towards the stack
	}

	@Override
	public boolean mayPlace(ItemStack stack)
	{
		return !isGhostSlot; //prevents shift clicking into ghost slot
	}

	@Override
	public void set(ItemStack stack)
	{
		if(mayPlace(stack))
		{
			container.setItem(getSlotIndex(), stack);
			setChanged();
		}
	}

	@Override
	public int getMaxStackSize(){
		return 1;
	}

	public boolean isGhostSlot()
	{
		return isGhostSlot;
	}
}
