package net.geforcemods.securitycraft.inventory;

import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

public class InsertOnlySidedInvWrapper extends SidedInvWrapper {
	public InsertOnlySidedInvWrapper(ISidedInventory inv, EnumFacing side) {
		super(inv, side);
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		return ItemStack.EMPTY;
	}
}
