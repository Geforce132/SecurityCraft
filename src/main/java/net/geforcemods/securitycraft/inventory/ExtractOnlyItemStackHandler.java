package net.geforcemods.securitycraft.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.ItemStackHandler;

public class ExtractOnlyItemStackHandler extends ItemStackHandler
{
	public ExtractOnlyItemStackHandler(NonNullList<ItemStack> stacks)
	{
		super(stacks);
	}

	@Override
	public void setStackInSlot(int slot, ItemStack stack) {}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
	{
		return stack;
	}

	@Override
	public boolean isItemValid(int slot, ItemStack stack)
	{
		return false;
	}
}
