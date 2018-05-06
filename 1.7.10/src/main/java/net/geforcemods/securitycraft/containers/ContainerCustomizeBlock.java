package net.geforcemods.securitycraft.containers;

import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.EnumLinkedAction;
import net.geforcemods.securitycraft.items.ItemModule;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerCustomizeBlock extends Container{

	private CustomizableSCTE tileEntity;

	public ContainerCustomizeBlock(InventoryPlayer inventory, CustomizableSCTE tileEntity) {
		this.tileEntity = tileEntity;

		if(tileEntity.getNumberOfCustomizableOptions() == 1)
			addSlotToContainer(new ModuleSlot(tileEntity, 0, 79, 20));
		else if(tileEntity.getNumberOfCustomizableOptions() == 2){
			addSlotToContainer(new ModuleSlot(tileEntity, 0, 70, 20));
			addSlotToContainer(new ModuleSlot(tileEntity, 1, 88, 20));
		}else if(tileEntity.getNumberOfCustomizableOptions() == 3){
			addSlotToContainer(new ModuleSlot(tileEntity, 0, 61, 20));
			addSlotToContainer(new ModuleSlot(tileEntity, 1, 79, 20));
			addSlotToContainer(new ModuleSlot(tileEntity, 2, 97, 20));
		}

		for(int i = 0; i < 3; i++)
			for(int j = 0; j < 9; ++j)
				addSlotToContainer(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));

		for(int i = 0; i < 9; i++)
			addSlotToContainer(new Slot(inventory, i, 8 + i * 18, 142));
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int index)
	{
		ItemStack itemstack = null;
		Slot slot = (Slot)inventorySlots.get(index);

		if (slot != null && slot.getHasStack())
		{
			ItemStack itemstack1 = slot.getStack();

			if(!(itemstack1.getItem() instanceof ItemModule))
				return null;

			itemstack = itemstack1.copy();

			if (index < tileEntity.getSizeInventory())
			{
				if (!mergeItemStack(itemstack1, 0, 35, true))
					return null;
				else{
					tileEntity.onModuleRemoved(itemstack1, EnumCustomModules.getModuleFromStack(itemstack1));
					tileEntity.createLinkedBlockAction(EnumLinkedAction.MODULE_REMOVED, new Object[]{ itemstack1, EnumCustomModules.getModuleFromStack(itemstack1) }, tileEntity);
				}
			}
			else if (itemstack1.getItem() != null && itemstack1.getItem() instanceof ItemModule && tileEntity.getAcceptedModules().contains(EnumCustomModules.getModuleFromStack(itemstack1)) && !mergeItemStack(itemstack1, 0, tileEntity.getSizeInventory(), false))
				return null;

			if (itemstack1.stackSize == 0)
				slot.putStack((ItemStack)null);
			else
				slot.onSlotChanged();

			if(itemstack1.stackSize == itemstack.stackSize)
				return null;

			slot.onPickupFromSlot(player, itemstack1);
		}

		return itemstack;
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return true;
	}


	public static class ModuleSlot extends Slot{
		private CustomizableSCTE tileEntity;
		public ModuleSlot(CustomizableSCTE te, int slotIndex, int xPos, int yPos) {
			super(te, slotIndex, xPos, yPos);
			tileEntity = te;
		}

		/**
		 * Check if the stack is a valid item for this slot. Always true beside for the armor slots.
		 */
		@Override
		public boolean isItemValid(ItemStack stack)
		{
			if(stack != null && stack.getItem() instanceof ItemModule && tileEntity.getAcceptedModules().contains(((ItemModule) stack.getItem()).getModule()) && !tileEntity.hasModule(((ItemModule) stack.getItem()).getModule()))
				return true;
			else
				return false;
		}

		@Override
		public ItemStack getStack(){
			return tileEntity.itemStacks[getSlotIndex()];
		}

		@Override
		public void putStack(ItemStack stack)
		{
			tileEntity.safeSetInventorySlotContents(getSlotIndex(), stack);
			onSlotChanged();
		}

		/**
		 * Decrease the size of the stack in slot (first int arg) by the amount of the second int arg. Returns the new
		 * stack.
		 */
		@Override
		public ItemStack decrStackSize(int amount)
		{
			return tileEntity.safeDecrStackSize(getSlotIndex(), amount);
		}

		/**
		 * Returns the maximum stack size for a given slot (usually the same as getInventoryStackLimit(), but 1 in the
		 * case of armor slots)
		 */
		@Override
		public int getSlotStackLimit()
		{
			return 1;
		}
	}

}
