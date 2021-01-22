package net.geforcemods.securitycraft.containers;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blocks.reinforced.IReinforcedBlock;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class ContainerBlockReinforcer extends Container
{
	private ItemStack blockReinforcer;
	private InventoryBasic itemInventory = new InventoryBasic("BlockReinforcer", true, 2);
	public final SlotBlockReinforcer reinforcingSlot;
	public final SlotBlockReinforcer unreinforcingSlot;
	public final boolean isLvl1;

	public ContainerBlockReinforcer(EntityPlayer player, InventoryPlayer inventory, boolean isLvl1)
	{
		blockReinforcer = player.inventory.getCurrentItem();

		//main player inventory
		for(int i = 0; i < 3; i++)
			for(int j = 0; j < 9; j++)
				addSlotToContainer(new Slot(inventory, 9 + j + i * 9, 8 + j * 18, 84 + i * 18));

		//player hotbar
		for(int i = 0; i < 9; i++)
			addSlotToContainer(new Slot(inventory, i, 8 + i * 18, 142));

		this.isLvl1 = isLvl1;
		addSlotToContainer(reinforcingSlot = new SlotBlockReinforcer(itemInventory, 0, 26, 20, true));

		if(!isLvl1)
			addSlotToContainer(unreinforcingSlot = new SlotBlockReinforcer(itemInventory, 1, 26, 45, false));
		else
			unreinforcingSlot = null;
	}

	@Override
	public boolean canInteractWith(EntityPlayer player)
	{
		return true;
	}

	@Override
	public void onContainerClosed(EntityPlayer player)
	{
		if(!itemInventory.getStackInSlot(0).isEmpty())
		{
			player.dropItem(reinforcingSlot.output, false);
			blockReinforcer.damageItem(reinforcingSlot.output.getCount(), player);
		}

		if(!isLvl1 && !itemInventory.getStackInSlot(1).isEmpty())
		{
			player.dropItem(unreinforcingSlot.output, false);
			blockReinforcer.damageItem(unreinforcingSlot.output.getCount(), player);
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

			if(id >= 36)
			{
				if(!mergeItemStack(slotStack, 0, 36, true))
					return ItemStack.EMPTY;
				slot.onSlotChange(slotStack, slotStackCopy);
			}
			else if(id < 36)
				if(!mergeItemStack(slotStack, 36, fixSlot(38), false))
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

	private int fixSlot(int slot)
	{
		return isLvl1 ? slot - 1 : slot;
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

				if(!slotStack.isEmpty() && slotStack.getItem() == stack.getItem() && (!stack.getHasSubtypes() || stack.getMetadata() == slotStack.getMetadata()) && ItemStack.areItemStackTagsEqual(stack, slotStack) && slot.isItemValid(stack))
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

				if(slotStack.isEmpty() && slot.isItemValid(stack))
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

	public class SlotBlockReinforcer extends Slot
	{
		private final boolean reinforce;
		private ItemStack output = ItemStack.EMPTY;

		public SlotBlockReinforcer(IInventory inventory, int index, int x, int y, boolean reinforce)
		{
			super(inventory, index, x, y);

			this.reinforce = reinforce;
		}

		@Override
		public boolean isItemValid(ItemStack stack)
		{
			//can only reinforce OR unreinforce at once
			if(!itemInventory.getStackInSlot((slotNumber + 1) % 2).isEmpty())
				return false;

			boolean validBlock = IReinforcedBlock.BLOCKS.stream().anyMatch(reinforcedBlock -> {
				if(reinforce)
					return ((IReinforcedBlock)reinforcedBlock).getVanillaBlocks().stream().anyMatch(vanillaBlock -> stack.getItem().equals(Item.getItemFromBlock(vanillaBlock)));
				else
				{
					NonNullList<ItemStack> subBlocks = NonNullList.create();

					reinforcedBlock.getSubBlocks(SecurityCraft.tabSCDecoration, subBlocks);
					return subBlocks.stream().anyMatch(subBlock -> stack.getMetadata() == subBlock.getMetadata() && stack.getItem() == subBlock.getItem());
				}
			});

			return validBlock &&
					(blockReinforcer.getMaxDamage() == 0 ? true : //lvl3
						blockReinforcer.getMaxDamage() - blockReinforcer.getItemDamage() >= stack.getCount() + (getHasStack() ? getStack().getCount() : 0)); //disallow putting in items that can't be handled by the ubr
		}


		@Override
		public void onSlotChanged()
		{
			ItemStack stack = itemInventory.getStackInSlot(slotNumber % 2);

			if(!stack.isEmpty())
			{
				Item item = stack.getItem();
				ItemStack newStack = ItemStack.EMPTY;
				int customMeta = -1;

				for(Block rb : IReinforcedBlock.BLOCKS)
				{
					IReinforcedBlock block = (IReinforcedBlock)rb;

					if(reinforce && block.getVanillaBlocks().contains(Block.getBlockFromItem(item)))
					{
						newStack = new ItemStack(rb);

						if(block.getVanillaBlocks().size() == block.getAmount())
							customMeta = block.getVanillaBlocks().indexOf(Block.getBlockFromItem(item));
					}
					else if(!reinforce && rb == ((ItemBlock)stack.getItem()).getBlock())
					{
						if(block.getVanillaBlocks().size() == block.getAmount())
						{
							newStack = new ItemStack(block.getVanillaBlocks().get(stack.getMetadata()));
							customMeta = -2;
						}
						else
							newStack = new ItemStack(block.getVanillaBlocks().get(0), 1, stack.getMetadata());
					}
				}

				if(!newStack.isEmpty())
				{
					if(customMeta != -1)
						newStack.setItemDamage(customMeta);
					else if(customMeta != -2)
						newStack.setItemDamage(stack.getItemDamage());

					newStack.setCount(stack.getCount());
					output = newStack;
				}
			}
		}

		public ItemStack getOutput()
		{
			return output;
		}
	}
}
