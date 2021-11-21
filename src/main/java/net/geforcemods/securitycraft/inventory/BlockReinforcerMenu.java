package net.geforcemods.securitycraft.inventory;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blocks.reinforced.IReinforcedBlock;
import net.geforcemods.securitycraft.items.UniversalBlockReinforcerItem;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class BlockReinforcerMenu extends AbstractContainerMenu
{
	private final ItemStack blockReinforcer;
	private final SimpleContainer itemInventory = new SimpleContainer(2);
	public final SlotBlockReinforcer reinforcingSlot;
	public final SlotBlockReinforcer unreinforcingSlot;
	public final boolean isLvl1;

	public BlockReinforcerMenu(int windowId, Inventory inventory, boolean isLvl1)
	{
		super(SCContent.mTypeBlockReinforcer, windowId);

		blockReinforcer = inventory.getSelected().getItem() instanceof UniversalBlockReinforcerItem ? inventory.getSelected() : inventory.offhand.get(0);
		this.isLvl1 = isLvl1;

		//main player inventory
		for(int i = 0; i < 3; i++)
			for(int j = 0; j < 9; j++)
				addSlot(new Slot(inventory, 9 + j + i * 9, 8 + j * 18, 84 + i * 18));

		//player hotbar
		for(int i = 0; i < 9; i++)
			addSlot(new Slot(inventory, i, 8 + i * 18, 142));

		addSlot(reinforcingSlot = new SlotBlockReinforcer(itemInventory, 0, 26, 20, true));

		if(!isLvl1)
			addSlot(unreinforcingSlot = new SlotBlockReinforcer(itemInventory, 1, 26, 45, false));
		else
			unreinforcingSlot = null;
	}

	@Override
	public boolean stillValid(Player player)
	{
		return true;
	}

	@Override
	public void removed(Player player)
	{
		if(!player.isAlive() || player instanceof ServerPlayer serverPlayer && serverPlayer.hasDisconnected())
		{
			for(int slot = 0; slot < itemInventory.getContainerSize(); ++slot)
			{
				player.drop(itemInventory.removeItemNoUpdate(slot), false);
			}

			return;
		}

		if(!itemInventory.getItem(0).isEmpty())
		{
			if (itemInventory.getItem(0).getCount() > reinforcingSlot.output.getCount()) { //if there's more in the slot than the reinforcer can reinforce (due to durability)
				ItemStack overflowStack = itemInventory.getItem(0).copy();

				overflowStack.setCount(itemInventory.getItem(0).getCount() - reinforcingSlot.output.getCount());
				player.drop(overflowStack, false);
			}

			player.drop(reinforcingSlot.output, false);
			blockReinforcer.hurtAndBreak(reinforcingSlot.output.getCount(), player, p -> p.broadcastBreakEvent(p.getUsedItemHand()));
		}

		if(!isLvl1 && !itemInventory.getItem(1).isEmpty())
		{
			if (itemInventory.getItem(1).getCount() > unreinforcingSlot.output.getCount()) {
				ItemStack overflowStack = itemInventory.getItem(1).copy();

				overflowStack.setCount(itemInventory.getItem(1).getCount() - unreinforcingSlot.output.getCount());
				player.drop(overflowStack, false);
			}

			player.drop(unreinforcingSlot.output, false);
			blockReinforcer.hurtAndBreak(unreinforcingSlot.output.getCount(), player, p -> p.broadcastBreakEvent(p.getUsedItemHand()));
		}
	}

	@Override
	public ItemStack quickMoveStack(Player player, int id)
	{
		ItemStack slotStackCopy = ItemStack.EMPTY;
		Slot slot = slots.get(id);

		if(slot != null && slot.hasItem())
		{
			ItemStack slotStack = slot.getItem();

			slotStackCopy = slotStack.copy();

			if(id >= 36)
			{
				if(!moveItemStackTo(slotStack, 0, 36, true))
					return ItemStack.EMPTY;
				slot.onQuickCraft(slotStack, slotStackCopy);
			}
			else if(id < 36)
				if(!moveItemStackTo(slotStack, 36, fixSlot(38), false))
					return ItemStack.EMPTY;

			if(slotStack.getCount() == 0)
				slot.set(ItemStack.EMPTY);
			else
				slot.setChanged();

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
	protected boolean moveItemStackTo(ItemStack stack, int startIndex, int endIndex, boolean useEndIndex)
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
				slot = slots.get(currentIndex);
				slotStack = slot.getItem();

				if(!slotStack.isEmpty() && ItemStack.isSameItemSameTags(stack, slotStack) && slot.mayPlace(stack))
				{
					int combinedCount = slotStack.getCount() + stack.getCount();

					if(combinedCount <= stack.getMaxStackSize())
					{
						stack.setCount(0);
						slotStack.setCount(combinedCount);
						slot.setChanged();
						merged = true;
					}
					else if(slotStack.getCount() < stack.getMaxStackSize())
					{
						stack.shrink(stack.getMaxStackSize() - slotStack.getCount());
						slotStack.setCount(stack.getMaxStackSize());
						slot.setChanged();
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
				slot = slots.get(currentIndex);
				slotStack = slot.getItem();

				if(slotStack.isEmpty() && slot.mayPlace(stack))
				{
					slot.set(stack.copy());
					slot.setChanged();
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

	@Override
	public void clicked(int slot, int dragType, ClickType clickType, Player player) {
		if(slot >= 0 && !player.getMainHandItem().isEmpty() && getSlot(slot).getItem() == player.getMainHandItem() && player.getMainHandItem().getItem() instanceof UniversalBlockReinforcerItem)
			return;
		else
			super.clicked(slot, dragType, clickType, player);
	}

	public class SlotBlockReinforcer extends Slot
	{
		private final boolean reinforce;
		private ItemStack output = ItemStack.EMPTY;

		public SlotBlockReinforcer(Container inventory, int index, int x, int y, boolean reinforce)
		{
			super(inventory, index, x, y);

			this.reinforce = reinforce;
		}

		@Override
		public boolean mayPlace(ItemStack stack)
		{
			//can only reinforce OR unreinforce at once
			if(!itemInventory.getItem((index + 1) % 2).isEmpty())
				return false;

			return (reinforce ? IReinforcedBlock.VANILLA_TO_SECURITYCRAFT : IReinforcedBlock.SECURITYCRAFT_TO_VANILLA).containsKey(Block.byItem(stack.getItem()));
		}

		@Override
		public void setChanged()
		{
			ItemStack stack = itemInventory.getItem(index % 2);

			if(!stack.isEmpty())
			{
				Block block = (reinforce ? IReinforcedBlock.VANILLA_TO_SECURITYCRAFT : IReinforcedBlock.SECURITYCRAFT_TO_VANILLA).get(Block.byItem(stack.getItem()));

				if(block != null)
				{
					boolean isLvl3 = blockReinforcer.getItem() == SCContent.UNIVERSAL_BLOCK_REINFORCER_LVL_3.get();

					output = new ItemStack(block);
					output.setCount(isLvl3 ? stack.getCount() : Math.min(stack.getCount(), blockReinforcer.getMaxDamage() - blockReinforcer.getDamageValue()));
				}
			}
		}

		public ItemStack getOutput()
		{
			return output;
		}
	}
}
