package net.geforcemods.securitycraft.containers;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableTileEntity;
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

	public CustomizableTileEntity tileEntity;

	public CustomizeBlockContainer(int windowId, World world, BlockPos pos, PlayerInventory inventory) {
		super(SCContent.cTypeCustomizeBlock, windowId);
		this.tileEntity = (CustomizableTileEntity)world.getTileEntity(pos);

		if(tileEntity.getNumberOfCustomizableOptions() == 1)
			addSlot(new ModuleSlot(tileEntity, 0, 79, 20));
		else if(tileEntity.getNumberOfCustomizableOptions() == 2){
			addSlot(new ModuleSlot(tileEntity, 0, 70, 20));
			addSlot(new ModuleSlot(tileEntity, 1, 88, 20));
		}else if(tileEntity.getNumberOfCustomizableOptions() == 3){
			addSlot(new ModuleSlot(tileEntity, 0, 61, 20));
			addSlot(new ModuleSlot(tileEntity, 1, 79, 20));
			addSlot(new ModuleSlot(tileEntity, 2, 97, 20));
		}else if(tileEntity.getNumberOfCustomizableOptions() == 4){
			addSlot(new ModuleSlot(tileEntity, 0, 52, 20));
			addSlot(new ModuleSlot(tileEntity, 1, 70, 20));
			addSlot(new ModuleSlot(tileEntity, 2, 88, 20));
			addSlot(new ModuleSlot(tileEntity, 3, 106, 20));
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
			tileEntity.onModuleRemoved(slotStack, CustomModules.getModuleFromStack(slotStack));
			tileEntity.createLinkedBlockAction(LinkedAction.MODULE_REMOVED, new Object[]{ slotStack, CustomModules.getModuleFromStack(slotStack) }, tileEntity);

			if(tileEntity instanceof SecurityCameraTileEntity)
				tileEntity.getWorld().notifyNeighborsOfStateChange(tileEntity.getPos().offset(tileEntity.getWorld().getBlockState(tileEntity.getPos()).get(SecurityCameraBlock.FACING), -1), tileEntity.getWorld().getBlockState(tileEntity.getPos()).getBlock());

			if (index < tileEntity.getSizeInventory())
			{
				if (!mergeItemStack(slotStack, 0, 35, true))
					return ItemStack.EMPTY;
			}
			else if (slotStack.getItem() != null && slotStack.getItem() instanceof ModuleItem && tileEntity.getAcceptedModules().contains(CustomModules.getModuleFromStack(slotStack)) && !mergeItemStack(slotStack, 0, tileEntity.getSizeInventory(), false))
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
		private CustomizableTileEntity tileEntity;

		public ModuleSlot(CustomizableTileEntity inventory, int index, int xPos, int yPos) {
			super(inventory, index, xPos, yPos);
			tileEntity = inventory;
		}

		/**
		 * Check if the stack is a valid item for this slot. Always true beside for the armor slots.
		 */
		@Override
		public boolean isItemValid(ItemStack stack)
		{
			if(!stack.isEmpty() && stack.getItem() instanceof ModuleItem && tileEntity.getAcceptedModules().contains(((ModuleItem) stack.getItem()).getModule()) && !tileEntity.hasModule(((ModuleItem) stack.getItem()).getModule()))
				return true;
			else
				return false;
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
