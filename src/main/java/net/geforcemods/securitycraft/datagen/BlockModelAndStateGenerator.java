package net.geforcemods.securitycraft.datagen;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.google.common.collect.ImmutableMap;

import net.geforcemods.securitycraft.RegistrationHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blocks.mines.BaseFullMineBlock;
import net.geforcemods.securitycraft.blocks.mines.DeepslateMineBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedCarpetBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedSlabBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedStainedGlassBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedStainedGlassPaneBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedStairsBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedWallBlock;
import net.geforcemods.securitycraft.util.SCItemGroup;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraft.world.level.block.state.properties.WallSide;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.ModelFile.UncheckedModelFile;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.client.model.generators.MultiPartBlockStateBuilder;
import net.minecraftforge.client.model.generators.VariantBlockStateBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

public class BlockModelAndStateGenerator extends BlockStateProvider {
	private static final Map<Direction, EnumProperty<WallSide>> DIR_TO_WALL_HEIGHT = ImmutableMap.of(Direction.EAST, BlockStateProperties.EAST_WALL, Direction.NORTH, BlockStateProperties.NORTH_WALL, Direction.SOUTH, BlockStateProperties.SOUTH_WALL, Direction.WEST, BlockStateProperties.WEST_WALL);
	private final SCBlockModelProvider scModels;

	public BlockModelAndStateGenerator(PackOutput output, ExistingFileHelper exFileHelper) {
		super(output, SecurityCraft.MODID, exFileHelper);

		scModels = new SCBlockModelProvider(output, exFileHelper);
	}

