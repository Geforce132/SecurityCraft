package net.geforcemods.securitycraft.datagen;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SCCreativeModeTabs;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IReinforcedBlock;
import net.geforcemods.securitycraft.blocks.SecureRedstoneInterfaceBlock;
import net.geforcemods.securitycraft.blocks.mines.BaseFullMineBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedButtonBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedCarpetBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedFenceBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedFenceGateBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedSlabBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedStairsBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedWallBlock;
import net.geforcemods.securitycraft.datagen.DataGenConstants.SCModelTemplates;
import net.geforcemods.securitycraft.datagen.DataGenConstants.SCTexturedModels;
import net.geforcemods.securitycraft.items.properties.ReinforcedTint;
import net.geforcemods.securitycraft.util.SCItemGroup;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelOutput;
import net.minecraft.client.data.models.blockstates.BlockStateGenerator;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.Variant;
import net.minecraft.client.data.models.blockstates.VariantProperties;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelInstance;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.data.models.model.TexturedModel;
import net.minecraft.client.renderer.item.properties.select.DisplayContext;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.BlockFamilies;
import net.minecraft.data.BlockFamily;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.registries.DeferredHolder;

public class BlockModelAndStateGenerator {
	private static final ItemTintSource CRYSTAL_QUARTZ_TINT = ItemModelUtils.constantTint(SCContent.CRYSTAL_QUARTZ_TINT);
	private static BlockModelGenerators blockModelGenerators;
	private static Consumer<BlockStateGenerator> blockStateOutput;
	private static BiConsumer<ResourceLocation, ModelInstance> modelOutput;
	private static ItemModelOutput itemInfo;
	private static Set<Block> generatedBlocks = new HashSet<>();

	private BlockModelAndStateGenerator() {}

