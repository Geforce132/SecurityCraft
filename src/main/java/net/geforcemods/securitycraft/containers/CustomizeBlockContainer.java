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
import net.minecraftforge.items.SlotItemHandler;

public class CustomizeBlockContainer extends Container{

	public IModuleInventory moduleInv;

	public CustomizeBlockContainer(int windowId, World world, BlockPos pos, PlayerInventory inventory) {
		super(SCContent.cTypeCustomizeBlock, windowId);
		this.moduleInv = (IModuleInventory)world.getTileEntity(pos);

		int slotId = 0;

		if(moduleInv.enableHack())
			slotId = 100;

		if(moduleInv.getMaxNumberOfModules() == 1)
			addSlot(new SlotItemHandler(moduleInv, slotId, 79, 20));
		else if(moduleInv.getMaxNumberOfModules() == 2){
			addSlot(new SlotItemHandler(moduleInv, slotId++, 70, 20));
			addSlot(new SlotItemHandler(moduleInv, slotId++, 88, 20));
		}else if(moduleInv.getMaxNumberOfModules() == 3){
			addSlot(new SlotItemHandler(moduleInv, slotId++, 61, 20));
			addSlot(new SlotItemHandler(moduleInv, slotId++, 79, 20));
			addSlot(new SlotItemHandler(moduleInv, slotId++, 97, 20));
		}else if(moduleInv.getMaxNumberOfModules() == 4){
			addSlot(new SlotItemHandler(moduleInv, slotId++, 52, 20));
			addSlot(new SlotItemHandler(moduleInv, slotId++, 70, 20));
			addSlot(new SlotItemHandler(moduleInv, slotId++, 88, 20));
			addSlot(new SlotItemHandler(moduleInv, slotId++, 106, 20));
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

			if (index < moduleInv.getSlots())
			{
				if (!mergeItemStack(slotStack, 0, 35, true))
					return ItemStack.EMPTY;
			}
			else if (slotStack.getItem() instanceof ModuleItem && moduleInv.getAcceptedModules().contains(CustomModules.getModuleFromStack(slotStack)) && !mergeItemStack(slotStack, 0, moduleInv.getSlots(), false))
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
}