	@Override
	protected void registerStatesAndModels() {
		List<Item> mineTabItems = RegistrationHandler.STACKS_FOR_ITEM_GROUPS.get(SCItemGroup.EXPLOSIVES).stream().map(ItemStack::getItem).toList();
		List<Item> decorationTabItems = RegistrationHandler.STACKS_FOR_ITEM_GROUPS.get(SCItemGroup.DECORATION).stream().map(ItemStack::getItem).toList();

		for (RegistryObject<Block> obj : SCContent.BLOCKS.getEntries()) {
			Block block = obj.get();
			Item item = block.asItem();

			if (decorationTabItems.contains(item)) {
				if (block instanceof ReinforcedSlabBlock)
					reinforcedSlabBlock(block);
				else if (block instanceof ReinforcedStainedGlassBlock)
					simpleBlockWithRenderType(block, "translucent");
				else if (block instanceof ReinforcedStainedGlassPaneBlock)
					reinforcedPaneBlock((IronBarsBlock) block, modelFile -> modelFile.renderType("translucent"));
				else if (block instanceof ReinforcedStairsBlock)
					reinforcedStairsBlock(block);
				else if (block instanceof ReinforcedWallBlock)
					reinforcedWallBlock(block);
				else if (block instanceof ReinforcedCarpetBlock)
					reinforcedCarpetBlock(block);
			}
			else if (mineTabItems.contains(item) && block instanceof BaseFullMineBlock mine && !(mine instanceof DeepslateMineBlock))
				blockMine(mine.getBlockDisguisedAs(), block);
		}

		blockMine(Blocks.ANCIENT_DEBRIS, SCContent.ANCIENT_DEBRIS_MINE.get());
		horizontalBlock(SCContent.FURNACE_MINE.get(), mcLoc(ModelProvider.BLOCK_FOLDER + "/furnace_side"), mcLoc(ModelProvider.BLOCK_FOLDER + "/furnace_front"), mcLoc(ModelProvider.BLOCK_FOLDER + "/furnace_top"));
		horizontalBlock(SCContent.SMOKER_MINE.get(), mcLoc(ModelProvider.BLOCK_FOLDER + "/smoker_side"), mcLoc(ModelProvider.BLOCK_FOLDER + "/smoker_front"), mcLoc(ModelProvider.BLOCK_FOLDER + "/smoker_top"));
		horizontalBlock(SCContent.BLAST_FURNACE_MINE.get(), mcLoc(ModelProvider.BLOCK_FOLDER + "/blast_furnace_side"), mcLoc(ModelProvider.BLOCK_FOLDER + "/blast_furnace_front"), mcLoc(ModelProvider.BLOCK_FOLDER + "/blast_furnace_top"));

		simpleBlockWithRenderType(SCContent.REINFORCED_GLASS.get(), "cutout");
		reinforcedCarpetBlock(SCContent.REINFORCED_MOSS_CARPET.get(), "block");
		reinforcedPaneBlock((IronBarsBlock) SCContent.REINFORCED_GLASS_PANE.get(), modelFile -> modelFile.renderType("cutout_mipped"));

		models().reinforcedColumn("reinforced_smooth_stone_slab_double", "smooth_stone_slab_side", "smooth_stone");

		reinforcedSlabBlock(SCContent.REINFORCED_OAK_SLAB.get(), "reinforced_oak_planks", "oak_planks");
		reinforcedSlabBlock(SCContent.REINFORCED_SPRUCE_SLAB.get(), "reinforced_spruce_planks", "spruce_planks");
		reinforcedSlabBlock(SCContent.REINFORCED_BIRCH_SLAB.get(), "reinforced_birch_planks", "birch_planks");
		reinforcedSlabBlock(SCContent.REINFORCED_JUNGLE_SLAB.get(), "reinforced_jungle_planks", "jungle_planks");
		reinforcedSlabBlock(SCContent.REINFORCED_ACACIA_SLAB.get(), "reinforced_acacia_planks", "acacia_planks");
		reinforcedSlabBlock(SCContent.REINFORCED_DARK_OAK_SLAB.get(), "reinforced_dark_oak_planks", "dark_oak_planks");
		reinforcedSlabBlock(SCContent.REINFORCED_MANGROVE_SLAB.get(), "reinforced_mangrove_planks", "mangrove_planks");
		reinforcedSlabBlock(SCContent.REINFORCED_CRIMSON_SLAB.get(), "reinforced_crimson_planks", "crimson_planks");
		reinforcedSlabBlock(SCContent.REINFORCED_WARPED_SLAB.get(), "reinforced_warped_planks", "warped_planks");
		reinforcedSlabBlock(SCContent.REINFORCED_NORMAL_STONE_SLAB.get(), "reinforced_stone", "stone");
		reinforcedSlabBlock(SCContent.REINFORCED_SMOOTH_STONE_SLAB.get(), "reinforced_smooth_stone_slab_double", "smooth_stone_slab_side", "smooth_stone");
		reinforcedSlabBlock(SCContent.REINFORCED_SANDSTONE_SLAB.get(), "reinforced_sandstone", "sandstone", "sandstone_bottom", "sandstone_top");
		reinforcedSlabBlock(SCContent.REINFORCED_CUT_SANDSTONE_SLAB.get(), "reinforced_cut_sandstone", "cut_sandstone", "sandstone_top");
		reinforcedSlabBlock(SCContent.REINFORCED_BRICK_SLAB.get(), "reinforced_bricks", "bricks");
		reinforcedSlabBlock(SCContent.REINFORCED_STONE_BRICK_SLAB.get(), "reinforced_stone_bricks", "stone_bricks");
		reinforcedSlabBlock(SCContent.REINFORCED_MUD_BRICK_SLAB.get(), "reinforced_mud_bricks", "mud_bricks");
		reinforcedSlabBlock(SCContent.REINFORCED_NETHER_BRICK_SLAB.get(), "reinforced_nether_bricks", "nether_bricks");
		reinforcedSlabBlock(SCContent.REINFORCED_QUARTZ_SLAB.get(), "reinforced_quartz_block", "quartz_block_side", "quartz_block_top");
		reinforcedSlabBlock(SCContent.REINFORCED_RED_SANDSTONE_SLAB.get(), "reinforced_red_sandstone", "red_sandstone", "red_sandstone_bottom", "red_sandstone_top");
		reinforcedSlabBlock(SCContent.REINFORCED_CUT_RED_SANDSTONE_SLAB.get(), "reinforced_cut_red_sandstone", "cut_red_sandstone", "red_sandstone_top");
		reinforcedSlabBlock(SCContent.REINFORCED_PURPUR_SLAB.get(), "reinforced_purpur_block", "purpur_block");
		reinforcedSlabBlock(SCContent.REINFORCED_PRISMARINE_SLAB.get(), "reinforced_prismarine", "prismarine");
		reinforcedSlabBlock(SCContent.REINFORCED_PRISMARINE_BRICK_SLAB.get(), "reinforced_prismarine_bricks", "prismarine_bricks");
		reinforcedSlabBlock(SCContent.REINFORCED_DARK_PRISMARINE_SLAB.get(), "reinforced_dark_prismarine", "dark_prismarine");
		reinforcedSlabBlock(SCContent.REINFORCED_POLISHED_GRANITE_SLAB.get(), "reinforced_polished_granite", "polished_granite");
		reinforcedSlabBlock(SCContent.REINFORCED_SMOOTH_RED_SANDSTONE_SLAB.get(), "reinforced_smooth_red_sandstone", "red_sandstone_top");
		reinforcedSlabBlock(SCContent.REINFORCED_MOSSY_STONE_BRICK_SLAB.get(), "reinforced_mossy_stone_bricks", "mossy_stone_bricks");
		reinforcedSlabBlock(SCContent.REINFORCED_POLISHED_DIORITE_SLAB.get(), "reinforced_polished_diorite", "polished_diorite");
		reinforcedSlabBlock(SCContent.REINFORCED_END_STONE_BRICK_SLAB.get(), "reinforced_end_stone_bricks", "end_stone_bricks");
		reinforcedSlabBlock(SCContent.REINFORCED_SMOOTH_SANDSTONE_SLAB.get(), "reinforced_smooth_sandstone", "sandstone_top");
		reinforcedSlabBlock(SCContent.REINFORCED_SMOOTH_QUARTZ_SLAB.get(), "reinforced_smooth_quartz", "quartz_block_bottom");
		reinforcedSlabBlock(SCContent.REINFORCED_GRANITE_SLAB.get(), "reinforced_granite", "granite");
		reinforcedSlabBlock(SCContent.REINFORCED_ANDESITE_SLAB.get(), "reinforced_andesite", "andesite");
		reinforcedSlabBlock(SCContent.REINFORCED_RED_NETHER_BRICK_SLAB.get(), "reinforced_red_nether_bricks", "red_nether_bricks");
		reinforcedSlabBlock(SCContent.REINFORCED_POLISHED_ANDESITE_SLAB.get(), "reinforced_polished_andesite", "polished_andesite");
		reinforcedSlabBlock(SCContent.REINFORCED_DIORITE_SLAB.get(), "reinforced_diorite", "diorite");
		reinforcedSlabBlock(SCContent.REINFORCED_POLISHED_BLACKSTONE_BRICK_SLAB.get(), "reinforced_polished_blackstone_bricks", "polished_blackstone_bricks");
		reinforcedSlabBlock(SCContent.CRYSTAL_QUARTZ_SLAB.get(), "reinforced_quartz_block", "quartz_block_side", "quartz_block_top");
		reinforcedSlabBlock(SCContent.SMOOTH_CRYSTAL_QUARTZ_SLAB.get(), "reinforced_smooth_quartz", "quartz_block_bottom", "quartz_block_bottom");
		reinforcedSlabBlock(SCContent.REINFORCED_CRYSTAL_QUARTZ_SLAB.get(), "reinforced_quartz_block", "quartz_block_side", "quartz_block_top");
		reinforcedSlabBlock(SCContent.REINFORCED_SMOOTH_CRYSTAL_QUARTZ_SLAB.get(), "reinforced_smooth_quartz", "quartz_block_bottom", "quartz_block_bottom");
		reinforcedSlabBlock(SCContent.REINFORCED_DEEPSLATE_BRICK_SLAB.get(), "reinforced_deepslate_bricks", "deepslate_bricks");
		reinforcedSlabBlock(SCContent.REINFORCED_DEEPSLATE_TILE_SLAB.get(), "reinforced_deepslate_tiles", "deepslate_tiles");

		reinforcedStairsBlock(SCContent.REINFORCED_PURPUR_STAIRS.get(), "purpur_block");
		reinforcedStairsBlock(SCContent.REINFORCED_OAK_STAIRS.get(), "oak_planks");
		reinforcedStairsBlock(SCContent.REINFORCED_BRICK_STAIRS.get(), "bricks");
		reinforcedStairsBlock(SCContent.REINFORCED_STONE_BRICK_STAIRS.get(), "stone_bricks");
		reinforcedStairsBlock(SCContent.REINFORCED_MUD_BRICK_STAIRS.get(), "mud_bricks");
		reinforcedStairsBlock(SCContent.REINFORCED_NETHER_BRICK_STAIRS.get(), "nether_bricks");
		reinforcedStairsBlock(SCContent.REINFORCED_SANDSTONE_STAIRS.get(), "sandstone", "sandstone_bottom", "sandstone_top");
		reinforcedStairsBlock(SCContent.REINFORCED_SPRUCE_STAIRS.get(), "spruce_planks");
		reinforcedStairsBlock(SCContent.REINFORCED_BIRCH_STAIRS.get(), "birch_planks");
		reinforcedStairsBlock(SCContent.REINFORCED_JUNGLE_STAIRS.get(), "jungle_planks");
		reinforcedStairsBlock(SCContent.REINFORCED_CRIMSON_STAIRS.get(), "crimson_planks");
		reinforcedStairsBlock(SCContent.REINFORCED_WARPED_STAIRS.get(), "warped_planks");
		reinforcedStairsBlock(SCContent.REINFORCED_QUARTZ_STAIRS.get(), "quartz_block_side", "quartz_block_top");
		reinforcedStairsBlock(SCContent.REINFORCED_ACACIA_STAIRS.get(), "acacia_planks");
		reinforcedStairsBlock(SCContent.REINFORCED_DARK_OAK_STAIRS.get(), "dark_oak_planks");
		reinforcedStairsBlock(SCContent.REINFORCED_MANGROVE_STAIRS.get(), "mangrove_planks");
		reinforcedStairsBlock(SCContent.REINFORCED_PRISMARINE_BRICK_STAIRS.get(), "prismarine_bricks");
		reinforcedStairsBlock(SCContent.REINFORCED_RED_SANDSTONE_STAIRS.get(), "red_sandstone", "red_sandstone_bottom", "red_sandstone_top");
		reinforcedStairsBlock(SCContent.REINFORCED_SMOOTH_RED_SANDSTONE_STAIRS.get(), "red_sandstone_top");
		reinforcedStairsBlock(SCContent.REINFORCED_MOSSY_STONE_BRICK_STAIRS.get(), "mossy_stone_bricks");
		reinforcedStairsBlock(SCContent.REINFORCED_END_STONE_BRICK_STAIRS.get(), "end_stone_bricks");
		reinforcedStairsBlock(SCContent.REINFORCED_SMOOTH_SANDSTONE_STAIRS.get(), "sandstone_top");
		reinforcedStairsBlock(SCContent.REINFORCED_SMOOTH_QUARTZ_STAIRS.get(), "quartz_block_bottom");
		reinforcedStairsBlock(SCContent.REINFORCED_RED_NETHER_BRICK_STAIRS.get(), "red_nether_bricks");
		reinforcedStairsBlock(SCContent.REINFORCED_POLISHED_BLACKSTONE_BRICK_STAIRS.get(), "polished_blackstone_bricks");
		reinforcedStairsBlock(SCContent.REINFORCED_CRYSTAL_QUARTZ_STAIRS.get(), "quartz_block_side", "quartz_block_top");
		reinforcedStairsBlock(SCContent.REINFORCED_SMOOTH_CRYSTAL_QUARTZ_STAIRS.get(), "quartz_block_bottom", "quartz_block_bottom");
		reinforcedStairsBlock(SCContent.CRYSTAL_QUARTZ_STAIRS.get(), "quartz_block_side", "quartz_block_top");
		reinforcedStairsBlock(SCContent.SMOOTH_CRYSTAL_QUARTZ_STAIRS.get(), "quartz_block_bottom", "quartz_block_bottom");
		reinforcedStairsBlock(SCContent.REINFORCED_DEEPSLATE_BRICK_STAIRS.get(), "deepslate_bricks");
		reinforcedStairsBlock(SCContent.REINFORCED_DEEPSLATE_TILE_STAIRS.get(), "deepslate_tiles");

		reinforcedWallBlock(SCContent.REINFORCED_BRICK_WALL.get(), "bricks");
		reinforcedWallBlock(SCContent.REINFORCED_MOSSY_STONE_BRICK_WALL.get(), "mossy_stone_bricks");
		reinforcedWallBlock(SCContent.REINFORCED_STONE_BRICK_WALL.get(), "stone_bricks");
		reinforcedWallBlock(SCContent.REINFORCED_MUD_BRICK_WALL.get(), "mud_bricks");
		reinforcedWallBlock(SCContent.REINFORCED_NETHER_BRICK_WALL.get(), "nether_bricks");
		reinforcedWallBlock(SCContent.REINFORCED_RED_NETHER_BRICK_WALL.get(), "red_nether_bricks");
		reinforcedWallBlock(SCContent.REINFORCED_END_STONE_BRICK_WALL.get(), "end_stone_bricks");
		reinforcedWallBlock(SCContent.REINFORCED_POLISHED_BLACKSTONE_BRICK_WALL.get(), "polished_blackstone_bricks");
		reinforcedWallBlock(SCContent.REINFORCED_DEEPSLATE_BRICK_WALL.get(), "deepslate_bricks");
		reinforcedWallBlock(SCContent.REINFORCED_DEEPSLATE_TILE_WALL.get(), "deepslate_tiles");
	}

