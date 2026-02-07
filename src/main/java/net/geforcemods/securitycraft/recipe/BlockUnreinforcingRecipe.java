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

public class BlockUnreinforcingRecipe extends AbstractReinforcerRecipe {
	public static final MapCodec<BlockUnreinforcingRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
			Ingredient.CODEC.fieldOf("reinforcer").forGetter(AbstractReinforcerRecipe::reinforcer)
	).apply(i, BlockUnreinforcingRecipe::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, BlockUnreinforcingRecipe> STREAM_CODEC = StreamCodec.composite(
			Ingredient.CONTENTS_STREAM_CODEC, AbstractReinforcerRecipe::reinforcer,
			BlockUnreinforcingRecipe::new
	);
	public static final RecipeSerializer<BlockUnreinforcingRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

	public BlockUnreinforcingRecipe(Ingredient reinforcer) {
		super(reinforcer);
	}

	@Override
	public Map<Block, Block> getBlockMap() {
		return IReinforcedBlock.SECURITYCRAFT_TO_VANILLA;
	}

	@Override
	public boolean isCorrectReinforcer(ItemStack reinforcer) {
		return !UniversalBlockReinforcerItem.isReinforcing(reinforcer);
	}

	@Override
	public RecipeSerializer<? extends CustomRecipe> getSerializer() {
		return SERIALIZER;
	}
}
