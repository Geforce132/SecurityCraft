package net.geforcemods.securitycraft.screen;

import java.util.List;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.inventory.KeypadBlastFurnaceMenu;
import net.minecraft.client.gui.screens.inventory.AbstractFurnaceScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.SearchRecipeBookCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeBookCategories;

public class KeypadBlastFurnaceScreen extends AbstractFurnaceScreen<KeypadBlastFurnaceMenu> {
	private static final ResourceLocation LIT_PROGRESS_SPRITE = SecurityCraft.mcResLoc("container/blast_furnace/lit_progress");
	private static final ResourceLocation BURN_PROGRESS_SPRITE = SecurityCraft.mcResLoc("container/blast_furnace/burn_progress");
	private static final ResourceLocation TEXTURE = SecurityCraft.mcResLoc("textures/gui/container/blast_furnace.png");
	private static final Component FILTER_NAME = Component.translatable("gui.recipebook.toggleRecipes.blastable");
	//@formatter:off
    private static final List<RecipeBookComponent.TabInfo> TABS = List.of(
        new RecipeBookComponent.TabInfo(SearchRecipeBookCategory.BLAST_FURNACE),
        new RecipeBookComponent.TabInfo(Items.REDSTONE_ORE, RecipeBookCategories.BLAST_FURNACE_BLOCKS),
        new RecipeBookComponent.TabInfo(Items.IRON_SHOVEL, Items.GOLDEN_LEGGINGS, RecipeBookCategories.BLAST_FURNACE_MISC));
    //@formatter:on

	public KeypadBlastFurnaceScreen(KeypadBlastFurnaceMenu menu, Inventory inv, Component title) {
		super(menu, inv, menu.be.hasCustomName() ? menu.be.getCustomName() : title, FILTER_NAME, TEXTURE, LIT_PROGRESS_SPRITE, BURN_PROGRESS_SPRITE, TABS);
	}
}