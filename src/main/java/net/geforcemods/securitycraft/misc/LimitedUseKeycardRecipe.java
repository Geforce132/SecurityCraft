package net.geforcemods.securitycraft.misc;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.items.KeycardItem;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ObjectHolder;

public class LimitedUseKeycardRecipe extends SpecialRecipe
{
	@ObjectHolder(SecurityCraft.MODID + ":limited_use_keycard_recipe")
	public static SpecialRecipeSerializer<LimitedUseKeycardRecipe> serializer = null;

	public LimitedUseKeycardRecipe(ResourceLocation id)
	{
		super(id);
	}

	@Override
	public boolean matches(CraftingInventory inv, World world)
	{
		boolean hasNormalKeycard = false;
		boolean hasLimitedUseKeycard = false;

		for(int i = 0; i < inv.getContainerSize(); ++i)
		{
			ItemStack stack = inv.getItem(i);
			Item item = stack.getItem();

			if(item instanceof KeycardItem)
			{
				if(item != SCContent.LIMITED_USE_KEYCARD.get())
				{
					if(hasNormalKeycard || stack.getOrCreateTag().getBoolean("limited"))
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
	public ItemStack assemble(CraftingInventory inv)
	{
		ItemStack keycard = ItemStack.EMPTY;

		for(int i = 0; i < inv.getContainerSize(); ++i)
		{
			ItemStack stack = inv.getItem(i);
			Item item = stack.getItem();

			if(item instanceof KeycardItem && item != SCContent.LIMITED_USE_KEYCARD.get())
			{
				keycard = stack.copy();
				break;
			}
		}

		if(keycard.isEmpty())
			return ItemStack.EMPTY;

		CompoundNBT tag = keycard.getOrCreateTag();

		tag.putBoolean("limited", true);
		tag.putInt("uses", 0);
		keycard.setCount(2);
		return keycard;
	}

	@Override
	public boolean canCraftInDimensions(int width, int height)
	{
		return width * height >= 2;
	}

	@Override
	public IRecipeSerializer<?> getSerializer()
	{
		return serializer;
	}
}
