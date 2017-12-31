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
	public boolean canInteractWith(EntityPlayer p_75145_1_)
	{
		return true;
	}

	@Override
	public void onContainerClosed(EntityPlayer player)
	{
		ItemStack stack = itemInventory.getStackInSlotOnClosing(0);

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
					newStack.setMetadata(customMeta);
				else
					newStack.setMetadata(stack.getMetadata());

				newStack.stackSize = stack.stackSize;
				blockReinforcer.damageItem(stack.stackSize, player);
				player.dropPlayerItemWithRandomChoice(newStack, false);
			}
		}
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int id)
	{
		ItemStack stack = null;
		Slot slot = (Slot) inventorySlots.get(id);

		if(slot != null && slot.getHasStack())
		{
			ItemStack stack1 = slot.getStack();

			stack = stack1.copy();

			if(id < 1)
			{
				if(!mergeItemStack(stack1, 1, 37, true))
					return null;
				slot.onSlotChange(stack1, stack);
			}
			else if(id >= 1)
				if(!mergeItemStack(stack1, 0, 1, false))
					return null;

			if(stack1.stackSize == 0)
				slot.putStack((ItemStack) null);
			else
				slot.onSlotChanged();

			if(stack1.stackSize == stack.stackSize)
				return null;
			slot.onPickupFromSlot(player, stack1);
		}

		return stack;
	}

	//edited to check if the item to be merged is valid in that slot
	@Override
	protected boolean mergeItemStack(ItemStack stack, int p_75135_2_, int p_75135_3_, boolean p_75135_4_)
	{
		boolean flag1 = false;
		int k = p_75135_2_;

		if(p_75135_4_)
			k = p_75135_3_ - 1;

		Slot slot;
		ItemStack itemstack1;

		if(stack.isStackable())
			while(stack.stackSize > 0 && (!p_75135_4_ && k < p_75135_3_ || p_75135_4_ && k >= p_75135_2_))
			{
				slot = (Slot)inventorySlots.get(k);
				itemstack1 = slot.getStack();

				if(!slot.isItemValid(stack))
				{
					flag1 = false;
					break;
				}

				if(itemstack1 != null && itemstack1.getItem() == stack.getItem() && (!stack.getHasSubtypes() || stack.getMetadata() == itemstack1.getMetadata()) && ItemStack.areItemStackTagsEqual(stack, itemstack1))
				{
					int l = itemstack1.stackSize + stack.stackSize;

					if(l <= stack.getMaxStackSize())
					{
						stack.stackSize = 0;
						itemstack1.stackSize = l;
						slot.onSlotChanged();
						flag1 = true;
					}
					else if(itemstack1.stackSize < stack.getMaxStackSize())
					{
						stack.stackSize -= stack.getMaxStackSize() - itemstack1.stackSize;
						itemstack1.stackSize = stack.getMaxStackSize();
						slot.onSlotChanged();
						flag1 = true;
					}
				}

				if(p_75135_4_)
					--k;
				else
					++k;
			}

		if(stack.stackSize > 0)
		{
			if(p_75135_4_)
				k = p_75135_3_ - 1;
			else
				k = p_75135_2_;

			while(!p_75135_4_ && k < p_75135_3_ || p_75135_4_ && k >= p_75135_2_)
			{
				slot = (Slot)inventorySlots.get(k);
				itemstack1 = slot.getStack();

				if(!slot.isItemValid(stack))
				{
					flag1 = false;
					break;
				}

				if(itemstack1 == null)
				{
					slot.putStack(stack.copy());
					slot.onSlotChanged();
					stack.stackSize = 0;
					flag1 = true;
					break;
				}

				if(p_75135_4_)
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
					(blockReinforcer.getMaxDurability() == 0 ? true : //lvl3
						blockReinforcer.getMaxDurability() - blockReinforcer.getMetadata() >= stack.stackSize + (getHasStack() ? getStack().stackSize : 0)); //disallow putting in items that can't be handled by the ubr
		}
	}
}
