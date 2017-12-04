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

public class ContainerBlockReinforcer extends Container
{
	private ItemStack blockReinforcer;
	private InventoryBasic itemInventory = new InventoryBasic("BlockReinforcer", true, 1);

	public ContainerBlockReinforcer(EntityPlayer player, InventoryPlayer inventory)
	{
		blockReinforcer = player.inventory.getCurrentItem();
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
	public boolean canInteractWith(EntityPlayer p_75145_1_)
	{
		return true;
	}

	@Override
	public void onContainerClosed(EntityPlayer player)
	{
		ItemStack stack = itemInventory.getStackInSlot(0);

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
					newStack.setItemDamage(customMeta);
				else
					newStack.setItemDamage(stack.getItemDamage());

				newStack.setCount(stack.getCount());
				blockReinforcer.damageItem(stack.getCount(), player);
				player.dropItem(newStack, false);
			}
		}
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int id)
	{
		ItemStack stack = ItemStack.EMPTY;
		Slot slot = inventorySlots.get(id);

		if(slot != null && slot.getHasStack())
		{
			ItemStack stack1 = slot.getStack();

			stack = stack1.copy();

			if(id < 1)
			{
				if(!mergeItemStack(stack1, 1, 37, true))
					return ItemStack.EMPTY;
				slot.onSlotChange(stack1, stack);
			}
			else if(id >= 1)
				if(!mergeItemStack(stack1, 0, 1, false))
					return ItemStack.EMPTY;

			if(stack1.getCount() == 0)
				slot.putStack(ItemStack.EMPTY);
			else
				slot.onSlotChanged();

			if(stack1.getCount() == stack.getCount())
				return ItemStack.EMPTY;
			slot.onTake(player, stack1);
		}

		return stack;
	}

	//edited to check if the item to be merged is valid in that slot
	@Override
	protected boolean mergeItemStack(ItemStack stack, int startIndex, int endIndex, boolean useEndIndex)
	{
		boolean flag1 = false;
		int k = startIndex;

		if(useEndIndex)
			k = endIndex - 1;

		Slot slot;
		ItemStack itemstack1;

		if(stack.isStackable())
			while(stack.getCount() > 0 && (!useEndIndex && k < endIndex || useEndIndex && k >= startIndex))
			{
				slot = inventorySlots.get(k);
				itemstack1 = slot.getStack();

				if(!itemstack1.isEmpty() && itemstack1.getItem() == stack.getItem() && (!stack.getHasSubtypes() || stack.getMetadata() == itemstack1.getMetadata()) && ItemStack.areItemStackTagsEqual(stack, itemstack1))
				{
					int l = itemstack1.getCount() + stack.getCount();

					if(l <= stack.getMaxStackSize())
					{
						stack.setCount(0);
						itemstack1.setCount(l);
						slot.onSlotChanged();
						flag1 = true;
					}
					else if(itemstack1.getCount() < stack.getMaxStackSize())
					{
						stack.shrink(stack.getMaxStackSize() - itemstack1.getCount());
						itemstack1.setCount(stack.getMaxStackSize());
						slot.onSlotChanged();
						flag1 = true;
					}
				}

				if(useEndIndex)
					--k;
				else
					++k;
			}

		if(stack.getCount() > 0)
		{
			if(useEndIndex)
				k = endIndex - 1;
			else
				k = startIndex;

			while(!useEndIndex && k < endIndex || useEndIndex && k >= startIndex)
			{
				slot = inventorySlots.get(k);
				itemstack1 = slot.getStack();

				if(itemstack1.isEmpty() && slot.isItemValid(stack)) // Forge: Make sure to respect isItemValid in the slot.
				{
					slot.putStack(stack.copy());
					slot.onSlotChanged();
					stack.setCount(0);
					flag1 = true;
					break;
				}

				if(useEndIndex)
					--k;
				else
					++k;
			}
		}

		return flag1;
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
							blockReinforcer.getMaxDamage() - blockReinforcer.getItemDamage() >= stack.getCount() + (getHasStack() ? getStack().getCount() : 0)); //disallow putting in items that can't be handled by the ubr
		}
	}
}
