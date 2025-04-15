package net.geforcemods.securitycraft.recipe;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;

public abstract class CombineRecipe extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {
	@Override
	public boolean matches(InventoryCrafting inv, World level) {
		ItemStack firstItem = ItemStack.EMPTY;
		ItemStack secondItem = ItemStack.EMPTY;

		for (int i = 0; i < inv.getSizeInventory(); ++i) {
			ItemStack stack = inv.getStackInSlot(i);

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
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		ItemStack firstItem = ItemStack.EMPTY;
		ItemStack secondItem = ItemStack.EMPTY;

		for (int i = 0; i < inv.getSizeInventory(); ++i) {
			ItemStack stack = inv.getStackInSlot(i);

			if (matchesFirstItem(stack))
				firstItem = stack;
			else if (matchesSecondItem(stack))
				secondItem = stack;
		}

		return combine(firstItem, secondItem);
	}

	@Override
	public boolean canFit(int width, int height) {
		return width * height >= 2;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean isDynamic() {
		return true;
	}

	public abstract boolean matchesFirstItem(ItemStack stack);

	public abstract boolean matchesSecondItem(ItemStack stack);

	public boolean canBeCombined(ItemStack firstItem, ItemStack secondItem) {
		return !firstItem.isEmpty() && !secondItem.isEmpty();
	}

	public abstract ItemStack combine(ItemStack firstItem, ItemStack secondItem);

	protected static NBTTagCompound getOrCreateTag(ItemStack stack) {
		if (!stack.hasTagCompound())
			stack.setTagCompound(new NBTTagCompound());

		return stack.getTagCompound();
	}
}
