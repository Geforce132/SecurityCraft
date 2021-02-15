package net.geforcemods.securitycraft.datagen;

import java.util.Collections;

import net.geforcemods.securitycraft.SecurityCraft;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
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
		ExistingFileHelper existingFileHelper = new ExistingFileHelper(Collections.EMPTY_LIST, Collections.EMPTY_SET, false);
		BlockTagGenerator blockTagGenerator = new BlockTagGenerator(generator, existingFileHelper);

		generator.addProvider(new BlockLootTableGenerator(generator));
		generator.addProvider(new BlockModelAndStateGenerator(generator, event.getExistingFileHelper()));
		generator.addProvider(blockTagGenerator);
		generator.addProvider(new ItemModelGenerator(generator, existingFileHelper));
		generator.addProvider(new FluidTagGenerator(generator, existingFileHelper));
		generator.addProvider(new ItemTagGenerator(generator, blockTagGenerator, existingFileHelper));
		generator.addProvider(new RecipeGenerator(generator));
	}
}
