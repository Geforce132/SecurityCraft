package net.geforcemods.securitycraft.datagen;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SCCreativeModeTabs;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blocks.SecureRedstoneInterfaceBlock;
import net.geforcemods.securitycraft.blocks.mines.BaseFullMineBlock;
import net.geforcemods.securitycraft.blocks.mines.BrushableMineBlock;
import net.geforcemods.securitycraft.blocks.mines.DeepslateMineBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedCarpetBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedSlabBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedStairsBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedWallBlock;
import net.geforcemods.securitycraft.datagen.DataGenConstants.SCModelTemplates;
import net.geforcemods.securitycraft.datagen.DataGenConstants.SCTexturedModels;
import net.geforcemods.securitycraft.util.SCItemGroup;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.blockstates.BlockStateGenerator;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.Variant;
import net.minecraft.client.data.models.blockstates.VariantProperties;
import net.minecraft.client.data.models.model.ModelInstance;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.data.models.model.TexturedModel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.registries.DeferredHolder;

public class BlockModelAndStateGenerator {
	private static BlockModelGenerators blockModelGenerators;
	private static Consumer<BlockStateGenerator> blockStateOutput;
	private static BiConsumer<ResourceLocation, ModelInstance> modelOutput;
	private static Set<Block> generatedBlocks = new HashSet<>();

