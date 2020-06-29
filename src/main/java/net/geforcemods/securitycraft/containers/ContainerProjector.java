package net.geforcemods.securitycraft.containers;

import net.geforcemods.securitycraft.tileentity.TileEntityProjector;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ContainerProjector extends Container {

	public static final int SIZE = 1;

	public TileEntityProjector te;

	public ContainerProjector(InventoryPlayer inventory, TileEntityProjector te)
	{
		this.te = te;

		for(int y = 0; y < 3; y++)
			for(int x = 0; x < 9; ++x)
				addSlotToContainer(new Slot(inventory, x + y * 9 + 9, 8 + x * 18, 84 + y * 18 + 59));

		for(int x = 0; x < 9; x++)
			addSlotToContainer(new Slot(inventory, x, 8 + x * 18, 142 + 59));

		// A custom slot that prevents non-Block items from being inserted into the projector
		addSlotToContainer(new Slot(te, 36, 79, 23)
		{
			@Override
			public boolean isItemValid(ItemStack stack)
			{
				return stack.getItem() instanceof ItemBlock;
			}
		});
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int index)
	{
		ItemStack slotStackCopy = ItemStack.EMPTY;
		Slot slot = inventorySlots.get(index);

		if(slot != null && slot.getHasStack()) {
			ItemStack slotStack = slot.getStack();
			slotStackCopy = slotStack.copy();

			if(index == 36) {
				if(!mergeItemStack(slotStack, 0, 36, false))
					return ItemStack.EMPTY;
			}
			else {
				if(!mergeItemStack(slotStack, 36, 37, false))
					return ItemStack.EMPTY;
			}

			if(slotStack.getCount() == 0)
				slot.putStack(ItemStack.EMPTY);
			else
				slot.onSlotChanged();

			if(slotStack.getCount() == slotStack.getCount())
				return ItemStack.EMPTY;

			slot.onTake(player, slotStack);
		}

		return slotStackCopy;
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn)
	{
		return true;
	}
}
