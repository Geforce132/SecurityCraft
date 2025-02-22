package net.geforcemods.securitycraft.recipe;

import java.util.Map;

import net.geforcemods.securitycraft.items.UniversalBlockReinforcerItem;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

public abstract class AbstractReinforcerRecipe extends CustomRecipe {
	protected AbstractReinforcerRecipe(CraftingBookCategory category) {
		super(category);
	}

	public abstract Map<Block, Block> getBlockMap();

	public abstract boolean isCorrectReinforcer(ItemStack reinforcer);

	@Override
	public boolean matches(CraftingInput inv, Level level) {
		boolean hasCorrectBlock = false;
		boolean hasReinforcer = false;
		Map<Block, Block> blockMap = getBlockMap();

		for (int i = 0; i < inv.size(); i++) {
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
	public ItemStack assemble(CraftingInput inv, HolderLookup.Provider lookupProvider) {
		for (int i = 0; i < inv.size(); i++) {
			if (inv.getItem(i).getItem() instanceof BlockItem blockItem)
				return new ItemStack(getBlockMap().get(blockItem.getBlock()));
		}

		return ItemStack.EMPTY;
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(CraftingInput inv) {
		NonNullList<ItemStack> newInv = NonNullList.withSize(inv.size(), ItemStack.EMPTY);

		for (int i = 0; i < newInv.size(); i++) {
			ItemStack stack = inv.getItem(i);

			if (stack.getItem() instanceof UniversalBlockReinforcerItem) {
				Player player = CommonHooks.getCraftingPlayer();
				Level level = player != null ? player.level() : ServerLifecycleHooks.getCurrentServer().overworld();

				if (level != null && !level.isClientSide)
					stack.hurtAndBreak(1, (ServerLevel) level, player, item -> {});

				newInv.set(i, stack.copy());
			}
		}

		return newInv;
	}
}