	protected static void run(BlockModelGenerators blockModelGenerators) {
		List<Item> mineTabItems = SCCreativeModeTabs.STACKS_FOR_ITEM_GROUPS.get(SCItemGroup.EXPLOSIVES).stream().map(ItemStack::getItem).toList();
		List<Item> decorationTabItems = SCCreativeModeTabs.STACKS_FOR_ITEM_GROUPS.get(SCItemGroup.DECORATION).stream().map(ItemStack::getItem).toList();

		BlockModelAndStateGenerator.blockModelGenerators = blockModelGenerators;
		blockStateOutput = blockModelGenerators.blockStateOutput;
		modelOutput = blockModelGenerators.modelOutput;
		createBlockMine(SCContent.ANCIENT_DEBRIS_MINE.get(), Blocks.ANCIENT_DEBRIS);
		createHorizontalBlock(SCContent.FURNACE_MINE.get(), Blocks.FURNACE, TexturedModel.ORIENTABLE_ONLY_TOP);
		createHorizontalBlock(SCContent.SMOKER_MINE.get(), Blocks.SMOKER, TexturedModel.ORIENTABLE_ONLY_TOP);
		createHorizontalBlock(SCContent.BLAST_FURNACE_MINE.get(), Blocks.BLAST_FURNACE, TexturedModel.ORIENTABLE_ONLY_TOP);
		createSecureRedstoneInterface();

		createTrivialBlockWithRenderType(SCContent.FLOOR_TRAP.get(), "translucent");
		createReinforcedCarpetBlock(SCContent.REINFORCED_MOSS_CARPET.get(), "block");
		blockModelGenerators.createGlassBlocks(SCContent.REINFORCED_GLASS.get(), SCContent.REINFORCED_GLASS_PANE.get());
		blockModelGenerators.createGlassBlocks(SCContent.REINFORCED_WHITE_STAINED_GLASS.get(), SCContent.REINFORCED_WHITE_STAINED_GLASS_PANE.get());
		blockModelGenerators.createGlassBlocks(SCContent.REINFORCED_ORANGE_STAINED_GLASS.get(), SCContent.REINFORCED_ORANGE_STAINED_GLASS_PANE.get());
		blockModelGenerators.createGlassBlocks(SCContent.REINFORCED_MAGENTA_STAINED_GLASS.get(), SCContent.REINFORCED_MAGENTA_STAINED_GLASS_PANE.get());
		blockModelGenerators.createGlassBlocks(SCContent.REINFORCED_LIGHT_BLUE_STAINED_GLASS.get(), SCContent.REINFORCED_LIGHT_BLUE_STAINED_GLASS_PANE.get());
		blockModelGenerators.createGlassBlocks(SCContent.REINFORCED_YELLOW_STAINED_GLASS.get(), SCContent.REINFORCED_YELLOW_STAINED_GLASS_PANE.get());
		blockModelGenerators.createGlassBlocks(SCContent.REINFORCED_LIME_STAINED_GLASS.get(), SCContent.REINFORCED_LIME_STAINED_GLASS_PANE.get());
		blockModelGenerators.createGlassBlocks(SCContent.REINFORCED_PINK_STAINED_GLASS.get(), SCContent.REINFORCED_PINK_STAINED_GLASS_PANE.get());
		blockModelGenerators.createGlassBlocks(SCContent.REINFORCED_GRAY_STAINED_GLASS.get(), SCContent.REINFORCED_GRAY_STAINED_GLASS_PANE.get());
		blockModelGenerators.createGlassBlocks(SCContent.REINFORCED_LIGHT_GRAY_STAINED_GLASS.get(), SCContent.REINFORCED_LIGHT_GRAY_STAINED_GLASS_PANE.get());
		blockModelGenerators.createGlassBlocks(SCContent.REINFORCED_CYAN_STAINED_GLASS.get(), SCContent.REINFORCED_CYAN_STAINED_GLASS_PANE.get());
		blockModelGenerators.createGlassBlocks(SCContent.REINFORCED_PURPLE_STAINED_GLASS.get(), SCContent.REINFORCED_PURPLE_STAINED_GLASS_PANE.get());
		blockModelGenerators.createGlassBlocks(SCContent.REINFORCED_BLUE_STAINED_GLASS.get(), SCContent.REINFORCED_BLUE_STAINED_GLASS_PANE.get());
		blockModelGenerators.createGlassBlocks(SCContent.REINFORCED_BROWN_STAINED_GLASS.get(), SCContent.REINFORCED_BROWN_STAINED_GLASS_PANE.get());
		blockModelGenerators.createGlassBlocks(SCContent.REINFORCED_GREEN_STAINED_GLASS.get(), SCContent.REINFORCED_GREEN_STAINED_GLASS_PANE.get());
		blockModelGenerators.createGlassBlocks(SCContent.REINFORCED_RED_STAINED_GLASS.get(), SCContent.REINFORCED_RED_STAINED_GLASS_PANE.get());
		blockModelGenerators.createGlassBlocks(SCContent.REINFORCED_BLACK_STAINED_GLASS.get(), SCContent.REINFORCED_BLACK_STAINED_GLASS_PANE.get());

		createReinforcedFence(SCContent.REINFORCED_OAK_FENCE.get(), Blocks.OAK_PLANKS);
		createReinforcedFence(SCContent.REINFORCED_SPRUCE_FENCE.get(), Blocks.SPRUCE_PLANKS);
		createReinforcedFence(SCContent.REINFORCED_BIRCH_FENCE.get(), Blocks.BIRCH_PLANKS);
		createReinforcedFence(SCContent.REINFORCED_JUNGLE_FENCE.get(), Blocks.JUNGLE_PLANKS);
		createReinforcedFence(SCContent.REINFORCED_ACACIA_FENCE.get(), Blocks.ACACIA_PLANKS);
		createReinforcedFence(SCContent.REINFORCED_DARK_OAK_FENCE.get(), Blocks.DARK_OAK_PLANKS);
		createReinforcedFence(SCContent.REINFORCED_MANGROVE_FENCE.get(), Blocks.MANGROVE_PLANKS);
		createReinforcedFence(SCContent.REINFORCED_CHERRY_FENCE.get(), Blocks.CHERRY_PLANKS);
		createReinforcedCustomFence(SCContent.REINFORCED_BAMBOO_FENCE.get(), Blocks.BAMBOO_FENCE);
		createReinforcedFence(SCContent.REINFORCED_CRIMSON_FENCE.get(), Blocks.CRIMSON_PLANKS);
		createReinforcedFence(SCContent.REINFORCED_WARPED_FENCE.get(), Blocks.WARPED_PLANKS);
		createReinforcedFence(SCContent.REINFORCED_NETHER_BRICK_FENCE.get(), Blocks.NETHER_BRICKS);
		createReinforcedFenceGate(SCContent.REINFORCED_OAK_FENCE_GATE.get(), Blocks.OAK_PLANKS);
		createReinforcedFenceGate(SCContent.REINFORCED_SPRUCE_FENCE_GATE.get(), Blocks.SPRUCE_PLANKS);
		createReinforcedFenceGate(SCContent.REINFORCED_BIRCH_FENCE_GATE.get(), Blocks.BIRCH_PLANKS);
		createReinforcedFenceGate(SCContent.REINFORCED_JUNGLE_FENCE_GATE.get(), Blocks.JUNGLE_PLANKS);
		createReinforcedFenceGate(SCContent.REINFORCED_ACACIA_FENCE_GATE.get(), Blocks.ACACIA_PLANKS);
		createReinforcedFenceGate(SCContent.REINFORCED_DARK_OAK_FENCE_GATE.get(), Blocks.DARK_OAK_PLANKS);
		createReinforcedFenceGate(SCContent.REINFORCED_MANGROVE_FENCE_GATE.get(), Blocks.MANGROVE_PLANKS);
		createReinforcedFenceGate(SCContent.REINFORCED_CHERRY_FENCE_GATE.get(), Blocks.CHERRY_PLANKS);
		createReinforcedCustomFenceGate(SCContent.REINFORCED_BAMBOO_FENCE_GATE.get(), Blocks.BAMBOO_FENCE_GATE);
		createReinforcedFenceGate(SCContent.REINFORCED_CRIMSON_FENCE_GATE.get(), Blocks.CRIMSON_PLANKS);
		createReinforcedFenceGate(SCContent.REINFORCED_WARPED_FENCE_GATE.get(), Blocks.WARPED_PLANKS);

		//@formatter:off
		SCModelTemplates.REINfORCED_CUBE_COLUMN.create(
				ModelLocationUtils.decorateBlockModelLocation(SecurityCraft.resLoc("reinforced_smooth_stone_slab_double").toString()),
				TextureMapping.column(
						TextureMapping.getBlockTexture(Blocks.SMOOTH_STONE_SLAB, "_side"),
						TextureMapping.cube(Blocks.SMOOTH_STONE).get(TextureSlot.TOP)),
				modelOutput);
		//@formatter:on

		createReinforcedSlab(SCContent.REINFORCED_OAK_SLAB.get(), "reinforced_oak_planks", "oak_planks");
		createReinforcedSlab(SCContent.REINFORCED_SPRUCE_SLAB.get(), "reinforced_spruce_planks", "spruce_planks");
		createReinforcedSlab(SCContent.REINFORCED_BIRCH_SLAB.get(), "reinforced_birch_planks", "birch_planks");
		createReinforcedSlab(SCContent.REINFORCED_JUNGLE_SLAB.get(), "reinforced_jungle_planks", "jungle_planks");
		createReinforcedSlab(SCContent.REINFORCED_ACACIA_SLAB.get(), "reinforced_acacia_planks", "acacia_planks");
		createReinforcedSlab(SCContent.REINFORCED_DARK_OAK_SLAB.get(), "reinforced_dark_oak_planks", "dark_oak_planks");
		createReinforcedSlab(SCContent.REINFORCED_MANGROVE_SLAB.get(), "reinforced_mangrove_planks", "mangrove_planks");
		createReinforcedSlab(SCContent.REINFORCED_CHERRY_SLAB.get(), "reinforced_cherry_planks", "cherry_planks");
		createReinforcedSlab(SCContent.REINFORCED_BAMBOO_SLAB.get(), "reinforced_bamboo_planks", "bamboo_planks");
		createReinforcedSlab(SCContent.REINFORCED_CRIMSON_SLAB.get(), "reinforced_crimson_planks", "crimson_planks");
		createReinforcedSlab(SCContent.REINFORCED_WARPED_SLAB.get(), "reinforced_warped_planks", "warped_planks");
		createReinforcedSlab(SCContent.REINFORCED_NORMAL_STONE_SLAB.get(), "reinforced_stone", "stone");
		createReinforcedSlab(SCContent.REINFORCED_SMOOTH_STONE_SLAB.get(), "reinforced_smooth_stone_slab_double", "smooth_stone_slab_side", "smooth_stone");
		createReinforcedSlab(SCContent.REINFORCED_SANDSTONE_SLAB.get(), "reinforced_sandstone", "sandstone", "sandstone_bottom", "sandstone_top");
		createReinforcedSlab(SCContent.REINFORCED_CUT_SANDSTONE_SLAB.get(), "reinforced_cut_sandstone", "cut_sandstone", "sandstone_top");
		createReinforcedSlab(SCContent.REINFORCED_BRICK_SLAB.get(), "reinforced_bricks", "bricks");
		createReinforcedSlab(SCContent.REINFORCED_STONE_BRICK_SLAB.get(), "reinforced_stone_bricks", "stone_bricks");
		createReinforcedSlab(SCContent.REINFORCED_MUD_BRICK_SLAB.get(), "reinforced_mud_bricks", "mud_bricks");
		createReinforcedSlab(SCContent.REINFORCED_NETHER_BRICK_SLAB.get(), "reinforced_nether_bricks", "nether_bricks");
		createReinforcedSlab(SCContent.REINFORCED_QUARTZ_SLAB.get(), "reinforced_quartz_block", "quartz_block_side", "quartz_block_top");
		createReinforcedSlab(SCContent.REINFORCED_RED_SANDSTONE_SLAB.get(), "reinforced_red_sandstone", "red_sandstone", "red_sandstone_bottom", "red_sandstone_top");
		createReinforcedSlab(SCContent.REINFORCED_CUT_RED_SANDSTONE_SLAB.get(), "reinforced_cut_red_sandstone", "cut_red_sandstone", "red_sandstone_top");
		createReinforcedSlab(SCContent.REINFORCED_PURPUR_SLAB.get(), "reinforced_purpur_block", "purpur_block");
		createReinforcedSlab(SCContent.REINFORCED_PRISMARINE_SLAB.get(), "reinforced_prismarine", "prismarine");
		createReinforcedSlab(SCContent.REINFORCED_PRISMARINE_BRICK_SLAB.get(), "reinforced_prismarine_bricks", "prismarine_bricks");
		createReinforcedSlab(SCContent.REINFORCED_DARK_PRISMARINE_SLAB.get(), "reinforced_dark_prismarine", "dark_prismarine");
		createReinforcedSlab(SCContent.REINFORCED_POLISHED_GRANITE_SLAB.get(), "reinforced_polished_granite", "polished_granite");
		createReinforcedSlab(SCContent.REINFORCED_SMOOTH_RED_SANDSTONE_SLAB.get(), "reinforced_smooth_red_sandstone", "red_sandstone_top");
		createReinforcedSlab(SCContent.REINFORCED_MOSSY_STONE_BRICK_SLAB.get(), "reinforced_mossy_stone_bricks", "mossy_stone_bricks");
		createReinforcedSlab(SCContent.REINFORCED_POLISHED_DIORITE_SLAB.get(), "reinforced_polished_diorite", "polished_diorite");
		createReinforcedSlab(SCContent.REINFORCED_END_STONE_BRICK_SLAB.get(), "reinforced_end_stone_bricks", "end_stone_bricks");
		createReinforcedSlab(SCContent.REINFORCED_SMOOTH_SANDSTONE_SLAB.get(), "reinforced_smooth_sandstone", "sandstone_top");
		createReinforcedSlab(SCContent.REINFORCED_SMOOTH_QUARTZ_SLAB.get(), "reinforced_smooth_quartz", "quartz_block_bottom");
		createReinforcedSlab(SCContent.REINFORCED_GRANITE_SLAB.get(), "reinforced_granite", "granite");
		createReinforcedSlab(SCContent.REINFORCED_ANDESITE_SLAB.get(), "reinforced_andesite", "andesite");
		createReinforcedSlab(SCContent.REINFORCED_RED_NETHER_BRICK_SLAB.get(), "reinforced_red_nether_bricks", "red_nether_bricks");
		createReinforcedSlab(SCContent.REINFORCED_POLISHED_ANDESITE_SLAB.get(), "reinforced_polished_andesite", "polished_andesite");
		createReinforcedSlab(SCContent.REINFORCED_DIORITE_SLAB.get(), "reinforced_diorite", "diorite");
		createReinforcedSlab(SCContent.REINFORCED_POLISHED_BLACKSTONE_BRICK_SLAB.get(), "reinforced_polished_blackstone_bricks", "polished_blackstone_bricks");
		createReinforcedSlab(SCContent.CRYSTAL_QUARTZ_SLAB.get(), "reinforced_quartz_block", "quartz_block_side", "quartz_block_top");
		createReinforcedSlab(SCContent.SMOOTH_CRYSTAL_QUARTZ_SLAB.get(), "reinforced_smooth_quartz", "quartz_block_bottom", "quartz_block_bottom");
		createReinforcedSlab(SCContent.REINFORCED_CRYSTAL_QUARTZ_SLAB.get(), "reinforced_quartz_block", "quartz_block_side", "quartz_block_top");
		createReinforcedSlab(SCContent.REINFORCED_SMOOTH_CRYSTAL_QUARTZ_SLAB.get(), "reinforced_smooth_quartz", "quartz_block_bottom", "quartz_block_bottom");
		createReinforcedSlab(SCContent.REINFORCED_DEEPSLATE_BRICK_SLAB.get(), "reinforced_deepslate_bricks", "deepslate_bricks");
		createReinforcedSlab(SCContent.REINFORCED_DEEPSLATE_TILE_SLAB.get(), "reinforced_deepslate_tiles", "deepslate_tiles");
		createReinforcedSlab(SCContent.REINFORCED_TUFF_BRICK_SLAB.get(), "reinforced_tuff_bricks", "tuff_bricks");

		createReinforcedStairs(SCContent.REINFORCED_PURPUR_STAIRS.get(), "purpur_block");
		createReinforcedStairs(SCContent.REINFORCED_OAK_STAIRS.get(), "oak_planks");
		createReinforcedStairs(SCContent.REINFORCED_BRICK_STAIRS.get(), "bricks");
		createReinforcedStairs(SCContent.REINFORCED_STONE_BRICK_STAIRS.get(), "stone_bricks");
		createReinforcedStairs(SCContent.REINFORCED_MUD_BRICK_STAIRS.get(), "mud_bricks");
		createReinforcedStairs(SCContent.REINFORCED_NETHER_BRICK_STAIRS.get(), "nether_bricks");
		createReinforcedStairs(SCContent.REINFORCED_SANDSTONE_STAIRS.get(), "sandstone", "sandstone_bottom", "sandstone_top");
		createReinforcedStairs(SCContent.REINFORCED_SPRUCE_STAIRS.get(), "spruce_planks");
		createReinforcedStairs(SCContent.REINFORCED_BIRCH_STAIRS.get(), "birch_planks");
		createReinforcedStairs(SCContent.REINFORCED_JUNGLE_STAIRS.get(), "jungle_planks");
		createReinforcedStairs(SCContent.REINFORCED_CRIMSON_STAIRS.get(), "crimson_planks");
		createReinforcedStairs(SCContent.REINFORCED_WARPED_STAIRS.get(), "warped_planks");
		createReinforcedStairs(SCContent.REINFORCED_QUARTZ_STAIRS.get(), "quartz_block_side", "quartz_block_top");
		createReinforcedStairs(SCContent.REINFORCED_ACACIA_STAIRS.get(), "acacia_planks");
		createReinforcedStairs(SCContent.REINFORCED_DARK_OAK_STAIRS.get(), "dark_oak_planks");
		createReinforcedStairs(SCContent.REINFORCED_MANGROVE_STAIRS.get(), "mangrove_planks");
		createReinforcedStairs(SCContent.REINFORCED_CHERRY_STAIRS.get(), "cherry_planks");
		createReinforcedStairs(SCContent.REINFORCED_BAMBOO_STAIRS.get(), "bamboo_planks");
		createReinforcedStairs(SCContent.REINFORCED_PRISMARINE_BRICK_STAIRS.get(), "prismarine_bricks");
		createReinforcedStairs(SCContent.REINFORCED_RED_SANDSTONE_STAIRS.get(), "red_sandstone", "red_sandstone_bottom", "red_sandstone_top");
		createReinforcedStairs(SCContent.REINFORCED_SMOOTH_RED_SANDSTONE_STAIRS.get(), "red_sandstone_top");
		createReinforcedStairs(SCContent.REINFORCED_MOSSY_STONE_BRICK_STAIRS.get(), "mossy_stone_bricks");
		createReinforcedStairs(SCContent.REINFORCED_END_STONE_BRICK_STAIRS.get(), "end_stone_bricks");
		createReinforcedStairs(SCContent.REINFORCED_SMOOTH_SANDSTONE_STAIRS.get(), "sandstone_top");
		createReinforcedStairs(SCContent.REINFORCED_SMOOTH_QUARTZ_STAIRS.get(), "quartz_block_bottom");
		createReinforcedStairs(SCContent.REINFORCED_RED_NETHER_BRICK_STAIRS.get(), "red_nether_bricks");
		createReinforcedStairs(SCContent.REINFORCED_POLISHED_BLACKSTONE_BRICK_STAIRS.get(), "polished_blackstone_bricks");
		createReinforcedStairs(SCContent.REINFORCED_CRYSTAL_QUARTZ_STAIRS.get(), "quartz_block_side", "quartz_block_top");
		createReinforcedStairs(SCContent.REINFORCED_SMOOTH_CRYSTAL_QUARTZ_STAIRS.get(), "quartz_block_bottom", "quartz_block_bottom");
		createReinforcedStairs(SCContent.CRYSTAL_QUARTZ_STAIRS.get(), "quartz_block_side", "quartz_block_top");
		createReinforcedStairs(SCContent.SMOOTH_CRYSTAL_QUARTZ_STAIRS.get(), "quartz_block_bottom", "quartz_block_bottom");
		createReinforcedStairs(SCContent.REINFORCED_DEEPSLATE_BRICK_STAIRS.get(), "deepslate_bricks");
		createReinforcedStairs(SCContent.REINFORCED_DEEPSLATE_TILE_STAIRS.get(), "deepslate_tiles");
		createReinforcedStairs(SCContent.REINFORCED_TUFF_BRICK_STAIRS.get(), "tuff_bricks");

		createReinforcedWall(SCContent.REINFORCED_BRICK_WALL.get(), "bricks");
		createReinforcedWall(SCContent.REINFORCED_MOSSY_STONE_BRICK_WALL.get(), "mossy_stone_bricks");
		createReinforcedWall(SCContent.REINFORCED_STONE_BRICK_WALL.get(), "stone_bricks");
		createReinforcedWall(SCContent.REINFORCED_MUD_BRICK_WALL.get(), "mud_bricks");
		createReinforcedWall(SCContent.REINFORCED_NETHER_BRICK_WALL.get(), "nether_bricks");
		createReinforcedWall(SCContent.REINFORCED_RED_NETHER_BRICK_WALL.get(), "red_nether_bricks");
		createReinforcedWall(SCContent.REINFORCED_END_STONE_BRICK_WALL.get(), "end_stone_bricks");
		createReinforcedWall(SCContent.REINFORCED_POLISHED_BLACKSTONE_BRICK_WALL.get(), "polished_blackstone_bricks");
		createReinforcedWall(SCContent.REINFORCED_DEEPSLATE_BRICK_WALL.get(), "deepslate_bricks");
		createReinforcedWall(SCContent.REINFORCED_DEEPSLATE_TILE_WALL.get(), "deepslate_tiles");
		createReinforcedWall(SCContent.REINFORCED_TUFF_BRICK_WALL.get(), "tuff_bricks");

		for (DeferredHolder<Block, ? extends Block> obj : SCContent.BLOCKS.getEntries()) {
			Block block = obj.get();
			Item item = block.asItem();

			if (generatedBlocks.contains(block))
				continue;

			if (decorationTabItems.contains(item)) {
				switch (block) {
					case ReinforcedSlabBlock slab -> createReinforcedSlab(block);
					case ReinforcedStairsBlock stairs -> createReinforcedStairs(block);
					case ReinforcedWallBlock wall -> createReinforcedWall(block);
					case ReinforcedCarpetBlock carpet -> createReinforcedCarpetBlock(block);
					default -> {
					}
				}
			}
			else if (mineTabItems.contains(item) && block instanceof BaseFullMineBlock mine && !(mine instanceof DeepslateMineBlock || mine instanceof BrushableMineBlock))
				createBlockMine(block, mine.getBlockDisguisedAs());
		}
	}

