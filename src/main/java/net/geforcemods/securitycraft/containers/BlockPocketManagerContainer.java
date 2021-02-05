package net.geforcemods.securitycraft.containers;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.tileentity.BlockPocketManagerTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.SlotItemHandler;

public class BlockPocketManagerContainer extends Container
{
	public BlockPocketManagerTileEntity te;
	public final boolean storage;

	public BlockPocketManagerContainer(int windowId, World world, BlockPos pos, PlayerInventory inventory)
	{
		super(SCContent.cTypeBlockPocketManager, windowId);

		TileEntity tile = world.getTileEntity(pos);

		if(tile instanceof BlockPocketManagerTileEntity)
			te = (BlockPocketManagerTileEntity)tile;

		storage = te != null && te.hasModule(ModuleType.STORAGE);

		if(storage)
		{
			for(int y = 0; y < 3; y++)
			{
				for(int x = 0; x < 9; ++x)
				{
					addSlot(new Slot(inventory, x + y * 9 + 9, 8 + x * 18, 84 + y * 18 + 74));
				}
			}

			for(int x = 0; x < 9; x++)
			{
				addSlot(new Slot(inventory, x, 8 + x * 18, 142 + 74));
			}

			te.getStorageHandler().ifPresent(storage -> {
				int slotId = 0;

				for(int x = 0; x < 7; x++)
				{
					for(int y = 0; y < 8; y++)
					{
						addSlot(new SlotItemHandler(storage, slotId++, 124 + x * 18, 8 + y * 18));
					}
				}
			});
		}
	}

	@Override
	public ItemStack transferStackInSlot(PlayerEntity player, int index)
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
	public boolean canInteractWith(PlayerEntity playerIn)
	{
		return true;
	}
}
