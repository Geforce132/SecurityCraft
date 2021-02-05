package net.geforcemods.securitycraft.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.ItemStackHandler;

public class InsertOnlyItemStackHandler extends ItemStackHandler
{
	public InsertOnlyItemStackHandler(NonNullList<ItemStack> stacks)
	{
		super(stacks);
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate)
	{
		return ItemStack.EMPTY;
	}
}
