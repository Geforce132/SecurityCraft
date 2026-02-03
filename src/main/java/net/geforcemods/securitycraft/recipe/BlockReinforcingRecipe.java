package net.geforcemods.securitycraft.recipe;

import java.util.Map;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IReinforcedBlock;
import net.geforcemods.securitycraft.items.UniversalBlockReinforcerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;

public class BlockReinforcingRecipe extends AbstractReinforcerRecipe {
	public BlockReinforcingRecipe(CraftingBookCategory category) {
		super(category);
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
		return SCContent.BLOCK_REINFORCING_RECIPE_SERIALIZER.get();
	}
}
