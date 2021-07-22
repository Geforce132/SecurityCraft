package net.geforcemods.securitycraft.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.wrapper.InvWrapper;

public class InsertOnlyInvWrapper extends InvWrapper
{
	public InsertOnlyInvWrapper(Container inv)
	{
		super(inv);
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate)
	{
		return ItemStack.EMPTY;
	}
}
