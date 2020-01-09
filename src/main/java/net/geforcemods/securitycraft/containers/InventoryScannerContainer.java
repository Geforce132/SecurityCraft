package net.geforcemods.securitycraft.containers;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.misc.CustomModules;
import net.geforcemods.securitycraft.tileentity.InventoryScannerTileEntity;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class InventoryScannerContainer extends Container {

	private final int numRows;
	public final InventoryScannerTileEntity te;

	public InventoryScannerContainer(int windowId, World world, BlockPos pos, PlayerInventory inventory){
		super(SCContent.cTypeInventoryScanner, windowId);
		te = (InventoryScannerTileEntity)world.getTileEntity(pos);
		numRows = te.getSizeInventory() / 9;

		for(int i = 0; i < 10; i++)
			addSlot(new OwnerRestrictedSlot(te, te, i, (4 + (i * 17)), 16, true));

		if(te.getOwner().isOwner(inventory.player) && te.hasModule(CustomModules.STORAGE))
			for(int i = 0; i < 9; i++)
				for(int j = 0; j < 3; j++)
					addSlot(new Slot(te, 10 + ((i * 3) + j), 177 + (j * 18), 17 + i * 18));

		for(int i = 0; i < 3; i++)
			for(int j = 0; j < 9; j++)
				addSlot(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 115 + i * 18));

		for(int i = 0; i < 9; i++)
			addSlot(new Slot(inventory, i, 8 + i * 18, 173));
	}

	/**
	 * Called when a player shift-clicks on a slot. You must override this or you will crash when someone does that.
	 */
	@Override
	public ItemStack transferStackInSlot(PlayerEntity player, int index)
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
	public void onContainerClosed(PlayerEntity player)
	{
		super.onContainerClosed(player);

		Utils.setISinTEAppropriately(player.world, te.getPos(), ((InventoryScannerTileEntity) player.world.getTileEntity(te.getPos())).getContents(), ((InventoryScannerTileEntity) player.world.getTileEntity(te.getPos())).getScanType());
	}

	@Override
	public boolean canInteractWith(PlayerEntity player) {
		return true;
	}

	@Override
	public ItemStack slotClick(int slotId, int dragType, ClickType clickType, PlayerEntity player)
	{
		if(slotId >= 0 && slotId < 10 && getSlot(slotId) instanceof OwnerRestrictedSlot && ((OwnerRestrictedSlot)getSlot(slotId)).isGhostSlot())
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
