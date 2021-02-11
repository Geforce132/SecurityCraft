package net.geforcemods.securitycraft.compat.jei;

import java.util.ArrayList;
import java.util.List;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blocks.reinforced.IReinforcedBlock;
import net.minecraft.item.ItemStack;

@JEIPlugin
public class SCJEIPlugin implements IModPlugin
{
	public static final String VTS_ID = SecurityCraft.MODID + ":vanilla_to_securitycraft";
	public static final String STV_ID = SecurityCraft.MODID + ":securitycraft_to_vanilla";

	@Override
	public void register(IModRegistry registry)
	{
		List<ReinforcerRecipe> vtsRecipes = new ArrayList<>();
		List<ReinforcerRecipe> stvRecipes = new ArrayList<>();

		registry.addAdvancedGuiHandlers(new SlotMover());
		registry.addIngredientInfo(new ItemStack(SCContent.adminTool), VanillaTypes.ITEM, "gui.securitycraft:scManual.recipe.adminTool");
		registry.addIngredientInfo(new ItemStack(SCContent.keypad), VanillaTypes.ITEM, "gui.securitycraft:scManual.recipe.keypad");
		registry.addIngredientInfo(new ItemStack(SCContent.keypadChest), VanillaTypes.ITEM, "gui.securitycraft:scManual.recipe.keypad_chest");
		registry.addIngredientInfo(new ItemStack(SCContent.keypadFurnace), VanillaTypes.ITEM, "gui.securitycraft:scManual.recipe.keypad_furnace");
		IReinforcedBlock.BLOCKS.forEach(rb -> {
			IReinforcedBlock reinforcedBlock = (IReinforcedBlock)rb;

			reinforcedBlock.getVanillaBlocks().forEach(vanillaBlock -> {
				if(reinforcedBlock.getVanillaBlocks().size() == reinforcedBlock.getAmount())
				{
					int meta = reinforcedBlock.getVanillaBlocks().indexOf(vanillaBlock);

					vtsRecipes.add(new ReinforcerRecipe(new ItemStack(vanillaBlock, 1, 0), new ItemStack(rb, 1, meta)));
					stvRecipes.add(new ReinforcerRecipe(new ItemStack(rb, 1, meta), new ItemStack(vanillaBlock, 1, 0)));
				}
				else
				{
					for(int i = 0; i < reinforcedBlock.getAmount(); i++)
					{
						vtsRecipes.add(new ReinforcerRecipe(new ItemStack(vanillaBlock, 1, i), new ItemStack(rb, 1, i)));
						stvRecipes.add(new ReinforcerRecipe(new ItemStack(rb, 1, i), new ItemStack(vanillaBlock, 1, i)));
					}
				}
			});
		});
		registry.addRecipes(vtsRecipes, VTS_ID);
		registry.addRecipes(stvRecipes, STV_ID);
		registry.addRecipeCatalyst(new ItemStack(SCContent.universalBlockReinforcerLvL1), VTS_ID);
		registry.addRecipeCatalyst(new ItemStack(SCContent.universalBlockReinforcerLvL2), VTS_ID, STV_ID);
		registry.addRecipeCatalyst(new ItemStack(SCContent.universalBlockReinforcerLvL3), VTS_ID, STV_ID);
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registration)
	{
		registration.addRecipeCategories(new VanillaToSecurityCraftCategory(registration.getJeiHelpers().getGuiHelper()));
		registration.addRecipeCategories(new SecurityCraftToVanillaCategory(registration.getJeiHelpers().getGuiHelper()));
	}
}