	protected static void run(BlockModelGenerators blockModelGenerators) {
		List<Item> mineTabItems = SCCreativeModeTabs.STACKS_FOR_ITEM_GROUPS.get(SCItemGroup.EXPLOSIVES).stream().map(ItemStack::getItem).toList();
		List<Item> decorationTabItems = SCCreativeModeTabs.STACKS_FOR_ITEM_GROUPS.get(SCItemGroup.DECORATION).stream().map(ItemStack::getItem).toList();

		BlockModelAndStateGenerator.blockModelGenerators = blockModelGenerators;
		blockStateOutput = blockModelGenerators.blockStateOutput;
		modelOutput = blockModelGenerators.modelOutput;
		itemInfo = blockModelGenerators.itemModelOutput;
		createBlockMine(SCContent.ANCIENT_DEBRIS_MINE.get(), Blocks.ANCIENT_DEBRIS);
		generateBlockMineInfo(SCContent.DEEPSLATE_MINE.get(), ModelLocationUtils.getModelLocation(Blocks.DEEPSLATE));
		generateBlockMineInfo(SCContent.SUSPICIOUS_GRAVEL_MINE.get(), ModelLocationUtils.getModelLocation(Blocks.SUSPICIOUS_GRAVEL, "_0"));
		generateBlockMineInfo(SCContent.SUSPICIOUS_SAND_MINE.get(), ModelLocationUtils.getModelLocation(Blocks.SUSPICIOUS_SAND, "_0"));
		createHorizontalBlockMine(SCContent.FURNACE_MINE.get(), Blocks.FURNACE, TexturedModel.ORIENTABLE_ONLY_TOP);
		createHorizontalBlockMine(SCContent.SMOKER_MINE.get(), Blocks.SMOKER, TexturedModel.ORIENTABLE_ONLY_TOP);
		createHorizontalBlockMine(SCContent.BLAST_FURNACE_MINE.get(), Blocks.BLAST_FURNACE, TexturedModel.ORIENTABLE_ONLY_TOP);

		createTrivialBlockWithRenderType(SCContent.FLOOR_TRAP.get(), "translucent");
		createReinforcedCarpet(SCContent.REINFORCED_MOSS_CARPET.get(), "block");
		createGlassBlocks(SCContent.REINFORCED_GLASS.get(), SCContent.REINFORCED_GLASS_PANE.get());
		createGlassBlocks(SCContent.REINFORCED_WHITE_STAINED_GLASS.get(), SCContent.REINFORCED_WHITE_STAINED_GLASS_PANE.get());
		createGlassBlocks(SCContent.REINFORCED_ORANGE_STAINED_GLASS.get(), SCContent.REINFORCED_ORANGE_STAINED_GLASS_PANE.get());
		createGlassBlocks(SCContent.REINFORCED_MAGENTA_STAINED_GLASS.get(), SCContent.REINFORCED_MAGENTA_STAINED_GLASS_PANE.get());
		createGlassBlocks(SCContent.REINFORCED_LIGHT_BLUE_STAINED_GLASS.get(), SCContent.REINFORCED_LIGHT_BLUE_STAINED_GLASS_PANE.get());
		createGlassBlocks(SCContent.REINFORCED_YELLOW_STAINED_GLASS.get(), SCContent.REINFORCED_YELLOW_STAINED_GLASS_PANE.get());
		createGlassBlocks(SCContent.REINFORCED_LIME_STAINED_GLASS.get(), SCContent.REINFORCED_LIME_STAINED_GLASS_PANE.get());
		createGlassBlocks(SCContent.REINFORCED_PINK_STAINED_GLASS.get(), SCContent.REINFORCED_PINK_STAINED_GLASS_PANE.get());
		createGlassBlocks(SCContent.REINFORCED_GRAY_STAINED_GLASS.get(), SCContent.REINFORCED_GRAY_STAINED_GLASS_PANE.get());
		createGlassBlocks(SCContent.REINFORCED_LIGHT_GRAY_STAINED_GLASS.get(), SCContent.REINFORCED_LIGHT_GRAY_STAINED_GLASS_PANE.get());
		createGlassBlocks(SCContent.REINFORCED_CYAN_STAINED_GLASS.get(), SCContent.REINFORCED_CYAN_STAINED_GLASS_PANE.get());
		createGlassBlocks(SCContent.REINFORCED_PURPLE_STAINED_GLASS.get(), SCContent.REINFORCED_PURPLE_STAINED_GLASS_PANE.get());
		createGlassBlocks(SCContent.REINFORCED_BLUE_STAINED_GLASS.get(), SCContent.REINFORCED_BLUE_STAINED_GLASS_PANE.get());
		createGlassBlocks(SCContent.REINFORCED_BROWN_STAINED_GLASS.get(), SCContent.REINFORCED_BROWN_STAINED_GLASS_PANE.get());
		createGlassBlocks(SCContent.REINFORCED_GREEN_STAINED_GLASS.get(), SCContent.REINFORCED_GREEN_STAINED_GLASS_PANE.get());
		createGlassBlocks(SCContent.REINFORCED_RED_STAINED_GLASS.get(), SCContent.REINFORCED_RED_STAINED_GLASS_PANE.get());
		createGlassBlocks(SCContent.REINFORCED_BLACK_STAINED_GLASS.get(), SCContent.REINFORCED_BLACK_STAINED_GLASS_PANE.get());

		createReinforcedCustomFence(SCContent.REINFORCED_BAMBOO_FENCE.get(), Blocks.BAMBOO_FENCE);
		createReinforcedCustomFenceGate(SCContent.REINFORCED_BAMBOO_FENCE_GATE.get(), Blocks.BAMBOO_FENCE_GATE);

		//@formatter:off
		SCModelTemplates.REINFORCED_CUBE_COLUMN.create(
				ModelLocationUtils.decorateBlockModelLocation(SecurityCraft.resLoc("reinforced_smooth_stone_slab_double").toString()),
				TextureMapping.column(
						TextureMapping.getBlockTexture(Blocks.SMOOTH_STONE_SLAB, "_side"),
						TextureMapping.cube(Blocks.SMOOTH_STONE).get(TextureSlot.TOP)),
				modelOutput);
		//@formatter:on

		createReinforcedSlab(SCContent.REINFORCED_SMOOTH_STONE_SLAB.get(), "reinforced_smooth_stone_slab_double", "smooth_stone_slab_side", "smooth_stone");
		createReinforcedSlab(SCContent.REINFORCED_SANDSTONE_SLAB.get(), "reinforced_sandstone", "sandstone", "sandstone_bottom", "sandstone_top");
		createReinforcedSlab(SCContent.REINFORCED_CUT_SANDSTONE_SLAB.get(), "reinforced_cut_sandstone", "cut_sandstone", "sandstone_top");
		createReinforcedSlab(SCContent.REINFORCED_QUARTZ_SLAB.get(), "reinforced_quartz_block", "quartz_block_side", "quartz_block_top");
		createReinforcedSlab(SCContent.REINFORCED_RED_SANDSTONE_SLAB.get(), "reinforced_red_sandstone", "red_sandstone", "red_sandstone_bottom", "red_sandstone_top");
		createReinforcedSlab(SCContent.REINFORCED_CUT_RED_SANDSTONE_SLAB.get(), "reinforced_cut_red_sandstone", "cut_red_sandstone", "red_sandstone_top");
		createReinforcedSlab(SCContent.REINFORCED_SMOOTH_RED_SANDSTONE_SLAB.get(), "reinforced_smooth_red_sandstone", "red_sandstone_top");
		createReinforcedSlab(SCContent.REINFORCED_SMOOTH_SANDSTONE_SLAB.get(), "reinforced_smooth_sandstone", "sandstone_top");
		createReinforcedSlab(SCContent.REINFORCED_SMOOTH_QUARTZ_SLAB.get(), "reinforced_smooth_quartz", "quartz_block_bottom");
		createTintedSlab(SCContent.CRYSTAL_QUARTZ_SLAB.get(), "reinforced_quartz_block", "quartz_block_side", "quartz_block_top", CRYSTAL_QUARTZ_TINT);
		createTintedSlab(SCContent.SMOOTH_CRYSTAL_QUARTZ_SLAB.get(), "reinforced_smooth_quartz", "quartz_block_bottom", "quartz_block_bottom", CRYSTAL_QUARTZ_TINT);
		createReinforcedSlab(SCContent.REINFORCED_CRYSTAL_QUARTZ_SLAB.get(), "reinforced_quartz_block", "quartz_block_side", "quartz_block_top", CRYSTAL_QUARTZ_TINT);
		createReinforcedSlab(SCContent.REINFORCED_SMOOTH_CRYSTAL_QUARTZ_SLAB.get(), "reinforced_smooth_quartz", "quartz_block_bottom", "quartz_block_bottom", CRYSTAL_QUARTZ_TINT);

		createReinforcedStairs(SCContent.REINFORCED_SANDSTONE_STAIRS.get(), "sandstone", "sandstone_bottom", "sandstone_top");
		createReinforcedStairs(SCContent.REINFORCED_QUARTZ_STAIRS.get(), "quartz_block_side", "quartz_block_top");
		createReinforcedStairs(SCContent.REINFORCED_RED_SANDSTONE_STAIRS.get(), "red_sandstone", "red_sandstone_bottom", "red_sandstone_top");
		createReinforcedStairs(SCContent.REINFORCED_SMOOTH_RED_SANDSTONE_STAIRS.get(), "red_sandstone_top");
		createReinforcedStairs(SCContent.REINFORCED_SMOOTH_SANDSTONE_STAIRS.get(), "sandstone_top");
		createReinforcedStairs(SCContent.REINFORCED_SMOOTH_QUARTZ_STAIRS.get(), "quartz_block_bottom");
		createReinforcedStairs(SCContent.REINFORCED_CRYSTAL_QUARTZ_STAIRS.get(), "quartz_block_side", "quartz_block_top", CRYSTAL_QUARTZ_TINT);
		createReinforcedStairs(SCContent.REINFORCED_SMOOTH_CRYSTAL_QUARTZ_STAIRS.get(), "quartz_block_bottom", "quartz_block_bottom", CRYSTAL_QUARTZ_TINT);
		createTintedStairs(SCContent.CRYSTAL_QUARTZ_STAIRS.get(), "quartz_block_side", "quartz_block_top", CRYSTAL_QUARTZ_TINT);
		createTintedStairs(SCContent.SMOOTH_CRYSTAL_QUARTZ_STAIRS.get(), "quartz_block_bottom", "quartz_block_bottom", CRYSTAL_QUARTZ_TINT);

		createSecureRedstoneInterface();

		for (DeferredHolder<Block, ? extends Block> obj : SCContent.BLOCKS.getEntries()) {
			Block block = obj.get();
			Item item = block.asItem();

			if (generatedBlocks.contains(block))
				continue;

			if (decorationTabItems.contains(item)) {
				switch (block) {
					case ReinforcedButtonBlock button -> createReinforcedButton(button);
					case ReinforcedCarpetBlock carpet -> createReinforcedCarpet(block);
					case ReinforcedFenceBlock fence -> createReinforcedFence(fence);
					case ReinforcedFenceGateBlock fenceGate -> createReinforcedFenceGate(fenceGate);
					case ReinforcedSlabBlock slab -> createReinforcedSlab(slab);
					case ReinforcedStairsBlock stairs -> createReinforcedStairs(stairs);
					case ReinforcedWallBlock wall -> createReinforcedWall(wall);
					case IReinforcedBlock reinforcedBlock -> registerReinforcedItemModel(block);
					default -> {
					}
				}
			}
			else if (mineTabItems.contains(item) && block instanceof BaseFullMineBlock mine)
				createBlockMine(block, mine.getBlockDisguisedAs());
		}
	}

