package net.geforcemods.securitycraft.items;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.misc.PageGroup;
import net.geforcemods.securitycraft.misc.SCManualPage;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.level.Level;

public class SCManualItem extends Item {
	public static final List<SCManualPage> PAGES = new ArrayList<>();
	public static int lastOpenPage = -1;

	public SCManualItem(Item.Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		if (level.isClientSide)
			ClientHandler.displaySCManualScreen();

		return InteractionResult.CONSUME;
	}

	public static Optional<List<RecipeDisplay>> findRecipes(MinecraftServer server, Item item, PageGroup pageGroup) {
		HolderLookup.Provider registryAccess = server.registryAccess();
		CraftingInput dummyInput = CraftingInput.of(3, 3, NonNullList.withSize(9, ItemStack.EMPTY));

		if (pageGroup == PageGroup.REINFORCED || item == SCContent.REINFORCED_HOPPER.get().asItem())
			return Optional.empty();
		else if (pageGroup == PageGroup.NONE) {
			for (RecipeHolder<?> recipeHolder : server.getRecipeManager().getRecipes()) {
				if (recipeHolder.value() instanceof ShapedRecipe shapedRecipe) {
					ItemStack resultItem = shapedRecipe.assemble(dummyInput, registryAccess);

					if (resultItem.is(item) && !(resultItem.is(SCContent.LENS.get()) && resultItem.has(DataComponents.DYED_COLOR)))
						return Optional.of(shapedRecipe.display());
				}
				else if (recipeHolder.value() instanceof ShapelessRecipe shapelessRecipe && shapelessRecipe.assemble(dummyInput, registryAccess).is(item)) {
					//don't show keycard reset recipes
					if (recipeHolder.id().location().getPath().endsWith("_reset"))
						continue;

					return Optional.of(shapelessRecipe.display());
				}
			}
		}
		else if (pageGroup.hasRecipeGrid()) {
			List<RecipeDisplay> displays = new ArrayList<>();
			List<Item> pageItems = pageGroup.getItems().stream().map(ItemStack::getItem).toList();
			int stacksLeft = pageItems.size();

			for (RecipeHolder<?> recipeHolder : server.getRecipeManager().getRecipes()) {
				if (stacksLeft == 0)
					break;

				if (recipeHolder.value() instanceof ShapedRecipe shapedRecipe) {
					ItemStack resultItem = shapedRecipe.assemble(dummyInput, registryAccess);

					if (!resultItem.isEmpty() && pageItems.contains(resultItem.getItem())) {
						displays.addAll(shapedRecipe.display());
						stacksLeft--;
					}
				}
				else if (recipeHolder.value() instanceof ShapelessRecipe shapelessRecipe) {
					ItemStack resultItem = shapelessRecipe.assemble(dummyInput, registryAccess);

					if (!resultItem.isEmpty() && pageItems.contains(resultItem.getItem())) {
						//don't show keycard reset recipes
						if (recipeHolder.id().location().getPath().endsWith("_reset"))
							continue;

						displays.addAll(shapelessRecipe.display());
						stacksLeft--;
					}
				}
			}

			if (!displays.isEmpty())
				return Optional.of(displays);
		}

		return Optional.empty();
	}
}
