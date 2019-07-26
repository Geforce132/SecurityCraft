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
import net.geforcemods.securitycraft.gui.GuiCustomizeBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

@JeiPlugin
public class SCJEIPlugin implements IModPlugin
{
	@Override
	public void registerRecipes(IRecipeRegistration registration)
	{
		registration.addIngredientInfo(new ItemStack(SCContent.adminTool), VanillaTypes.ITEM, "gui.securitycraft:scManual.recipe.adminTool");
		IPasswordConvertible.BLOCKS.forEach((pc) ->  {
			registration.addIngredientInfo(new ItemStack(pc), VanillaTypes.ITEM, "gui.securitycraft:scManual.recipe." + pc.getRegistryName().getPath());
		});
		IReinforcedBlock.BLOCKS.forEach((rb) -> {
			IReinforcedBlock reinforcedBlock = (IReinforcedBlock)rb;

			registration.addIngredientInfo(new ItemStack(rb), VanillaTypes.ITEM, "jei.securitycraft:reinforcedBlock.info", "", reinforcedBlock.getVanillaBlock().getTranslationKey());
		});
	}

	@Override
	public void registerGuiHandlers(IGuiHandlerRegistration registration)
	{
		registration.addGuiContainerHandler(GuiCustomizeBlock.class, new SlotMover());
	}

	@Override
	public ResourceLocation getPluginUid()
	{
		return new ResourceLocation(SecurityCraft.MODID, SecurityCraft.MODID);
	}
}