	public static void createBlockMine(Block block, Block vanillaBlock) {
		ResourceLocation vanillaModel = ModelLocationUtils.getModelLocation(vanillaBlock);

		generate(block, BlockModelGenerators.createSimpleBlock(block, vanillaModel));
		generateBlockMineInfo(block, vanillaModel);
	}

	public static void generateBlockMineInfo(Block block, ResourceLocation vanillaModel) {
		generatedBlocks.add(block);
		//@formatter:off
		itemInfo.accept(block.asItem(),
				ItemModelUtils.select(new DisplayContext(),
						ItemModelUtils.plainModel(vanillaModel),
						ItemModelUtils.when(
								List.of(
									ItemDisplayContext.FIRST_PERSON_LEFT_HAND,
									ItemDisplayContext.FIRST_PERSON_RIGHT_HAND,
									ItemDisplayContext.GUI),
								ItemModelUtils.composite(
										ItemModelUtils.plainModel(vanillaModel),
										ItemModelUtils.plainModel(ModelLocationUtils.decorateItemModelLocation(SecurityCraft.resLoc("block_mine_overlay").toString()))))));
		//@formatter:on
	}

	public static void createHorizontalBlockMine(Block furnaceBlock, Block mockBlock, TexturedModel.Provider modelProvider) {
		//@formatter:off
		ResourceLocation modelLocation = modelProvider.get(furnaceBlock)
				.updateTextures(mapping -> {
					mapping.put(TextureSlot.FRONT, TextureMapping.getBlockTexture(mockBlock, "_front"));
					mapping.put(TextureSlot.SIDE, TextureMapping.getBlockTexture(mockBlock, "_side"));
					mapping.put(TextureSlot.TOP, TextureMapping.getBlockTexture(mockBlock, "_top"));
				})
				.create(furnaceBlock, modelOutput);

		generate(furnaceBlock, MultiVariantGenerator.multiVariant(furnaceBlock, Variant.variant().with(VariantProperties.MODEL, modelLocation))
				.with(BlockModelGenerators.createHorizontalFacingDispatch()));
		//@formatter:on
		generateBlockMineInfo(furnaceBlock, ModelLocationUtils.getModelLocation(mockBlock));
	}

