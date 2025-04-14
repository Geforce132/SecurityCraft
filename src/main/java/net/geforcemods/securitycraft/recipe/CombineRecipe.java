package net.geforcemods.securitycraft.recipe;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.level.Level;

public abstract class CombineRecipe extends CustomRecipe {
	protected CombineRecipe(CraftingBookCategory craftingBookCategory) {
		super(craftingBookCategory);
	}

	@Override
	public boolean matches(CraftingContainer inv, Level level) {
		ItemStack firstItem = ItemStack.EMPTY;
		ItemStack secondItem = ItemStack.EMPTY;

		for (int i = 0; i < inv.getContainerSize(); ++i) {
			ItemStack stack = inv.getItem(i);

			if (matchesFirstItem(stack)) {
				if (firstItem.isEmpty())
					firstItem = stack;
				else
					return false;
			}
			else if (matchesSecondItem(stack)) {
				if (secondItem.isEmpty())
					secondItem = stack;
				else
					return false;
			}
			else if (!stack.isEmpty())
				return false;
		}

		return canBeCombined(firstItem, secondItem);
	}

	@Override
	public ItemStack assemble(CraftingContainer inv, RegistryAccess registryAccess) {
		ItemStack firstItem = ItemStack.EMPTY;
		ItemStack secondItem = ItemStack.EMPTY;

		for (int i = 0; i < inv.getContainerSize(); ++i) {
			ItemStack stack = inv.getItem(i);

			if (matchesFirstItem(stack))
				firstItem = stack;
			else if (matchesSecondItem(stack))
				secondItem = stack;
		}

		return combine(firstItem, secondItem);
	}

	public abstract boolean matchesFirstItem(ItemStack stack);

	public abstract boolean matchesSecondItem(ItemStack stack);

	public boolean canBeCombined(ItemStack firstItem, ItemStack secondItem) {
		return !firstItem.isEmpty() && !secondItem.isEmpty();
	}

	public abstract ItemStack combine(ItemStack firstItem, ItemStack secondItem);

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width * height >= 2;
	}
}
