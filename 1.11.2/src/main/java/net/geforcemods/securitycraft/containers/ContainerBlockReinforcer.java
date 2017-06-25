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

		if(stack != null)
		{
			String name = stack.getItem().getUnlocalizedName();
			ItemStack newStack = ItemStack.EMPTY;
			
			if(name.equals(Item.getItemFromBlock(Blocks.DIRT).getUnlocalizedName()) || name.equals(Item.getItemFromBlock(Blocks.GRASS).getUnlocalizedName()))
				newStack = new ItemStack(mod_SecurityCraft.reinforcedDirt);
			else if(name.equals(Item.getItemFromBlock(Blocks.STONE).getUnlocalizedName()))
				newStack = new ItemStack(mod_SecurityCraft.reinforcedStone);
			else if(name.equals(Item.getItemFromBlock(Blocks.PLANKS).getUnlocalizedName()))
				newStack = new ItemStack(mod_SecurityCraft.reinforcedWoodPlanks);
			else if(name.equals(Item.getItemFromBlock(Blocks.GLASS).getUnlocalizedName()))
				newStack = new ItemStack(mod_SecurityCraft.reinforcedGlass);
			else if(name.equals(Item.getItemFromBlock(Blocks.COBBLESTONE).getUnlocalizedName()))
				newStack = new ItemStack(mod_SecurityCraft.reinforcedCobblestone);
			else if(name.equals(Item.getItemFromBlock(Blocks.IRON_BARS).getUnlocalizedName()))
				newStack = new ItemStack(mod_SecurityCraft.unbreakableIronBars);
			else if(name.equals(Item.getItemFromBlock(Blocks.SANDSTONE).getUnlocalizedName()))
				newStack = new ItemStack(mod_SecurityCraft.reinforcedSandstone);
			
			if(!stack.isEmpty())
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
			else
			{
				if(id >= 1)
				{
					if(!mergeItemStack(stack1, 0, 1, false))
						return null;
				}
			}

			if(stack1.getCount() == 0)
				slot.putStack((ItemStack) null);
			else
				slot.onSlotChanged();

			if(stack1.getCount() == stack.getCount())
				return null;
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

                if(itemstack1 != null && itemstack1.getItem() == stack.getItem() && (!stack.getHasSubtypes() || stack.getMetadata() == itemstack1.getMetadata()) && ItemStack.areItemStackTagsEqual(stack, itemstack1))
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

                if(itemstack1 == null && slot.isItemValid(stack)) // Forge: Make sure to respect isItemValid in the slot.
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
			String name = stack.getItem().getUnlocalizedName();

			return (name.equals(Item.getItemFromBlock(Blocks.DIRT).getUnlocalizedName()) ||
					name.equals(Item.getItemFromBlock(Blocks.GRASS).getUnlocalizedName()) ||
					name.equals(Item.getItemFromBlock(Blocks.STONE).getUnlocalizedName()) ||
					name.equals(Item.getItemFromBlock(Blocks.PLANKS).getUnlocalizedName()) ||
					name.equals(Item.getItemFromBlock(Blocks.GLASS).getUnlocalizedName()) ||
					name.equals(Item.getItemFromBlock(Blocks.GLASS_PANE).getUnlocalizedName()) ||
					name.equals(Item.getItemFromBlock(Blocks.COBBLESTONE).getUnlocalizedName()) ||
					name.equals(Item.getItemFromBlock(Blocks.IRON_BARS).getUnlocalizedName()) ||
					name.equals(Item.getItemFromBlock(Blocks.SANDSTONE).getUnlocalizedName())) &&
					(blockReinforcer.getMaxDamage() == 0 ? true : //lvl3
						blockReinforcer.getMaxDamage() - blockReinforcer.getItemDamage() >= stack.getCount() + (getHasStack() ? getStack().getCount() : 0)); //disallow putting in items that can't be handled by the ubr
		}
	}
}
