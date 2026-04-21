package net.geforcemods.securitycraft.recipe;

import java.util.Map;

import net.geforcemods.securitycraft.items.UniversalBlockReinforcerItem;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.server.ServerLifecycleHooks;

public abstract class AbstractReinforcerRecipe extends CustomRecipe {
	protected AbstractReinforcerRecipe(ResourceLocation id, CraftingBookCategory category) {
		super(id, category);
	}

	public abstract Map<Block, Block> getBlockMap();

	public abstract boolean isCorrectReinforcer(ItemStack reinforcer);

	@Override
	public boolean matches(CraftingContainer inv, Level level) {
		boolean hasCorrectBlock = false;
		boolean hasReinforcer = false;
		Map<Block, Block> blockMap = getBlockMap();

		for (int i = 0; i < inv.getContainerSize(); i++) {
			ItemStack stack = inv.getItem(i);
			Item item = stack.getItem();

			if (item instanceof BlockItem blockItem) {
				if (blockMap.containsKey(blockItem.getBlock())) {
					if (hasCorrectBlock)
						return false;

					hasCorrectBlock = true;
				}
				else
					return false;
			}
			else if (item instanceof UniversalBlockReinforcerItem) {
				if (hasReinforcer || !isCorrectReinforcer(stack))
					return false;

				hasReinforcer = true;
			}
			else if (!stack.isEmpty())
				return false;
		}

		return hasReinforcer && hasCorrectBlock;
	}

	@Override
	public ItemStack assemble(CraftingContainer inv, RegistryAccess registryAccess) {
		for (int i = 0; i < inv.getContainerSize(); i++) {
			if (inv.getItem(i).getItem() instanceof BlockItem blockItem)
				return new ItemStack(getBlockMap().get(blockItem.getBlock()));
		}

		return ItemStack.EMPTY;
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(CraftingContainer inv) {
		NonNullList<ItemStack> newInv = NonNullList.withSize(inv.getContainerSize(), ItemStack.EMPTY);

		for (int i = 0; i < newInv.size(); i++) {
			ItemStack stack = inv.getItem(i);

			if (stack.getItem() instanceof UniversalBlockReinforcerItem) {
				Player player = ForgeHooks.getCraftingPlayer();

				if (stack.hurt(1, ServerLifecycleHooks.getCurrentServer().overworld().getRandom(), player instanceof ServerPlayer serverPlayer ? serverPlayer : null)) {
					stack.shrink(1); //Manual stack#hurtAndBreak implementation to handle null player
					stack.setDamageValue(0);

					if (player != null)
						player.awardStat(Stats.ITEM_BROKEN.get(stack.getItem()));
				}

				newInv.set(i, stack.copy());
			}
		}

		return newInv;
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width * height >= 2;
	}
}