	public static void createReinforcedButton(ReinforcedButtonBlock block) {
		TextureMapping texture = TextureMapping.defaultTexture(getBaseBlock(block, BlockFamily.Variant.BUTTON));
		ResourceLocation defaultModel = SCModelTemplates.REINFORCED_BUTTON.create(block, texture, modelOutput);
		ResourceLocation pressedModel = SCModelTemplates.REINFORCED_BUTTON_PRESSED.create(block, texture, modelOutput);
		ResourceLocation inventoryModel = SCModelTemplates.REINFORCED_BUTTON_INVENTORY.create(block, texture, modelOutput);

		blockStateOutput.accept(BlockModelGenerators.createButton(block, defaultModel, pressedModel));
		registerReinforcedItemModel(block, inventoryModel);
	}

	public static void createReinforcedCarpet(Block block) {
		createReinforcedCarpet(block, "wool");
	}

	public static void createReinforcedCarpet(Block block, String carpetReplacement) {
		String name = name(block);
		ResourceLocation baseBlockName = SecurityCraft.mcResLoc(name.replace("reinforced_", "").replace("carpet", carpetReplacement));
		Block baseBlock = BuiltInRegistries.BLOCK.get(baseBlockName).orElseThrow(() -> new IllegalStateException(baseBlockName + " does not exist!")).value();
		ResourceLocation modelLocation = SCTexturedModels.REINFORCED_CARPET.get(baseBlock).create(block, modelOutput);

		generate(block, BlockModelGenerators.createSimpleBlock(block, modelLocation));
		registerReinforcedItemModel(block);
	}

