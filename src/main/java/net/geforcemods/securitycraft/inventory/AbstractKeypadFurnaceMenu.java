package net.geforcemods.securitycraft.inventory;

import net.geforcemods.securitycraft.blockentities.AbstractKeypadFurnaceBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.AbstractFurnaceContainer;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.crafting.AbstractCookingRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeBookCategory;
import net.minecraft.util.IWorldPosCallable;

public abstract class AbstractKeypadFurnaceMenu extends AbstractFurnaceContainer {
	private final Block furnaceBlock;
	public final AbstractKeypadFurnaceBlockEntity be;
	private final IWorldPosCallable containerLevelAccess;

	protected AbstractKeypadFurnaceMenu(ContainerType<?> menuType, IRecipeType<? extends AbstractCookingRecipe> recipeType, RecipeBookCategory recipeBookType, int windowId, PlayerInventory inventory, AbstractKeypadFurnaceBlockEntity be) {
		super(menuType, recipeType, recipeBookType, windowId, inventory, be, be.getFurnaceData());

		furnaceBlock = be.getBlockState().getBlock();
		this.be = be;
		containerLevelAccess = IWorldPosCallable.create(be.getLevel(), be.getBlockPos());
		this.be.startOpen(inventory.player);
	}

	@Override
	public boolean stillValid(PlayerEntity player) {
		return stillValid(containerLevelAccess, player, furnaceBlock);
	}

	@Override
	public void removed(PlayerEntity player) {
		super.removed(player);
		be.stopOpen(player);
	}
}
