package net.geforcemods.securitycraft.blockentities;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.inventory.KeypadFurnaceMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.state.BlockState;

public class KeypadFurnaceBlockEntity extends AbstractKeypadFurnaceBlockEntity {
	public KeypadFurnaceBlockEntity(BlockPos pos, BlockState state) {
		super(SCContent.KEYPAD_FURNACE_BLOCK_ENTITY.get(), pos, state, RecipeType.SMELTING);
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inv) {
		return new KeypadFurnaceMenu(windowId, level, worldPosition, inv, this, dataAccess);
	}
}
