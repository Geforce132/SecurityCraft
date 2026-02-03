package net.geforcemods.securitycraft.datagen;

import java.util.List;
import java.util.Set;

import net.geforcemods.securitycraft.SecurityCraft;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.LootTableProvider.SubProviderEntry;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.data.event.GatherDataEvent.DataProviderFromOutputLookup;

@EventBusSubscriber(modid = SecurityCraft.MODID, value = Dist.CLIENT)
public class DataGenRegistrar {
	private DataGenRegistrar() {}

	@SubscribeEvent
	public static void onGatherData(GatherDataEvent.Client event) {
		SecurityCraft.collectSCContentData(false);
		event.createProvider(DamageTypeTagGenerator::new);
		event.createProvider(EntityTypeTagGenerator::new);
		event.createProvider(FluidTagGenerator::new);
		event.createProvider((DataProviderFromOutputLookup<LootTableProvider>) (output, lookupProvider) -> new LootTableProvider(output, Set.of(), List.of(new SubProviderEntry(BlockLootTableGenerator::new, LootContextParamSets.BLOCK)), lookupProvider));
		event.createProvider(output -> new ModelProvider(output, SecurityCraft.MODID) {
			@Override
			protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
				BlockModelAndStateGenerator.run(blockModels);
				ItemModelGenerator.run(itemModels);
			}
		});
		event.createBlockAndItemTags(BlockTagGenerator::new, ItemTagGenerator::new);
		event.createProvider(RecipeGenerator.Runner::new);

		//		if (ModList.get().isLoaded("projecte"))
		//			generator.addProvider(event.includeServer(), new ProjectECompatConversionProvider(generator));
	}
}
