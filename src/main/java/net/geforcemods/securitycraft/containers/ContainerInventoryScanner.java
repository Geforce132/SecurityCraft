package net.geforcemods.securitycraft.containers;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.misc.EnumModuleType;
import net.geforcemods.securitycraft.tileentity.TileEntityInventoryScanner;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerInventoryScanner extends Container {

	private final int numRows;
	private final TileEntityInventoryScanner te;

	public ContainerInventoryScanner(InventoryPlayer inventory, TileEntityInventoryScanner te){
		numRows = te.getSizeInventory() / 9;
		this.te = te;

		//prohibited items
		for(int i = 0; i < 10; i++)
			addSlotToContainer(new SlotOwnerRestricted(te, te, i, (6 + (i * 18)), 16, true));

		//inventory scanner storage
		if(te.getOwner().isOwner(inventory.player) && te.hasModule(EnumModuleType.STORAGE))
			for(int i = 0; i < 9; i++)
				for(int j = 0; j < 3; j++)
					addSlotToContainer(new Slot(te, 10 + ((i * 3) + j), 188 + (j * 18), 29 + i * 18));

		//inventory
		for(int i = 0; i < 3; i++)
			for(int j = 0; j < 9; j++)
				addSlotToContainer(new Slot(inventory, j + i * 9 + 9, 15 + j * 18, 115 + i * 18));

		//hotbar
		for(int i = 0; i < 9; i++)
			addSlotToContainer(new Slot(inventory, i, 15 + i * 18, 173));
	}

	/**
	 * Called when a player shift-clicks on a slot. You must override this or you will crash when someone does that.
	 */
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int index)
	{
		ItemStack slotStackCopy = ItemStack.EMPTY;
		Slot slot = inventorySlots.get(index);

		if (slot != null && slot.getHasStack())
		{
			ItemStack slotStack = slot.getStack();
			slotStackCopy = slotStack.copy();

			if (index < numRows * 9)
			{
				if (!mergeItemStack(slotStack, numRows * 9, inventorySlots.size(), true))
					return ItemStack.EMPTY;
			}
			else if (!mergeItemStack(slotStack, 0, numRows * 9, false))
				return ItemStack.EMPTY;

			if (slotStack.getCount() == 0)
				slot.putStack(ItemStack.EMPTY);
			else
				slot.onSlotChanged();
		}

		return slotStackCopy;
	}

	/**
	 * Called when the container is closed.
	 */
	@Override
	public void onContainerClosed(EntityPlayer player)
	{
		super.onContainerClosed(player);

		Utils.setISinTEAppropriately(player.world, te.getPos(), ((TileEntityInventoryScanner) player.world.getTileEntity(te.getPos())).getContents());
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return BlockUtils.isWithinUsableDistance(te.getWorld(), te.getPos(), player, SCContent.inventoryScanner);
	}

	@Override
	public ItemStack slotClick(int slotId, int dragType, ClickType clickType, EntityPlayer player)
	{
		if(slotId >= 0 && slotId < 10 && getSlot(slotId) instanceof SlotOwnerRestricted && ((SlotOwnerRestricted)getSlot(slotId)).isGhostSlot())
		{
			if(te.getOwner().isOwner(player))
			{
				ItemStack pickedUpStack = player.inventory.getItemStack().copy();

				pickedUpStack.setCount(1);
				te.getContents().set(slotId, pickedUpStack);
			}

			return ItemStack.EMPTY;
		}
		else return super.slotClick(slotId, dragType, clickType, player);
	}
}
