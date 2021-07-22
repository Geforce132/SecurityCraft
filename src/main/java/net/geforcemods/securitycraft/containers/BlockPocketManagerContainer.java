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
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.SlotItemHandler;

public class BlockPocketManagerContainer extends Container
{
	public BlockPocketManagerTileEntity te;
	private IWorldPosCallable worldPosCallable;
	public final boolean storage;
	public final boolean isOwner;

	public BlockPocketManagerContainer(int windowId, World world, BlockPos pos, PlayerInventory inventory)
	{
		super(SCContent.cTypeBlockPocketManager, windowId);

		TileEntity tile = world.getBlockEntity(pos);

		if(tile instanceof BlockPocketManagerTileEntity)
			te = (BlockPocketManagerTileEntity)tile;

		worldPosCallable = IWorldPosCallable.create(world, pos);
		isOwner = te.getOwner().isOwner(inventory.player);
		storage = te != null && te.hasModule(ModuleType.STORAGE) && isOwner;

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

				for(int y = 0; y < 8; y++)
				{
					for(int x = 0; x < 7; x++)
					{
						addSlot(new SlotItemHandler(storage, slotId++, 124 + x * 18, 8 + y * 18));
					}
				}
			});
		}
	}

	@Override
	public ItemStack quickMoveStack(PlayerEntity player, int index)
	{
		ItemStack copy = ItemStack.EMPTY;
		Slot slot = slots.get(index);

		if(slot != null && slot.hasItem())
		{
			ItemStack slotStack = slot.getItem();

			copy = slotStack.copy();

			if(index >= 36) //block pocket manager slots
			{
				if(!moveItemStackTo(slotStack, 0, 36, true))
					return ItemStack.EMPTY;
			}
			else if(index >= 0 && index <= 35) //main inventory and hotbar
			{
				if(!moveItemStackTo(slotStack, 36, slots.size(), false))
					return ItemStack.EMPTY;
			}

			if(slotStack.isEmpty())
				slot.set(ItemStack.EMPTY);
			else
				slot.setChanged();
		}

		return copy;
	}

	@Override
	public boolean stillValid(PlayerEntity player)
	{
		return stillValid(worldPosCallable, player, SCContent.BLOCK_POCKET_MANAGER.get());
	}
}
