package net.geforcemods.securitycraft.compat.jei;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class SecurityCraftToVanillaCategory extends BaseCategory
{
	public SecurityCraftToVanillaCategory(IGuiHelper helper)
	{
		super(helper);
	}

	@Override
	public void draw(ReinforcerRecipe recipe, double mouseX, double mouseY)
	{
		Minecraft.getInstance().fontRenderer.drawString(OUTPUT_TEXT, 24, 30, 4210752);
	}

	@Override
	public String getTitle()
	{
		return ClientUtils.localize("jei.securitycraft.category.unreinforcing").getFormattedText();
	}

	@Override
	public ResourceLocation getUid()
	{
		return SCJEIPlugin.STV_ID;
	}

	@Override
	public void setIngredients(ReinforcerRecipe recipe, IIngredients ingredients)
	{
		ingredients.setInput(VanillaTypes.ITEM, new ItemStack(recipe.getSecurityCraftBlock()));
		ingredients.setOutput(VanillaTypes.ITEM, new ItemStack(recipe.getVanillaBlock()));
	}

	@Override
	public void setRecipe(IRecipeLayout layout, ReinforcerRecipe recipe, IIngredients ingredients)
	{
		IGuiItemStackGroup group = layout.getItemStacks();

		group.init(0, true, 0, 25);
		group.init(1, false, 90, 25);
		group.set(0, new ItemStack(recipe.getSecurityCraftBlock()));
		group.set(1, new ItemStack(recipe.getVanillaBlock()));
	}
}