	public static void createBlockMine(Block block, Block vanillaBlock) {
		generate(block, BlockModelGenerators.createSimpleBlock(block, mcBlock(Utils.getRegistryName(vanillaBlock).getPath())));
	}

	public static void createHorizontalBlock(Block furnaceBlock, Block mockBlock, TexturedModel.Provider modelProvider) {
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
	}

	public static void createReinforcedCarpetBlock(Block block) {
		createReinforcedCarpetBlock(block, "wool");
	}

	public static void createReinforcedCarpetBlock(Block block, String carpetReplacement) {
		String name = name(block);
		ResourceLocation baseBlockName = SecurityCraft.mcResLoc(name.replace("reinforced_", "").replace("carpet", carpetReplacement));
		Block baseBlock = BuiltInRegistries.BLOCK.get(baseBlockName).orElseThrow(() -> new IllegalStateException(baseBlockName + " does not exist!")).value();
		ResourceLocation modelLocation = SCTexturedModels.REINFORCED_CARPET.get(baseBlock).create(block, modelOutput);

		generate(block, BlockModelGenerators.createSimpleBlock(block, modelLocation));
	}

	public static void createReinforcedCustomFence(Block fenceBlock, Block baseBlock) {
		TextureMapping textureMapping = TextureMapping.customParticle(baseBlock);
		ResourceLocation postModel = SCModelTemplates.CUSTOM_REINFORCED_FENCE_POST.create(fenceBlock, textureMapping, modelOutput);
		ResourceLocation northSideModel = SCModelTemplates.CUSTOM_REINFORCED_FENCE_SIDE_NORTH.create(fenceBlock, textureMapping, modelOutput);
		ResourceLocation eastSideModel = SCModelTemplates.CUSTOM_REINFORCED_FENCE_SIDE_EAST.create(fenceBlock, textureMapping, modelOutput);
		ResourceLocation southSideModel = SCModelTemplates.CUSTOM_REINFORCED_FENCE_SIDE_SOUTH.create(fenceBlock, textureMapping, modelOutput);
		ResourceLocation westSideModel = SCModelTemplates.CUSTOM_REINFORCED_FENCE_SIDE_WEST.create(fenceBlock, textureMapping, modelOutput);
		ResourceLocation inventoryModel = SCModelTemplates.CUSTOM_REINFORCED_FENCE_INVENTORY.create(fenceBlock, textureMapping, modelOutput);

		generate(fenceBlock, BlockModelGenerators.createCustomFence(fenceBlock, postModel, northSideModel, eastSideModel, southSideModel, westSideModel));
	}

