package net.geforcemods.securitycraft.compat.jei;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public abstract class BaseCategory implements IRecipeCategory<ReinforcerRecipe> {
	protected static final Component OUTPUT_TEXT = Utils.localize("gui.securitycraft:blockReinforcer.output");
	private final IDrawable background;
	private final IDrawable icon;

	protected BaseCategory(IGuiHelper helper) {
		background = helper.createDrawable(new ResourceLocation(SecurityCraft.MODID, "textures/gui/container/universal_block_reinforcer.png"), 25, 19, 126, 43);
		icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(SCContent.UNIVERSAL_BLOCK_REINFORCER_LVL_3.get()));
	}

	@Override
	public IDrawable getBackground() {
		return background;
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}
}
