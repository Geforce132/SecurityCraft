package net.geforcemods.securitycraft.containers;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableTileEntity;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.LinkedAction;
import net.geforcemods.securitycraft.items.ModuleItem;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class CustomizeBlockContainer extends Container{

	public IModuleInventory moduleInv;
	private IWorldPosCallable worldPosCallable;
	private final int maxSlots;

	public CustomizeBlockContainer(int windowId, World world, BlockPos pos, PlayerInventory inventory) {
		super(SCContent.cTypeCustomizeBlock, windowId);
		this.moduleInv = (IModuleInventory)world.getBlockEntity(pos);
		worldPosCallable = IWorldPosCallable.create(world, pos);

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
	public ItemStack quickMoveStack(PlayerEntity player, int index)
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
	public boolean stillValid(PlayerEntity player) {
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

				if(moduleInv instanceof CustomizableTileEntity)
					ModuleUtils.createLinkedAction(LinkedAction.MODULE_REMOVED, oldStack, (CustomizableTileEntity)moduleInv);
			}
		}
	}
}
