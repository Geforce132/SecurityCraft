package net.geforcemods.securitycraft.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.components.KeycardData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class LimitedUseKeycardRecipe extends CombineRecipe {
	public static final MapCodec<LimitedUseKeycardRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
			Ingredient.CODEC.fieldOf("first").forGetter(CombineRecipe::first),
			Ingredient.CODEC.fieldOf("second").forGetter(CombineRecipe::second),
			ItemStackTemplate.CODEC.fieldOf("result").forGetter(CombineRecipe::result)
	).apply(i, LimitedUseKeycardRecipe::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, LimitedUseKeycardRecipe> STREAM_CODEC = StreamCodec.composite(
			Ingredient.CONTENTS_STREAM_CODEC, CombineRecipe::first,
			Ingredient.CONTENTS_STREAM_CODEC, CombineRecipe::second,
			ItemStackTemplate.STREAM_CODEC, CombineRecipe::result,
			LimitedUseKeycardRecipe::new
	);
	public static final RecipeSerializer<LimitedUseKeycardRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

	public LimitedUseKeycardRecipe(Ingredient first, Ingredient second, ItemStackTemplate result) {
		super(first, second, result);
	}

	@Override
	public boolean matchesFirstItem(ItemStack stack) {
		return !stack.getOrDefault(SCContent.KEYCARD_DATA, KeycardData.DEFAULT).limited() && super.matchesFirstItem(stack);
	}

	@Override
	public RecipeSerializer<? extends CustomRecipe> getSerializer() {
		return SERIALIZER;
	}
}