	public void blockMine(Block vanillaBlock, Block block) {
		getVariantBuilder(block).forAllStates(state -> new ConfiguredModel[] {
				new ConfiguredModel(new UncheckedModelFile(mcLoc(ModelProvider.BLOCK_FOLDER + "/" + Utils.getRegistryName(vanillaBlock).getPath())))
		});
	}

	public void fourWayWallHeight(MultiPartBlockStateBuilder builder, ModelFile model, WallSide height) {
		//@formatter:off
		Arrays.stream(Direction.values()).filter(dir -> dir.getAxis().isHorizontal()).forEach(dir -> {
			builder.part().modelFile(model).rotationY((((int) dir.toYRot()) + 180) % 360).uvLock(true).addModel()
			.condition(DIR_TO_WALL_HEIGHT.get(dir), height);
		});
		//@formatter:on
	}

	public void reinforcedCarpetBlock(Block block) {
		reinforcedCarpetBlock(block, "wool");
	}

	public void reinforcedCarpetBlock(Block block, String carpetReplacement) {
		String name = name(block);
		ModelFile model = models().reinforcedCarpet(name, mcLoc(ModelProvider.BLOCK_FOLDER + "/" + name.replace("reinforced_", "").replace("carpet", carpetReplacement)));

		getVariantBuilder(block).forAllStates(state -> new ConfiguredModel[] {
				new ConfiguredModel(model)
		});
	}

