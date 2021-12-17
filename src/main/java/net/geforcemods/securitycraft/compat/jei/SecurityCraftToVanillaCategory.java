package net.geforcemods.securitycraft.compat.jei;

import com.mojang.blaze3d.vertex.PoseStack;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
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
		Minecraft.getInstance().font.draw(pose, OUTPUT_TEXT, 24, 30, 4210752);
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
	public void setIngredients(ReinforcerRecipe recipe, IIngredients ingredients) {
		ingredients.setInput(VanillaTypes.ITEM, new ItemStack(recipe.getSecurityCraftBlock()));
		ingredients.setOutput(VanillaTypes.ITEM, new ItemStack(recipe.getVanillaBlock()));
	}

	@Override
	public void setRecipe(IRecipeLayout layout, ReinforcerRecipe recipe, IIngredients ingredients) {
		IGuiItemStackGroup group = layout.getItemStacks();

		group.init(0, true, 0, 25);
		group.init(1, false, 90, 25);
		group.set(0, new ItemStack(recipe.getSecurityCraftBlock()));
		group.set(1, new ItemStack(recipe.getVanillaBlock()));
	}
}
