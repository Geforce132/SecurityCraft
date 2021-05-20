package net.geforcemods.securitycraft.misc;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.items.ItemKeycard;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class LimitedUseKeycardRecipe extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe
{
	@Override
	public boolean matches(InventoryCrafting inv, World world)
	{
		boolean hasNormalKeycard = false;
		boolean hasLimitedUseKeycard = false;

		for(int i = 0; i < inv.getSizeInventory(); ++i)
		{
			ItemStack stack = inv.getStackInSlot(i);
			Item item = stack.getItem();

			if(item instanceof ItemKeycard)
			{
				if(item != SCContent.limitedUseKeycard)
				{
					if(hasNormalKeycard || (stack.hasTagCompound() && stack.getTagCompound().getBoolean("limited")))
						return false;

					hasNormalKeycard = true;
					continue;
				}
				else //item is SCContent.LIMITED_USE_KEYCARD.get()
				{
					if(hasLimitedUseKeycard)
						return false;

					hasLimitedUseKeycard = true;
					continue;
				}
			}
			else if(!stack.isEmpty())
				return false;
		}

		return hasNormalKeycard && hasLimitedUseKeycard;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv)
	{
		ItemStack keycard = ItemStack.EMPTY;

		for(int i = 0; i < inv.getSizeInventory(); ++i)
		{
			ItemStack stack = inv.getStackInSlot(i);
			Item item = stack.getItem();

			if(item instanceof ItemKeycard && item != SCContent.limitedUseKeycard)
			{
				keycard = stack.copy();
				break;
			}
		}

		if(keycard.isEmpty())
			return ItemStack.EMPTY;

		boolean hasTag = keycard.hasTagCompound();
		NBTTagCompound tag = hasTag ? keycard.getTagCompound() : new NBTTagCompound();

		tag.setBoolean("limited", true);
		tag.setInteger("uses", 0);
		keycard.setCount(2);

		if(!hasTag)
			keycard.setTagCompound(tag);

		return keycard;
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
	public boolean isDynamic()
	{
		return true;
	}
}
