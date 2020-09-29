package net.geforcemods.securitycraft.compat.jei;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;

public class ReinforcerRecipe implements IRecipeWrapper
{
	private final ItemStack fromBlock;
	private final ItemStack toBlock;

	public ReinforcerRecipe(ItemStack fromBlock, ItemStack toBlock)
	{
		this.fromBlock = fromBlock;
		this.toBlock = toBlock;
	}

	public ItemStack getFromBlock()
	{
		return fromBlock;
	}

	public ItemStack getToBlock()
	{
		return toBlock;
	}

	@Override
	public void getIngredients(IIngredients ingredients)
	{
		ingredients.setInput(VanillaTypes.ITEM, fromBlock);
		ingredients.setOutput(VanillaTypes.ITEM, toBlock);
	}
}