	public static void createReinforcedFence(Block fenceBlock, Block baseBlock) {
		TextureMapping textureMapping = TextureMapping.customParticle(baseBlock);
		ResourceLocation postModel = SCModelTemplates.REINFORCED_FENCE_POST.create(fenceBlock, textureMapping, modelOutput);
		ResourceLocation sideModel = SCModelTemplates.REINFORCED_FENCE_SIDE.create(fenceBlock, textureMapping, modelOutput);
		ResourceLocation inventoryModel = SCModelTemplates.REINFORCED_FENCE_INVENTORY.create(fenceBlock, textureMapping, modelOutput);

		generate(fenceBlock, BlockModelGenerators.createFence(fenceBlock, postModel, sideModel));
	}

	public static void createReinforcedCustomFenceGate(Block customFenceGateBlock, Block baseBlock) {
		TextureMapping textureMapping = TextureMapping.customParticle(baseBlock);
		ResourceLocation openModel = SCModelTemplates.CUSTOM_REINFORCED_FENCE_GATE_OPEN.create(customFenceGateBlock, textureMapping, modelOutput);
		ResourceLocation closedModel = SCModelTemplates.CUSTOM_REINFORCED_FENCE_GATE_CLOSED.create(customFenceGateBlock, textureMapping, modelOutput);
		ResourceLocation wallOpenModel = SCModelTemplates.CUSTOM_REINFORCED_FENCE_GATE_WALL_OPEN.create(customFenceGateBlock, textureMapping, modelOutput);
		ResourceLocation wallClosedModel = SCModelTemplates.CUSTOM_REINFORCED_FENCE_GATE_WALL_CLOSED.create(customFenceGateBlock, textureMapping, modelOutput);

		generate(customFenceGateBlock, BlockModelGenerators.createFenceGate(customFenceGateBlock, openModel, closedModel, wallOpenModel, wallClosedModel, false));
	}

