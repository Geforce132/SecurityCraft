package net.geforcemods.securitycraft.datagen;

import net.geforcemods.securitycraft.SecurityCraft;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

@EventBusSubscriber(modid=SecurityCraft.MODID, bus=Bus.MOD)
public class DataGenRegistrar
{
	@SubscribeEvent
	public static void onGatherData(GatherDataEvent event)
	{
		DataGenerator generator = event.getGenerator();
		BlockTagGenerator blockTagGenerator = new BlockTagGenerator(generator);

		generator.addProvider(new BlockLootTableGenerator(generator));
		generator.addProvider(new BlockModelAndStateGenerator(generator, event.getExistingFileHelper()));
		generator.addProvider(blockTagGenerator);
		generator.addProvider(new ItemModelGenerator(generator, event.getExistingFileHelper()));
		generator.addProvider(new FluidTagGenerator(generator));
		generator.addProvider(new ItemTagGenerator(generator, blockTagGenerator));
		generator.addProvider(new RecipeGenerator(generator));
	}
}
