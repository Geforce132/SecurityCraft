package net.geforcemods.securitycraft.containers;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.inventory.ModuleItemInventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;

public class DisguiseModuleContainer extends AbstractContainerMenu {

	private ModuleItemInventory inventory;

	public DisguiseModuleContainer(int windowId, Inventory playerInventory, ModuleItemInventory moduleInventory) {
		super(SCContent.cTypeDisguiseModule, windowId);
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

			if(index < inventory.SIZE) {
				if(!moveItemStackTo(slotStack, inventory.SIZE, 37, true))
					return ItemStack.EMPTY;

				slot.onQuickCraft(slotStack, slotStackCopy);
			}
			else if(index >= inventory.SIZE)
				if(!moveItemStackTo(slotStack, 0, inventory.SIZE, false))
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
	public ItemStack clicked(int slot, int dragType, ClickType clickType, Player player)
	{
		if(slot >= 0 && getSlot(slot) != null && ((!player.getMainHandItem().isEmpty() && getSlot(slot).getItem() == player.getMainHandItem() && player.getMainHandItem().getItem() == SCContent.DISGUISE_MODULE.get())))
			return ItemStack.EMPTY;

		return super.clicked(slot, dragType, clickType, player);
	}

	@Override
	public boolean stillValid(Player player) {
		return true;
	}


	public static class AddonSlot extends Slot {

		private ModuleItemInventory inventory;

		public AddonSlot(ModuleItemInventory inventory, int index, int xPos, int yPos) {
			super(inventory, index, xPos, yPos);
			this.inventory = inventory;
		}

		@Override
		public boolean mayPlace(ItemStack itemStack) {
			int numberOfItems = 0;
			int numberOfBlocks = 0;
			boolean isStackBlock = itemStack.getItem() instanceof BlockItem;

			for(ItemStack stack : inventory.moduleInventory)
				if(!stack.isEmpty())
					if(stack.getItem() instanceof BlockItem)
						numberOfBlocks++;
					else if(stack.getItem() != null)
						numberOfItems++;

			return (isStackBlock && numberOfBlocks < inventory.maxNumberOfBlocks) || (!isStackBlock && numberOfItems < inventory.maxNumberOfItems);
		}

		@Override
		public int getMaxStackSize() {
			return 1;
		}
	}

}
