package net.geforcemods.securitycraft.inventory;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableBlockEntity;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.LinkedAction;
import net.geforcemods.securitycraft.items.ModuleItem;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class CustomizeBlockMenu extends AbstractContainerMenu{

	public IModuleInventory moduleInv;
	private ContainerLevelAccess worldPosCallable;
	private final int maxSlots;

	public CustomizeBlockMenu(int windowId, Level world, BlockPos pos, Inventory inventory) {
		super(SCContent.mTypeCustomizeBlock, windowId);
		this.moduleInv = (IModuleInventory)world.getBlockEntity(pos);
		worldPosCallable = ContainerLevelAccess.create(world, pos);

		int slotId = 0;

		for(int i = 0; i < 3; i++)
			for(int j = 0; j < 9; ++j)
				addSlot(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));

		for(int i = 0; i < 9; i++)
			addSlot(new Slot(inventory, i, 8 + i * 18, 142));

		if(moduleInv.enableHack())
			slotId = 100;

		if(moduleInv.getMaxNumberOfModules() == 1)
			addSlot(new CustomSlotItemHandler(moduleInv, slotId, 79, 20));
		else if(moduleInv.getMaxNumberOfModules() == 2){
			addSlot(new CustomSlotItemHandler(moduleInv, slotId++, 70, 20));
			addSlot(new CustomSlotItemHandler(moduleInv, slotId++, 88, 20));
		}else if(moduleInv.getMaxNumberOfModules() == 3){
			addSlot(new CustomSlotItemHandler(moduleInv, slotId++, 61, 20));
			addSlot(new CustomSlotItemHandler(moduleInv, slotId++, 79, 20));
			addSlot(new CustomSlotItemHandler(moduleInv, slotId++, 97, 20));
		}else if(moduleInv.getMaxNumberOfModules() == 4){
			addSlot(new CustomSlotItemHandler(moduleInv, slotId++, 52, 20));
			addSlot(new CustomSlotItemHandler(moduleInv, slotId++, 70, 20));
			addSlot(new CustomSlotItemHandler(moduleInv, slotId++, 88, 20));
			addSlot(new CustomSlotItemHandler(moduleInv, slotId++, 106, 20));
		}else if(moduleInv.getMaxNumberOfModules() == 5){
			addSlot(new CustomSlotItemHandler(moduleInv, slotId++, 34, 20));
			addSlot(new CustomSlotItemHandler(moduleInv, slotId++, 52, 20));
			addSlot(new CustomSlotItemHandler(moduleInv, slotId++, 70, 20));
			addSlot(new CustomSlotItemHandler(moduleInv, slotId++, 88, 20));
			addSlot(new CustomSlotItemHandler(moduleInv, slotId++, 106, 20));
		}

		maxSlots = 36 + moduleInv.getMaxNumberOfModules();
	}

	@Override
	public ItemStack quickMoveStack(Player player, int index)
	{
		ItemStack copy = ItemStack.EMPTY;
		Slot slot = slots.get(index);

		if(slot != null && slot.hasItem())
		{
			ItemStack slotStack = slot.getItem();
			boolean isModule = slotStack.getItem() instanceof ModuleItem;

			copy = slotStack.copy();

			if(index >= 36 && index <= maxSlots) //module slots
			{
				if(!moveItemStackTo(slotStack, 0, 36, true)) //main inventory + hotbar
					return ItemStack.EMPTY;
			}
			else if(index >= 27 && index <= 35) //hotbar
			{
				if(isModule && !moveItemStackTo(slotStack, 36, maxSlots, false)) //module slots
					return ItemStack.EMPTY;
				else if(!moveItemStackTo(slotStack, 0, 27, false)) //main inventory
					return ItemStack.EMPTY;
			}
			else if(index <= 26) //main inventory
			{
				if(isModule && !moveItemStackTo(slotStack, 36, maxSlots, false)) //module slots
					return ItemStack.EMPTY;
				else if(!moveItemStackTo(slotStack, 27, 36, false)) //hotbar
					return ItemStack.EMPTY;
			}

			slot.onQuickCraft(slotStack, copy);

			if(slotStack.isEmpty())
				slot.set(ItemStack.EMPTY);
			else
				slot.setChanged();
		}

		return copy;
	}

	@Override
	public boolean stillValid(Player player) {
		return stillValid(worldPosCallable, player, moduleInv.getTileEntity().getBlockState().getBlock());
	}

	private class CustomSlotItemHandler extends SlotItemHandler
	{
		private final int index;

		public CustomSlotItemHandler(IItemHandler itemHandler, int index, int xPosition, int yPosition)
		{
			super(itemHandler, index, xPosition, yPosition);
			this.index = index;
		}

		@Override
		public void onQuickCraft(ItemStack newStack, ItemStack oldStack)
		{
			if((index >= 36 || index < maxSlots) && oldStack.getItem() instanceof ModuleItem)
			{
				moduleInv.onModuleRemoved(oldStack, ((ModuleItem)oldStack.getItem()).getModuleType());

				if(moduleInv instanceof CustomizableBlockEntity)
					ModuleUtils.createLinkedAction(LinkedAction.MODULE_REMOVED, oldStack, (CustomizableBlockEntity)moduleInv);
			}
		}
	}
}