	public static void createReinforcedFenceGate(Block fenceGateBlock, Block baseBlock) {
		TextureMapping textureMapping = TextureMapping.customParticle(baseBlock);
		ResourceLocation openModel = SCModelTemplates.REINFORCED_FENCE_GATE_OPEN.create(fenceGateBlock, textureMapping, modelOutput);
		ResourceLocation closedModel = SCModelTemplates.REINFORCED_FENCE_GATE_CLOSED.create(fenceGateBlock, textureMapping, modelOutput);
		ResourceLocation wallOpenModel = SCModelTemplates.REINFORCED_FENCE_GATE_WALL_OPEN.create(fenceGateBlock, textureMapping, modelOutput);
		ResourceLocation wallClosedModel = SCModelTemplates.REINFORCED_FENCE_GATE_WALL_CLOSED.create(fenceGateBlock, textureMapping, modelOutput);

		generate(fenceGateBlock, BlockModelGenerators.createFenceGate(fenceGateBlock, openModel, closedModel, wallOpenModel, wallClosedModel, true));
	}

	public static void createReinforcedSlab(Block block) {
		String name = name(block).replace("_slab", "");

		createReinforcedSlab(block, name, name.replace("reinforced_", ""));
	}

	public static void createReinforcedSlab(Block block, String doubleSlabModel, String texture) {
		ResourceLocation textureLocation = mcBlock(texture);

		createReinforcedSlab(block, name(block), modBlock(doubleSlabModel), textureLocation, textureLocation, textureLocation);
	}

