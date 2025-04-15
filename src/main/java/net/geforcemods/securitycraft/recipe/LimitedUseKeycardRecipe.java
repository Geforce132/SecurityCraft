package net.geforcemods.securitycraft.recipe;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.items.KeycardItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ObjectHolder;

public class LimitedUseKeycardRecipe extends CombineRecipe {
	@ObjectHolder(SecurityCraft.MODID + ":limited_use_keycard_recipe")
	public static final SpecialRecipeSerializer<LimitedUseKeycardRecipe> SERIALIZER = null;

	public LimitedUseKeycardRecipe(ResourceLocation id) {
		super(id);
	}

	@Override
	public boolean matchesFirstItem(ItemStack stack) {
		return stack.getItem() instanceof KeycardItem && !matchesSecondItem(stack) && !stack.getOrCreateTag().getBoolean("limited");
	}

	@Override
	public boolean matchesSecondItem(ItemStack stack) {
		return stack.getItem() == SCContent.LIMITED_USE_KEYCARD.get();
	}

	@Override
	public ItemStack combine(ItemStack keycardToCopy, ItemStack limitedUseKeycard) {
		ItemStack outputKeycard = keycardToCopy.copy();
		CompoundNBT tag = outputKeycard.getOrCreateTag();

		tag.putBoolean("limited", true);
		tag.putInt("uses", 0);
		outputKeycard.setCount(2);
		return outputKeycard;
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}
}
