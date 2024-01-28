package net.geforcemods.securitycraft.compat.jei;

import java.util.ArrayList;
import java.util.List;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IReinforcedBlock;
import net.geforcemods.securitycraft.screen.BlockChangeDetectorScreen;
import net.geforcemods.securitycraft.screen.BlockPocketManagerScreen;
import net.geforcemods.securitycraft.screen.CustomizeBlockScreen;
import net.geforcemods.securitycraft.screen.DisguiseModuleScreen;
import net.geforcemods.securitycraft.screen.InventoryScannerScreen;
import net.geforcemods.securitycraft.screen.ProjectorScreen;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

@JEIPlugin
public class SCJEIPlugin implements IModPlugin {
	public static final String VTS_ID = SecurityCraft.MODID + ":vanilla_to_securitycraft";
	public static final String STV_ID = SecurityCraft.MODID + ":securitycraft_to_vanilla";

	@Override
	public void register(IModRegistry registry) {
		List<ReinforcerRecipe> vtsRecipes = new ArrayList<>();
		List<ReinforcerRecipe> stvRecipes = new ArrayList<>();
		ItemStack vanillaCauldron = new ItemStack(Items.CAULDRON);
		ItemStack reinforcedCauldron = new ItemStack(SCContent.reinforcedCauldron);

		//@formatter:off
		registry.addAdvancedGuiHandlers(
				new SlotMover<>(CustomizeBlockScreen.class),
				new SlotMover<>(ProjectorScreen.class),
				new SlotMover<>(DisguiseModuleScreen.class),
				new SlotMover<>(BlockChangeDetectorScreen.class),
				new SlotMover<>(BlockPocketManagerScreen.class));
		//@formatter:on
		registry.addIngredientInfo(new ItemStack(SCContent.adminTool), VanillaTypes.ITEM, "gui.securitycraft:scManual.recipe.admin_tool");
		registry.addIngredientInfo(new ItemStack(SCContent.keypad), VanillaTypes.ITEM, "gui.securitycraft:scManual.recipe.keypad");
		registry.addIngredientInfo(new ItemStack(SCContent.keypadChest), VanillaTypes.ITEM, "gui.securitycraft:scManual.recipe.keypad_chest");
		registry.addIngredientInfo(new ItemStack(SCContent.keypadFurnace), VanillaTypes.ITEM, "gui.securitycraft:scManual.recipe.keypad_furnace");
		registry.addIngredientInfo(new ItemStack(SCContent.keypadTrapdoor), VanillaTypes.ITEM, "gui.securitycraft:scManual.recipe.keypad_trapdoor");
		IReinforcedBlock.VANILLA_TO_SECURITYCRAFT.forEach((vanillaBlock, securityCraftBlock) -> {
			IReinforcedBlock reinforcedBlock = (IReinforcedBlock) securityCraftBlock;
			NonNullList<ItemStack> subBlocks = NonNullList.create();

			vanillaBlock.getSubBlocks(CreativeTabs.SEARCH, subBlocks);
			subBlocks.forEach(vanillaStack -> {
				if (!vanillaStack.isEmpty()) {
					ItemStack reinforcedStack = reinforcedBlock.convertToReinforcedStack(vanillaStack, vanillaBlock);

					vtsRecipes.add(new ReinforcerRecipe(vanillaStack, reinforcedStack));
					stvRecipes.add(new ReinforcerRecipe(reinforcedStack, vanillaStack));
				}
			});
		});
		vtsRecipes.add(new ReinforcerRecipe(vanillaCauldron, reinforcedCauldron));
		stvRecipes.add(new ReinforcerRecipe(reinforcedCauldron, vanillaCauldron));
		registry.addRecipes(vtsRecipes, VTS_ID);
		registry.addRecipes(stvRecipes, STV_ID);
		registry.addRecipeCatalyst(new ItemStack(SCContent.keypadFurnace), VanillaRecipeCategoryUid.SMELTING);
		registry.addRecipeCatalyst(new ItemStack(SCContent.universalBlockReinforcerLvL1), VTS_ID);
		registry.addRecipeCatalyst(new ItemStack(SCContent.universalBlockReinforcerLvL2), VTS_ID, STV_ID);
		registry.addRecipeCatalyst(new ItemStack(SCContent.universalBlockReinforcerLvL3), VTS_ID, STV_ID);
		registry.addGhostIngredientHandler(InventoryScannerScreen.class, new InventoryScannerGhostIngredientHandler());
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registration) {
		registration.addRecipeCategories(new VanillaToSecurityCraftCategory(registration.getJeiHelpers().getGuiHelper()));
		registration.addRecipeCategories(new SecurityCraftToVanillaCategory(registration.getJeiHelpers().getGuiHelper()));
	}
}
