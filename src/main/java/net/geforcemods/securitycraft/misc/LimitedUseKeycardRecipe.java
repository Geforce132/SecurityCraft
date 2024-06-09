package net.geforcemods.securitycraft.misc;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.components.KeycardData;
import net.geforcemods.securitycraft.items.KeycardItem;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class LimitedUseKeycardRecipe extends CustomRecipe {
	public LimitedUseKeycardRecipe(CraftingBookCategory craftingBookCategory) {
		super(craftingBookCategory);
	}

	@Override
	public boolean matches(CraftingInput inv, Level level) {
		boolean hasNormalKeycard = false;
		boolean hasLimitedUseKeycard = false;

		for (int i = 0; i < inv.size(); ++i) {
			ItemStack stack = inv.getItem(i);
			Item item = stack.getItem();

			if (item instanceof KeycardItem) {
				if (item != SCContent.LIMITED_USE_KEYCARD.get()) {
					if (hasNormalKeycard || stack.getOrDefault(SCContent.KEYCARD_DATA, KeycardData.DEFAULT).limited())
						return false;

					hasNormalKeycard = true;
				}
				else { //item is SCContent.LIMITED_USE_KEYCARD.get()
					if (hasLimitedUseKeycard)
						return false;

					hasLimitedUseKeycard = true;
				}
			}
			else if (!stack.isEmpty())
				return false;
		}

		return hasNormalKeycard && hasLimitedUseKeycard;
	}

	@Override
	public ItemStack assemble(CraftingInput inv, HolderLookup.Provider lookupProvider) {
		ItemStack keycard = ItemStack.EMPTY;

		for (int i = 0; i < inv.size(); ++i) {
			ItemStack stack = inv.getItem(i);
			Item item = stack.getItem();

			if (item instanceof KeycardItem && item != SCContent.LIMITED_USE_KEYCARD.get()) {
				keycard = stack.copy();
				break;
			}
		}

		if (keycard.isEmpty())
			return ItemStack.EMPTY;

		keycard.update(SCContent.KEYCARD_DATA, KeycardData.DEFAULT, data -> data.setLimitedAndUsesLeft(true, 0));
		keycard.setCount(2);
		return keycard;
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width * height >= 2;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return SCContent.LIMITED_USE_KEYCARD_RECIPE_SERIALIZER.get();
	}
}