	public void reinforcedPaneBlock(IronBarsBlock block, Function<BlockModelBuilder, BlockModelBuilder> renderTypeApplier) {
		String name = name(block);

		paneBlock(block, modLoc(ModelProvider.BLOCK_FOLDER + "/" + name.replace("_pane", "")), modLoc(ModelProvider.BLOCK_FOLDER + "/" + name + "_top"), renderTypeApplier);
	}

	public void paneBlock(IronBarsBlock block, ResourceLocation pane, ResourceLocation edge, Function<BlockModelBuilder, BlockModelBuilder> renderTypeApplier) {
		paneBlockInternal(block, name(block).toString(), pane, edge, renderTypeApplier);
	}

	private void paneBlockInternal(IronBarsBlock block, String baseName, ResourceLocation pane, ResourceLocation edge, Function<BlockModelBuilder, BlockModelBuilder> renderTypeApplier) {
		ModelFile post = renderTypeApplier.apply(models().panePost(baseName + "_post", pane, edge));
		ModelFile side = renderTypeApplier.apply(models().paneSide(baseName + "_side", pane, edge));
		ModelFile sideAlt = renderTypeApplier.apply(models().paneSideAlt(baseName + "_side_alt", pane, edge));
		ModelFile noSide = renderTypeApplier.apply(models().paneNoSide(baseName + "_noside", pane));
		ModelFile noSideAlt = renderTypeApplier.apply(models().paneNoSideAlt(baseName + "_noside_alt", pane));
		paneBlock(block, post, side, sideAlt, noSide, noSideAlt);
	}

