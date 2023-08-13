package net.geforcemods.securitycraft.inventory;

import net.geforcemods.securitycraft.SCContent;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;

public class DisguiseModuleMenu extends StateSelectorAccessMenu {
	private ModuleItemContainer inventory;

	public DisguiseModuleMenu(InventoryPlayer playerInventory, ModuleItemContainer moduleInventory) {
		inventory = moduleInventory;
		moduleInventory.setContainer(this);
		addSlotToContainer(new AddonSlot(inventory, 0, 79, 20));

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				addSlotToContainer(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}

		for (int i = 0; i < 9; i++) {
			addSlotToContainer(new Slot(playerInventory, i, 8 + i * 18, 142));
		}
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int index) {
		ItemStack slotStackCopy = ItemStack.EMPTY;
		Slot slot = inventorySlots.get(index);

		if (slot != null && slot.getHasStack()) {
			ItemStack slotStack = slot.getStack();
			slotStackCopy = slotStack.copy();

			if (index < 1) {
				if (!mergeItemStack(slotStack, 1, 37, true))
					return ItemStack.EMPTY;

				slot.onSlotChange(slotStack, slotStackCopy);
			}
			else if (!mergeItemStack(slotStack, 0, 1, false))
				return ItemStack.EMPTY;

			if (slotStack.getCount() == 0)
				slot.putStack(ItemStack.EMPTY);
			else
				slot.onSlotChanged();

			if (slotStack.getCount() == slotStackCopy.getCount())
				return ItemStack.EMPTY;

			slot.onTake(player, slotStack);
			detectAndSendChanges();
		}

		return slotStackCopy;
	}

	@Override
	public ItemStack slotClick(int slot, int dragType, ClickType clickType, EntityPlayer player) {
		if (slot >= 0 && getSlot(slot) != null && getSlot(slot).getStack().getItem() == SCContent.disguiseModule)
			return ItemStack.EMPTY;

		return super.slotClick(slot, dragType, clickType, player);
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return true;
	}

	@Override
	public ItemStack getStateStack() {
		return inventorySlots.get(0).getStack();
	}

	@Override
	public IBlockState getSavedState() {
		ItemStack stack = inventory.getModule();

		if (!stack.hasTagCompound())
			stack.setTagCompound(new NBTTagCompound());

		return NBTUtil.readBlockState(inventory.getModule().getTagCompound().getCompoundTag("SavedState"));
	}

	public ModuleItemContainer getModuleInventory() {
		return inventory;
	}

	public static class AddonSlot extends Slot {
		public AddonSlot(ModuleItemContainer inventory, int index, int xPos, int yPos) {
			super(inventory, index, xPos, yPos);
		}

		@Override
		public boolean isItemValid(ItemStack itemStack) {
			return itemStack.getItem() instanceof ItemBlock;
		}

		@Override
		public int getSlotStackLimit() {
			return 1;
		}
	}
}
