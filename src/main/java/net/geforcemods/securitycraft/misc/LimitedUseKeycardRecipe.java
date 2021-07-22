package net.geforcemods.securitycraft.misc;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.items.KeycardItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ObjectHolder;

public class LimitedUseKeycardRecipe extends CustomRecipe
{
	@ObjectHolder(SecurityCraft.MODID + ":limited_use_keycard_recipe")
	public static SimpleRecipeSerializer<LimitedUseKeycardRecipe> serializer = null;

	public LimitedUseKeycardRecipe(ResourceLocation id)
	{
		super(id);
	}

	@Override
	public boolean matches(CraftingContainer inv, Level world)
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
	public ItemStack assemble(CraftingContainer inv)
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

		CompoundTag tag = keycard.getOrCreateTag();

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
	public RecipeSerializer<?> getSerializer()
	{
		return serializer;
	}
}
