package net.geforcemods.securitycraft.compat.jei;

import java.util.stream.Collectors;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaRecipeCategoryUid;
import mezz.jei.api.constants.VanillaTypes;
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
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;

@JeiPlugin
public class SCJEIPlugin implements IModPlugin {
	public static final ResourceLocation VTS_ID = new ResourceLocation(SecurityCraft.MODID, "vanilla_to_securitycraft");
	public static final ResourceLocation STV_ID = new ResourceLocation(SecurityCraft.MODID, "securitycraft_to_vanilla");

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		registration.addIngredientInfo(new ItemStack(SCContent.ADMIN_TOOL.get()), VanillaTypes.ITEM, "gui.securitycraft:scManual.recipe.admin_tool");
		registration.addIngredientInfo(new ItemStack(SCContent.KEYPAD.get()), VanillaTypes.ITEM, "gui.securitycraft:scManual.recipe." + SCContent.KEYPAD.get().getRegistryName().getPath());
		registration.addIngredientInfo(new ItemStack(SCContent.KEYPAD_CHEST.get()), VanillaTypes.ITEM, "gui.securitycraft:scManual.recipe." + SCContent.KEYPAD_CHEST.get().getRegistryName().getPath());
		registration.addIngredientInfo(new ItemStack(SCContent.KEYPAD_FURNACE.get()), VanillaTypes.ITEM, "gui.securitycraft:scManual.recipe." + SCContent.KEYPAD_FURNACE.get().getRegistryName().getPath());
		registration.addIngredientInfo(new ItemStack(SCContent.KEYPAD_SMOKER.get()), VanillaTypes.ITEM, "gui.securitycraft:scManual.recipe." + SCContent.KEYPAD_SMOKER.get().getRegistryName().getPath());
		registration.addIngredientInfo(new ItemStack(SCContent.KEYPAD_BLAST_FURNACE.get()), VanillaTypes.ITEM, "gui.securitycraft:scManual.recipe." + SCContent.KEYPAD_BLAST_FURNACE.get().getRegistryName().getPath());
		registration.addIngredientInfo(new ItemStack(SCContent.KEYPAD_TRAPDOOR.get()), VanillaTypes.ITEM, "gui.securitycraft:scManual.recipe." + SCContent.KEYPAD_TRAPDOOR.get().getRegistryName().getPath());
		//@formatter:off
		registration.addRecipes(IReinforcedBlock.VANILLA_TO_SECURITYCRAFT.entrySet().stream()
				.filter(entry -> entry.getKey().asItem() != Items.AIR && entry.getValue().asItem() != Items.AIR)
				.map(entry -> new ReinforcerRecipe(entry.getKey(), entry.getValue()))
				.collect(Collectors.toList()), VTS_ID);
		registration.addRecipes(IReinforcedBlock.SECURITYCRAFT_TO_VANILLA.entrySet().stream()
				.filter(entry -> entry.getKey().asItem() != Items.AIR && entry.getValue().asItem() != Items.AIR)
				.map(entry -> new ReinforcerRecipe(entry.getValue(), entry.getKey()))
				.collect(Collectors.toList()), STV_ID);
		//@formatter:on
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registration) {
		registration.addRecipeCategories(new VanillaToSecurityCraftCategory(registration.getJeiHelpers().getGuiHelper()));
		registration.addRecipeCategories(new SecurityCraftToVanillaCategory(registration.getJeiHelpers().getGuiHelper()));
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
		registration.addRecipeCatalyst(new ItemStack(SCContent.KEYPAD_FURNACE.get()), VanillaRecipeCategoryUid.FURNACE);
		registration.addRecipeCatalyst(new ItemStack(SCContent.KEYPAD_SMOKER.get()), VanillaRecipeCategoryUid.SMOKING);
		registration.addRecipeCatalyst(new ItemStack(SCContent.KEYPAD_BLAST_FURNACE.get()), VanillaRecipeCategoryUid.BLASTING);
		registration.addRecipeCatalyst(new ItemStack(SCContent.UNIVERSAL_BLOCK_REINFORCER_LVL_1.get()), VTS_ID);
		registration.addRecipeCatalyst(new ItemStack(SCContent.UNIVERSAL_BLOCK_REINFORCER_LVL_2.get()), VTS_ID, STV_ID);
		registration.addRecipeCatalyst(new ItemStack(SCContent.UNIVERSAL_BLOCK_REINFORCER_LVL_3.get()), VTS_ID, STV_ID);
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
