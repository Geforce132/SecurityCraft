package net.geforcemods.securitycraft.containers;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.inventory.BriefcaseInventory;
import net.geforcemods.securitycraft.items.BriefcaseItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

public class BriefcaseContainer extends Container {

	public BriefcaseContainer(int windowId, PlayerInventory playerInventory, BriefcaseInventory briefcaseInventory) {
		super(SCContent.cTypeBriefcaseInventory, windowId);

		for(int i = 0; i < 3; i++)
			for(int j = 0; j < 4; j++)
				addSlot(new ItemRestrictedSlot(briefcaseInventory, j + (i * 4), 53 + (j * 18), 17 + (i * 18), SCContent.BRIEFCASE.get()));

		for(int i = 0; i < 3; i++)
			for(int j = 0; j < 9; j++)
				addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));

		for(int i = 0; i < 9; i++)
			addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
	}

	@Override
	public ItemStack quickMoveStack(PlayerEntity player, int index) {
		ItemStack slotStackCopy = ItemStack.EMPTY;
		Slot slot = slots.get(index);

		if(slot != null && slot.hasItem()) {
			ItemStack slotStack = slot.getItem();
			slotStackCopy = slotStack.copy();

			if(index < BriefcaseInventory.SIZE) {
				if(!moveItemStackTo(slotStack, BriefcaseInventory.SIZE, 48, true))
					return ItemStack.EMPTY;

				slot.onQuickCraft(slotStack, slotStackCopy);
			}
			else if(index >= BriefcaseInventory.SIZE)
				if(!moveItemStackTo(slotStack, 0, BriefcaseInventory.SIZE, false))
					return ItemStack.EMPTY;

			if(slotStack.getCount() == 0)
				slot.set(ItemStack.EMPTY);
			else
				slot.setChanged();

			if(slotStack.getCount() == slotStackCopy.getCount())
				return ItemStack.EMPTY;

			slot.onTake(player, slotStack);
		}

		return slotStackCopy;
	}

	@Override
	public ItemStack clicked(int slot, int dragType, ClickType clickType, PlayerEntity player) {
		if(slot >= 0 && getSlot(slot) != null && (!player.getMainHandItem().isEmpty() && getSlot(slot).getItem() == player.getMainHandItem() && player.getMainHandItem().getItem() instanceof BriefcaseItem))
			return ItemStack.EMPTY;

		return super.clicked(slot, dragType, clickType, player);
	}

	@Override
	public boolean stillValid(PlayerEntity player) {
		return true;
	}

}
