package net.geforcemods.securitycraft.mixin.furnace;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.google.common.collect.Lists;

import net.geforcemods.securitycraft.inventory.KeypadBlastFurnaceMenu;
import net.geforcemods.securitycraft.inventory.KeypadFurnaceMenu;
import net.geforcemods.securitycraft.inventory.KeypadSmokerMenu;
import net.minecraft.client.util.ClientRecipeBook;
import net.minecraft.client.util.RecipeBookCategories;
import net.minecraft.inventory.container.RecipeBookContainer;

/**
 * Fixes the game crashing when opening the recipe book for the password-protected furnace/smoker/blast furnace by providing
 * the correct recipe categories
 */
@Mixin(ClientRecipeBook.class)
public class ClientRecipeBookMixin {
	@Inject(method = "getCategories", at = @At("HEAD"), cancellable = true)
	private static void provideCorrectCategories(RecipeBookContainer<?> container, CallbackInfoReturnable<List<RecipeBookCategories>> callback) {
		if (container instanceof KeypadFurnaceMenu)
			callback.setReturnValue(Lists.newArrayList(RecipeBookCategories.FURNACE_SEARCH, RecipeBookCategories.FURNACE_FOOD, RecipeBookCategories.FURNACE_BLOCKS, RecipeBookCategories.FURNACE_MISC));
		else if (container instanceof KeypadSmokerMenu)
			callback.setReturnValue(Lists.newArrayList(RecipeBookCategories.SMOKER_SEARCH, RecipeBookCategories.SMOKER_FOOD));
		else if (container instanceof KeypadBlastFurnaceMenu)
			callback.setReturnValue(Lists.newArrayList(RecipeBookCategories.BLAST_FURNACE_SEARCH, RecipeBookCategories.BLAST_FURNACE_BLOCKS, RecipeBookCategories.BLAST_FURNACE_MISC));
	}
}
