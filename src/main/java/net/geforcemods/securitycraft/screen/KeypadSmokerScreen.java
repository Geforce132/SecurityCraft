package net.geforcemods.securitycraft.screen;

import java.util.List;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.inventory.KeypadSmokerMenu;
import net.minecraft.client.gui.screens.inventory.AbstractFurnaceScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.SearchRecipeBookCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeBookCategories;

public class KeypadSmokerScreen extends AbstractFurnaceScreen<KeypadSmokerMenu> {
	private static final ResourceLocation LIT_PROGRESS_SPRITE = SecurityCraft.mcResLoc("container/smoker/lit_progress");
	private static final ResourceLocation BURN_PROGRESS_SPRITE = SecurityCraft.mcResLoc("container/smoker/burn_progress");
	private static final ResourceLocation TEXTURE = SecurityCraft.mcResLoc("textures/gui/container/smoker.png");
	private static final Component FILTER_NAME = Component.translatable("gui.recipebook.toggleRecipes.smokable");
	//@formatter:off
	private static final List<RecipeBookComponent.TabInfo> TABS = List.of(
			new RecipeBookComponent.TabInfo(SearchRecipeBookCategory.SMOKER),
			new RecipeBookComponent.TabInfo(Items.PORKCHOP, RecipeBookCategories.SMOKER_FOOD));
	//@formatter:on

	public KeypadSmokerScreen(KeypadSmokerMenu menu, Inventory inv, Component title) {
		super(menu, inv, menu.be.hasCustomName() ? menu.be.getCustomName() : title, FILTER_NAME, TEXTURE, LIT_PROGRESS_SPRITE, BURN_PROGRESS_SPRITE, TABS);
	}
}