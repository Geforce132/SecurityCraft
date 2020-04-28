package net.geforcemods.securitycraft.containers;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableTileEntity;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.LinkedAction;
import net.geforcemods.securitycraft.blocks.SecurityCameraBlock;
import net.geforcemods.securitycraft.items.ModuleItem;
import net.geforcemods.securitycraft.misc.CustomModules;
import net.geforcemods.securitycraft.tileentity.SecurityCameraTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CustomizeBlockContainer extends Container{

	public IModuleInventory moduleInv;

	public CustomizeBlockContainer(int windowId, World world, BlockPos pos, PlayerInventory inventory) {
		super(SCContent.cTypeCustomizeBlock, windowId);
		this.moduleInv = (IModuleInventory)world.getTileEntity(pos);

		if(moduleInv.getMaxNumberOfModules() == 1)
			addSlot(new ModuleSlot(moduleInv, 0, 79, 20));
		else if(moduleInv.getMaxNumberOfModules() == 2){
			addSlot(new ModuleSlot(moduleInv, 0, 70, 20));
			addSlot(new ModuleSlot(moduleInv, 1, 88, 20));
		}else if(moduleInv.getMaxNumberOfModules() == 3){
			addSlot(new ModuleSlot(moduleInv, 0, 61, 20));
			addSlot(new ModuleSlot(moduleInv, 1, 79, 20));
			addSlot(new ModuleSlot(moduleInv, 2, 97, 20));
		}else if(moduleInv.getMaxNumberOfModules() == 4){
			addSlot(new ModuleSlot(moduleInv, 0, 52, 20));
			addSlot(new ModuleSlot(moduleInv, 1, 70, 20));
			addSlot(new ModuleSlot(moduleInv, 2, 88, 20));
			addSlot(new ModuleSlot(moduleInv, 3, 106, 20));
		}

		for(int i = 0; i < 3; i++)
			for(int j = 0; j < 9; ++j)
				addSlot(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));

		for(int i = 0; i < 9; i++)
			addSlot(new Slot(inventory, i, 8 + i * 18, 142));
	}

	@Override
	public ItemStack transferStackInSlot(PlayerEntity player, int index)
	{
		ItemStack slotStackCopy = ItemStack.EMPTY;
		Slot slot = inventorySlots.get(index);

		if (slot != null && slot.getHasStack())
		{
			ItemStack slotStack = slot.getStack();

			if(!(slotStack.getItem() instanceof ModuleItem))
				return ItemStack.EMPTY;

			slotStackCopy = slotStack.copy();
			moduleInv.onModuleRemoved(slotStack, CustomModules.getModuleFromStack(slotStack));

			if(moduleInv instanceof CustomizableTileEntity)
				((CustomizableTileEntity)moduleInv).createLinkedBlockAction(LinkedAction.MODULE_REMOVED, new Object[]{ slotStack, CustomModules.getModuleFromStack(slotStack) }, (CustomizableTileEntity)moduleInv);

			if(moduleInv instanceof SecurityCameraTileEntity)
			{
				SecurityCameraTileEntity cam = (SecurityCameraTileEntity)moduleInv;

				cam.getWorld().notifyNeighborsOfStateChange(cam.getPos().offset(cam.getWorld().getBlockState(cam.getPos()).get(SecurityCameraBlock.FACING), -1), cam.getWorld().getBlockState(cam.getPos()).getBlock());
			}

			if (index < moduleInv.getSizeInventory())
			{
				if (!mergeItemStack(slotStack, 0, 35, true))
					return ItemStack.EMPTY;
			}
			else if (slotStack.getItem() instanceof ModuleItem && moduleInv.getAcceptedModules().contains(CustomModules.getModuleFromStack(slotStack)) && !mergeItemStack(slotStack, 0, moduleInv.getSizeInventory(), false))
				return ItemStack.EMPTY;

			if (slotStack.getCount() == 0)
				slot.putStack(ItemStack.EMPTY);
			else
				slot.onSlotChanged();

			if(slotStack.getCount() == slotStackCopy.getCount())
				return ItemStack.EMPTY;

			slot.onTake(player, slotStack);
		}

		return slotStackCopy;
	}

	@Override
	public boolean canInteractWith(PlayerEntity player) {
		return true;
	}

	public static class ModuleSlot extends Slot{
		private IModuleInventory tileEntity;

		public ModuleSlot(IModuleInventory inventory, int index, int xPos, int yPos) {
			super(inventory, index, xPos, yPos);
			tileEntity = inventory;
		}

		/**
		 * Check if the stack is a valid item for this slot. Always true beside for the armor slots.
		 */
		@Override
		public boolean isItemValid(ItemStack stack)
		{
			return !stack.isEmpty() && stack.getItem() instanceof ModuleItem && tileEntity.getAcceptedModules().contains(((ModuleItem) stack.getItem()).getModule()) && !tileEntity.hasModule(((ModuleItem) stack.getItem()).getModule());
		}

		@Override
		public ItemStack getStack(){
			return tileEntity.getInventory().get(getSlotIndex());
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
