package net.geforcemods.securitycraft.recipe;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.items.KeycardItem;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class LimitedUseKeycardRecipe extends CustomRecipe {
	public LimitedUseKeycardRecipe(CraftingBookCategory craftingBookCategory) {
		super(craftingBookCategory);
	}

	@Override
	public boolean matches(CraftingContainer inv, Level level) {
		boolean hasNormalKeycard = false;
		boolean hasLimitedUseKeycard = false;

		for (int i = 0; i < inv.getContainerSize(); ++i) {
			ItemStack stack = inv.getItem(i);
			Item item = stack.getItem();

			if (item instanceof KeycardItem) {
				if (item != SCContent.LIMITED_USE_KEYCARD.get()) {
					if (hasNormalKeycard || stack.getOrCreateTag().getBoolean("limited"))
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
	public ItemStack assemble(CraftingContainer inv, RegistryAccess registryAccess) {
		ItemStack keycard = ItemStack.EMPTY;

		for (int i = 0; i < inv.getContainerSize(); ++i) {
			ItemStack stack = inv.getItem(i);
			Item item = stack.getItem();

			if (item instanceof KeycardItem && item != SCContent.LIMITED_USE_KEYCARD.get()) {
				keycard = stack.copy();
				break;
			}
		}

		if (keycard.isEmpty())
			return ItemStack.EMPTY;

		CompoundTag tag = keycard.getOrCreateTag();

		tag.putBoolean("limited", true);
		tag.putInt("uses", 0);
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
