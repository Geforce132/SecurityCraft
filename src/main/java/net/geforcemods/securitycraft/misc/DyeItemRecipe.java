package net.geforcemods.securitycraft.misc;

import java.util.ArrayList;
import java.util.List;

import net.geforcemods.securitycraft.items.ColorableItem;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.oredict.DyeUtils;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class DyeItemRecipe extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {
	@Override
	public boolean matches(InventoryCrafting inv, World world) {
		ItemStack colorableItem = ItemStack.EMPTY;
		List<ItemStack> dyes = new ArrayList<>();

		for (int i = 0; i < inv.getSizeInventory(); ++i) {
			ItemStack slotStack = inv.getStackInSlot(i);

			if (!slotStack.isEmpty()) {
				if (slotStack.getItem() instanceof ColorableItem) {
					if (!colorableItem.isEmpty())
						return false;

					colorableItem = slotStack;
				}
				else {
					if (!DyeUtils.isDye(slotStack))
						return false;

					dyes.add(slotStack);
				}
			}
		}

		return !colorableItem.isEmpty() && !dyes.isEmpty();
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		ItemStack colorableItem = ItemStack.EMPTY;
		List<ItemStack> dyes = new ArrayList<>();

		for (int slot = 0; slot < inv.getSizeInventory(); ++slot) {
			ItemStack slotStack = inv.getStackInSlot(slot);

			if (!slotStack.isEmpty()) {
				if (slotStack.getItem() instanceof ColorableItem)
					colorableItem = slotStack.copy();
				else if (DyeUtils.isDye(slotStack))
					dyes.add(slotStack);
				else
					return ItemStack.EMPTY;
			}
		}

		return !colorableItem.isEmpty() ? dyeItem(colorableItem, dyes) : colorableItem;
	}

	public static ItemStack dyeItem(ItemStack stack, List<ItemStack> dyes) {
		ColorableItem colorableItem = (ColorableItem) stack.getItem();
		ItemStack resultStack = stack.copy();
		int[] resultRgb = new int[3];
		int i = 0;
		int j = 0;

		resultStack.setCount(1);

		if (colorableItem.hasColor(stack)) {
			int rgb = colorableItem.getColor(resultStack);
			float r = (rgb >> 16 & 255) / 255.0F;
			float g = (rgb >> 8 & 255) / 255.0F;
			float b = (rgb & 255) / 255.0F;

			i = (int) (i + Math.max(r, Math.max(g, b)) * 255.0F);
			resultRgb[0] = (int) (resultRgb[0] + r * 255.0F);
			resultRgb[1] = (int) (resultRgb[1] + g * 255.0F);
			resultRgb[2] = (int) (resultRgb[2] + b * 255.0F);
			++j;
		}

		for (ItemStack dye : dyes) {
			float[] dyeColor = DyeUtils.colorFromStack(dye).get().getColorComponentValues();
			int dyeR = (int) (dyeColor[0] * 255.0F);
			int dyeG = (int) (dyeColor[1] * 255.0F);
			int dyeB = (int) (dyeColor[2] * 255.0F);

			i += Math.max(dyeR, Math.max(dyeG, dyeB));
			resultRgb[0] += dyeR;
			resultRgb[1] += dyeG;
			resultRgb[2] += dyeB;
			++j;
		}

		int r = resultRgb[0] / j;
		int g = resultRgb[1] / j;
		int b = resultRgb[2] / j;
		float f3 = (float) i / (float) j;
		float f4 = Math.max(r, Math.max(g, b));
		int resultColor;

		r = (int) (r * f3 / f4);
		g = (int) (g * f3 / f4);
		b = (int) (b * f3 / f4);
		resultColor = (r << 8) + g;
		resultColor = (resultColor << 8) + b;
		colorableItem.setColor(resultStack, resultColor);
		return resultStack;
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
	public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
		NonNullList<ItemStack> remainingItems = NonNullList.<ItemStack>withSize(inv.getSizeInventory(), ItemStack.EMPTY);

		for (int i = 0; i < remainingItems.size(); ++i) {
			remainingItems.set(i, ForgeHooks.getContainerItem(inv.getStackInSlot(i)));
		}

		return remainingItems;
	}

	@Override
	public boolean isDynamic() {
		return true;
	}
}
