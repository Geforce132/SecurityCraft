package net.geforcemods.securitycraft.blockentities;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.inventory.KeypadBlastFurnaceMenu;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;

public class KeypadBlastFurnaceBlockEntity extends AbstractKeypadFurnaceBlockEntity {
	public KeypadBlastFurnaceBlockEntity() {
		super(SCContent.KEYPAD_BLAST_FURNACE_BLOCK_ENTITY.get(), IRecipeType.BLASTING);
	}

	@Override
	protected int getBurnDuration(ItemStack fuel) {
		return super.getBurnDuration(fuel) / 2;
	}

	@Override
	protected Container createMenu(int windowId, PlayerInventory inv) {
		return new KeypadBlastFurnaceMenu(windowId, inv, this);
	}
}