	public static void createReinforcedCustomFence(Block block, Block baseBlock) {
		TextureMapping textureMapping = TextureMapping.customParticle(baseBlock);
		ResourceLocation postModel = SCModelTemplates.CUSTOM_REINFORCED_FENCE_POST.create(block, textureMapping, modelOutput);
		ResourceLocation northSideModel = SCModelTemplates.CUSTOM_REINFORCED_FENCE_SIDE_NORTH.create(block, textureMapping, modelOutput);
		ResourceLocation eastSideModel = SCModelTemplates.CUSTOM_REINFORCED_FENCE_SIDE_EAST.create(block, textureMapping, modelOutput);
		ResourceLocation southSideModel = SCModelTemplates.CUSTOM_REINFORCED_FENCE_SIDE_SOUTH.create(block, textureMapping, modelOutput);
		ResourceLocation westSideModel = SCModelTemplates.CUSTOM_REINFORCED_FENCE_SIDE_WEST.create(block, textureMapping, modelOutput);
		ResourceLocation inventoryModel = SCModelTemplates.CUSTOM_REINFORCED_FENCE_INVENTORY.create(block, textureMapping, modelOutput);

		generate(block, BlockModelGenerators.createCustomFence(block, postModel, northSideModel, eastSideModel, southSideModel, westSideModel));
		registerReinforcedItemModel(block, inventoryModel);
	}

	public static void createReinforcedFence(ReinforcedFenceBlock block) {
		TextureMapping textureMapping = TextureMapping.customParticle(getBaseBlock(block, BlockFamily.Variant.FENCE));
		ResourceLocation postModel = SCModelTemplates.REINFORCED_FENCE_POST.create(block, textureMapping, modelOutput);
		ResourceLocation sideModel = SCModelTemplates.REINFORCED_FENCE_SIDE.create(block, textureMapping, modelOutput);
		ResourceLocation inventoryModel = SCModelTemplates.REINFORCED_FENCE_INVENTORY.create(block, textureMapping, modelOutput);

		generate(block, BlockModelGenerators.createFence(block, postModel, sideModel));
		registerReinforcedItemModel(block, inventoryModel);
	}

