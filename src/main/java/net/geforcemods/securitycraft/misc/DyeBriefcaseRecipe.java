package net.geforcemods.securitycraft.misc;

import java.util.List;

import com.google.common.collect.Lists;

import net.geforcemods.securitycraft.items.ItemBriefcase;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.oredict.DyeUtils;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class DyeBriefcaseRecipe extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe
{
	@Override
	public boolean matches(InventoryCrafting inv, World world)
	{
		ItemStack stack = ItemStack.EMPTY;
		List<ItemStack> list = Lists.<ItemStack>newArrayList();

		for (int i = 0; i < inv.getSizeInventory(); ++i)
		{
			ItemStack slotStack = inv.getStackInSlot(i);

			if (!slotStack.isEmpty())
			{
				if(slotStack.getItem() instanceof ItemBriefcase)
				{
					if(!stack.isEmpty())
						return false;

					stack = slotStack;
				}
				else
				{
					if(!DyeUtils.isDye(slotStack))
						return false;

					list.add(slotStack);
				}
			}
		}

		return !stack.isEmpty() && !list.isEmpty();
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv)
	{
		ItemStack resultStack = ItemStack.EMPTY;
		int[] rgb = new int[3];
		int i = 0;
		int j = 0;
		ItemBriefcase briefcase = null;

		for(int k = 0; k < inv.getSizeInventory(); ++k)
		{
			ItemStack slotStack = inv.getStackInSlot(k);

			if (!slotStack.isEmpty())
			{
				if(slotStack.getItem() instanceof ItemBriefcase)
				{
					briefcase = (ItemBriefcase)slotStack.getItem();

					if(!resultStack.isEmpty())
						return ItemStack.EMPTY;

					resultStack = slotStack.copy();
					resultStack.setCount(1);

					if(briefcase.hasColor(slotStack))
					{
						int color = briefcase.getColor(resultStack);
						float r = (color >> 16 & 255) / 255.0F;
						float g = (color >> 8 & 255) / 255.0F;
						float b = (color & 255) / 255.0F;

						i = (int)(i + Math.max(r, Math.max(g, b)) * 255.0F);
						rgb[0] = (int)(rgb[0] + r * 255.0F);
						rgb[1] = (int)(rgb[1] + g * 255.0F);
						rgb[2] = (int)(rgb[2] + b * 255.0F);
						++j;
					}
				}
				else
				{
					if(!DyeUtils.isDye(slotStack))
						return ItemStack.EMPTY;

					float[] rgbFloat = DyeUtils.colorFromStack(slotStack).get().getColorComponentValues();
					int r = (int)(rgbFloat[0] * 255.0F);
					int g = (int)(rgbFloat[1] * 255.0F);
					int b = (int)(rgbFloat[2] * 255.0F);

					i += Math.max(r, Math.max(g, b));
					rgb[0] += r;
					rgb[1] += g;
					rgb[2] += b;
					++j;
				}
			}
		}

		if(briefcase == null)
			return ItemStack.EMPTY;
		else
		{
			int r = rgb[0] / j;
			int g = rgb[1] / j;
			int b = rgb[2] / j;
			float f3 = (float)i / (float)j;
			float f4 = Math.max(r, Math.max(g, b));
			int resultingColor;

			r = (int)(r * f3 / f4);
			g = (int)(g * f3 / f4);
			b = (int)(b * f3 / f4);
			resultingColor = (r << 8) + g;
			resultingColor = (resultingColor << 8) + b;
			briefcase.setColor(resultStack, resultingColor);
			return resultStack;
		}
	}

	@Override
	public boolean canFit(int width, int height)
	{
		return width * height >= 2;
	}

	@Override
	public ItemStack getRecipeOutput()
	{
		return ItemStack.EMPTY;
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv)
	{
		NonNullList<ItemStack> remainingItems = NonNullList.<ItemStack>withSize(inv.getSizeInventory(), ItemStack.EMPTY);

		for(int i = 0; i < remainingItems.size(); ++i)
		{
			remainingItems.set(i, ForgeHooks.getContainerItem(inv.getStackInSlot(i)));
		}

		return remainingItems;
	}

	@Override
	public boolean isDynamic()
	{
		return true;
	}
}
