package net.geforcemods.securitycraft.datagen;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import net.geforcemods.securitycraft.SecurityCraft;
import net.minecraft.DetectedVersion;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.LootTableProvider.SubProviderEntry;
import net.minecraft.data.metadata.PackMetadataGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.util.InclusiveRange;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod.EventBusSubscriber;
import net.neoforged.fml.common.Mod.EventBusSubscriber.Bus;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = SecurityCraft.MODID, bus = Bus.MOD)
public class DataGenRegistrar {
	private DataGenRegistrar() {}

	@SubscribeEvent
	public static void onGatherData(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		PackOutput output = generator.getPackOutput();
		CompletableFuture<Provider> lookupProvider = event.getLookupProvider();
		ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
		BlockTagGenerator blockTagGenerator = new BlockTagGenerator(output, lookupProvider, existingFileHelper);

		generator.addProvider(event.includeClient(), new BlockModelAndStateGenerator(output, existingFileHelper));
		generator.addProvider(event.includeServer(), new DamageTypeTagGenerator(output, lookupProvider, existingFileHelper));
		generator.addProvider(event.includeClient(), new ItemModelGenerator(output, existingFileHelper));
		generator.addProvider(event.includeServer(), new LootTableProvider(output, Set.of(), List.of(new SubProviderEntry(BlockLootTableGenerator::new, LootContextParamSets.BLOCK))));
		generator.addProvider(event.includeServer(), blockTagGenerator);
		generator.addProvider(event.includeServer(), new ItemTagGenerator(output, lookupProvider, blockTagGenerator.contentsGetter(), existingFileHelper));

		//		if (ModList.get().isLoaded("projecte"))
		//			generator.addProvider(event.includeServer(), new ProjectECompatConversionProvider(generator));

		//@formatter:off
		generator.addProvider(true, new PackMetadataGenerator(output)
                .add(PackMetadataSection.TYPE, new PackMetadataSection(Component.literal("SecurityCraft resources & data"),
                        DetectedVersion.BUILT_IN.getPackVersion(PackType.CLIENT_RESOURCES),
                        Optional.of(new InclusiveRange<>(0, Integer.MAX_VALUE)))));
		//@formatter:on
		generator.addProvider(event.includeServer(), new RecipeGenerator(output));
	}
}
