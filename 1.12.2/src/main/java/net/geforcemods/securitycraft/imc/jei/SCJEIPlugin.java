package net.geforcemods.securitycraft.imc.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blocks.IPasswordConvertible;
import net.geforcemods.securitycraft.blocks.reinforced.IReinforcedBlock;
import net.minecraft.item.ItemStack;

@JEIPlugin
public class SCJEIPlugin implements IModPlugin
{
	@Override
	public void register(IModRegistry registry)
	{
		registry.addAdvancedGuiHandlers(new SlotMover());
		registry.addIngredientInfo(new ItemStack(SCContent.adminTool), ItemStack.class, "gui.scManual.recipe.adminTool");
		IPasswordConvertible.BLOCKS.forEach((pc) ->  {
			registry.addIngredientInfo(new ItemStack(pc), ItemStack.class, "gui.scManual.recipe." + pc.getRegistryName().getPath());
		});
		registry.addIngredientInfo(new ItemStack(SCContent.scManual), ItemStack.class, "gui.scManual.recipe.scManual");
		IReinforcedBlock.BLOCKS.forEach((rb) -> {
			IReinforcedBlock reinforcedBlock = (IReinforcedBlock)rb;

			reinforcedBlock.getVanillaBlocks().forEach((vanillaBlock) -> {
				if(reinforcedBlock.getVanillaBlocks().size() == reinforcedBlock.getAmount())
					registry.addIngredientInfo(new ItemStack(rb, 1, reinforcedBlock.getVanillaBlocks().indexOf(vanillaBlock)), ItemStack.class, "jei.reinforcedBlock.info", "", vanillaBlock.getTranslationKey() + ".name");
				else
				{
					for(int i = 0; i < reinforcedBlock.getAmount(); i++)
					{
						registry.addIngredientInfo(new ItemStack(rb, 1, i), ItemStack.class, "jei.reinforcedBlock.info", "", new ItemStack(vanillaBlock, 1, i).getTranslationKey() + ".name");
					}
				}
			});
		});
	}
}
