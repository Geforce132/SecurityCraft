package net.geforcemods.securitycraft.containers;

import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
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
		ItemStack stack = itemInventory.removeStackFromSlot(0);

		if(stack != null)
		{
			Item item = stack.getItem();
			ItemStack newStack = null;
			int customMeta = 0;

			if(item.equals(Item.getItemFromBlock(Blocks.dirt)) || item.equals(Item.getItemFromBlock(Blocks.grass)))
				newStack = new ItemStack(mod_SecurityCraft.reinforcedDirt);
			else if(item.equals(Item.getItemFromBlock(Blocks.stone)))
			{
				stack.setItemDamage(0);
				newStack = new ItemStack(mod_SecurityCraft.reinforcedStone);
			}
			else if(item.equals(Item.getItemFromBlock(Blocks.planks)))
				newStack = new ItemStack(mod_SecurityCraft.reinforcedWoodPlanks);
			else if(item.equals(Item.getItemFromBlock(Blocks.glass)))
				newStack = new ItemStack(mod_SecurityCraft.reinforcedGlass);
			else if(item.equals(Item.getItemFromBlock(Blocks.glass_pane)))
				newStack = new ItemStack(mod_SecurityCraft.reinforcedGlassPane);
			else if(item.equals(Item.getItemFromBlock(Blocks.cobblestone)))
				newStack = new ItemStack(mod_SecurityCraft.reinforcedCobblestone);
			else if(item.equals(Item.getItemFromBlock(Blocks.iron_bars)))
				newStack = new ItemStack(mod_SecurityCraft.unbreakableIronBars);
			else if(item.equals(Item.getItemFromBlock(Blocks.sandstone)))
				newStack = new ItemStack(mod_SecurityCraft.reinforcedSandstone);
			else if(item.equals(Item.getItemFromBlock(Blocks.stonebrick)))
				newStack = new ItemStack(mod_SecurityCraft.reinforcedStoneBrick);
			else if(item.equals(Item.getItemFromBlock(Blocks.mossy_cobblestone)))
				newStack = new ItemStack(mod_SecurityCraft.reinforcedMossyCobblestone);
			else if(item.equals(Item.getItemFromBlock(Blocks.brick_block)))
				newStack = new ItemStack(mod_SecurityCraft.reinforcedBrick);
			else if(item.equals(Item.getItemFromBlock(Blocks.nether_brick)))
				newStack = new ItemStack(mod_SecurityCraft.reinforcedNetherBrick);
			else if(item.equals(Item.getItemFromBlock(Blocks.hardened_clay)))
				newStack = new ItemStack(mod_SecurityCraft.reinforcedHardenedClay);
			else if(item.equals(Item.getItemFromBlock(Blocks.stained_hardened_clay)))
				newStack = new ItemStack(mod_SecurityCraft.reinforcedStainedHardenedClay);
			else if(item.equals(Item.getItemFromBlock(Blocks.log)))
				newStack = new ItemStack(mod_SecurityCraft.reinforcedOldLogs);
			else if(item.equals(Item.getItemFromBlock(Blocks.log2)))
				newStack = new ItemStack(mod_SecurityCraft.reinforcedNewLogs);
			else if(item.equals(Item.getItemFromBlock(Blocks.lapis_block)))
			{
				newStack = new ItemStack(mod_SecurityCraft.reinforcedCompressedBlocks);
				customMeta = 0;
			}
			else if(item.equals(Item.getItemFromBlock(Blocks.coal_block)))
			{
				newStack = new ItemStack(mod_SecurityCraft.reinforcedCompressedBlocks);
				customMeta = 1;
			}
			else if(item.equals(Item.getItemFromBlock(Blocks.gold_block)))
			{
				newStack = new ItemStack(mod_SecurityCraft.reinforcedMetals);
				customMeta = 0;
			}
			else if(item.equals(Item.getItemFromBlock(Blocks.iron_block)))
			{
				newStack = new ItemStack(mod_SecurityCraft.reinforcedMetals);
				customMeta = 1;
			}
			else if(item.equals(Item.getItemFromBlock(Blocks.diamond_block)))
			{
				newStack = new ItemStack(mod_SecurityCraft.reinforcedMetals);
				customMeta = 2;
			}
			else if(item.equals(Item.getItemFromBlock(Blocks.emerald_block)))
			{
				newStack = new ItemStack(mod_SecurityCraft.reinforcedMetals);
				customMeta = 3;
			}
			else if(item.equals(Item.getItemFromBlock(Blocks.wool)))
				newStack = new ItemStack(mod_SecurityCraft.reinforcedWool);
			else if(item.equals(Item.getItemFromBlock(Blocks.quartz_block)))
				newStack = new ItemStack(mod_SecurityCraft.reinforcedQuartz);
			else if(item.equals(Item.getItemFromBlock(Blocks.prismarine)))
				newStack = new ItemStack(mod_SecurityCraft.reinforcedPrismarine);
			else if(item.equals(Item.getItemFromBlock(Blocks.red_sandstone)))
				newStack = new ItemStack(mod_SecurityCraft.reinforcedRedSandstone);

			if(newStack != null)
			{
				if(Block.getBlockFromItem(newStack.getItem()) == mod_SecurityCraft.reinforcedMetals || Block.getBlockFromItem(newStack.getItem()) == mod_SecurityCraft.reinforcedCompressedBlocks)
					newStack.setItemDamage(customMeta);
				else
					newStack.setItemDamage(stack.getItemDamage());

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
		Slot slot = inventorySlots.get(id);

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
	protected boolean mergeItemStack(ItemStack stack, int startIndex, int endIndex, boolean useEndIndex)
	{
		boolean flag1 = false;
		int k = startIndex;

		if(useEndIndex)
			k = endIndex - 1;

		Slot slot;
		ItemStack itemstack1;

		if(stack.isStackable())
			while(stack.stackSize > 0 && (!useEndIndex && k < endIndex || useEndIndex && k >= startIndex))
			{
				slot = inventorySlots.get(k);
				itemstack1 = slot.getStack();

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

				if(useEndIndex)
					--k;
				else
					++k;
			}

		if(stack.stackSize > 0)
		{
			if(useEndIndex)
				k = endIndex - 1;
			else
				k = startIndex;

			while(!useEndIndex && k < endIndex || useEndIndex && k >= startIndex)
			{
				slot = inventorySlots.get(k);
				itemstack1 = slot.getStack();

				if(itemstack1 == null && slot.isItemValid(stack)) // Forge: Make sure to respect isItemValid in the slot.
				{
					slot.putStack(stack.copy());
					slot.onSlotChanged();
					stack.stackSize = 0;
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
			Item item = stack.getItem();

			return (item.equals(Item.getItemFromBlock(Blocks.dirt)) ||
					item.equals(Item.getItemFromBlock(Blocks.grass)) ||
					item.equals(Item.getItemFromBlock(Blocks.stone)) ||
					item.equals(Item.getItemFromBlock(Blocks.planks)) ||
					item.equals(Item.getItemFromBlock(Blocks.glass)) ||
					item.equals(Item.getItemFromBlock(Blocks.glass_pane)) ||
					item.equals(Item.getItemFromBlock(Blocks.cobblestone)) ||
					item.equals(Item.getItemFromBlock(Blocks.iron_bars)) ||
					item.equals(Item.getItemFromBlock(Blocks.sandstone)) ||
					item.equals(Item.getItemFromBlock(Blocks.stonebrick)) ||
					item.equals(Item.getItemFromBlock(Blocks.mossy_cobblestone)) ||
					item.equals(Item.getItemFromBlock(Blocks.brick_block)) ||
					item.equals(Item.getItemFromBlock(Blocks.nether_brick)) ||
					item.equals(Item.getItemFromBlock(Blocks.hardened_clay)) ||
					item.equals(Item.getItemFromBlock(Blocks.stained_hardened_clay)) ||
					item.equals(Item.getItemFromBlock(Blocks.log)) ||
					item.equals(Item.getItemFromBlock(Blocks.log2)) ||
					item.equals(Item.getItemFromBlock(Blocks.lapis_block)) ||
					item.equals(Item.getItemFromBlock(Blocks.coal_block)) ||
					item.equals(Item.getItemFromBlock(Blocks.gold_block)) ||
					item.equals(Item.getItemFromBlock(Blocks.iron_block)) ||
					item.equals(Item.getItemFromBlock(Blocks.diamond_block)) ||
					item.equals(Item.getItemFromBlock(Blocks.emerald_block)) ||
					item.equals(Item.getItemFromBlock(Blocks.wool)) ||
					item.equals(Item.getItemFromBlock(Blocks.quartz_block)) ||
					item.equals(Item.getItemFromBlock(Blocks.prismarine)) ||
					item.equals(Item.getItemFromBlock(Blocks.red_sandstone))) &&
					(blockReinforcer.getMaxDamage() == 0 ? true : //lvl3
						blockReinforcer.getMaxDamage() - blockReinforcer.getItemDamage() >= stack.stackSize + (getHasStack() ? getStack().stackSize : 0)); //disallow putting in items that can't be handled by the ubr
		}
	}
}