	public static void createReinforcedSlab(Block block, String doubleSlabModel, String side, String end) {
		ResourceLocation endTextureLocation = mcBlock(end);

		createReinforcedSlab(block, name(block), modBlock(doubleSlabModel), mcBlock(side), endTextureLocation, endTextureLocation);
	}

	public static void createReinforcedSlab(Block block, String doubleSlabModel, String side, String bottom, String top) {
		createReinforcedSlab(block, name(block), modBlock(doubleSlabModel), mcBlock(side), mcBlock(bottom), mcBlock(top));
	}

	public static void createReinforcedSlab(Block block, String baseName, ResourceLocation doubleSlab, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
		TextureMapping textureMapping = new TextureMapping().put(TextureSlot.BOTTOM, bottom).put(TextureSlot.SIDE, side).put(TextureSlot.TOP, top);
		ResourceLocation bottomModel = SCModelTemplates.REINFORCED_SLAB_BOTTOM.create(block, textureMapping, modelOutput);
		ResourceLocation topModel = SCModelTemplates.REINFORCED_SLAB_TOP.create(block, textureMapping, modelOutput);

		generate(block, BlockModelGenerators.createSlab(block, bottomModel, topModel, doubleSlab));
	}

	public static void createReinforcedStairs(Block block) {
		createReinforcedStairs(block, name(block).replace("reinforced_", "").replace("_stairs", ""));
	}

