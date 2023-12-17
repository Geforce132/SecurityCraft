package net.geforcemods.securitycraft.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraftforge.items.VanillaHopperItemHandler;

public class VanillaHopperInsertOnlyItemHandler extends VanillaHopperItemHandler {
	public VanillaHopperInsertOnlyItemHandler(TileEntityHopper hopper) {
		super(hopper);
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		return ItemStack.EMPTY;
	}
}