	public static void createReinforcedCustomFenceGate(Block block, Block baseBlock) {
		TextureMapping textureMapping = TextureMapping.customParticle(baseBlock);
		ResourceLocation openModel = SCModelTemplates.CUSTOM_REINFORCED_FENCE_GATE_OPEN.create(block, textureMapping, modelOutput);
		ResourceLocation closedModel = SCModelTemplates.CUSTOM_REINFORCED_FENCE_GATE_CLOSED.create(block, textureMapping, modelOutput);
		ResourceLocation wallOpenModel = SCModelTemplates.CUSTOM_REINFORCED_FENCE_GATE_WALL_OPEN.create(block, textureMapping, modelOutput);
		ResourceLocation wallClosedModel = SCModelTemplates.CUSTOM_REINFORCED_FENCE_GATE_WALL_CLOSED.create(block, textureMapping, modelOutput);

		generate(block, BlockModelGenerators.createFenceGate(block, openModel, closedModel, wallOpenModel, wallClosedModel, false));
		registerReinforcedItemModel(block);
	}

	public static void createReinforcedFenceGate(ReinforcedFenceGateBlock block) {
		TextureMapping textureMapping = TextureMapping.customParticle(getBaseBlock(block, BlockFamily.Variant.FENCE_GATE));
		ResourceLocation openModel = SCModelTemplates.REINFORCED_FENCE_GATE_OPEN.create(block, textureMapping, modelOutput);
		ResourceLocation closedModel = SCModelTemplates.REINFORCED_FENCE_GATE_CLOSED.create(block, textureMapping, modelOutput);
		ResourceLocation wallOpenModel = SCModelTemplates.REINFORCED_FENCE_GATE_WALL_OPEN.create(block, textureMapping, modelOutput);
		ResourceLocation wallClosedModel = SCModelTemplates.REINFORCED_FENCE_GATE_WALL_CLOSED.create(block, textureMapping, modelOutput);

		generate(block, BlockModelGenerators.createFenceGate(block, openModel, closedModel, wallOpenModel, wallClosedModel, true));
		registerReinforcedItemModel(block);
	}

	public static void createReinforcedSlab(ReinforcedSlabBlock block) {
		Block baseBlock = getBaseBlock(block, BlockFamily.Variant.SLAB);
		Block reinforcedBaseBlock = IReinforcedBlock.VANILLA_TO_SECURITYCRAFT.get(baseBlock);

		if (reinforcedBaseBlock != null)
			createReinforcedSlab(block, name(reinforcedBaseBlock), name(baseBlock));
		else
			throw new IllegalStateException("Couldn't find reinforced block for " + Utils.getRegistryName(baseBlock));
	}

	public static void createReinforcedSlab(Block block, String doubleSlabModel, String texture) {
		ResourceLocation textureLocation = mcBlock(texture);

		generateReinforcedSlab(block, modBlock(doubleSlabModel), textureLocation, textureLocation, textureLocation, ReinforcedTint.DEFAULT_BASE);
	}

	public static void createReinforcedSlab(Block block, String doubleSlabModel, String side, String end) {
		createReinforcedSlab(block, doubleSlabModel, side, end, ReinforcedTint.DEFAULT_BASE);
	}

	public static void createReinforcedSlab(Block block, String doubleSlabModel, String side, String end, ItemTintSource baseTint) {
		ResourceLocation endTextureLocation = mcBlock(end);

		generateReinforcedSlab(block, modBlock(doubleSlabModel), mcBlock(side), endTextureLocation, endTextureLocation, baseTint);
	}

	public static void createTintedSlab(Block block, String doubleSlabModel, String side, String end, ItemTintSource tint) {
		ResourceLocation endTextureLocation = mcBlock(end);

		generateTintedSlab(block, modBlock(doubleSlabModel), mcBlock(side), endTextureLocation, endTextureLocation, tint);
	}

	public static void createReinforcedSlab(Block block, String doubleSlabModel, String side, String bottom, String top) {
		generateReinforcedSlab(block, modBlock(doubleSlabModel), mcBlock(side), mcBlock(bottom), mcBlock(top), ReinforcedTint.DEFAULT_BASE);
	}

	public static void generateReinforcedSlab(Block block, ResourceLocation doubleSlab, ResourceLocation side, ResourceLocation bottom, ResourceLocation top, ItemTintSource baseTint) {
		createTintedSlab(block, doubleSlab, side, bottom, top);
		registerReinforcedItemModel(block, baseTint);
	}

