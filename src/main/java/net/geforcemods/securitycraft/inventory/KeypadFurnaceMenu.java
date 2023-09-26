package net.geforcemods.securitycraft.inventory;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blockentities.AbstractKeypadFurnaceBlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeBookCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class KeypadFurnaceMenu extends AbstractKeypadFurnaceMenu {
	public KeypadFurnaceMenu(int windowId, World level, BlockPos pos, PlayerInventory inventory) {
		this(windowId, inventory, (AbstractKeypadFurnaceBlockEntity) level.getBlockEntity(pos));
	}

	public KeypadFurnaceMenu(int windowId, PlayerInventory inventory, AbstractKeypadFurnaceBlockEntity be) {
		super(SCContent.KEYPAD_FURNACE_MENU.get(), IRecipeType.SMELTING, RecipeBookCategory.FURNACE, windowId, inventory, be);
	}
}