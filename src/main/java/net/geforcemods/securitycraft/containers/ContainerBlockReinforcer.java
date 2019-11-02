package net.geforcemods.securitycraft.containers;

import net.geforcemods.securitycraft.SCContent;
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

public class ContainerBlockReinforcer extends Container
{
	private ItemStack blockReinforcer;
	private InventoryBasic itemInventory = new InventoryBasic("BlockReinforcer", true, 1);

	public ContainerBlockReinforcer(EntityPlayer player, InventoryPlayer inventory)
	{
		blockReinforcer = player.getCurrentEquippedItem();
		addSlotToContainer(new SlotBlockReinforcer(itemInventory, 0, 79, 20)); //input & output slot

		//main player inventory
		for(int i = 0; i < 3; i++)
			for(int j = 0; j < 9; j++)
				addSlotToContainer(new Slot(inventory, 9 + j + i * 9, 8 + j * 18, 84 + i * 18));

		//player hotbar
		for(int i = 0; i < 9; i++)
			addSlotToContainer(new Slot(inventory, i, 8 + i * 18, 142));
	}

	@Override
	public boolean canInteractWith(EntityPlayer player)
	{
		return true;
	}

	@Override
	public void onContainerClosed(EntityPlayer player)
	{
		ItemStack stack = itemInventory.removeStackFromSlot(0);

		if(stack != null)
		{
			Item item = stack.getItem();
			ItemStack newStack = null;
			int customMeta = 0;

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

			if(newStack != null)
			{
				if(Block.getBlockFromItem(newStack.getItem()) == SCContent.reinforcedMetals || Block.getBlockFromItem(newStack.getItem()) == SCContent.reinforcedCompressedBlocks)
					newStack.setItemDamage(customMeta);
				else
					newStack.setItemDamage(stack.getItemDamage());

				newStack.stackSize = stack.stackSize;
				player.getCurrentEquippedItem().damageItem(stack.stackSize, player);
				player.dropPlayerItemWithRandomChoice(newStack, false);
			}
		}
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int id)
	{
		ItemStack slotStackCopy = null;
		Slot slot = inventorySlots.get(id);

		if(slot != null && slot.getHasStack())
		{
			ItemStack slotStack = slot.getStack();

			slotStackCopy = slotStack.copy();

			if(id < 1)
			{
				if(!mergeItemStack(slotStack, 1, 37, true))
					return null;
				slot.onSlotChange(slotStack, slotStackCopy);
			}
			else if(id >= 1)
				if(!mergeItemStack(slotStack, 0, 1, false))
					return null;

			if(slotStack.stackSize == 0)
				slot.putStack((ItemStack) null);
			else
				slot.onSlotChanged();

			if(slotStack.stackSize == slotStackCopy.stackSize)
				return null;
			slot.onPickupFromSlot(player, slotStack);
		}

		return slotStackCopy;
	}

	//edited to check if the item to be merged is valid in that slot
	@Override
	protected boolean mergeItemStack(ItemStack stack, int startIndex, int endIndex, boolean useEndIndex)
	{
		boolean merged = false;
		int currentSlot = startIndex;

		if(useEndIndex)
			currentSlot = endIndex - 1;

		Slot slot;
		ItemStack slotStack;

		if(stack.isStackable())
			while(stack.stackSize > 0 && (!useEndIndex && currentSlot < endIndex || useEndIndex && currentSlot >= startIndex))
			{
				slot = inventorySlots.get(currentSlot);
				slotStack = slot.getStack();

				if(slotStack != null && slotStack.getItem() == stack.getItem() && (!stack.getHasSubtypes() || stack.getMetadata() == slotStack.getMetadata()) && ItemStack.areItemStackTagsEqual(stack, slotStack))
				{
					int combinedStackSize = slotStack.stackSize + stack.stackSize;

					if(combinedStackSize <= stack.getMaxStackSize())
					{
						stack.stackSize = 0;
						slotStack.stackSize = combinedStackSize;
						slot.onSlotChanged();
						merged = true;
					}
					else if(slotStack.stackSize < stack.getMaxStackSize())
					{
						stack.stackSize -= stack.getMaxStackSize() - slotStack.stackSize;
						slotStack.stackSize = stack.getMaxStackSize();
						slot.onSlotChanged();
						merged = true;
					}
				}

				if(useEndIndex)
					--currentSlot;
				else
					++currentSlot;
			}

		if(stack.stackSize > 0)
		{
			if(useEndIndex)
				currentSlot = endIndex - 1;
			else
				currentSlot = startIndex;

			while(!useEndIndex && currentSlot < endIndex || useEndIndex && currentSlot >= startIndex)
			{
				slot = inventorySlots.get(currentSlot);
				slotStack = slot.getStack();

				if(slotStack == null && slot.isItemValid(stack)) // Forge: Make sure to respect isItemValid in the slot.
				{
					slot.putStack(stack.copy());
					slot.onSlotChanged();
					stack.stackSize = 0;
					merged = true;
					break;
				}

				if(useEndIndex)
					--currentSlot;
				else
					++currentSlot;
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
		{
			boolean validBlock = IReinforcedBlock.BLOCKS.stream().anyMatch((reinforcedBlock) -> {
				return ((IReinforcedBlock)reinforcedBlock).getVanillaBlocks().stream().anyMatch((vanillaBlock) -> {
					return stack.getItem().equals(Item.getItemFromBlock(vanillaBlock));
				});
			});

			return validBlock &&
					(blockReinforcer.getMaxDamage() == 0 ? true : //lvl3
						blockReinforcer.getMaxDamage() - blockReinforcer.getItemDamage() >= stack.stackSize + (getHasStack() ? getStack().stackSize : 0)); //disallow putting in items that can't be handled by the ubr
		}
	}
}
