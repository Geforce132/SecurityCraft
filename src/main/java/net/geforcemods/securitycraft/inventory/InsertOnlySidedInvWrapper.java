package net.geforcemods.securitycraft.inventory;

import net.minecraft.core.Direction;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.LazyOptional;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;

public class InsertOnlySidedInvWrapper extends SidedInvWrapper {
	public InsertOnlySidedInvWrapper(WorldlyContainer inv, Direction side) {
		super(inv, side);
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		return ItemStack.EMPTY;
	}

	public static LazyOptional<IItemHandlerModifiable>[] create(WorldlyContainer inv, Direction... sides) {
		LazyOptional<IItemHandlerModifiable>[] ret = new LazyOptional[sides.length];

		for (int i = 0; i < sides.length; i++) {
			Direction side = sides[i];

			ret[i] = LazyOptional.of(() -> new InsertOnlySidedInvWrapper(inv, side));
		}

		return ret;
	}
}
