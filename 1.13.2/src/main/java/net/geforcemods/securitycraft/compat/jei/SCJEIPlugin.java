package net.geforcemods.securitycraft.compat.jei;

/*
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blocks.IPasswordConvertible;
import net.geforcemods.securitycraft.blocks.reinforced.IReinforcedBlock;
import net.minecraft.item.ItemStack;*/

/*
@JEIPlugin
public class SCJEIPlugin implements IModPlugin
{*/
public class SCJEIPlugin 
{
	/*@Override
	public void register(IModRegistry registry)
	{
		registry.addAdvancedGuiHandlers(new SlotMover());
		registry.addIngredientInfo(new ItemStack(SCContent.adminTool), ItemStack.class, "gui.securitycraft:scManual.recipe.adminTool");
		IPasswordConvertible.BLOCKS.forEach((pc) ->  {
			registry.addIngredientInfo(new ItemStack(pc), ItemStack.class, "gui.securitycraft:scManual.recipe." + pc.getRegistryName().getPath());
		});
		IReinforcedBlock.BLOCKS.forEach((rb) -> {
			IReinforcedBlock reinforcedBlock = (IReinforcedBlock)rb;

			registry.addIngredientInfo(new ItemStack(rb), ItemStack.class, "jei.securitycraft:reinforcedBlock.info", "", reinforcedBlock.getVanillaBlock().getTranslationKey() + ".name");
		});
	}*/
}