	public void reinforcedSlabBlock(Block block) {
		String name = name(block).replace("_slab", "");

		reinforcedSlabBlock(block, name, name.replace("reinforced_", ""));
	}

	public void reinforcedSlabBlock(Block block, String doubleSlabModel, String texture) {
		ResourceLocation textureLocation = mcLoc(ModelProvider.BLOCK_FOLDER + "/" + texture);

		reinforcedSlabBlock(block, name(block), modLoc(ModelProvider.BLOCK_FOLDER + "/" + doubleSlabModel), textureLocation, textureLocation, textureLocation);
	}

	public void reinforcedSlabBlock(Block block, String doubleSlabModel, String side, String end) {
		ResourceLocation endTextureLocation = mcLoc(ModelProvider.BLOCK_FOLDER + "/" + end);

		reinforcedSlabBlock(block, name(block), modLoc(ModelProvider.BLOCK_FOLDER + "/" + doubleSlabModel), mcLoc(ModelProvider.BLOCK_FOLDER + "/" + side), endTextureLocation, endTextureLocation);
	}

	public void reinforcedSlabBlock(Block block, String doubleSlabModel, String side, String bottom, String top) {
		reinforcedSlabBlock(block, name(block), modLoc(ModelProvider.BLOCK_FOLDER + "/" + doubleSlabModel), mcLoc(ModelProvider.BLOCK_FOLDER + "/" + side), mcLoc(ModelProvider.BLOCK_FOLDER + "/" + bottom), mcLoc(ModelProvider.BLOCK_FOLDER + "/" + top));
	}

