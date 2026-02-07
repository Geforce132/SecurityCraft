package net.geforcemods.securitycraft.recipe;

import java.util.Map;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.geforcemods.securitycraft.api.IReinforcedBlock;
import net.geforcemods.securitycraft.items.UniversalBlockReinforcerItem;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;

public class BlockReinforcingRecipe extends AbstractReinforcerRecipe {
	public static final MapCodec<BlockReinforcingRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
			Ingredient.CODEC.fieldOf("reinforcer").forGetter(AbstractReinforcerRecipe::reinforcer)
	).apply(i, BlockReinforcingRecipe::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, BlockReinforcingRecipe> STREAM_CODEC = StreamCodec.composite(
			Ingredient.CONTENTS_STREAM_CODEC, AbstractReinforcerRecipe::reinforcer,
			BlockReinforcingRecipe::new
	);
	public static final RecipeSerializer<BlockReinforcingRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

	public BlockReinforcingRecipe(Ingredient reinforcer) {
		super(reinforcer);
	}

	@Override
	public Map<Block, Block> getBlockMap() {
		return IReinforcedBlock.VANILLA_TO_SECURITYCRAFT;
	}

	@Override
	public boolean isCorrectReinforcer(ItemStack reinforcer) {
		return UniversalBlockReinforcerItem.isReinforcing(reinforcer);
	}

	@Override
	public RecipeSerializer<? extends CustomRecipe> getSerializer() {
		return SERIALIZER;
	}
}