	public static void generateTintedSlab(Block block, ResourceLocation doubleSlab, ResourceLocation side, ResourceLocation bottom, ResourceLocation top, ItemTintSource tint) {
		createTintedSlab(block, doubleSlab, side, bottom, top);
		itemInfo.accept(block.asItem(), ItemModelUtils.tintedModel(ModelLocationUtils.getModelLocation(block), tint));
	}

	public static void createTintedSlab(Block block, ResourceLocation doubleSlab, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
		TextureMapping textureMapping = new TextureMapping().put(TextureSlot.BOTTOM, bottom).put(TextureSlot.SIDE, side).put(TextureSlot.TOP, top);
		ResourceLocation bottomModel = SCModelTemplates.REINFORCED_SLAB_BOTTOM.create(block, textureMapping, modelOutput);
		ResourceLocation topModel = SCModelTemplates.REINFORCED_SLAB_TOP.create(block, textureMapping, modelOutput);

		generate(block, BlockModelGenerators.createSlab(block, bottomModel, topModel, doubleSlab));
	}

	public static void createReinforcedStairs(ReinforcedStairsBlock block) {
		createReinforcedStairs(block, name(getBaseBlock(block, BlockFamily.Variant.STAIRS)));
	}

	public static void createReinforcedStairs(Block block, String texture) {
		ResourceLocation textureLocation = mcBlock(texture);

		generateReinforcedStairs(block, textureLocation, textureLocation, textureLocation, ReinforcedTint.DEFAULT_BASE);
	}

	public static void createReinforcedStairs(Block block, String side, String end) {
		createReinforcedStairs(block, side, end, ReinforcedTint.DEFAULT_BASE);
	}

	public static void createReinforcedStairs(Block block, String side, String end, ItemTintSource baseTint) {
		ResourceLocation textureLocationEnd = mcBlock(end);

		generateReinforcedStairs(block, mcBlock(side), textureLocationEnd, textureLocationEnd, baseTint);
	}

	public static void createTintedStairs(Block block, String side, String end, ItemTintSource tint) {
		ResourceLocation textureLocationEnd = mcBlock(end);

		generateTintedStairs(block, mcBlock(side), textureLocationEnd, textureLocationEnd, tint);
	}

	public static void createReinforcedStairs(Block block, String side, String bottom, String top) {
		generateReinforcedStairs(block, mcBlock(side), mcBlock(bottom), mcBlock(top), ReinforcedTint.DEFAULT_BASE);
	}

	public static void generateReinforcedStairs(Block block, ResourceLocation side, ResourceLocation bottom, ResourceLocation top, ItemTintSource baseTint) {
		createTintedStairs(block, side, bottom, top);
		registerReinforcedItemModel(block, baseTint);
	}

	public static void generateTintedStairs(Block block, ResourceLocation side, ResourceLocation bottom, ResourceLocation top, ItemTintSource tint) {
		createTintedStairs(block, side, bottom, top);
		itemInfo.accept(block.asItem(), ItemModelUtils.tintedModel(ModelLocationUtils.getModelLocation(block), tint));
	}

	public static void createTintedStairs(Block block, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
		TextureMapping textureMapping = new TextureMapping().put(TextureSlot.BOTTOM, bottom).put(TextureSlot.SIDE, side).put(TextureSlot.TOP, top);
		ResourceLocation innerModel = SCModelTemplates.REINFORCED_STAIRS_INNER.create(block, textureMapping, modelOutput);
		ResourceLocation straightModel = SCModelTemplates.REINFORCED_STAIRS_STRAIGHT.create(block, textureMapping, modelOutput);
		ResourceLocation outerModel = SCModelTemplates.REINFORCED_STAIRS_OUTER.create(block, textureMapping, modelOutput);

		generate(block, BlockModelGenerators.createStairs(block, innerModel, straightModel, outerModel));
	}

