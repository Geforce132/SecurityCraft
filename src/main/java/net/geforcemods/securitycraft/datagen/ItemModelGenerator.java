package net.geforcemods.securitycraft.datagen;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.items.properties.BlockLinked;
import net.geforcemods.securitycraft.items.properties.CodebreakerState;
import net.geforcemods.securitycraft.items.properties.HitCheck;
import net.geforcemods.securitycraft.items.properties.KeycardCount;
import net.geforcemods.securitycraft.items.properties.SentryLinked;
import net.minecraft.client.color.item.Dye;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ItemModelOutput;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelInstance;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class ItemModelGenerator {
	private static ItemModelGenerators itemModels;
	private static BiConsumer<ResourceLocation, ModelInstance> modelOutput;
	private static ItemModelOutput itemInfo;

	private ItemModelGenerator() {}

	protected static void run(ItemModelGenerators itemModels) {
		//@formatter:off
		List<Item> singleTextureItems = new ArrayList<>(SCContent.ITEMS.getEntries().stream().map(Holder::value).toList());
		List<Item> handheldItems = List.of(
				SCContent.UNIVERSAL_BLOCK_REINFORCER_LVL_1.get(),
				SCContent.UNIVERSAL_BLOCK_REINFORCER_LVL_2.get(),
				SCContent.UNIVERSAL_BLOCK_REINFORCER_LVL_3.get(),
				SCContent.UNIVERSAL_KEY_CHANGER.get());
		List<Item> manualModelItems = List.of(
				SCContent.TASER.get(),
				SCContent.TASER_POWERED.get(),
				SCContent.UNIVERSAL_BLOCK_MODIFIER.get(),
				SCContent.UNIVERSAL_BLOCK_REMOVER.get(),
				SCContent.UNIVERSAL_OWNER_CHANGER.get(),
				SCContent.WIRE_CUTTERS.get());

		singleTextureItems.removeAll(List.of(
				SCContent.ANCIENT_DEBRIS_MINE_ITEM.get(),
				SCContent.BRIEFCASE.get(),
				SCContent.CAMERA_MONITOR.get(),
				SCContent.CODEBREAKER.get(),
				SCContent.DISPLAY_CASE_ITEM.get(),
				SCContent.GLOW_DISPLAY_CASE_ITEM.get(),
				SCContent.KEYCARD_HOLDER.get(),
				SCContent.KEYPAD_CHEST_ITEM.get(),
				SCContent.LENS.get(),
				SCContent.MINE_REMOTE_ACCESS_TOOL.get(),
				SCContent.PROJECTOR_ITEM.get(),
				SCContent.REDSTONE_MODULE.get(),
				SCContent.REINFORCED_SCAFFOLDING_ITEM.get(),
				SCContent.SENTRY_REMOTE_ACCESS_TOOL.get(),
				SCContent.SONIC_SECURITY_SYSTEM_ITEM.get(),
				SCContent.SPEED_MODULE.get()));
		//@formatter:on
		ItemModelGenerator.itemModels = itemModels;
		modelOutput = itemModels.modelOutput;
		itemInfo = itemModels.itemModelOutput;
		singleTextureItems.removeAll(handheldItems);
		singleTextureItems.removeAll(manualModelItems);

		for (Item item : singleTextureItems) {
			itemModels.generateFlatItem(item, ModelTemplates.FLAT_ITEM);
		}

		for (Item item : handheldItems) {
			itemModels.generateFlatItem(item, ModelTemplates.FLAT_HANDHELD_ITEM);
		}

		for (Item item : manualModelItems) {
			itemInfo.accept(item, ItemModelUtils.plainModel(ModelLocationUtils.getModelLocation(item)));
		}

		generateLinkingStateItem(SCContent.CAMERA_MONITOR.get(), new BlockLinked(SCContent.BOUND_CAMERAS.get(), HitCheck.SECURITY_CAMERA));
		generateBriefcase(SCContent.BRIEFCASE.get());
		generateCodebreaker(SCContent.CODEBREAKER.get());
		generateKeycardHolder(SCContent.KEYCARD_HOLDER.get());
		generateLens(SCContent.LENS.get());
		generateLinkingStateItem(SCContent.MINE_REMOTE_ACCESS_TOOL.get(), new BlockLinked(SCContent.BOUND_MINES.get(), HitCheck.EXPLOSIVE_BLOCK));
		generateLinkingStateItem(SCContent.SENTRY_REMOTE_ACCESS_TOOL.get(), new SentryLinked());
		generateLinkingStateItem(SCContent.SONIC_SECURITY_SYSTEM_ITEM.get(), new BlockLinked(SCContent.SSS_LINKED_BLOCKS.get(), HitCheck.LOCKABLE));
		generateTwoLayerModule(SCContent.REDSTONE_MODULE.get(), Items.REDSTONE);
		generateTwoLayerModule(SCContent.SPEED_MODULE.get(), Items.SUGAR);
	}

	public static void generateBriefcase(Item item) {
		//@formatter:off
		itemInfo.accept(item,
				ItemModelUtils.tintedModel(
						ModelLocationUtils.decorateItemModelLocation(SecurityCraft.resLoc("briefcase").toString()),
						new Dye(0xFF333333)));
		//@formatter:on
	}

	public static void generateCodebreaker(Item item) {
		ResourceLocation decodingModel = itemModels.createFlatItemModel(item, "_" + CodebreakerState.DECODING, ModelTemplates.FLAT_ITEM);
		ResourceLocation failureModel = itemModels.createFlatItemModel(item, "_" + CodebreakerState.FAILURE, ModelTemplates.FLAT_ITEM);
		ResourceLocation successModel = itemModels.createFlatItemModel(item, "_" + CodebreakerState.SUCCESS, ModelTemplates.FLAT_ITEM);
		ResourceLocation defaultModel = itemModels.createFlatItemModel(item, ModelTemplates.FLAT_ITEM);

		//@formatter:off
		itemInfo.accept(item,
				ItemModelUtils.select(new CodebreakerState(HitCheck.CODEBREAKABLE),
						ItemModelUtils.when(CodebreakerState.DEFAULT, ItemModelUtils.plainModel(defaultModel)),
						ItemModelUtils.when(CodebreakerState.DECODING, ItemModelUtils.plainModel(decodingModel)),
						ItemModelUtils.when(CodebreakerState.SUCCESS, ItemModelUtils.plainModel(successModel)),
						ItemModelUtils.when(CodebreakerState.FAILURE, ItemModelUtils.plainModel(failureModel))));
		//@formatter:on
	}

	public static void generateKeycardHolder(Item item) {
		ResourceLocation normalModel = itemModels.createFlatItemModel(item, ModelTemplates.FLAT_ITEM);
		ResourceLocation fullModel = itemModels.createFlatItemModel(item, "_full", ModelTemplates.FLAT_ITEM);

		//@formatter:off
		itemInfo.register(item, new ClientItem(
				ItemModelUtils.rangeSelect(new KeycardCount(),
						5,
						ItemModelUtils.plainModel(normalModel),
						ItemModelUtils.override(ItemModelUtils.plainModel(fullModel), 1)),
				new ClientItem.Properties(false)));
		//@formatter:on
	}

	public static void generateLens(Item item) {
		ResourceLocation coloredLens = ModelLocationUtils.decorateItemModelLocation(SecurityCraft.resLoc("colored_lens").toString());
		ResourceLocation coloredModel = ModelTemplates.FLAT_ITEM.create(coloredLens, TextureMapping.layer0(coloredLens), modelOutput);
		ResourceLocation normalModel = itemModels.createFlatItemModel(item, ModelTemplates.FLAT_ITEM);

		//@formatter:off
		itemInfo.accept(item,
				ItemModelUtils.conditional(
						ItemModelUtils.hasComponent(DataComponents.DYED_COLOR),
						ItemModelUtils.tintedModel(
								coloredModel,
								new Dye(0xFFFFFFFF)),
						ItemModelUtils.plainModel(normalModel)));
		//@formatter:on
	}

	public static void generateLinkingStateItem(Item item, SelectItemModelProperty<String> property) {
		ResourceLocation noPositionsModel = itemModels.createFlatItemModel(item, "_idle", ModelTemplates.FLAT_ITEM);
		ResourceLocation notLinkedModel = itemModels.createFlatItemModel(item, "_" + BlockLinked.NOT_LINKED, ModelTemplates.FLAT_ITEM);
		ResourceLocation linkedModel = itemModels.createFlatItemModel(item, "_" + BlockLinked.LINKED, ModelTemplates.FLAT_ITEM);
		ResourceLocation unknownModel = itemModels.createFlatItemModel(item, ModelTemplates.FLAT_ITEM);

		//@formatter:off
		itemInfo.accept(item,
				ItemModelUtils.select(property,
						ItemModelUtils.when(BlockLinked.NO_POSITIONS, ItemModelUtils.plainModel(noPositionsModel)),
						ItemModelUtils.when(BlockLinked.UNKNOWN, ItemModelUtils.plainModel(unknownModel)),
						ItemModelUtils.when( BlockLinked.NOT_LINKED, ItemModelUtils.plainModel(notLinkedModel)),
						ItemModelUtils.when(BlockLinked.LINKED, ItemModelUtils.plainModel(linkedModel))));
		//@formatter:on
	}

	public static void generateTwoLayerModule(Item module, Item overlay) {
		//@formatter:off
		itemInfo.accept(module,
				ItemModelUtils.plainModel(
						itemModels.generateLayeredItem(
								module,
								ModelLocationUtils.decorateItemModelLocation(SecurityCraft.resLoc("module_background").toString()),
								TextureMapping.getItemTexture(overlay))));
		//@formatter:on
	}
}