	public void reinforcedSlabBlock(Block block, String baseName, ResourceLocation doubleSlab, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
		ModelFile bottomModel = models().reinforcedSlab(baseName, side, bottom, top);
		ModelFile topModel = models().reinforcedSlabTop(baseName + "_top", side, bottom, top);
		ModelFile doubleSlabModel = models().getExistingFile(doubleSlab);

		//@formatter:off
		getVariantBuilder(block)
		.partialState().with(SlabBlock.TYPE, SlabType.BOTTOM).addModels(new ConfiguredModel(bottomModel))
		.partialState().with(SlabBlock.TYPE, SlabType.TOP).addModels(new ConfiguredModel(topModel))
		.partialState().with(SlabBlock.TYPE, SlabType.DOUBLE).addModels(new ConfiguredModel(doubleSlabModel));
		//@formatter:on
	}

	public void reinforcedStairsBlock(Block block) {
		reinforcedStairsBlock(block, name(block).replace("reinforced_", "").replace("_stairs", ""));
	}

	public void reinforcedStairsBlock(Block block, String texture) {
		ResourceLocation textureLocation = mcLoc(ModelProvider.BLOCK_FOLDER + "/" + texture);

		reinforcedStairsBlock(block, Utils.getRegistryName(block).toString(), textureLocation, textureLocation, textureLocation);
	}

	public void reinforcedStairsBlock(Block block, String side, String end) {
		ResourceLocation textureLocationEnd = mcLoc(ModelProvider.BLOCK_FOLDER + "/" + end);

		reinforcedStairsBlock(block, Utils.getRegistryName(block).toString(), mcLoc(ModelProvider.BLOCK_FOLDER + "/" + side), textureLocationEnd, textureLocationEnd);
	}

