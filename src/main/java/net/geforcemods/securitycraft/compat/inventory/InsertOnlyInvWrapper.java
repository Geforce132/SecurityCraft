package net.geforcemods.securitycraft.compat.inventory;

import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.wrapper.InvWrapper;

public class InsertOnlyInvWrapper extends InvWrapper
{
	public InsertOnlyInvWrapper(ISidedInventory inv)
	{
		super(inv);
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate)
	{
		return ItemStack.EMPTY;
	}
}
