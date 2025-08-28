package net.geforcemods.securitycraft.inventory;

import net.geforcemods.securitycraft.api.IOwnable;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class OwnerRestrictedSlot extends Slot {
	private final AbstractContainerMenu menu;
	private final IOwnable ownable;
	private final boolean isHighlightable;
	private final boolean isGhostSlot;

	public OwnerRestrictedSlot(AbstractContainerMenu menu, Container inventory, IOwnable ownable, int index, int xPos, int yPos, boolean highlightable, boolean ghostSlot) {
		super(inventory, index, xPos, yPos);
		this.menu = menu;
		this.ownable = ownable;
		isHighlightable = highlightable;
		isGhostSlot = ghostSlot;
	}

	@Override
	public boolean mayPickup(Player player) {
		return ownable.isOwnedBy(player) && !isGhostSlot; //the !isGhostSlot check helps to prevent double clicking a stack to pull all items towards the stack
	}

	@Override
	public boolean mayPlace(ItemStack stack) {
		return !isGhostSlot; //prevents shift clicking into ghost slot
	}

	@Override
	public void set(ItemStack stack) {
		if (mayPlace(stack)) {
			container.setItem(getSlotIndex(), stack);
			setChanged();
			menu.slotsChanged(container);
		}
	}

	@Override
	public boolean isHighlightable() {
		return isHighlightable;
	}

	public boolean isGhostSlot() {
		return isGhostSlot;
	}
}
