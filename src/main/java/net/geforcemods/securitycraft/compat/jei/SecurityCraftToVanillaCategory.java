package net.geforcemods.securitycraft.compat.jei;

import com.mojang.blaze3d.vertex.PoseStack;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class SecurityCraftToVanillaCategory extends BaseCategory {
	private static final Component TITLE = Utils.localize("jei.securitycraft.category.unreinforcing");

	public SecurityCraftToVanillaCategory(IGuiHelper helper) {
		super(helper);
	}

	@Override
	public void draw(ReinforcerRecipe recipe, PoseStack pose, double mouseX, double mouseY) {
		Minecraft.getInstance().font.draw(pose, OUTPUT_TEXT, 24, 30, 0x404040);
	}

	@Override
	public Component getTitle() {
		return TITLE;
	}

	@Override
	public ResourceLocation getUid() {
		return SCJEIPlugin.STV_ID;
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, ReinforcerRecipe recipe, IFocusGroup focuses) {
		super.setRecipe(builder, recipe, focuses);
		builder.addSlot(RecipeIngredientRole.INPUT, 1, 26).addIngredient(VanillaTypes.ITEM, new ItemStack(recipe.getSecurityCraftBlock()));
		builder.addSlot(RecipeIngredientRole.OUTPUT, 91, 26).addIngredient(VanillaTypes.ITEM, new ItemStack(recipe.getVanillaBlock()));
	}
}