	public static void createReinforcedWall(ReinforcedWallBlock block) {
		TextureMapping texture = new TextureMapping().put(TextureSlot.WALL, mcBlock(name(getBaseBlock(block, BlockFamily.Variant.WALL))));
		ResourceLocation postModel = SCModelTemplates.REINFORCED_WALL_POST.create(block, texture, modelOutput);
		ResourceLocation lowSideModel = SCModelTemplates.REINFORCED_WALL_LOW_SIDE.create(block, texture, modelOutput);
		ResourceLocation tallSideModel = SCModelTemplates.REINFORCED_WALL_TALL_SIDE.create(block, texture, modelOutput);
		ResourceLocation inventoryModel = SCModelTemplates.REINFORCED_WALL_INVENTORY.create(block, texture, modelOutput);

		generate(block, BlockModelGenerators.createWall(block, postModel, lowSideModel, tallSideModel));
		registerReinforcedItemModel(block, inventoryModel);
	}

	public static void createSecureRedstoneInterface() {
		Block block = SCContent.SECURE_REDSTONE_INTERFACE.get();
		ResourceLocation senderLocation = TextureMapping.getBlockTexture(block, "_sender");
		ResourceLocation receiverLocation = TextureMapping.getBlockTexture(block, "_receiver");

		//@formatter:off
		generate(block, MultiVariantGenerator.multiVariant(block)
				.with(BlockModelGenerators.createBooleanModelDispatch(SecureRedstoneInterfaceBlock.SENDER, senderLocation, receiverLocation))
				.with(blockModelGenerators.createColumnWithFacing()));
		//@formatter:on
		blockModelGenerators.registerSimpleItemModel(block, ModelLocationUtils.getModelLocation(block, "_sender"));
	}

	public static void createGlassBlocks(Block glass, Block pane) {
		blockModelGenerators.createGlassBlocks(glass, pane);
		itemInfo.accept(glass.asItem(), ItemModelUtils.plainModel(ModelLocationUtils.getModelLocation(glass)));
		generatedBlocks.add(glass);
		generatedBlocks.add(pane);
	}

	public static void createTrivialBlockWithRenderType(Block block, String renderType) {
		//@formatter:off
		blockModelGenerators.createTrivialBlock(block, TexturedModel.CUBE
				.updateTemplate(template -> template
						.extend()
						.renderType(renderType)
						.build()));
		//@formatter:on
		generatedBlocks.add(block);
	}

	public static void generate(Block block, BlockStateGenerator generator) {
		blockStateOutput.accept(generator);
		generatedBlocks.add(block);
	}

	public static void registerReinforcedItemModel(Block block) {
		registerReinforcedItemModel(block, ModelLocationUtils.getModelLocation(block));
	}

	public static void registerReinforcedItemModel(Block block, ResourceLocation model) {
		registerReinforcedItemModel(block, model, ReinforcedTint.DEFAULT_BASE);
	}

	public static void registerReinforcedItemModel(Block block, ItemTintSource base) {
		registerReinforcedItemModel(block, ModelLocationUtils.getModelLocation(block), base);
	}

	public static void registerReinforcedItemModel(Block block, ResourceLocation model, ItemTintSource base) {
		itemInfo.accept(block.asItem(), ItemModelUtils.tintedModel(model, new ReinforcedTint(base)));
	}

	public static ResourceLocation mcBlock(String path) {
		return ModelLocationUtils.decorateBlockModelLocation(SecurityCraft.mcResLoc(path).toString());
	}

	public static ResourceLocation modBlock(String path) {
		return ModelLocationUtils.decorateBlockModelLocation(SecurityCraft.resLoc(path).toString());
	}

	private static String name(Block block) {
		return Utils.getRegistryName(block).getPath();
	}

	private static Block getBaseBlock(IReinforcedBlock block, BlockFamily.Variant variant) {
		BlockFamily family = BlockFamilies.getAllFamilies().filter(f -> f.getVariants().get(variant) == block.getVanillaBlock()).findFirst().orElse(null);

		if (family != null)
			return family.getBaseBlock();
		else
			throw new IllegalStateException("Couldn't find block family for " + Utils.getRegistryName((Block) block));
	}
}
