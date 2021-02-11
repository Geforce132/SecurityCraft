package net.geforcemods.securitycraft.containers;

import net.geforcemods.securitycraft.misc.EnumModuleType;
import net.geforcemods.securitycraft.tileentity.TileEntityBlockPocketManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerBlockPocketManager extends Container
{
	public TileEntityBlockPocketManager te;
	public final boolean storage;
	public final boolean isOwner;

	public ContainerBlockPocketManager(InventoryPlayer inventory, TileEntityBlockPocketManager te)
	{
		this.te = te;
		isOwner = te.getOwner().isOwner(inventory.player);
		storage = te != null && te.hasModule(EnumModuleType.STORAGE) && isOwner;

		if(storage)
		{
			for(int y = 0; y < 3; y++)
			{
				for(int x = 0; x < 9; ++x)
				{
					addSlotToContainer(new Slot(inventory, x + y * 9 + 9, 8 + x * 18, 84 + y * 18 + 74));
				}
			}

			for(int x = 0; x < 9; x++)
			{
				addSlotToContainer(new Slot(inventory, x, 8 + x * 18, 142 + 74));
			}

			IItemHandler storage = te.getStorageHandler();
			int slotId = 0;

			for(int x = 0; x < 7; x++)
			{
				for(int y = 0; y < 8; y++)
				{
					addSlotToContainer(new SlotItemHandler(storage, slotId++, 124 + x * 18, 8 + y * 18));
				}
			}
		}
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int index)
	{
		ItemStack copy = ItemStack.EMPTY;
		Slot slot = inventorySlots.get(index);

		if(slot != null && slot.getHasStack())
		{
			ItemStack slotStack = slot.getStack();

			copy = slotStack.copy();

			if(index >= 36) //block pocket manager slots
			{
				if(!mergeItemStack(slotStack, 0, 36, true))
					return ItemStack.EMPTY;
			}
			else if(index >= 0 && index <= 35) //main inventory and hotbar
			{
				if(!mergeItemStack(slotStack, 36, inventorySlots.size(), false))
					return ItemStack.EMPTY;
			}

			if(slotStack.isEmpty())
				slot.putStack(ItemStack.EMPTY);
			else
				slot.onSlotChanged();
		}

		return copy;
	}

	@Override
	public boolean canInteractWith(EntityPlayer player)
	{
		return true;
	}
}
