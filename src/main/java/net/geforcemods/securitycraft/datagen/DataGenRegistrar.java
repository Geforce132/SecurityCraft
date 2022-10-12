package net.geforcemods.securitycraft.datagen;

import java.util.Collections;

import net.geforcemods.securitycraft.SecurityCraft;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(modid = SecurityCraft.MODID, bus = Bus.MOD)
public class DataGenRegistrar {
	@SubscribeEvent
	public static void onGatherData(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		ExistingFileHelper existingFileHelper = new ExistingFileHelper(Collections.EMPTY_LIST, Collections.EMPTY_SET, false, null, null);
		BlockTagGenerator blockTagGenerator = new BlockTagGenerator(generator, existingFileHelper);

		generator.addProvider(event.includeClient(), new BlockModelAndStateGenerator(generator, existingFileHelper));
		generator.addProvider(event.includeClient(), new ItemModelGenerator(generator, existingFileHelper));
		generator.addProvider(event.includeServer(), new BlockLootTableGenerator(generator));
		generator.addProvider(event.includeServer(), blockTagGenerator);
		generator.addProvider(event.includeServer(), new ItemTagGenerator(generator, blockTagGenerator, existingFileHelper));
		generator.addProvider(event.includeServer(), new ProjectECompatConversionProvider(generator));
		generator.addProvider(event.includeServer(), new RecipeGenerator(generator));
	}
}
