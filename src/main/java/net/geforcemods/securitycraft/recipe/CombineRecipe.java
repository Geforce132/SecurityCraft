package net.geforcemods.securitycraft.recipe;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public abstract class CombineRecipe extends SpecialRecipe {
	protected CombineRecipe(ResourceLocation id) {
		super(id);
	}

	@Override
	public boolean matches(CraftingInventory inv, World level) {
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
	public ItemStack assemble(CraftingInventory inv) {
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

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width * height >= 2;
	}

	public abstract boolean matchesFirstItem(ItemStack stack);

	public abstract boolean matchesSecondItem(ItemStack stack);

	public boolean canBeCombined(ItemStack firstItem, ItemStack secondItem) {
		return !firstItem.isEmpty() && !secondItem.isEmpty();
	}

	public abstract ItemStack combine(ItemStack firstItem, ItemStack secondItem);
}
