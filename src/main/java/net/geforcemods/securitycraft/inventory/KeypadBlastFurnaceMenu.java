package net.geforcemods.securitycraft.inventory;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blockentities.AbstractKeypadFurnaceBlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeBookCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class KeypadBlastFurnaceMenu extends AbstractKeypadFurnaceMenu {
	public KeypadBlastFurnaceMenu(int windowId, World world, BlockPos pos, PlayerInventory inventory) {
		this(windowId, inventory, (AbstractKeypadFurnaceBlockEntity) world.getBlockEntity(pos));
	}

	public KeypadBlastFurnaceMenu(int windowId, PlayerInventory inventory, AbstractKeypadFurnaceBlockEntity be) {
		super(SCContent.KEYPAD_BLAST_FURNACE_MENU.get(), IRecipeType.BLASTING, RecipeBookCategory.BLAST_FURNACE, windowId, inventory, be);
	}
}