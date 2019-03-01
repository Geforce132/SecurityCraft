package net.geforcemods.securitycraft.containers;

import net.geforcemods.securitycraft.blocks.reinforced.IReinforcedBlock;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;

public class ContainerBlockReinforcer extends Container
{
	private ItemStack blockReinforcer;
	private InventoryBasic itemventory = new InventoryBasic(new TextComponentString("BlockReinforcer"), 1);

	public ContainerBlockReinforcer(EntityPlayer player, InventoryPlayer inventory)
	{
		blockReinforcer = player.inventory.getCurrentItem();
		addSlot(new SlotBlockReinforcer(itemventory, 0, 79, 20)); //input & output slot

		//main player inventory
		for(int i = 0; i < 3; i++)
			for(int j = 0; j < 9; j++)
				addSlot(new Slot(inventory, 9 + j + i * 9, 8 + j * 18, 84 + i * 18));

		//player hotbar
		for(int i = 0; i < 9; i++)
			addSlot(new Slot(inventory, i, 8 + i * 18, 142));
	}

	@Override
	public boolean canInteractWith(EntityPlayer player)
	{
		return true;
	}

	@Override
	public void onContainerClosed(EntityPlayer player)
	{
		ItemStack stack = itemventory.getStackInSlot(0);

		if(!stack.isEmpty())
		{
			Item item = stack.getItem();
			ItemStack newStack = ItemStack.EMPTY;
			int customMeta = -1;

			for(Block rb : IReinforcedBlock.BLOCKS)
			{
				IReinforcedBlock block = (IReinforcedBlock)rb;

				if(block.getVanillaBlocks().contains(Block.getBlockFromItem(item)))
				{
					newStack = new ItemStack(rb);

					if(block.getVanillaBlocks().size() == block.getAmount())
						customMeta = block.getVanillaBlocks().indexOf(Block.getBlockFromItem(item));
				}
			}

			if(!newStack.isEmpty())
			{
				if(customMeta != -1)
					newStack.setDamage(customMeta);
				else
					newStack.setDamage(stack.getDamage());

				newStack.setCount(stack.getCount());
				blockReinforcer.damageItem(stack.getCount(), player);
				player.dropItem(newStack, false);
			}
		}
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int id)
	{
		ItemStack slotStackCopy = ItemStack.EMPTY;
		Slot slot = inventorySlots.get(id);

		if(slot != null && slot.getHasStack())
		{
			ItemStack slotStack = slot.getStack();

			slotStackCopy = slotStack.copy();

			if(id < 1)
			{
				if(!mergeItemStack(slotStack, 1, 37, true))
					return ItemStack.EMPTY;
				slot.onSlotChange(slotStack, slotStackCopy);
			}
			else if(id >= 1)
				if(!mergeItemStack(slotStack, 0, 1, false))
					return ItemStack.EMPTY;

			if(slotStack.getCount() == 0)
				slot.putStack(ItemStack.EMPTY);
			else
				slot.onSlotChanged();

			if(slotStack.getCount() == slotStackCopy.getCount())
				return ItemStack.EMPTY;
			slot.onTake(player, slotStack);
		}

		return slotStackCopy;
	}

	//edited to check if the item to be merged is valid in that slot
	@Override
	protected boolean mergeItemStack(ItemStack stack, int startIndex, int endIndex, boolean useEndIndex)
	{
		boolean merged = false;
		int currentIndex = startIndex;

		if(useEndIndex)
			currentIndex = endIndex - 1;

		Slot slot;
		ItemStack slotStack;

		if(stack.isStackable())
			while(stack.getCount() > 0 && (!useEndIndex && currentIndex < endIndex || useEndIndex && currentIndex >= startIndex))
			{
				slot = inventorySlots.get(currentIndex);
				slotStack = slot.getStack();

				if(!slotStack.isEmpty() && areItemsAndTagsEqual(stack, slotStack))
				{
					int combinedCount = slotStack.getCount() + stack.getCount();

					if(combinedCount <= stack.getMaxStackSize())
					{
						stack.setCount(0);
						slotStack.setCount(combinedCount);
						slot.onSlotChanged();
						merged = true;
					}
					else if(slotStack.getCount() < stack.getMaxStackSize())
					{
						stack.shrink(stack.getMaxStackSize() - slotStack.getCount());
						slotStack.setCount(stack.getMaxStackSize());
						slot.onSlotChanged();
						merged = true;
					}
				}

				if(useEndIndex)
					--currentIndex;
				else
					++currentIndex;
			}

		if(stack.getCount() > 0)
		{
			if(useEndIndex)
				currentIndex = endIndex - 1;
			else
				currentIndex = startIndex;

			while(!useEndIndex && currentIndex < endIndex || useEndIndex && currentIndex >= startIndex)
			{
				slot = inventorySlots.get(currentIndex);
				slotStack = slot.getStack();

				if(slotStack.isEmpty() && slot.isItemValid(stack)) // Forge: Make sure to respect isItemValid in the slot.
				{
					slot.putStack(stack.copy());
					slot.onSlotChanged();
					stack.setCount(0);
					merged = true;
					break;
				}

				if(useEndIndex)
					--currentIndex;
				else
					++currentIndex;
			}
		}

		return merged;
	}

	private class SlotBlockReinforcer extends Slot
	{
		public SlotBlockReinforcer(IInventory inventory, int index, int x, int y)
		{
			super(inventory, index, x, y);
		}

		@Override
		public boolean isItemValid(ItemStack stack)
		{			boolean validBlock = IReinforcedBlock.BLOCKS.stream().anyMatch((reinforcedBlock) -> {
			return ((IReinforcedBlock)reinforcedBlock).getVanillaBlocks().stream().anyMatch((vanillaBlock) -> {
				return stack.getItem().equals(vanillaBlock.asItem());
			});
		});

		return validBlock &&
				(blockReinforcer.getMaxDamage() == 0 ? true : //lvl3
					blockReinforcer.getMaxDamage() - blockReinforcer.getDamage() >= stack.getCount() + (getHasStack() ? getStack().getCount() : 0)); //disallow putting in items that can't be handled by the ubr
		}
	}
}
