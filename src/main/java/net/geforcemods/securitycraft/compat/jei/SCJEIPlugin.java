package net.geforcemods.securitycraft.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IReinforcedBlock;
import net.geforcemods.securitycraft.screen.BlockChangeDetectorScreen;
import net.geforcemods.securitycraft.screen.BlockPocketManagerScreen;
import net.geforcemods.securitycraft.screen.CustomizeBlockScreen;
import net.geforcemods.securitycraft.screen.DisguiseModuleScreen;
import net.geforcemods.securitycraft.screen.InventoryScannerScreen;
import net.geforcemods.securitycraft.screen.ProjectorScreen;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

@JeiPlugin
public class SCJEIPlugin implements IModPlugin {
	public static final RecipeType<ReinforcerRecipe> VTS = RecipeType.create(SecurityCraft.MODID, "vanilla_to_securitycraft", ReinforcerRecipe.class);
	public static final RecipeType<ReinforcerRecipe> STV = RecipeType.create(SecurityCraft.MODID, "securitycraft_to_vanilla", ReinforcerRecipe.class);

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		registration.addIngredientInfo(new ItemStack(SCContent.ADMIN_TOOL.get()), VanillaTypes.ITEM_STACK, Utils.localize("gui.securitycraft:scManual.recipe.admin_tool"));
		registration.addIngredientInfo(new ItemStack(SCContent.KEYPAD.get()), VanillaTypes.ITEM_STACK, Utils.localize("gui.securitycraft:scManual.recipe." + Utils.getRegistryName(SCContent.KEYPAD.get()).getPath()));
		registration.addIngredientInfo(new ItemStack(SCContent.KEYPAD_CHEST.get()), VanillaTypes.ITEM_STACK, Utils.localize("gui.securitycraft:scManual.recipe." + Utils.getRegistryName(SCContent.KEYPAD_CHEST.get()).getPath()));
		registration.addIngredientInfo(new ItemStack(SCContent.KEYPAD_FURNACE.get()), VanillaTypes.ITEM_STACK, Utils.localize("gui.securitycraft:scManual.recipe." + Utils.getRegistryName(SCContent.KEYPAD_FURNACE.get()).getPath()));
		registration.addIngredientInfo(new ItemStack(SCContent.KEYPAD_SMOKER.get()), VanillaTypes.ITEM_STACK, Utils.localize("gui.securitycraft:scManual.recipe." + Utils.getRegistryName(SCContent.KEYPAD_SMOKER.get()).getPath()));
		registration.addIngredientInfo(new ItemStack(SCContent.KEYPAD_BLAST_FURNACE.get()), VanillaTypes.ITEM_STACK, Utils.localize("gui.securitycraft:scManual.recipe." + Utils.getRegistryName(SCContent.KEYPAD_BLAST_FURNACE.get()).getPath()));
		registration.addIngredientInfo(new ItemStack(SCContent.KEYPAD_TRAPDOOR.get()), VanillaTypes.ITEM_STACK, Utils.localize("gui.securitycraft:scManual.recipe." + Utils.getRegistryName(SCContent.KEYPAD_TRAPDOOR.get()).getPath()));
		//@formatter:off
		registration.addRecipes(VTS, IReinforcedBlock.VANILLA_TO_SECURITYCRAFT.entrySet().stream()
				.filter(entry -> entry.getKey().asItem() != Items.AIR && entry.getValue().asItem() != Items.AIR)
				.map(entry -> new ReinforcerRecipe(entry.getKey(), entry.getValue()))
				.toList());
		registration.addRecipes(STV, IReinforcedBlock.SECURITYCRAFT_TO_VANILLA.entrySet().stream()
				.filter(entry -> entry.getKey().asItem() != Items.AIR && entry.getValue().asItem() != Items.AIR)
				.map(entry -> new ReinforcerRecipe(entry.getValue(), entry.getKey()))
				.toList());
		//@formatter:on
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registration) {
		registration.addRecipeCategories(new VanillaToSecurityCraftCategory(registration.getJeiHelpers().getGuiHelper()));
		registration.addRecipeCategories(new SecurityCraftToVanillaCategory(registration.getJeiHelpers().getGuiHelper()));
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
		registration.addRecipeCatalyst(new ItemStack(SCContent.KEYPAD_FURNACE.get()), RecipeTypes.SMELTING);
		registration.addRecipeCatalyst(new ItemStack(SCContent.KEYPAD_SMOKER.get()), RecipeTypes.SMOKING);
		registration.addRecipeCatalyst(new ItemStack(SCContent.KEYPAD_BLAST_FURNACE.get()), RecipeTypes.BLASTING);
		registration.addRecipeCatalyst(new ItemStack(SCContent.UNIVERSAL_BLOCK_REINFORCER_LVL_1.get()), VTS);
		registration.addRecipeCatalyst(new ItemStack(SCContent.UNIVERSAL_BLOCK_REINFORCER_LVL_2.get()), VTS, STV);
		registration.addRecipeCatalyst(new ItemStack(SCContent.UNIVERSAL_BLOCK_REINFORCER_LVL_3.get()), VTS, STV);
	}

	@Override
	public void registerGuiHandlers(IGuiHandlerRegistration registration) {
		registration.addGuiContainerHandler(BlockChangeDetectorScreen.class, new SlotMover<>());
		registration.addGuiContainerHandler(BlockPocketManagerScreen.class, new SlotMover<>());
		registration.addGuiContainerHandler(CustomizeBlockScreen.class, new SlotMover<>());
		registration.addGuiContainerHandler(DisguiseModuleScreen.class, new SlotMover<>());
		registration.addGuiContainerHandler(ProjectorScreen.class, new SlotMover<>());
		registration.addGhostIngredientHandler(InventoryScannerScreen.class, new InventoryScannerGhostIngredientHandler());
	}

	@Override
	public ResourceLocation getPluginUid() {
		return new ResourceLocation(SecurityCraft.MODID, SecurityCraft.MODID);
	}
}
