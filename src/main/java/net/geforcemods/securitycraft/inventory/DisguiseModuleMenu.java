package net.geforcemods.securitycraft.inventory;

import net.geforcemods.securitycraft.SCContent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;

public class DisguiseModuleMenu extends AbstractContainerMenu {

	private ModuleItemContainer inventory;

	public DisguiseModuleMenu(int windowId, Inventory playerInventory, ModuleItemContainer moduleInventory) {
		super(SCContent.mTypeDisguiseModule, windowId);
		inventory = moduleInventory;
		addSlot(new AddonSlot(inventory, 0, 79, 20));

		for(int i = 0; i < 3; i++)
			for(int j = 0; j < 9; j++)
				addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));

		for(int i = 0; i < 9; i++)
			addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
	}

	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		ItemStack slotStackCopy = ItemStack.EMPTY;
		Slot slot = slots.get(index);

		if(slot != null && slot.hasItem()) {
			ItemStack slotStack = slot.getItem();
			slotStackCopy = slotStack.copy();

			if(index < inventory.size) {
				if(!moveItemStackTo(slotStack, inventory.size, 37, true))
					return ItemStack.EMPTY;

				slot.onQuickCraft(slotStack, slotStackCopy);
			}
			else if(index >= inventory.size)
				if(!moveItemStackTo(slotStack, 0, inventory.size, false))
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
	public void clicked(int slot, int dragType, ClickType clickType, Player player)
	{
		if(slot >= 0 && getSlot(slot) != null && ((!player.getMainHandItem().isEmpty() && getSlot(slot).getItem() == player.getMainHandItem() && player.getMainHandItem().getItem() == SCContent.DISGUISE_MODULE.get())))
			return;
		else
			super.clicked(slot, dragType, clickType, player);
	}

	@Override
	public boolean stillValid(Player player) {
		return true;
	}

	public static class AddonSlot extends Slot {

		public AddonSlot(ModuleItemContainer inventory, int index, int xPos, int yPos) {
			super(inventory, index, xPos, yPos);
		}

		@Override
		public boolean mayPlace(ItemStack itemStack) {
			return itemStack.getItem() instanceof BlockItem;
		}

		@Override
		public int getMaxStackSize() {
			return 1;
		}
	}

}
