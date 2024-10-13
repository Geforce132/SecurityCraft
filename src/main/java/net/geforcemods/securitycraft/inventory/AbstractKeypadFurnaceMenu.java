package net.geforcemods.securitycraft.inventory;

import net.geforcemods.securitycraft.blockentities.AbstractKeypadFurnaceBlockEntity;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipePropertySet;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;

public abstract class AbstractKeypadFurnaceMenu extends AbstractFurnaceMenu {
	private final Block furnaceBlock;
	public final AbstractKeypadFurnaceBlockEntity be;
	private final ContainerLevelAccess containerLevelAccess;

	protected AbstractKeypadFurnaceMenu(MenuType<?> menuType, RecipeType<? extends AbstractCookingRecipe> recipeType, ResourceKey<RecipePropertySet> recipePropertySet, RecipeBookType recipeBookType, int windowId, Inventory inventory, AbstractKeypadFurnaceBlockEntity be) {
		super(menuType, recipeType, recipePropertySet, recipeBookType, windowId, inventory, be, be.getFurnaceData());

		furnaceBlock = be.getBlockState().getBlock();
		this.be = be;
		containerLevelAccess = ContainerLevelAccess.create(be.getLevel(), be.getBlockPos());
		be.startOpen(inventory.player);
	}

	@Override
	public boolean stillValid(Player player) {
		return stillValid(containerLevelAccess, player, furnaceBlock);
	}

	@Override
	public void removed(Player player) {
		super.removed(player);
		be.stopOpen(player);
	}
}
