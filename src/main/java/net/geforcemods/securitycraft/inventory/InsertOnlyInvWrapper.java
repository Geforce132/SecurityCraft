package net.geforcemods.securitycraft.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.wrapper.InvWrapper;

public class InsertOnlyInvWrapper extends InvWrapper
{
	public InsertOnlyInvWrapper(IInventory inv)
	{
		super(inv);
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate)
	{
		return ItemStack.EMPTY;
	}
}
