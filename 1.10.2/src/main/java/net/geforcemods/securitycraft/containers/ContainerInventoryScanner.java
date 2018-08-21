package net.geforcemods.securitycraft.containers;

import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.tileentity.TileEntityInventoryScanner;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerInventoryScanner extends Container {

	private final int numRows;
	private final TileEntityInventoryScanner inventoryScannerTE;

	public ContainerInventoryScanner(IInventory par1IInventory, TileEntityInventoryScanner par2TileEntityInventoryScanner){
		numRows = par2TileEntityInventoryScanner.getSizeInventory() / 9;
		inventoryScannerTE = par2TileEntityInventoryScanner;

		for(int i = 0; i < 10; i++)
			addSlotToContainer(new SlotOwnerRestricted(par2TileEntityInventoryScanner, par2TileEntityInventoryScanner, i, (4 + (i * 17)), 16, true));

		if(((CustomizableSCTE) par2TileEntityInventoryScanner).hasModule(EnumCustomModules.STORAGE))
			for(int i = 0; i < 9; i++)
				for(int j = 0; j < 3; j++)
					addSlotToContainer(new Slot(par2TileEntityInventoryScanner, 10 + ((i * 3) + j), 177 + (j * 18), 17 + i * 18));

		for(int i = 0; i < 3; i++)
			for(int j = 0; j < 9; j++)
				addSlotToContainer(new Slot(par1IInventory, j + i * 9 + 9, 8 + j * 18, 115 + i * 18));

		for(int i = 0; i < 9; i++)
			addSlotToContainer(new Slot(par1IInventory, i, 8 + i * 18, 173));
	}

	/**
	 * Called when a player shift-clicks on a slot. You must override this or you will crash when someone does that.
	 */
	@Override
	public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2)
	{
		ItemStack itemstack = null;
		Slot slot = inventorySlots.get(par2);

		if (slot != null && slot.getHasStack())
		{
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			if (par2 < numRows * 9)
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
	public void onContainerClosed(EntityPlayer par1EntityPlayer)
	{
		super.onContainerClosed(par1EntityPlayer);

		Utils.setISinTEAppropriately(par1EntityPlayer.world, inventoryScannerTE.getPos(), ((TileEntityInventoryScanner) par1EntityPlayer.world.getTileEntity(inventoryScannerTE.getPos())).getContents(), ((TileEntityInventoryScanner) par1EntityPlayer.world.getTileEntity(inventoryScannerTE.getPos())).getType());
	}

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		return true;
	}

	@Override
	public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player)
	{
		if(slotId >= 0 && slotId < 10 && getSlot(slotId) instanceof SlotOwnerRestricted && ((SlotOwnerRestricted)getSlot(slotId)).isGhostSlot())
		{
			if(inventoryScannerTE.getOwner().isOwner(player))
			{
				ItemStack pickedUpStack = player.inventory.getItemStack();

				if(pickedUpStack != null)
				{
					pickedUpStack = pickedUpStack.copy();
					pickedUpStack.stackSize = 1;
				}

				inventoryScannerTE.getContents()[slotId] = pickedUpStack;
			}

			return null;
		}
		else return super.slotClick(slotId, dragType, clickTypeIn, player);
	}
}
