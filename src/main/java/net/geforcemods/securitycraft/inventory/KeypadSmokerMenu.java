package net.geforcemods.securitycraft.inventory;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blockentities.AbstractKeypadFurnaceBlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeBookCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class KeypadSmokerMenu extends AbstractKeypadFurnaceMenu {
	public KeypadSmokerMenu(int windowId, World world, BlockPos pos, PlayerInventory inventory) {
		this(windowId, inventory, (AbstractKeypadFurnaceBlockEntity) world.getBlockEntity(pos));
	}

	public KeypadSmokerMenu(int windowId, PlayerInventory inventory, AbstractKeypadFurnaceBlockEntity be) {
		super(SCContent.KEYPAD_SMOKER_MENU.get(), IRecipeType.SMOKING, RecipeBookCategory.SMOKER, windowId, inventory, be);
	}
}