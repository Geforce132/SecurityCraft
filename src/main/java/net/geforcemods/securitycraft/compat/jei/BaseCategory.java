package net.geforcemods.securitycraft.compat.jei;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public abstract class BaseCategory implements IRecipeCategory<ReinforcerRecipe>
{
	protected static final String OUTPUT_TEXT = Utils.localize("gui.securitycraft:blockReinforcer.output").getFormattedText();
	private final IDrawable background;
	private final IDrawable icon;
	private final int yOffset;

	public BaseCategory(IGuiHelper helper, int yOffset)
	{
		background = helper.createDrawable(new ResourceLocation(SecurityCraft.MODID, "textures/gui/container/universal_block_reinforcer.png"), 25, 19, 126, 43);
		icon = helper.createDrawableIngredient(new ItemStack(SCContent.universalBlockReinforcerLvL3));
		this.yOffset = yOffset;
	}

	@Override
	public IDrawable getBackground()
	{
		return background;
	}

	@Override
	public IDrawable getIcon()
	{
		return icon;
	}

	@Override
	public String getModName()
	{
		return SecurityCraft.MODID;
	}

	@Override
	public void setRecipe(IRecipeLayout layout, ReinforcerRecipe recipe, IIngredients ingredients)
	{
		IGuiItemStackGroup group = layout.getItemStacks();

		group.init(0, true, 0, yOffset);
		group.init(1, false, 90, yOffset);
		group.set(0, recipe.getFromBlock());
		group.set(1, recipe.getToBlock());
	}

	@Override
	public void drawExtras(Minecraft mc)
	{
		mc.fontRenderer.drawString(OUTPUT_TEXT, 24, 5 + yOffset, 4210752);
	}
}
