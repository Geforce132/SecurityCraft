package net.geforcemods.securitycraft.containers;

import net.geforcemods.securitycraft.main.mod_SecurityCraft;
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
		blockReinforcer = player.inventory.getCurrentItem();
		addSlotToContainer(new SlotBlockReinforcer(itemInventory, 0, 79, 20)); //input & output slot

		//main player inventory
		for(int i = 0; i < 3; i++)
		{
			for(int j = 0; j < 9; j++)
			{
				addSlotToContainer(new Slot(inventory, 9 + j + i * 9, 8 + j * 18, 84 + i * 18));
			}
		}

		//player hotbar
		for(int i = 0; i < 9; i++)
		{
			addSlotToContainer(new Slot(inventory, i, 8 + i * 18, 142));
		}
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
			
			if(item.equals(Item.getItemFromBlock(Blocks.DIRT)) || item.equals(Item.getItemFromBlock(Blocks.GRASS)))
				newStack = new ItemStack(mod_SecurityCraft.reinforcedDirt);
			else if(item.equals(Item.getItemFromBlock(Blocks.STONE)))
			{
				stack.setItemDamage(0);
				newStack = new ItemStack(mod_SecurityCraft.reinforcedStone);
			}
			else if(item.equals(Item.getItemFromBlock(Blocks.PLANKS)))
				newStack = new ItemStack(mod_SecurityCraft.reinforcedWoodPlanks);
			else if(item.equals(Item.getItemFromBlock(Blocks.GLASS)))
				newStack = new ItemStack(mod_SecurityCraft.reinforcedGlass);
			else if(item.equals(Item.getItemFromBlock(Blocks.COBBLESTONE)))
				newStack = new ItemStack(mod_SecurityCraft.reinforcedCobblestone);
			else if(item.equals(Item.getItemFromBlock(Blocks.IRON_BARS)))
				newStack = new ItemStack(mod_SecurityCraft.unbreakableIronBars);
			else if(item.equals(Item.getItemFromBlock(Blocks.SANDSTONE)))
				newStack = new ItemStack(mod_SecurityCraft.reinforcedSandstone);
			else if(item.equals(Item.getItemFromBlock(Blocks.STONEBRICK)))
				newStack = new ItemStack(mod_SecurityCraft.reinforcedStoneBrick);
			else if(item.equals(Item.getItemFromBlock(Blocks.MOSSY_COBBLESTONE)))
				newStack = new ItemStack(mod_SecurityCraft.reinforcedMossyCobblestone);
			else if(item.equals(Item.getItemFromBlock(Blocks.BRICK_BLOCK)))
				newStack = new ItemStack(mod_SecurityCraft.reinforcedBrick);
			else if(item.equals(Item.getItemFromBlock(Blocks.NETHER_BRICK)))
				newStack = new ItemStack(mod_SecurityCraft.reinforcedNetherBrick);
			else if(item.equals(Item.getItemFromBlock(Blocks.HARDENED_CLAY)))
				newStack = new ItemStack(mod_SecurityCraft.reinforcedHardenedClay);
			
			if(!newStack.isEmpty())
			{
				newStack.setCount(stack.getCount());
				newStack.setItemDamage(stack.getItemDamage());
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
			else
			{
				if(id >= 1)
				{
					if(!mergeItemStack(stack1, 0, 1, false))
						return ItemStack.EMPTY;
				}
			}

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
        {
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
			Item item = stack.getItem();

			return (item.equals(Item.getItemFromBlock(Blocks.DIRT)) ||
					item.equals(Item.getItemFromBlock(Blocks.GRASS)) ||
					item.equals(Item.getItemFromBlock(Blocks.STONE)) ||
					item.equals(Item.getItemFromBlock(Blocks.PLANKS)) ||
					item.equals(Item.getItemFromBlock(Blocks.GLASS)) ||
					item.equals(Item.getItemFromBlock(Blocks.COBBLESTONE)) ||
					item.equals(Item.getItemFromBlock(Blocks.IRON_BARS)) ||
					item.equals(Item.getItemFromBlock(Blocks.SANDSTONE)) ||
					item.equals(Item.getItemFromBlock(Blocks.STONEBRICK)) ||
					item.equals(Item.getItemFromBlock(Blocks.MOSSY_COBBLESTONE)) ||
					item.equals(Item.getItemFromBlock(Blocks.BRICK_BLOCK)) ||
					item.equals(Item.getItemFromBlock(Blocks.NETHER_BRICK)) ||
					item.equals(Item.getItemFromBlock(Blocks.HARDENED_CLAY))) &&
					(blockReinforcer.getMaxDamage() == 0 ? true : //lvl3
						blockReinforcer.getMaxDamage() - blockReinforcer.getItemDamage() >= stack.getCount() + (getHasStack() ? getStack().getCount() : 0)); //disallow putting in items that can't be handled by the ubr
		}
	}
}
