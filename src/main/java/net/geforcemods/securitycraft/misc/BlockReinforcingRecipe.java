package net.geforcemods.securitycraft.misc;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IReinforcedBlock;
import net.geforcemods.securitycraft.items.UniversalBlockReinforcerItem;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

public class BlockReinforcingRecipe extends CustomRecipe {
	public BlockReinforcingRecipe(CraftingBookCategory category) {
		super(category);
	}

	@Override
	public boolean matches(CraftingContainer inv, Level level) {
		boolean hasReinforceableBlock = false;
		boolean hasReinforcedBlock = false;
		boolean hasReinforcer = false;
		boolean isReinforcerUnreinforcing = false;

		for (int i = 0; i < inv.getContainerSize(); ++i) {
			ItemStack stack = inv.getItem(i);
			Item item = stack.getItem();

			if (item instanceof BlockItem blockItem) {
				if (IReinforcedBlock.VANILLA_TO_SECURITYCRAFT.containsKey(blockItem.getBlock())) {
					if (hasReinforceableBlock || hasReinforcedBlock)
						return false;

					hasReinforceableBlock = true;
				}
				else if (IReinforcedBlock.SECURITYCRAFT_TO_VANILLA.containsKey(blockItem.getBlock())) {
					if (hasReinforceableBlock || hasReinforcedBlock)
						return false;

					hasReinforcedBlock = true;
				}
				else
					return false;
			}
			else if (item instanceof UniversalBlockReinforcerItem) {
				if (hasReinforcer)
					return false;

				hasReinforcer = true;
				isReinforcerUnreinforcing = !UniversalBlockReinforcerItem.isReinforcing(stack);
			}
			else if (!stack.isEmpty())
				return false;
		}

		return hasReinforcer && ((!isReinforcerUnreinforcing && hasReinforceableBlock) || (isReinforcerUnreinforcing && hasReinforcedBlock));
	}

	@Override
	public ItemStack assemble(CraftingContainer inv, HolderLookup.Provider lookupProvider) {
		ItemStack block = ItemStack.EMPTY;
		boolean isUnreinforcing = false;

		for (int i = 0; i < inv.getContainerSize(); ++i) {
			ItemStack stack = inv.getItem(i);
			Item item = stack.getItem();

			if (item instanceof BlockItem)
				block = stack.copy();
			else if (item instanceof UniversalBlockReinforcerItem)
				isUnreinforcing = !UniversalBlockReinforcerItem.isReinforcing(stack);
		}

		return new ItemStack((isUnreinforcing ? IReinforcedBlock.SECURITYCRAFT_TO_VANILLA : IReinforcedBlock.VANILLA_TO_SECURITYCRAFT).get(Block.byItem(block.getItem())));
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(CraftingContainer inv) {
		NonNullList<ItemStack> newInv = NonNullList.withSize(inv.getContainerSize(), ItemStack.EMPTY);

		for (int i = 0; i < newInv.size(); i++) {
			ItemStack stack = inv.getItem(i);

			if (stack.getItem() instanceof UniversalBlockReinforcerItem) {
				stack.hurtAndBreak(1, ServerLifecycleHooks.getCurrentServer().overworld().getRandom(), null, () -> stack.setCount(0));
				newInv.set(i, stack.copy());
			}
		}

		return newInv;
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width * height >= 2;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return SCContent.BLOCK_REINFORCING_RECIPE_SERIALIZER.get();
	}
}
