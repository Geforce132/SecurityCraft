package net.geforcemods.securitycraft.misc;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.common.brewing.AbstractBrewingRecipe;

public class IngredientBrewingRecipe extends AbstractBrewingRecipe<Ingredient> {
	public IngredientBrewingRecipe(ItemStack input, Ingredient ingredient, ItemStack output) {
		super(input, ingredient, output);
	}

	@Override
	public boolean isIngredient(ItemStack ingredient) {
		return getIngredient().apply(ingredient);
	}
}
