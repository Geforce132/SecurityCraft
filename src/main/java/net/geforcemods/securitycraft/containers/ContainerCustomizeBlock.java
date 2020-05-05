package net.geforcemods.securitycraft.containers;

import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.EnumLinkedAction;
import net.geforcemods.securitycraft.items.ItemModule;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerCustomizeBlock extends Container{

	private CustomizableSCTE tileEntity;
	private final int maxSlots;

	public ContainerCustomizeBlock(InventoryPlayer inventory, CustomizableSCTE tileEntity) {
		this.tileEntity = tileEntity;

		for(int i = 0; i < 3; i++)
			for(int j = 0; j < 9; ++j)
				addSlotToContainer(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));

		for(int i = 0; i < 9; i++)
			addSlotToContainer(new Slot(inventory, i, 8 + i * 18, 142));

		if(tileEntity.getNumberOfCustomizableOptions() == 1)
			addSlotToContainer(new ModuleSlot(tileEntity, 0, 79, 20));
		else if(tileEntity.getNumberOfCustomizableOptions() == 2){
			addSlotToContainer(new ModuleSlot(tileEntity, 0, 70, 20));
			addSlotToContainer(new ModuleSlot(tileEntity, 1, 88, 20));
		}else if(tileEntity.getNumberOfCustomizableOptions() == 3){
			addSlotToContainer(new ModuleSlot(tileEntity, 0, 61, 20));
			addSlotToContainer(new ModuleSlot(tileEntity, 1, 79, 20));
			addSlotToContainer(new ModuleSlot(tileEntity, 2, 97, 20));
		}else if(tileEntity.getNumberOfCustomizableOptions() == 4){
			addSlotToContainer(new ModuleSlot(tileEntity, 0, 52, 20));
			addSlotToContainer(new ModuleSlot(tileEntity, 1, 70, 20));
			addSlotToContainer(new ModuleSlot(tileEntity, 2, 88, 20));
			addSlotToContainer(new ModuleSlot(tileEntity, 3, 106, 20));
		}

		maxSlots = 36 + tileEntity.getNumberOfCustomizableOptions();
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int index)
	{
		ItemStack copy = ItemStack.EMPTY;
		Slot slot = inventorySlots.get(index);

		if(slot != null && slot.getHasStack())
		{
			ItemStack slotStack = slot.getStack();
			boolean isModule = slotStack.getItem() instanceof ItemModule;

			copy = slotStack.copy();

			if(index >= 36 && index <= maxSlots) //module slots
			{
				if(!mergeItemStack(slotStack, 0, 36, false)) //main inventory + hotbar
				{
					tileEntity.onModuleRemoved(slotStack, ((ItemModule)slotStack.getItem()).getModule());
					ModuleUtils.createLinkedAction(EnumLinkedAction.MODULE_REMOVED, slotStack, tileEntity);
					return ItemStack.EMPTY;
				}
			}
			else if(index >= 27 && index <= 35) //hotbar
			{
				if(isModule && !mergeItemStack(slotStack, 36, maxSlots, false)) //module slots
				{
					tileEntity.onModuleInserted(slotStack, ((ItemModule)slotStack.getItem()).getModule());
					ModuleUtils.createLinkedAction(EnumLinkedAction.MODULE_INSERTED, slotStack, tileEntity);
					return ItemStack.EMPTY;
				}
				else if(!mergeItemStack(slotStack, 0, 27, false)) //main inventory
					return ItemStack.EMPTY;
			}
			else if(index <= 26) //main inventory
			{
				if(isModule && !mergeItemStack(slotStack, 36, maxSlots, false)) //module slots
				{
					tileEntity.onModuleInserted(slotStack, ((ItemModule)slotStack.getItem()).getModule());
					ModuleUtils.createLinkedAction(EnumLinkedAction.MODULE_INSERTED, slotStack, tileEntity);
					return ItemStack.EMPTY;
				}
				else if(!mergeItemStack(slotStack, 27, 36, false)) //hotbar
					return ItemStack.EMPTY;
			}

			if(slotStack.isEmpty())
				slot.putStack(ItemStack.EMPTY);
			else
				slot.onSlotChanged();
		}

		return copy;
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return true;
	}


	public static class ModuleSlot extends Slot{
		private CustomizableSCTE tileEntity;
		public ModuleSlot(CustomizableSCTE inventory, int index, int xPos, int yPos) {
			super(inventory, index, xPos, yPos);
			tileEntity = inventory;
		}

		/**
		 * Check if the stack is a valid item for this slot. Always true beside for the armor slots.
		 */
		@Override
		public boolean isItemValid(ItemStack stack)
		{
			return !stack.isEmpty() && stack.getItem() instanceof ItemModule && tileEntity.getAcceptedModules().contains(((ItemModule) stack.getItem()).getModule()) && !tileEntity.hasModule(((ItemModule) stack.getItem()).getModule());
		}

		@Override
		public ItemStack getStack(){
			return tileEntity.modules.get(getSlotIndex());
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
		public ItemStack decrStackSize(int index)
		{
			return tileEntity.safeDecrStackSize(getSlotIndex(), index);
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
