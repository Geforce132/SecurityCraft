package net.geforcemods.securitycraft.recipe;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.components.KeycardData;
import net.geforcemods.securitycraft.items.KeycardItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class LimitedUseKeycardRecipe extends CombineRecipe {
	public LimitedUseKeycardRecipe(CraftingBookCategory craftingBookCategory) {
		super(craftingBookCategory);
	}

	@Override
	public boolean matchesFirstItem(ItemStack stack) {
		return stack.getItem() instanceof KeycardItem && !matchesSecondItem(stack) && !stack.getOrDefault(SCContent.KEYCARD_DATA, KeycardData.DEFAULT).limited();
	}

	@Override
	public boolean matchesSecondItem(ItemStack stack) {
		return stack.is(SCContent.LIMITED_USE_KEYCARD);
	}

	@Override
	public ItemStack combine(ItemStack keycardToCopy, ItemStack limitedUseKeycard) {
		keycardToCopy.update(SCContent.KEYCARD_DATA, KeycardData.DEFAULT, data -> data.setLimitedAndUsesLeft(true, 0));
		keycardToCopy.setCount(2);
		return keycardToCopy;
	}

	@Override
	public RecipeSerializer<? extends CustomRecipe> getSerializer() {
		return SCContent.LIMITED_USE_KEYCARD_RECIPE_SERIALIZER.get();
	}
}