	public static void createReinforcedStairs(Block block, String texture) {
		ResourceLocation textureLocation = mcBlock(texture);

		createReinforcedStairs(block, Utils.getRegistryName(block).toString(), textureLocation, textureLocation, textureLocation);
	}

	public static void createReinforcedStairs(Block block, String side, String end) {
		ResourceLocation textureLocationEnd = mcBlock(end);

		createReinforcedStairs(block, Utils.getRegistryName(block).toString(), mcBlock(side), textureLocationEnd, textureLocationEnd);
	}

	public static void createReinforcedStairs(Block block, String side, String bottom, String top) {
		createReinforcedStairs(block, Utils.getRegistryName(block).toString(), mcBlock(side), mcBlock(bottom), mcBlock(top));
	}

	public static void createReinforcedStairs(Block block, String baseName, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
		TextureMapping textureMapping = new TextureMapping().put(TextureSlot.BOTTOM, bottom).put(TextureSlot.SIDE, side).put(TextureSlot.TOP, top);
		ResourceLocation innerModel = SCModelTemplates.REINFORCED_STAIRS_INNER.create(block, textureMapping, modelOutput);
		ResourceLocation straightModel = SCModelTemplates.REINFORCED_STAIRS_STRAIGHT.create(block, textureMapping, modelOutput);
		ResourceLocation outerModel = SCModelTemplates.REINFORCED_STAIRS_OUTER.create(block, textureMapping, modelOutput);

		generate(block, BlockModelGenerators.createStairs(block, innerModel, straightModel, outerModel));
	}

