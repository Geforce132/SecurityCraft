package net.geforcemods.securitycraft.datagen;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import net.geforcemods.securitycraft.SecurityCraft;
import net.minecraft.DetectedVersion;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.LootTableProvider.SubProviderEntry;
import net.minecraft.data.metadata.PackMetadataGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.util.InclusiveRange;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.data.event.GatherDataEvent.DataProviderFromOutputLookup;

@EventBusSubscriber(modid = SecurityCraft.MODID, bus = Bus.MOD)
public class DataGenRegistrar {
	private DataGenRegistrar() {}

	@SubscribeEvent
	public static void onGatherData(GatherDataEvent event) {
		boolean includeClient = event.includeClient();
		boolean includeServer = event.includeServer();

		event.createProvider(includeClient, BlockModelAndStateGenerator::new);
		event.createProvider(includeServer, DamageTypeTagGenerator::new);
		event.createProvider(includeServer, EntityTypeTagGenerator::new);
		event.createProvider(includeServer, FluidTagGenerator::new);
		event.createProvider(includeClient, ItemModelGenerator::new);
		event.createProvider(includeServer, (DataProviderFromOutputLookup<LootTableProvider>) (output, lookupProvider) -> new LootTableProvider(output, Set.of(), List.of(new SubProviderEntry(BlockLootTableGenerator::new, LootContextParamSets.BLOCK)), lookupProvider));
		event.createBlockAndItemTags(BlockTagGenerator::new, ItemTagGenerator::new);

		//		if (ModList.get().isLoaded("projecte"))
		//			generator.addProvider(event.includeServer(), new ProjectECompatConversionProvider(generator));

		//@formatter:off
		event.createProvider(true, output -> new PackMetadataGenerator(output)
                .add(PackMetadataSection.TYPE, new PackMetadataSection(Component.literal("SecurityCraft resources & data"),
                        DetectedVersion.BUILT_IN.getPackVersion(PackType.CLIENT_RESOURCES),
                        Optional.of(new InclusiveRange<>(0, Integer.MAX_VALUE)))));
		//@formatter:on
		event.createProvider(includeServer, RecipeGenerator.Runner::new);
	}
}
