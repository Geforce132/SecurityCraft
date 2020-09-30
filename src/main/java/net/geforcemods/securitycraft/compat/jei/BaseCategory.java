package net.geforcemods.securitycraft.compat.jei;

import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;

public abstract class BaseCategory implements IRecipeCategory<ReinforcerRecipe>
{
	protected static final TranslationTextComponent OUTPUT_TEXT = ClientUtils.localize("gui.securitycraft:blockReinforcer.output");
	private final IDrawable background;
	private final IDrawable icon;

	public BaseCategory(IGuiHelper helper)
	{
		background = helper.createDrawable(new ResourceLocation(SecurityCraft.MODID, "textures/gui/container/universal_block_reinforcer.png"), 25, 19, 126, 43);
		icon = helper.createDrawableIngredient(new ItemStack(SCContent.UNIVERSAL_BLOCK_REINFORCER_LVL_3.get()));
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
	public Class<? extends ReinforcerRecipe> getRecipeClass()
	{
		return ReinforcerRecipe.class;
	}
}
