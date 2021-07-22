package net.geforcemods.securitycraft.inventory;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
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