	public static void createReinforcedWall(Block block) {
		createReinforcedWall(block, name(block).replace("reinforced_", "").replace("_wall", ""));
	}

	public static void createReinforcedWall(Block block, String textureName) {
		TextureMapping texture = new TextureMapping().put(TextureSlot.WALL, mcBlock(textureName));
		ResourceLocation postModel = SCModelTemplates.REINFORCED_WALL_POST.create(block, texture, modelOutput);
		ResourceLocation lowSideModel = SCModelTemplates.REINFORCED_WALL_LOW_SIDE.create(block, texture, modelOutput);
		ResourceLocation tallSideModel = SCModelTemplates.REINFORCED_WALL_TALL_SIDE.create(block, texture, modelOutput);
		ResourceLocation inventoryModel = SCModelTemplates.REINFORCED_WALL_INVENTORY.create(block, texture, modelOutput);

		generate(block, BlockModelGenerators.createWall(block, postModel, lowSideModel, tallSideModel));
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

	public static ResourceLocation mcBlock(String path) {
		return SecurityCraft.mcResLoc("block/" + path);
	}

	public static ResourceLocation modBlock(String path) {
		return SecurityCraft.resLoc("block/" + path);
	}

	private static String name(Block block) {
		return Utils.getRegistryName(block).getPath();
	}
}
