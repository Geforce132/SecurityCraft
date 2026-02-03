package net.geforcemods.securitycraft.blockentities;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.inventory.KeypadBlastFurnaceMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.FuelValues;
import net.minecraft.world.level.block.state.BlockState;

public class KeypadBlastFurnaceBlockEntity extends AbstractKeypadFurnaceBlockEntity {
	public KeypadBlastFurnaceBlockEntity(BlockPos pos, BlockState state) {
		super(SCContent.KEYPAD_BLAST_FURNACE_BLOCK_ENTITY.get(), pos, state, RecipeType.BLASTING);
	}

	@Override
	protected int getBurnDuration(FuelValues fuelValues, ItemStack fuel) {
		return super.getBurnDuration(fuelValues, fuel) / 2;
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inv) {
		return new KeypadBlastFurnaceMenu(windowId, inv, this);
	}
}