	public void reinforcedStairsBlock(Block block, String side, String bottom, String top) {
		reinforcedStairsBlock(block, Utils.getRegistryName(block).toString(), mcLoc(ModelProvider.BLOCK_FOLDER + "/" + side), mcLoc(ModelProvider.BLOCK_FOLDER + "/" + bottom), mcLoc(ModelProvider.BLOCK_FOLDER + "/" + top));
	}

	public void reinforcedStairsBlock(Block block, String baseName, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
		ModelFile stairs = models().reinforcedStairs(baseName, side, bottom, top);
		ModelFile stairsInner = models().reinforcedStairsInner(baseName + "_inner", side, bottom, top);
		ModelFile stairsOuter = models().reinforcedStairsOuter(baseName + "_outer", side, bottom, top);

		getVariantBuilder(block).forAllStatesExcept(state -> {
			Direction facing = state.getValue(StairBlock.FACING);
			Half half = state.getValue(StairBlock.HALF);
			StairsShape shape = state.getValue(StairBlock.SHAPE);
			int yRot = (int) facing.getClockWise().toYRot();

			if (shape == StairsShape.INNER_LEFT || shape == StairsShape.OUTER_LEFT)
				yRot += 270;

			if (shape != StairsShape.STRAIGHT && half == Half.TOP)
				yRot += 90;

			yRot %= 360;

			//@formatter:off
			return ConfiguredModel.builder()
					.modelFile(shape == StairsShape.STRAIGHT ? stairs : shape == StairsShape.INNER_LEFT || shape == StairsShape.INNER_RIGHT ? stairsInner : stairsOuter)
					.rotationX(half == Half.BOTTOM ? 0 : 180)
					.rotationY(yRot)
					.uvLock(yRot != 0 || half == Half.TOP)
					.build();
			//@formatter:on
		}, StairBlock.WATERLOGGED);
	}

	public void reinforcedWallBlock(Block block) {
		reinforcedWallBlock(block, name(block).replace("reinforced_", "").replace("_wall", ""));
	}

	public void reinforcedWallBlock(Block block, String textureName) {
		ResourceLocation texture = new ResourceLocation(ModelProvider.BLOCK_FOLDER + "/" + textureName);
		String baseName = Utils.getRegistryName(block).toString();
		ModelFile post = models().reinforcedWallPost(baseName + "_post", texture);
		ModelFile side = models().reinforcedWallSide(baseName + "_side", texture, false);
		ModelFile sideTall = models().reinforcedWallSide(baseName + "_side_tall", texture, true);
		//@formatter:off
		MultiPartBlockStateBuilder builder = getMultipartBuilder(block)
				.part().modelFile(post).addModel()
				.condition(WallBlock.UP, true).end();
		//@formatter:on

		fourWayWallHeight(builder, side, WallSide.LOW);
		fourWayWallHeight(builder, sideTall, WallSide.TALL);
		models().reinforcedWallInventory(baseName + "_inventory", texture);
	}

	public void simpleBlockWithRenderType(Block block, String renderType) {
		simpleBlock(block, models().cubeAll(name(block), blockTexture(block)).renderType(renderType));
	}

	@Override
	public String getName() {
		return "SecurityCraft Block States/Models";
	}

	@Override
	public MultiPartBlockStateBuilder getMultipartBuilder(Block b) {
		//because some blocks' states get set twice (once during scanning SCContent, then actually generating the proper model),
		//and the super method checking if the state has already been generated, and erroring if so, the key has to be removed so it can be remade
		if (registeredBlocks.containsKey(b))
			registeredBlocks.remove(b);

		return super.getMultipartBuilder(b);
	}

	@Override
	public VariantBlockStateBuilder getVariantBuilder(Block b) {
		//because some blocks' states get set twice (once during scanning SCContent, then actually generating the proper model),
		//and the super method checking if the state has already been generated, and erroring if so, the key has to be removed so it can be remade
		if (registeredBlocks.containsKey(b))
			registeredBlocks.remove(b);

		return super.getVariantBuilder(b);
	}

	@Override
	public SCBlockModelProvider models() {
		return scModels;
	}

	private String name(Block block) {
		return Utils.getRegistryName(block).getPath();
	}
}
