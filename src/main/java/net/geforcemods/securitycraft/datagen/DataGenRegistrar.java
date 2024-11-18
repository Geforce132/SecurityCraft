package net.geforcemods.securitycraft.datagen;

import net.geforcemods.securitycraft.SecurityCraft;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

@EventBusSubscriber(modid = SecurityCraft.MODID, bus = Bus.MOD)
public class DataGenRegistrar {
	private DataGenRegistrar() {}

	@SubscribeEvent
	public static void onGatherData(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
		BlockTagGenerator blockTagGenerator = new BlockTagGenerator(generator, existingFileHelper);

		generator.addProvider(new BlockLootTableGenerator(generator));
		generator.addProvider(new BlockModelAndStateGenerator(generator, existingFileHelper));
		generator.addProvider(blockTagGenerator);
		generator.addProvider(new ItemModelGenerator(generator, existingFileHelper));
		generator.addProvider(new ItemTagGenerator(generator, blockTagGenerator, existingFileHelper));
		generator.addProvider(new FluidTagGenerator(generator, existingFileHelper));

		if (ModList.get().isLoaded("projecte"))
			generator.addProvider(new ProjectECompatConversionProvider(generator));

		generator.addProvider(new RecipeGenerator(generator));
	}
}
