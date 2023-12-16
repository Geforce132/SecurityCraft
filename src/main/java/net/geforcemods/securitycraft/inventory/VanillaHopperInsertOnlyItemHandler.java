package net.geforcemods.securitycraft.inventory;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraftforge.items.VanillaHopperItemHandler;

public class VanillaHopperInsertOnlyItemHandler extends VanillaHopperItemHandler {
	public VanillaHopperInsertOnlyItemHandler(HopperBlockEntity hopper) {
		super(hopper);
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		return ItemStack.EMPTY;
	}
}
