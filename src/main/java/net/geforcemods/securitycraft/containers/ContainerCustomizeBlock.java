package net.geforcemods.securitycraft.containers;

import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.EnumLinkedAction;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.items.ItemModule;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerCustomizeBlock extends Container{

	private IModuleInventory moduleInv;
	private final int maxSlots;

	public ContainerCustomizeBlock(InventoryPlayer inventory, IModuleInventory tileEntity) {
		this.moduleInv = tileEntity;

		int slotId = 0;

		for(int i = 0; i < 3; i++)
			for(int j = 0; j < 9; ++j)
				addSlotToContainer(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));

		for(int i = 0; i < 9; i++)
			addSlotToContainer(new Slot(inventory, i, 8 + i * 18, 142));

		if(moduleInv.enableHack())
			slotId = 100;

		if(moduleInv.getMaxNumberOfModules() == 1)
			addSlotToContainer(new CustomSlotItemHandler(moduleInv, slotId, 79, 20));
		else if(moduleInv.getMaxNumberOfModules() == 2){
			addSlotToContainer(new CustomSlotItemHandler(moduleInv, slotId++, 70, 20));
			addSlotToContainer(new CustomSlotItemHandler(moduleInv, slotId++, 88, 20));
		}else if(moduleInv.getMaxNumberOfModules() == 3){
			addSlotToContainer(new CustomSlotItemHandler(moduleInv, slotId++, 61, 20));
			addSlotToContainer(new CustomSlotItemHandler(moduleInv, slotId++, 79, 20));
			addSlotToContainer(new CustomSlotItemHandler(moduleInv, slotId++, 97, 20));
		}else if(moduleInv.getMaxNumberOfModules() == 4){
			addSlotToContainer(new CustomSlotItemHandler(moduleInv, slotId++, 52, 20));
			addSlotToContainer(new CustomSlotItemHandler(moduleInv, slotId++, 70, 20));
			addSlotToContainer(new CustomSlotItemHandler(moduleInv, slotId++, 88, 20));
			addSlotToContainer(new CustomSlotItemHandler(moduleInv, slotId++, 106, 20));
		}else if(moduleInv.getMaxNumberOfModules() == 5){
			addSlotToContainer(new CustomSlotItemHandler(moduleInv, slotId++, 34, 20));
			addSlotToContainer(new CustomSlotItemHandler(moduleInv, slotId++, 52, 20));
			addSlotToContainer(new CustomSlotItemHandler(moduleInv, slotId++, 70, 20));
			addSlotToContainer(new CustomSlotItemHandler(moduleInv, slotId++, 88, 20));
			addSlotToContainer(new CustomSlotItemHandler(moduleInv, slotId++, 106, 20));
		}

		maxSlots = 36 + moduleInv.getMaxNumberOfModules();
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
				if(!mergeItemStack(slotStack, 0, 36, true)) //main inventory + hotbar
					return ItemStack.EMPTY;
			}
			else if(index >= 27 && index <= 35) //hotbar
			{
				if(isModule && !mergeItemStack(slotStack, 36, maxSlots, false)) //module slots
					return ItemStack.EMPTY;
				else if(!mergeItemStack(slotStack, 0, 27, false)) //main inventory
					return ItemStack.EMPTY;
			}
			else if(index <= 26) //main inventory
			{
				if(isModule && !mergeItemStack(slotStack, 36, maxSlots, false)) //module slots
					return ItemStack.EMPTY;
				else if(!mergeItemStack(slotStack, 27, 36, false)) //hotbar
					return ItemStack.EMPTY;
			}

			slot.onSlotChange(slotStack, copy);

			if(slotStack.isEmpty())
				slot.putStack(ItemStack.EMPTY);
			else
				slot.onSlotChanged();
		}

		return copy;
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		TileEntity te = moduleInv.getTileEntity();

		return BlockUtils.isWithinUsableDistance(te.getWorld(), te.getPos(), player, te.getBlockType());
	}

	private class CustomSlotItemHandler extends SlotItemHandler
	{
		public CustomSlotItemHandler(IItemHandler itemHandler, int index, int xPosition, int yPosition)
		{
			super(itemHandler, index, xPosition, yPosition);
		}

		@Override
		public void onSlotChange(ItemStack newStack, ItemStack oldStack)
		{
			if((slotNumber >= 36 || slotNumber < maxSlots) && oldStack.getItem() instanceof ItemModule)
			{
				moduleInv.onModuleRemoved(oldStack, ((ItemModule)oldStack.getItem()).getModuleType());

				if(moduleInv instanceof CustomizableSCTE)
					ModuleUtils.createLinkedAction(EnumLinkedAction.MODULE_REMOVED, oldStack, (CustomizableSCTE)moduleInv);
			}
		}
	}
}
