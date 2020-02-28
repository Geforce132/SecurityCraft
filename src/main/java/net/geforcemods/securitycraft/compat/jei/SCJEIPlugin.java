package net.geforcemods.securitycraft.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blocks.IPasswordConvertible;
import net.geforcemods.securitycraft.blocks.reinforced.IReinforcedBlock;
import net.geforcemods.securitycraft.screen.CustomizeBlockScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

@JeiPlugin
public class SCJEIPlugin implements IModPlugin
{
	@Override
	public void registerRecipes(IRecipeRegistration registration)
	{
		registration.addIngredientInfo(new ItemStack(SCContent.ADMIN_TOOL.get()), VanillaTypes.ITEM, "gui.securitycraft:scManual.recipe.adminTool");
		IPasswordConvertible.BLOCKS.forEach((pc) ->  {
			registration.addIngredientInfo(new ItemStack(pc), VanillaTypes.ITEM, "gui.securitycraft:scManual.recipe." + pc.getRegistryName().getPath());
		});
		IReinforcedBlock.VANILLA_TO_SECURITYCRAFT.forEach((v, sc) -> {
			registration.addIngredientInfo(new ItemStack(sc), VanillaTypes.ITEM, "jei.securitycraft:reinforcedBlock.info", "", v.getTranslationKey());
		});
	}

	@Override
	public void registerGuiHandlers(IGuiHandlerRegistration registration)
	{
		registration.addGuiContainerHandler(CustomizeBlockScreen.class, new SlotMover());
	}

	@Override
	public ResourceLocation getPluginUid()
	{
		return new ResourceLocation(SecurityCraft.MODID, SecurityCraft.MODID);
	}
}
