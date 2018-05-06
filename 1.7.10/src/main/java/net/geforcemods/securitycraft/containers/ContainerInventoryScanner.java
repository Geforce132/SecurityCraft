package net.geforcemods.securitycraft.containers;

import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.tileentity.TileEntityInventoryScanner;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerInventoryScanner extends Container {

	private final int numRows;
	private final TileEntityInventoryScanner inventoryScannerTE;

	public ContainerInventoryScanner(IInventory inventory, TileEntityInventoryScanner te){
		numRows = te.getSizeInventory() / 9;
		inventoryScannerTE = te;

		for(int i = 0; i < 10; i++)
			addSlotToContainer(new SlotOwnerRestricted(te, te, i, (4 + (i * 17)), 16));

		if(((CustomizableSCTE) te).hasModule(EnumCustomModules.STORAGE))
			for(int i = 0; i < 9; i++)
				for(int j = 0; j < 3; j++)
					addSlotToContainer(new Slot(te, 10 + ((i * 3) + j), 177 + (j * 18), 17 + i * 18));

		for(int i = 0; i < 3; i++)
			for(int j = 0; j < 9; j++)
				addSlotToContainer(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 115 + i * 18));

		for(int i = 0; i < 9; i++)
			addSlotToContainer(new Slot(inventory, i, 8 + i * 18, 173));
	}

	/**
	 * Called when a player shift-clicks on a slot. You must override this or you will crash when someone does that.
	 */
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int index)
	{
		ItemStack itemstack = null;
		Slot slot = (Slot)inventorySlots.get(index);

		if (slot != null && slot.getHasStack())
		{
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			if (index < numRows * 9)
			{
				if (!mergeItemStack(itemstack1, numRows * 9, inventorySlots.size(), true))
					return null;
			}
			else if (!mergeItemStack(itemstack1, 0, numRows * 9, false))
				return null;

			if (itemstack1.stackSize == 0)
				slot.putStack((ItemStack)null);
			else
				slot.onSlotChanged();
		}

		return itemstack;
	}

	/**
	 * Called when the container is closed.
	 */
	@Override
	public void onContainerClosed(EntityPlayer player)
	{
		super.onContainerClosed(player);

		Utils.setISinTEAppropriately(player.worldObj, inventoryScannerTE.xCoord, inventoryScannerTE.yCoord, inventoryScannerTE.zCoord, ((TileEntityInventoryScanner) player.worldObj.getTileEntity(inventoryScannerTE.xCoord, inventoryScannerTE.yCoord, inventoryScannerTE.zCoord)).getContents(), ((TileEntityInventoryScanner) player.worldObj.getTileEntity(inventoryScannerTE.xCoord, inventoryScannerTE.yCoord, inventoryScannerTE.zCoord)).getType());
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return true;
	}

}
