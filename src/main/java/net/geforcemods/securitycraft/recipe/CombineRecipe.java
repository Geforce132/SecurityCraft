package net.geforcemods.securitycraft.recipe;

import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;

public abstract class CombineRecipe extends CustomRecipe {
	private final Ingredient first;
	private final Ingredient second;
	private final ItemStackTemplate result;

	protected CombineRecipe(Ingredient first, Ingredient second, ItemStackTemplate result) {
		this.first = first;
		this.second = second;
		this.result = result;
	}

	@Override
	public boolean matches(CraftingInput inv, Level level) {
 		ItemStack firstItem = ItemStack.EMPTY;
		ItemStack secondItem = ItemStack.EMPTY;

		for (int i = 0; i < inv.size(); ++i) {
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
	public ItemStack assemble(CraftingInput inv) {
		ItemStack firstItem = ItemStack.EMPTY;

		for (int i = 0; i < inv.size(); ++i) {
			ItemStack stack = inv.getItem(i);

			if (matchesFirstItem(stack)) {
				firstItem = stack;
				break;
			}
		}

		return result.apply(firstItem.getComponentsPatch());
	}

	public boolean matchesFirstItem(ItemStack stack) {
		return first.test(stack);
	}

	public boolean matchesSecondItem(ItemStack stack) {
		return second.test(stack);
	}

	public boolean canBeCombined(ItemStack firstItem, ItemStack secondItem) {
		return !firstItem.isEmpty() && !secondItem.isEmpty();
	}

	public Ingredient first() {
		return first;
	}

	public Ingredient second() {
		return second;
	}

	public ItemStackTemplate result() {
		return result;
	}

	public Holder<Item> resultItem() {
		return result.item();
	}
}
