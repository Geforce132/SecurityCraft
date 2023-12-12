package net.geforcemods.securitycraft.inventory;

import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

public class InsertOnlySidedInvWrapper extends SidedInvWrapper {
	public InsertOnlySidedInvWrapper(ISidedInventory inv, Direction side) {
		super(inv, side);
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		return ItemStack.EMPTY;
	}

	public static LazyOptional<IItemHandlerModifiable>[] create(ISidedInventory inv, Direction... sides) {
		LazyOptional<IItemHandlerModifiable>[] ret = new LazyOptional[sides.length];

		for (int i = 0; i < sides.length; i++) {
			Direction side = sides[i];

			ret[i] = LazyOptional.of(() -> new InsertOnlySidedInvWrapper(inv, side));
		}

		return ret;
	}
}
