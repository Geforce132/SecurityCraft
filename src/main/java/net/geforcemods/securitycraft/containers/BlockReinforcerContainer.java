package net.geforcemods.securitycraft.containers;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blocks.reinforced.IReinforcedBlock;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

public class BlockReinforcerContainer extends Container
{
	private final ItemStack blockReinforcer;
	private final Inventory itemInventory = new Inventory(2);
	public final SlotBlockReinforcer reinforcingSlot;
	public final SlotBlockReinforcer unreinforcingSlot;

	public BlockReinforcerContainer(int windowId, PlayerInventory inventory)
	{
		super(SCContent.cTypeBlockReinforcer, windowId);

		blockReinforcer = inventory.getCurrentItem();
		addSlot(reinforcingSlot = new SlotBlockReinforcer(itemInventory, 0, 26, 20, true));
		addSlot(unreinforcingSlot = new SlotBlockReinforcer(itemInventory, 1, 26, 45, false));

		//main player inventory
		for(int i = 0; i < 3; i++)
			for(int j = 0; j < 9; j++)
				addSlot(new Slot(inventory, 9 + j + i * 9, 8 + j * 18, 84 + i * 18));

		//player hotbar
		for(int i = 0; i < 9; i++)
			addSlot(new Slot(inventory, i, 8 + i * 18, 142));
	}

	@Override
	public boolean canInteractWith(PlayerEntity player)
	{
		return true;
	}

	@Override
	public void onContainerClosed(PlayerEntity player)
	{
		if(!player.isAlive() || player instanceof ServerPlayerEntity && ((ServerPlayerEntity)player).hasDisconnected())
		{
			for(int slot = 0; slot < itemInventory.getSizeInventory(); ++slot)
			{
				player.dropItem(itemInventory.removeStackFromSlot(slot), false);
			}

			return;
		}

		if(!itemInventory.getStackInSlot(0).isEmpty())
		{
			player.dropItem(reinforcingSlot.output, false);
			blockReinforcer.damageItem(reinforcingSlot.output.getCount(), player, p -> p.sendBreakAnimation(p.getActiveHand()));
		}

		if(!itemInventory.getStackInSlot(1).isEmpty())
		{
			player.dropItem(unreinforcingSlot.output, false);
			blockReinforcer.damageItem(unreinforcingSlot.output.getCount(), player, p -> p.sendBreakAnimation(p.getActiveHand()));
		}
	}

	@Override
	public ItemStack transferStackInSlot(PlayerEntity player, int id)
	{
		ItemStack slotStackCopy = ItemStack.EMPTY;
		Slot slot = inventorySlots.get(id);

		if(slot != null && slot.getHasStack())
		{
			ItemStack slotStack = slot.getStack();

			slotStackCopy = slotStack.copy();

			if(id <= 1)
			{
				if(!mergeItemStack(slotStack, 2, 38, true))
					return ItemStack.EMPTY;
				slot.onSlotChange(slotStack, slotStackCopy);
			}
			else if(id > 1)
				if(!mergeItemStack(slotStack, 0, 2, false))
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

				if(!slotStack.isEmpty() && areItemsAndTagsEqual(stack, slotStack) && slot.isItemValid(stack))
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

			return (reinforce ? IReinforcedBlock.VANILLA_TO_SECURITYCRAFT : IReinforcedBlock.SECURITYCRAFT_TO_VANILLA).containsKey(Block.getBlockFromItem(stack.getItem())) &&
					(blockReinforcer.getMaxDamage() == 0 ? true : //lvl3
						blockReinforcer.getMaxDamage() - blockReinforcer.getDamage() >= stack.getCount() + (getHasStack() ? getStack().getCount() : 0)); //disallow putting in items that can't be handled by the ubr
		}

		@Override
		public void onSlotChanged()
		{
			ItemStack stack = itemInventory.getStackInSlot(slotNumber);

			if(!stack.isEmpty())
			{
				Block block = (reinforce ? IReinforcedBlock.VANILLA_TO_SECURITYCRAFT : IReinforcedBlock.SECURITYCRAFT_TO_VANILLA).get(Block.getBlockFromItem(stack.getItem()));

				if(block != null)
				{
					output = new ItemStack(block);
					output.setCount(stack.getCount());
				}
			}
		}

		public ItemStack getOutput()
		{
			return output;
		}
	}
}
