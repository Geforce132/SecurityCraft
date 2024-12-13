package net.geforcemods.securitycraft.datagen;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import net.geforcemods.securitycraft.SCContent;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ModelInstance;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class ItemModelGenerator {
	private static ItemModelGenerators itemModels;
	private static BiConsumer<ResourceLocation, ModelInstance> modelOutput;

	protected static void run(ItemModelGenerators itemModels) {
		//@formatter:off
		List<Item> singleTextureItems = new ArrayList<>(SCContent.ITEMS.getEntries().stream().map(Holder::value).toList());
		List<Item> handheldItems = List.of(
				SCContent.UNIVERSAL_BLOCK_REINFORCER_LVL_1.get(),
				SCContent.UNIVERSAL_BLOCK_REINFORCER_LVL_2.get(),
				SCContent.UNIVERSAL_BLOCK_REINFORCER_LVL_3.get(),
				SCContent.UNIVERSAL_KEY_CHANGER.get());
		List<Item> linkingStateItems = List.of(
				SCContent.CAMERA_MONITOR.get(),
				SCContent.MINE_REMOTE_ACCESS_TOOL.get(),
				SCContent.SENTRY_REMOTE_ACCESS_TOOL.get(),
				SCContent.SONIC_SECURITY_SYSTEM_ITEM.get());

		singleTextureItems.removeAll(List.of(
				SCContent.ANCIENT_DEBRIS_MINE_ITEM.get(),
				SCContent.BRIEFCASE.get(),
				SCContent.CODEBREAKER.get(),
				SCContent.DISPLAY_CASE_ITEM.get(),
				SCContent.GLOW_DISPLAY_CASE_ITEM.get(),
				SCContent.KEYCARD_HOLDER.get(),
				SCContent.KEYPAD_CHEST_ITEM.get(),
				SCContent.LENS.get(),
				SCContent.REDSTONE_MODULE.get(),
				SCContent.REINFORCED_SCAFFOLDING_ITEM.get(),
				SCContent.SPEED_MODULE.get(),
				SCContent.TASER.get(),
				SCContent.TASER_POWERED.get(),
				SCContent.UNIVERSAL_BLOCK_MODIFIER.get(),
				SCContent.UNIVERSAL_BLOCK_REMOVER.get(),
				SCContent.UNIVERSAL_OWNER_CHANGER.get(),
				SCContent.WIRE_CUTTERS.get()));
		//@formatter:on
		ItemModelGenerator.itemModels = itemModels;
		modelOutput = itemModels.modelOutput;
		singleTextureItems.removeAll(handheldItems);
		singleTextureItems.removeAll(linkingStateItems);

		for (Item item : singleTextureItems) {
			itemModels.createFlatItemModel(item, ModelTemplates.FLAT_ITEM);
		}

		for (Item item : handheldItems) {
			itemModels.createFlatItemModel(item, ModelTemplates.FLAT_HANDHELD_ITEM);
		}

		linkingStateItems.forEach(ItemModelGenerator::linkingStateItem);
		codebreaker();
	}

	public static void linkingStateItem(Item item) {
		itemModels.createFlatItemModel(item, "_idle", ModelTemplates.FLAT_ITEM);
		itemModels.createFlatItemModel(item, "_not_linked", ModelTemplates.FLAT_ITEM);
		itemModels.createFlatItemModel(item, "_linked", ModelTemplates.FLAT_ITEM);
		itemModels.createFlatItemModel(item, ModelTemplates.FLAT_ITEM);
	}

	public static void codebreaker() {
		itemModels.createFlatItemModel(SCContent.CODEBREAKER.get(), "_decoding", ModelTemplates.FLAT_ITEM);
		itemModels.createFlatItemModel(SCContent.CODEBREAKER.get(), "_failure", ModelTemplates.FLAT_ITEM);
		itemModels.createFlatItemModel(SCContent.CODEBREAKER.get(), "_success", ModelTemplates.FLAT_ITEM);
		itemModels.createFlatItemModel(SCContent.CODEBREAKER.get(), ModelTemplates.FLAT_ITEM);
	}
}
