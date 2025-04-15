package net.geforcemods.securitycraft.recipe;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.items.KeycardItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class LimitedUseKeycardRecipe extends CombineRecipe {
	public LimitedUseKeycardRecipe(ResourceLocation id, CraftingBookCategory craftingBookCategory) {
		super(id, craftingBookCategory);
	}

	@Override
	public boolean matchesFirstItem(ItemStack stack) {
		return stack.getItem() instanceof KeycardItem && !matchesSecondItem(stack) && !stack.getOrCreateTag().getBoolean("limited");
	}

	@Override
	public boolean matchesSecondItem(ItemStack stack) {
		return stack.is(SCContent.LIMITED_USE_KEYCARD.get());
	}

	@Override
	public ItemStack combine(ItemStack keycardToCopy, ItemStack limitedUseKeycard) {
		ItemStack outputKeycard = keycardToCopy.copy();
		CompoundTag tag = outputKeycard.getOrCreateTag();

		tag.putBoolean("limited", true);
		tag.putInt("uses", 0);
		outputKeycard.setCount(2);
		return outputKeycard;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return SCContent.LIMITED_USE_KEYCARD_RECIPE_SERIALIZER.get();
	}
}
