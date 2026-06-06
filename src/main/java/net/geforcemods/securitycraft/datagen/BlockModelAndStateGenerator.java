package net.geforcemods.securitycraft.datagen;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableMap;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SCCreativeModeTabs;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IBlockMine;
import net.geforcemods.securitycraft.api.IReinforcedBlock;
import net.geforcemods.securitycraft.blocks.SecureRedstoneInterfaceBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedCarpetBlock;
import net.geforcemods.securitycraft.datagen.DataGenConstants.SCModelTemplates;
import net.geforcemods.securitycraft.datagen.DataGenConstants.SCTexturedModels;
import net.geforcemods.securitycraft.datagen.ReinforcedWoodProvider.LogGenerator;
import net.geforcemods.securitycraft.items.properties.ReinforcedTint;
import net.geforcemods.securitycraft.renderers.DisplayCaseSpecialRenderer;
import net.geforcemods.securitycraft.renderers.SecurityCameraSpecialRenderer;
import net.geforcemods.securitycraft.util.SCItemGroup;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.color.item.GrassColorSource;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.BlockModelGenerators.BlockFamilyProvider;
import net.minecraft.client.data.models.ItemModelOutput;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.BlockModelDefinitionGenerator;
import net.minecraft.client.data.models.blockstates.MultiPartGenerator;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelInstance;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.data.models.model.TexturedModel;
import net.minecraft.client.renderer.block.dispatch.Variant;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.properties.select.DisplayContext;
import net.minecraft.client.renderer.special.ChestSpecialRenderer;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.BlockFamilies;
import net.minecraft.data.BlockFamily;
import net.minecraft.resources.Identifier;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.SignItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ColorCollection;
import net.minecraft.world.level.block.WeatheringCopperCollection.ByState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.SideChainPart;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;

public class BlockModelAndStateGenerator {
	private static final ItemTintSource CRYSTAL_QUARTZ_TINT = ItemModelUtils.constantTint(SCContent.CRYSTAL_QUARTZ_TINT);
	//@formatter:off
	static final Map<Block, BlockModelGenerators.BlockStateGeneratorSupplier> FULL_BLOCK_MODEL_CUSTOM_GENERATORS = ImmutableMap.<Block, BlockModelGenerators.BlockStateGeneratorSupplier>builder()
			.put(Blocks.STONE, BlockModelAndStateGenerator::createMirroredCubeGenerator)
			.put(Blocks.DEEPSLATE, BlockModelAndStateGenerator::createMirroredColumnGenerator)
			.put(Blocks.MUD_BRICKS, BlockModelAndStateGenerator::createNorthWestMirroredCubeGenerator)
			.build();
	static final Map<Block, TexturedModel> TEXTURED_MODELS = ImmutableMap.<Block, TexturedModel>builder()
			.put(Blocks.SANDSTONE, SCTexturedModels.REINFORCED_TOP_BOTTOM_WITH_WALL.get(Blocks.SANDSTONE))
			.put(Blocks.RED_SANDSTONE, SCTexturedModels.REINFORCED_TOP_BOTTOM_WITH_WALL.get(Blocks.RED_SANDSTONE))
			.put(Blocks.SMOOTH_SANDSTONE, SCTexturedModels.createAllSame(TextureMapping.getBlockTexture(Blocks.SANDSTONE, "_top").sprite()))
			.put(Blocks.SMOOTH_RED_SANDSTONE, SCTexturedModels.createAllSame(TextureMapping.getBlockTexture(Blocks.RED_SANDSTONE, "_top").sprite()))
			.put(
				Blocks.CUT_SANDSTONE,
				SCTexturedModels.REINFORCED_COLUMN
					.get(Blocks.SANDSTONE)
					.updateTextures(textureMapping -> textureMapping.put(TextureSlot.SIDE, TextureMapping.getBlockTexture(Blocks.CUT_SANDSTONE)))
			)
			.put(
				Blocks.CUT_RED_SANDSTONE,
				SCTexturedModels.REINFORCED_COLUMN
					.get(Blocks.RED_SANDSTONE)
					.updateTextures(textureMapping -> textureMapping.put(TextureSlot.SIDE, TextureMapping.getBlockTexture(Blocks.CUT_RED_SANDSTONE)))
			)
			.put(Blocks.QUARTZ_BLOCK, SCTexturedModels.REINFORCED_COLUMN.get(Blocks.QUARTZ_BLOCK))
			.put(Blocks.SMOOTH_QUARTZ, SCTexturedModels.createAllSame(TextureMapping.getBlockTexture(Blocks.QUARTZ_BLOCK, "_bottom").sprite()))
			.put(Blocks.BLACKSTONE, SCTexturedModels.REINFORCED_COLUMN_WITH_WALL.get(Blocks.BLACKSTONE))
			.put(Blocks.DEEPSLATE, SCTexturedModels.REINFORCED_COLUMN_WITH_WALL.get(Blocks.DEEPSLATE))
			.put(
				Blocks.CHISELED_QUARTZ_BLOCK,
				SCTexturedModels.REINFORCED_COLUMN
					.get(Blocks.CHISELED_QUARTZ_BLOCK)
					.updateTextures(textureMapping -> textureMapping.put(TextureSlot.SIDE, TextureMapping.getBlockTexture(Blocks.CHISELED_QUARTZ_BLOCK)))
			)
			.put(Blocks.CHISELED_SANDSTONE, SCTexturedModels.REINFORCED_COLUMN.get(Blocks.CHISELED_SANDSTONE).updateTextures(textureMapping -> {
				textureMapping.put(TextureSlot.END, TextureMapping.getBlockTexture(Blocks.SANDSTONE, "_top"));
				textureMapping.put(TextureSlot.SIDE, TextureMapping.getBlockTexture(Blocks.CHISELED_SANDSTONE));
			}))
			.put(Blocks.CHISELED_RED_SANDSTONE, SCTexturedModels.REINFORCED_COLUMN.get(Blocks.CHISELED_RED_SANDSTONE).updateTextures(textureMapping -> {
				textureMapping.put(TextureSlot.END, TextureMapping.getBlockTexture(Blocks.RED_SANDSTONE, "_top"));
				textureMapping.put(TextureSlot.SIDE, TextureMapping.getBlockTexture(Blocks.CHISELED_RED_SANDSTONE));
			}))
			.put(Blocks.CHISELED_TUFF_BRICKS, SCTexturedModels.REINFORCED_COLUMN_WITH_WALL.get(Blocks.CHISELED_TUFF_BRICKS))
			.put(Blocks.CHISELED_TUFF, SCTexturedModels.REINFORCED_COLUMN_WITH_WALL.get(Blocks.CHISELED_TUFF))
			.build();
	//@formatter:on
	private static final Set<ReinforcedBlockFamilyProvider> REINFORCED_FAMILIES = new HashSet<>();
	static BlockModelGenerators blockModelGenerators;
	static Consumer<BlockModelDefinitionGenerator> blockStateOutput;
	static BiConsumer<Identifier, ModelInstance> modelOutput;
	static ItemModelOutput itemInfo;
	static Set<Block> generatedBlocks = new HashSet<>();

	private BlockModelAndStateGenerator() {}

	protected static void run(BlockModelGenerators blockModelGenerators) {
		List<Holder<Item>> mineTabItems = SCCreativeModeTabs.getItemList(SCItemGroup.EXPLOSIVES);
		List<Holder<Item>> decorationTabItems = SCCreativeModeTabs.getItemList(SCItemGroup.DECORATION);
		ByState<BlockFamily> copperFamilies = BlockFamilies.COPPER_BLOCK.weathering();
		ByState<BlockFamily> cutCopperFamilies = BlockFamilies.CUT_COPPER.weathering();
		//TODO Add new reinforced blocks for these families
		List<BlockFamily> excludedFamilies = List.of(BlockFamilies.CINNABAR, BlockFamilies.CINNABAR_BRICKS, BlockFamilies.POLISHED_CINNABAR, BlockFamilies.POLISHED_SULFUR, BlockFamilies.SULFUR, BlockFamilies.SULFUR_BRICKS);

		BlockModelAndStateGenerator.blockModelGenerators = blockModelGenerators;
		blockStateOutput = blockModelGenerators.blockStateOutput;
		modelOutput = blockModelGenerators.modelOutput;
		itemInfo = blockModelGenerators.itemModelOutput;
		createBlockMine(SCContent.ANCIENT_DEBRIS_MINE.get(), Blocks.ANCIENT_DEBRIS);
		createCreakingHeartMine();
		generateBlockMineInfo(SCContent.DEEPSLATE_MINE.get(), ModelLocationUtils.getModelLocation(Blocks.DEEPSLATE));
		generateBlockMineInfo(SCContent.SUSPICIOUS_GRAVEL_MINE.get(), ModelLocationUtils.getModelLocation(Blocks.SUSPICIOUS_GRAVEL, "_0"));
		generateBlockMineInfo(SCContent.SUSPICIOUS_SAND_MINE.get(), ModelLocationUtils.getModelLocation(Blocks.SUSPICIOUS_SAND, "_0"));
		createHorizontalBlockMine(SCContent.FURNACE_MINE.get(), Blocks.FURNACE, TexturedModel.ORIENTABLE_ONLY_TOP);
		createHorizontalBlockMine(SCContent.SMOKER_MINE.get(), Blocks.SMOKER, TexturedModel.ORIENTABLE_ONLY_TOP);
		createHorizontalBlockMine(SCContent.BLAST_FURNACE_MINE.get(), Blocks.BLAST_FURNACE, TexturedModel.ORIENTABLE_ONLY_TOP);

		registerSimpleItemModelFromItem(SCContent.ALARM.get());
		registerSimpleItemModel(SCContent.BLOCK_CHANGE_DETECTOR.get());
		registerReinforcedItemModel(SCContent.BLOCK_POCKET_MANAGER.get(), "_1", CRYSTAL_QUARTZ_TINT);
		registerReinforcedItemModel(SCContent.BLOCK_POCKET_WALL.get(), "_see_through", CRYSTAL_QUARTZ_TINT);
		registerSimpleItemModelFromItem(SCContent.BOUNCING_BETTY.get());
		registerSimpleItemModel(SCContent.CAGE_TRAP.get());
		registerSimpleItemModel(SCContent.CLAYMORE.get());
		createDisplayCases();
		createElectrifiedFenceAndGate();
		createFloorTrap();
		registerSimpleItemModel(SCContent.FRAME.get());
		registerSimpleItemModelFromItem(SCContent.IMS.get());
		registerSimpleItemModel(SCContent.INVENTORY_SCANNER.get());
		registerSimpleItemModel(SCContent.KEYCARD_LOCK.get(), "_off");
		registerSimpleItemModel(SCContent.KEYCARD_READER.get(), "_off");
		registerSimpleItemModel(SCContent.KEYPAD.get());
		registerSimpleItemModel(SCContent.KEYPAD_BARREL.get());
		createKeypadChest();
		registerSimpleItemModel(SCContent.KEYPAD_BLAST_FURNACE.get(), "_open");
		registerSimpleItemModel(SCContent.KEYPAD_FURNACE.get(), "_open");
		registerSimpleItemModel(SCContent.KEYPAD_SMOKER.get(), "_open");
		registerSimpleItemModel(SCContent.KEYPAD_TRAPDOOR.get(), "_bottom");
		registerSimpleItemModel(SCContent.LASER_BLOCK.get());
		registerSimpleItemModelFromItem(SCContent.MINE.get());
		registerSimpleItemModel(SCContent.MOTION_ACTIVATED_LIGHT.get());
		registerSimpleItemModelFromItem(SCContent.PANIC_BUTTON.get());
		registerSimpleItemModel(SCContent.PORTABLE_RADAR.get());
		registerSimpleItemModel(SCContent.PROJECTOR.get());
		registerSimpleItemModel(SCContent.PROTECTO.get(), "_deactivated");
		createRailMine();
		registerReinforcedFlatItemModel(SCContent.REINFORCED_CAULDRON.get());
		SCContent.REINFORCED_COPPER_BARS.forEach(BlockModelAndStateGenerator::createBarsAndItem);
		SCContent.REINFORCED_COPPER_CHAIN.forEach(BlockModelAndStateGenerator::registerReinforcedFlatItemModel);
		SCContent.REINFORCED_COPPER_LANTERN.forEach(BlockModelAndStateGenerator::registerReinforcedFlatItemModel);
		SCContent.REINFORCED_LIGHTNING_ROD.forEach(BlockModelAndStateGenerator::createReinforcedLightningRod);
		registerReinforcedFlatItemModel(SCContent.REINFORCED_IRON_CHAIN.get());
		registerReinforcedItemModel(SCContent.REINFORCED_CHISELED_BOOKSHELF.get(), "_inventory");
		registerReinforcedFlatItemModelFromBlock(SCContent.REINFORCED_COBWEB.get());
		createReinforcedGrassBlock();
		registerReinforcedFlatItemModel(SCContent.REINFORCED_HOPPER.get());
		createBarsAndItem(SCContent.REINFORCED_IRON_BARS);
		registerSimpleItemModel(SCContent.REINFORCED_IRON_TRAPDOOR.get(), "_bottom");
		registerReinforcedFlatItemModelFromBlock(SCContent.REINFORCED_LADDER.get());
		registerReinforcedFlatItemModel(SCContent.REINFORCED_LANTERN.get());
		registerReinforcedFlatItemModelFromBlock(SCContent.REINFORCED_LEVER.get());
		createReinforcedCarpet(SCContent.REINFORCED_MOSS_CARPET.get(), "block");
		createReinforcedPistons();
		registerReinforcedItemModel(SCContent.REINFORCED_SCAFFOLDING.get(), "_stable");
		registerReinforcedFlatItemModel(SCContent.REINFORCED_SOUL_LANTERN.get());
		registerSimpleItemModelFromItem(SCContent.RETINAL_SCANNER.get());
		registerSimpleItemModel(SCContent.SCANNER_TRAPDOOR.get(), "_bottom");
		registerSimpleItemModel(SCContent.SECURE_TRADING_STATION.get());
		createSecureRedstoneInterface();
		createSecurityCamera();
		registerSimpleItemModel(SCContent.TROPHY_SYSTEM.get());
		registerSimpleItemModel(SCContent.USERNAME_LOGGER.get());

		createGlassBlocks(SCContent.REINFORCED_GLASS, SCContent.REINFORCED_GLASS_PANE);
		ColorCollection.zipApply(SCContent.REINFORCED_STAINED_GLASS, SCContent.REINFORCED_STAINED_GLASS_PANE, BlockModelAndStateGenerator::createGlassBlocks);

		ReinforcedWoodProvider.of(SCContent.REINFORCED_ACACIA_LOG.get(), LogGenerator.HORIZONTAL).wood(SCContent.REINFORCED_ACACIA_WOOD.get());
		ReinforcedWoodProvider.of(SCContent.REINFORCED_STRIPPED_ACACIA_LOG.get(), LogGenerator.HORIZONTAL).wood(SCContent.REINFORCED_STRIPPED_ACACIA_WOOD.get());
		ReinforcedWoodProvider.of(SCContent.REINFORCED_BAMBOO_BLOCK.get(), LogGenerator.UV_LOCKED);
		ReinforcedWoodProvider.of(SCContent.REINFORCED_STRIPPED_BAMBOO_BLOCK.get(), LogGenerator.UV_LOCKED);
		ReinforcedWoodProvider.of(SCContent.REINFORCED_BIRCH_LOG.get(), LogGenerator.HORIZONTAL).wood(SCContent.REINFORCED_BIRCH_WOOD.get());
		ReinforcedWoodProvider.of(SCContent.REINFORCED_STRIPPED_BIRCH_LOG.get(), LogGenerator.HORIZONTAL).wood(SCContent.REINFORCED_STRIPPED_BIRCH_WOOD.get());
		ReinforcedWoodProvider.of(SCContent.REINFORCED_CHERRY_LOG.get(), LogGenerator.UV_LOCKED).wood(SCContent.REINFORCED_CHERRY_WOOD.get());
		ReinforcedWoodProvider.of(SCContent.REINFORCED_STRIPPED_CHERRY_LOG.get(), LogGenerator.UV_LOCKED).wood(SCContent.REINFORCED_STRIPPED_CHERRY_WOOD.get());
		ReinforcedWoodProvider.of(SCContent.REINFORCED_CRIMSON_STEM.get(), LogGenerator.DEFAULT).wood(SCContent.REINFORCED_CRIMSON_HYPHAE.get());
		ReinforcedWoodProvider.of(SCContent.REINFORCED_STRIPPED_CRIMSON_STEM.get(), LogGenerator.DEFAULT).wood(SCContent.REINFORCED_STRIPPED_CRIMSON_HYPHAE.get());
		ReinforcedWoodProvider.of(SCContent.REINFORCED_DARK_OAK_LOG.get(), LogGenerator.HORIZONTAL).wood(SCContent.REINFORCED_DARK_OAK_WOOD.get());
		ReinforcedWoodProvider.of(SCContent.REINFORCED_STRIPPED_DARK_OAK_LOG.get(), LogGenerator.HORIZONTAL).wood(SCContent.REINFORCED_STRIPPED_DARK_OAK_WOOD.get());
		ReinforcedWoodProvider.of(SCContent.REINFORCED_JUNGLE_LOG.get(), LogGenerator.HORIZONTAL).wood(SCContent.REINFORCED_JUNGLE_WOOD.get());
		ReinforcedWoodProvider.of(SCContent.REINFORCED_STRIPPED_JUNGLE_LOG.get(), LogGenerator.HORIZONTAL).wood(SCContent.REINFORCED_STRIPPED_JUNGLE_WOOD.get());
		ReinforcedWoodProvider.of(SCContent.REINFORCED_MANGROVE_LOG.get(), LogGenerator.HORIZONTAL).wood(SCContent.REINFORCED_MANGROVE_WOOD.get());
		ReinforcedWoodProvider.of(SCContent.REINFORCED_STRIPPED_MANGROVE_LOG.get(), LogGenerator.HORIZONTAL).wood(SCContent.REINFORCED_STRIPPED_MANGROVE_WOOD.get());
		ReinforcedWoodProvider.of(SCContent.REINFORCED_OAK_LOG.get(), LogGenerator.HORIZONTAL).wood(SCContent.REINFORCED_OAK_WOOD.get());
		ReinforcedWoodProvider.of(SCContent.REINFORCED_STRIPPED_OAK_LOG.get(), LogGenerator.HORIZONTAL).wood(SCContent.REINFORCED_STRIPPED_OAK_WOOD.get());
		ReinforcedWoodProvider.of(SCContent.REINFORCED_PALE_OAK_LOG.get(), LogGenerator.HORIZONTAL).wood(SCContent.REINFORCED_PALE_OAK_WOOD.get());
		ReinforcedWoodProvider.of(SCContent.REINFORCED_STRIPPED_PALE_OAK_LOG.get(), LogGenerator.HORIZONTAL).wood(SCContent.REINFORCED_STRIPPED_PALE_OAK_WOOD.get());
		ReinforcedWoodProvider.of(SCContent.REINFORCED_SPRUCE_LOG.get(), LogGenerator.HORIZONTAL).wood(SCContent.REINFORCED_SPRUCE_WOOD.get());
		ReinforcedWoodProvider.of(SCContent.REINFORCED_STRIPPED_SPRUCE_LOG.get(), LogGenerator.HORIZONTAL).wood(SCContent.REINFORCED_STRIPPED_SPRUCE_WOOD.get());
		ReinforcedWoodProvider.of(SCContent.REINFORCED_WARPED_STEM.get(), LogGenerator.DEFAULT).wood(SCContent.REINFORCED_WARPED_HYPHAE.get());
		ReinforcedWoodProvider.of(SCContent.REINFORCED_STRIPPED_WARPED_STEM.get(), LogGenerator.DEFAULT).wood(SCContent.REINFORCED_STRIPPED_WARPED_HYPHAE.get());

		createReinforcedShelf(SCContent.REINFORCED_ACACIA_SHELF.get(), Blocks.ACACIA_SHELF, Blocks.STRIPPED_ACACIA_LOG);
		createReinforcedShelf(SCContent.REINFORCED_BAMBOO_SHELF.get(), Blocks.BAMBOO_SHELF, Blocks.STRIPPED_BAMBOO_BLOCK);
		createReinforcedShelf(SCContent.REINFORCED_BIRCH_SHELF.get(), Blocks.BIRCH_SHELF, Blocks.STRIPPED_BIRCH_LOG);
		createReinforcedShelf(SCContent.REINFORCED_CHERRY_SHELF.get(), Blocks.CHERRY_SHELF, Blocks.STRIPPED_CHERRY_LOG);
		createReinforcedShelf(SCContent.REINFORCED_CRIMSON_SHELF.get(), Blocks.CRIMSON_SHELF, Blocks.STRIPPED_CRIMSON_STEM);
		createReinforcedShelf(SCContent.REINFORCED_DARK_OAK_SHELF.get(), Blocks.DARK_OAK_SHELF, Blocks.STRIPPED_DARK_OAK_LOG);
		createReinforcedShelf(SCContent.REINFORCED_JUNGLE_SHELF.get(), Blocks.JUNGLE_SHELF, Blocks.STRIPPED_JUNGLE_LOG);
		createReinforcedShelf(SCContent.REINFORCED_MANGROVE_SHELF.get(), Blocks.MANGROVE_SHELF, Blocks.STRIPPED_MANGROVE_LOG);
		createReinforcedShelf(SCContent.REINFORCED_OAK_SHELF.get(), Blocks.OAK_SHELF, Blocks.STRIPPED_OAK_LOG);
		createReinforcedShelf(SCContent.REINFORCED_PALE_OAK_SHELF.get(), Blocks.PALE_OAK_SHELF, Blocks.STRIPPED_PALE_OAK_LOG);
		createReinforcedShelf(SCContent.REINFORCED_SPRUCE_SHELF.get(), Blocks.SPRUCE_SHELF, Blocks.STRIPPED_SPRUCE_LOG);
		createReinforcedShelf(SCContent.REINFORCED_WARPED_SHELF.get(), Blocks.WARPED_SHELF, Blocks.STRIPPED_WARPED_STEM);

		createSecretSign(SCContent.SECRET_ACACIA_SIGN_ITEM.get(), Blocks.ACACIA_SIGN, Blocks.ACACIA_WALL_SIGN);
		createSecretSign(SCContent.SECRET_BAMBOO_SIGN_ITEM.get(), Blocks.BAMBOO_SIGN, Blocks.BAMBOO_WALL_SIGN);
		createSecretSign(SCContent.SECRET_BIRCH_SIGN_ITEM.get(), Blocks.BIRCH_SIGN, Blocks.BIRCH_WALL_SIGN);
		createSecretSign(SCContent.SECRET_CHERRY_SIGN_ITEM.get(), Blocks.CHERRY_SIGN, Blocks.CHERRY_WALL_SIGN);
		createSecretSign(SCContent.SECRET_CRIMSON_SIGN_ITEM.get(), Blocks.CRIMSON_SIGN, Blocks.CRIMSON_WALL_SIGN);
		createSecretSign(SCContent.SECRET_DARK_OAK_SIGN_ITEM.get(), Blocks.DARK_OAK_SIGN, Blocks.DARK_OAK_WALL_SIGN);
		createSecretSign(SCContent.SECRET_JUNGLE_SIGN_ITEM.get(), Blocks.JUNGLE_SIGN, Blocks.JUNGLE_WALL_SIGN);
		createSecretSign(SCContent.SECRET_MANGROVE_SIGN_ITEM.get(), Blocks.MANGROVE_SIGN, Blocks.MANGROVE_WALL_SIGN);
		createSecretSign(SCContent.SECRET_OAK_SIGN_ITEM.get(), Blocks.OAK_SIGN, Blocks.OAK_WALL_SIGN);
		createSecretSign(SCContent.SECRET_PALE_OAK_SIGN_ITEM.get(), Blocks.PALE_OAK_SIGN, Blocks.PALE_OAK_WALL_SIGN);
		createSecretSign(SCContent.SECRET_SPRUCE_SIGN_ITEM.get(), Blocks.SPRUCE_SIGN, Blocks.SPRUCE_WALL_SIGN);
		createSecretSign(SCContent.SECRET_WARPED_SIGN_ITEM.get(), Blocks.WARPED_SIGN, Blocks.WARPED_WALL_SIGN);
		createSecretHangingSign(SCContent.SECRET_ACACIA_HANGING_SIGN_ITEM.get(), Blocks.ACACIA_HANGING_SIGN, Blocks.ACACIA_WALL_HANGING_SIGN);
		createSecretHangingSign(SCContent.SECRET_BAMBOO_HANGING_SIGN_ITEM.get(), Blocks.BAMBOO_HANGING_SIGN, Blocks.BAMBOO_WALL_HANGING_SIGN);
		createSecretHangingSign(SCContent.SECRET_BIRCH_HANGING_SIGN_ITEM.get(), Blocks.BIRCH_HANGING_SIGN, Blocks.BIRCH_WALL_HANGING_SIGN);
		createSecretHangingSign(SCContent.SECRET_CHERRY_HANGING_SIGN_ITEM.get(), Blocks.CHERRY_HANGING_SIGN, Blocks.CHERRY_WALL_HANGING_SIGN);
		createSecretHangingSign(SCContent.SECRET_CRIMSON_HANGING_SIGN_ITEM.get(), Blocks.CRIMSON_HANGING_SIGN, Blocks.CRIMSON_WALL_HANGING_SIGN);
		createSecretHangingSign(SCContent.SECRET_DARK_OAK_HANGING_SIGN_ITEM.get(), Blocks.DARK_OAK_HANGING_SIGN, Blocks.DARK_OAK_WALL_HANGING_SIGN);
		createSecretHangingSign(SCContent.SECRET_JUNGLE_HANGING_SIGN_ITEM.get(), Blocks.JUNGLE_HANGING_SIGN, Blocks.JUNGLE_WALL_HANGING_SIGN);
		createSecretHangingSign(SCContent.SECRET_MANGROVE_HANGING_SIGN_ITEM.get(), Blocks.MANGROVE_HANGING_SIGN, Blocks.MANGROVE_WALL_HANGING_SIGN);
		createSecretHangingSign(SCContent.SECRET_OAK_HANGING_SIGN_ITEM.get(), Blocks.OAK_HANGING_SIGN, Blocks.OAK_WALL_HANGING_SIGN);
		createSecretHangingSign(SCContent.SECRET_PALE_OAK_HANGING_SIGN_ITEM.get(), Blocks.PALE_OAK_HANGING_SIGN, Blocks.PALE_OAK_WALL_HANGING_SIGN);
		createSecretHangingSign(SCContent.SECRET_SPRUCE_HANGING_SIGN_ITEM.get(), Blocks.SPRUCE_HANGING_SIGN, Blocks.SPRUCE_WALL_HANGING_SIGN);
		createSecretHangingSign(SCContent.SECRET_WARPED_HANGING_SIGN_ITEM.get(), Blocks.WARPED_HANGING_SIGN, Blocks.WARPED_WALL_HANGING_SIGN);

		//@formatter:off
		SCModelTemplates.REINFORCED_CUBE_COLUMN.create(
				ModelLocationUtils.decorateBlockModelLocation(SecurityCraft.resLoc("reinforced_smooth_stone_slab_double").toString()),
				TextureMapping.column(
						TextureMapping.getBlockTexture(Blocks.SMOOTH_STONE_SLAB, "_side"),
						TextureMapping.cube(Blocks.SMOOTH_STONE).get(TextureSlot.TOP)),
				modelOutput);
		BlockFamilies.getAllFamilies()
			.filter(BlockFamily::shouldGenerateModel)
			.filter(Predicates.not(excludedFamilies::contains))
			.forEach(BlockModelAndStateGenerator::reinforcedFamily);
		//@formatter:on

		reinforcedFamily(copperFamilies.unaffected());
		reinforcedFamily(cutCopperFamilies.unaffected());
		reinforcedFamily(copperFamilies.exposed());
		reinforcedFamily(cutCopperFamilies.exposed());
		reinforcedFamily(copperFamilies.weathered());
		reinforcedFamily(cutCopperFamilies.weathered());
		reinforcedFamily(copperFamilies.oxidized());
		reinforcedFamily(cutCopperFamilies.oxidized());
		REINFORCED_FAMILIES.forEach(family -> family.generateFor(null));
		createReinforcedSlab(SCContent.REINFORCED_SMOOTH_STONE_SLAB.get(), "reinforced_smooth_stone_slab_double", "smooth_stone_slab_side", "smooth_stone");
		createCrystalQuartzBlocks();
		SCContent.BLOCKS.getEntries().stream().map(DeferredHolder::get).filter(b -> !generatedBlocks.contains(b)).forEach(block -> {
			Reference<Item> item = block.asItem().builtInRegistryHolder();

			if (decorationTabItems.contains(item)) {
				switch (block) {
					case ReinforcedCarpetBlock carpet -> createReinforcedCarpet(block);
					case IReinforcedBlock reinforcedBlock -> registerReinforcedItemModel(block);
					default -> {
					}
				}
			}
			else if (mineTabItems.contains(item) && block instanceof IBlockMine mine)
				createBlockMine(block, mine.getBlockDisguisedAs());
		});
	}

	public static void createBarsAndItem(DeferredBlock<? extends Block> dBlock) {
		Block block = dBlock.get();

		blockModelGenerators.createBarsAndItem(block);
		generatedBlocks.add(block);
	}

	public static void createBlockMine(Block block, Block vanillaBlock) {
		Identifier vanillaModelPath = ModelLocationUtils.getModelLocation(vanillaBlock);
		MultiVariant vanillaModel = BlockModelGenerators.plainVariant(vanillaModelPath);

		generate(block, BlockModelGenerators.createSimpleBlock(block, vanillaModel));
		generateBlockMineInfo(block, vanillaModelPath);
	}

	public static void createCreakingHeartMine() {
		Block creakingHeartMine = SCContent.CREAKING_HEART_MINE.get();
		Identifier vanillaModel = ModelLocationUtils.getModelLocation(Blocks.CREAKING_HEART);

		generateBlockMineInfo(creakingHeartMine, vanillaModel);
		generatedBlocks.add(creakingHeartMine);
	}

	public static void generateBlockMineInfo(Block block, Identifier vanillaModel) {
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
		generatedBlocks.add(block);
	}

	public static void createCrystalQuartzBlocks() {
		Identifier chiseledModel = ModelLocationUtils.getModelLocation(SCContent.REINFORCED_CHISELED_QUARTZ.get());
		Identifier blockModel = ModelLocationUtils.getModelLocation(SCContent.REINFORCED_QUARTZ_BLOCK.get());
		Identifier bricksModel = ModelLocationUtils.getModelLocation(SCContent.REINFORCED_QUARTZ_BRICKS.get());
		Identifier pillarModel = ModelLocationUtils.getModelLocation(SCContent.REINFORCED_QUARTZ_PILLAR.get());
		Identifier smoothModel = ModelLocationUtils.getModelLocation(SCContent.REINFORCED_SMOOTH_QUARTZ.get());

		registerTintedItemModel(SCContent.CHISELED_CRYSTAL_QUARTZ.get(), chiseledModel, CRYSTAL_QUARTZ_TINT);
		registerTintedItemModel(SCContent.CRYSTAL_QUARTZ_BLOCK.get(), blockModel, CRYSTAL_QUARTZ_TINT);
		registerTintedItemModel(SCContent.CRYSTAL_QUARTZ_BRICKS.get(), bricksModel, CRYSTAL_QUARTZ_TINT);
		registerTintedItemModel(SCContent.CRYSTAL_QUARTZ_PILLAR.get(), pillarModel, CRYSTAL_QUARTZ_TINT);
		registerTintedItemModel(SCContent.SMOOTH_CRYSTAL_QUARTZ.get(), smoothModel, CRYSTAL_QUARTZ_TINT);
		createTintedSlab(SCContent.CRYSTAL_QUARTZ_SLAB.get(), "reinforced_quartz_block", "quartz_block_side", "quartz_block_top", CRYSTAL_QUARTZ_TINT);
		createTintedSlab(SCContent.SMOOTH_CRYSTAL_QUARTZ_SLAB.get(), "reinforced_smooth_quartz", "quartz_block_bottom", "quartz_block_bottom", CRYSTAL_QUARTZ_TINT);
		createTintedStairs(SCContent.CRYSTAL_QUARTZ_STAIRS.get(), "quartz_block_side", "quartz_block_top", CRYSTAL_QUARTZ_TINT);
		createTintedStairs(SCContent.SMOOTH_CRYSTAL_QUARTZ_STAIRS.get(), "quartz_block_bottom", "quartz_block_bottom", CRYSTAL_QUARTZ_TINT);
		registerReinforcedItemModel(SCContent.REINFORCED_CHISELED_CRYSTAL_QUARTZ.get(), chiseledModel, CRYSTAL_QUARTZ_TINT);
		registerReinforcedItemModel(SCContent.REINFORCED_CRYSTAL_QUARTZ_BLOCK.get(), blockModel, CRYSTAL_QUARTZ_TINT);
		registerReinforcedItemModel(SCContent.REINFORCED_CRYSTAL_QUARTZ_BRICKS.get(), bricksModel, CRYSTAL_QUARTZ_TINT);
		registerReinforcedItemModel(SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get(), pillarModel, CRYSTAL_QUARTZ_TINT);
		registerReinforcedItemModel(SCContent.REINFORCED_SMOOTH_CRYSTAL_QUARTZ.get(), smoothModel, CRYSTAL_QUARTZ_TINT);
		createReinforcedSlab(SCContent.REINFORCED_CRYSTAL_QUARTZ_SLAB.get(), "reinforced_quartz_block", "quartz_block_side", "quartz_block_top", CRYSTAL_QUARTZ_TINT);
		createReinforcedSlab(SCContent.REINFORCED_SMOOTH_CRYSTAL_QUARTZ_SLAB.get(), "reinforced_smooth_quartz", "quartz_block_bottom", "quartz_block_bottom", CRYSTAL_QUARTZ_TINT);
		createReinforcedStairs(SCContent.REINFORCED_CRYSTAL_QUARTZ_STAIRS.get(), "quartz_block_side", "quartz_block_top", CRYSTAL_QUARTZ_TINT);
		createReinforcedStairs(SCContent.REINFORCED_SMOOTH_CRYSTAL_QUARTZ_STAIRS.get(), "quartz_block_bottom", "quartz_block_bottom", CRYSTAL_QUARTZ_TINT);
	}

	public static void createDisplayCases() {
		Block normal = SCContent.DISPLAY_CASE.get();
		Block glow = SCContent.GLOW_DISPLAY_CASE.get();
		Identifier baseModel = ModelLocationUtils.getModelLocation(normal.asItem());

		blockModelGenerators.createParticleOnlyBlock(normal, Blocks.IRON_BLOCK);
		blockModelGenerators.createParticleOnlyBlock(glow, Blocks.IRON_BLOCK);
		//@formatter:off
		itemInfo.accept(normal.asItem(),
				ItemModelUtils.specialModel(baseModel,
						new DisplayCaseSpecialRenderer.Unbaked(
								SecurityCraft.resLoc("normal"),
								0.0F,
								Optional.empty())));
		itemInfo.accept(glow.asItem(),
				ItemModelUtils.specialModel(baseModel,
						new DisplayCaseSpecialRenderer.Unbaked(
								SecurityCraft.resLoc("glow"),
								0.0F,
								Optional.of(LightCoordsUtil.FULL_BRIGHT))));
		//@formatter:on
		generatedBlocks.add(normal);
		generatedBlocks.add(glow);
	}

	public static void createElectrifiedFenceAndGate() {
		Block fence = SCContent.ELECTRIFIED_IRON_FENCE.get();
		Block gate = SCContent.ELECTRIFIED_IRON_FENCE_GATE.get();
		BlockFamilyProvider blockFamilyProvider = blockModelGenerators.new BlockFamilyProvider(TextureMapping.fence(fence)); //The texture mapping is unused for custom fence/-gate generation

		blockFamilyProvider.customFence(fence);
		blockFamilyProvider.customFenceGate(gate);
		itemInfo.accept(gate.asItem(), ItemModelUtils.plainModel(ModelLocationUtils.getModelLocation(gate)));
		generatedBlocks.add(fence);
		generatedBlocks.add(gate);
	}

	public static void createHorizontalBlockMine(Block furnaceBlock, Block mockBlock, TexturedModel.Provider modelProvider) {
		//@formatter:off
		Identifier modelLocation = modelProvider.get(furnaceBlock)
				.updateTextures(mapping -> {
					mapping.put(TextureSlot.FRONT, TextureMapping.getBlockTexture(mockBlock, "_front"));
					mapping.put(TextureSlot.SIDE, TextureMapping.getBlockTexture(mockBlock, "_side"));
					mapping.put(TextureSlot.TOP, TextureMapping.getBlockTexture(mockBlock, "_top"));
				})
				.create(furnaceBlock, modelOutput);

		generate(furnaceBlock, MultiVariantGenerator.dispatch(furnaceBlock, BlockModelGenerators.plainVariant(modelLocation))
				.with(BlockModelGenerators.ROTATION_HORIZONTAL_FACING));
		//@formatter:on
		generateBlockMineInfo(furnaceBlock, ModelLocationUtils.getModelLocation(mockBlock));
	}

	public static void createKeypadChest() {
		Block block = SCContent.KEYPAD_CHEST.get();
		Block particleBlock = Blocks.IRON_BLOCK;
		Item item = block.asItem();
		Identifier inventoryModel = ModelTemplates.CHEST_INVENTORY.create(item, TextureMapping.particle(particleBlock), modelOutput);
		ItemModel.Unbaked defaultUnbaked = ItemModelUtils.specialModel(inventoryModel, new ChestSpecialRenderer.Unbaked(SecurityCraft.resLoc("inactive")));
		ItemModel.Unbaked christmasUnbaked = ItemModelUtils.specialModel(inventoryModel, new ChestSpecialRenderer.Unbaked(SecurityCraft.resLoc("christmas")));

		blockModelGenerators.createParticleOnlyBlock(block, particleBlock);
		itemInfo.accept(item, ItemModelUtils.isXmas(christmasUnbaked, defaultUnbaked));
		generatedBlocks.add(block);
	}

	public static void createRailMine() {
		Block block = SCContent.TRACK_MINE.get();

		blockModelGenerators.createPassiveRail(block);
		generatedBlocks.add(block);
	}

	public static void createReinforcedButton(Block block, TextureMapping textureMapping) {
		MultiVariant defaultModel = BlockModelGenerators.plainVariant(SCModelTemplates.REINFORCED_BUTTON.create(block, textureMapping, modelOutput));
		MultiVariant pressedModel = BlockModelGenerators.plainVariant(SCModelTemplates.REINFORCED_BUTTON_PRESSED.create(block, textureMapping, modelOutput));
		Identifier inventoryModel = SCModelTemplates.REINFORCED_BUTTON_INVENTORY.create(block, textureMapping, modelOutput);

		blockStateOutput.accept(BlockModelGenerators.createButton(block, defaultModel, pressedModel));
		registerReinforcedItemModel(block, inventoryModel);
	}

	public static void createReinforcedCarpet(Block block) {
		createReinforcedCarpet(block, "wool");
	}

	public static void createReinforcedCarpet(Block block, String carpetReplacement) {
		String name = name(block);
		Identifier baseBlockName = SecurityCraft.mcResLoc(name.replace("reinforced_", "").replace("carpet", carpetReplacement));
		Block baseBlock = BuiltInRegistries.BLOCK.get(baseBlockName).orElseThrow(() -> new IllegalStateException(baseBlockName + " does not exist!")).value();
		MultiVariant modelLocation = BlockModelGenerators.plainVariant(SCTexturedModels.REINFORCED_CARPET.get(baseBlock).create(block, modelOutput));

		generate(block, BlockModelGenerators.createSimpleBlock(block, modelLocation));
		registerReinforcedItemModel(block);
	}

	public static void createReinforcedCustomFence(Block block, TextureMapping textureMapping) {
		MultiVariant postModel = BlockModelGenerators.plainVariant(SCModelTemplates.CUSTOM_REINFORCED_FENCE_POST.create(block, textureMapping, modelOutput));
		MultiVariant northSideModel = BlockModelGenerators.plainVariant(SCModelTemplates.CUSTOM_REINFORCED_FENCE_SIDE_NORTH.create(block, textureMapping, modelOutput));
		MultiVariant eastSideModel = BlockModelGenerators.plainVariant(SCModelTemplates.CUSTOM_REINFORCED_FENCE_SIDE_EAST.create(block, textureMapping, modelOutput));
		MultiVariant southSideModel = BlockModelGenerators.plainVariant(SCModelTemplates.CUSTOM_REINFORCED_FENCE_SIDE_SOUTH.create(block, textureMapping, modelOutput));
		MultiVariant westSideModel = BlockModelGenerators.plainVariant(SCModelTemplates.CUSTOM_REINFORCED_FENCE_SIDE_WEST.create(block, textureMapping, modelOutput));
		Identifier inventoryModel = SCModelTemplates.CUSTOM_REINFORCED_FENCE_INVENTORY.create(block, textureMapping, modelOutput);

		generate(block, BlockModelGenerators.createCustomFence(block, postModel, northSideModel, eastSideModel, southSideModel, westSideModel));
		registerReinforcedItemModel(block, inventoryModel);
	}

	public static void createReinforcedFence(Block block, TextureMapping textureMapping) {
		MultiVariant postModel = BlockModelGenerators.plainVariant(SCModelTemplates.REINFORCED_FENCE_POST.create(block, textureMapping, modelOutput));
		MultiVariant sideModel = BlockModelGenerators.plainVariant(SCModelTemplates.REINFORCED_FENCE_SIDE.create(block, textureMapping, modelOutput));
		Identifier inventoryModel = SCModelTemplates.REINFORCED_FENCE_INVENTORY.create(block, textureMapping, modelOutput);

		generate(block, BlockModelGenerators.createFence(block, postModel, sideModel));
		registerReinforcedItemModel(block, inventoryModel);
	}

	public static void createReinforcedCustomFenceGate(Block block, TextureMapping textureMapping) {
		MultiVariant openModel = BlockModelGenerators.plainVariant(SCModelTemplates.CUSTOM_REINFORCED_FENCE_GATE_OPEN.create(block, textureMapping, modelOutput));
		MultiVariant closedModel = BlockModelGenerators.plainVariant(SCModelTemplates.CUSTOM_REINFORCED_FENCE_GATE_CLOSED.create(block, textureMapping, modelOutput));
		MultiVariant wallOpenModel = BlockModelGenerators.plainVariant(SCModelTemplates.CUSTOM_REINFORCED_FENCE_GATE_WALL_OPEN.create(block, textureMapping, modelOutput));
		MultiVariant wallClosedModel = BlockModelGenerators.plainVariant(SCModelTemplates.CUSTOM_REINFORCED_FENCE_GATE_WALL_CLOSED.create(block, textureMapping, modelOutput));

		generate(block, BlockModelGenerators.createFenceGate(block, openModel, closedModel, wallOpenModel, wallClosedModel, false));
		registerReinforcedItemModel(block);
	}

	public static void createReinforcedFenceGate(Block block, TextureMapping textureMapping) {
		MultiVariant openModel = BlockModelGenerators.plainVariant(SCModelTemplates.REINFORCED_FENCE_GATE_OPEN.create(block, textureMapping, modelOutput));
		MultiVariant closedModel = BlockModelGenerators.plainVariant(SCModelTemplates.REINFORCED_FENCE_GATE_CLOSED.create(block, textureMapping, modelOutput));
		MultiVariant wallOpenModel = BlockModelGenerators.plainVariant(SCModelTemplates.REINFORCED_FENCE_GATE_WALL_OPEN.create(block, textureMapping, modelOutput));
		MultiVariant wallClosedModel = BlockModelGenerators.plainVariant(SCModelTemplates.REINFORCED_FENCE_GATE_WALL_CLOSED.create(block, textureMapping, modelOutput));

		generate(block, BlockModelGenerators.createFenceGate(block, openModel, closedModel, wallOpenModel, wallClosedModel, true));
		registerReinforcedItemModel(block);
	}

	public static void createReinforcedGrassBlock() {
		Block block = SCContent.REINFORCED_GRASS_BLOCK.get();
		Identifier model = ModelLocationUtils.getModelLocation(block);
		MultiVariant nonSnowyModel = BlockModelGenerators.createRotatedVariants(BlockModelGenerators.plainModel(model));
		MultiVariant snowyModel = BlockModelGenerators.plainVariant(model.withSuffix("_snow"));

		blockModelGenerators.createGrassLikeBlock(block, nonSnowyModel, snowyModel);
		//@formatter:off
		itemInfo.accept(block.asItem(),
				ItemModelUtils.tintedModel(model,
						new ReinforcedTint(),
						new ReinforcedTint(new GrassColorSource())));
		//@formatter:on
		generatedBlocks.add(block);
	}

	public static void createReinforcedLightningRod(DeferredBlock<? extends Block> dBlock) {
		Block block = dBlock.get();
		MultiVariant powered = BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(SCContent.REINFORCED_LIGHTNING_ROD.unaffected().get(), "_on"));
		MultiVariant unpowered = BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(block));
		//@formatter:off
		blockStateOutput.accept(
				MultiVariantGenerator.dispatch(block)
						.with(BlockModelGenerators.createBooleanModelDispatch(BlockStateProperties.POWERED, powered, unpowered))
						.with(BlockModelGenerators.ROTATIONS_COLUMN_WITH_FACING));
		//@formatter:on
	}

	public static void createReinforcedPistons() {
		Block normal = SCContent.REINFORCED_PISTON.get();
		Block sticky = SCContent.REINFORCED_STICKY_PISTON.get();

		registerReinforcedItemModel(normal, ModelLocationUtils.getModelLocation(normal, "_inventory"));
		registerReinforcedItemModel(sticky, ModelLocationUtils.getModelLocation(sticky, "_inventory"));
	}

	public static void createReinforcedPressurePlate(Block block, TextureMapping textureMapping) {
		MultiVariant upModel = BlockModelGenerators.plainVariant(SCModelTemplates.REINFORCED_PRESSURE_PLATE_UP.create(block, textureMapping, modelOutput));
		MultiVariant downModel = BlockModelGenerators.plainVariant(SCModelTemplates.REINFORCED_PRESSURE_PLATE_DOWN.create(block, textureMapping, modelOutput));

		generate(block, BlockModelGenerators.createPressurePlate(block, upModel, downModel));
		registerReinforcedItemModel(block);
	}

	public static void createReinforcedShelf(Block block, Block vanilla, Block particles) {
		TextureMapping textures = new TextureMapping().put(TextureSlot.ALL, TextureMapping.getBlockTexture(vanilla)).put(TextureSlot.PARTICLE, TextureMapping.getBlockTexture(particles));
		MultiPartGenerator generator = MultiPartGenerator.multiPart(block);

		addShelfPart(block, textures, generator, SCModelTemplates.REINFORCED_SHELF_BODY, null, null);
		addShelfPart(block, textures, generator, SCModelTemplates.REINFORCED_SHELF_UNPOWERED, false, null);
		addShelfPart(block, textures, generator, SCModelTemplates.REINFORCED_SHELF_UNCONNECTED, true, SideChainPart.UNCONNECTED);
		addShelfPart(block, textures, generator, SCModelTemplates.REINFORCED_SHELF_LEFT, true, SideChainPart.LEFT);
		addShelfPart(block, textures, generator, SCModelTemplates.REINFORCED_SHELF_CENTER, true, SideChainPart.CENTER);
		addShelfPart(block, textures, generator, SCModelTemplates.REINFORCED_SHELF_RIGHT, true, SideChainPart.RIGHT);
		generate(block, generator);
		registerReinforcedItemModel(block, SCModelTemplates.REINFORCED_SHELF_INVENTORY.create(block, textures, modelOutput));
	}

	public static void addShelfPart(Block block, TextureMapping textures, MultiPartGenerator multiPartGenerator, ModelTemplate template, Boolean powered, SideChainPart sideChainPart) {
		MultiVariant model = BlockModelGenerators.plainVariant(template.create(block, textures, modelOutput));

		BlockModelGenerators.forEachHorizontalDirection((direction, variant) -> multiPartGenerator.with(BlockModelGenerators.shelfCondition(direction, powered, sideChainPart), model.with(variant)));
	}

	public static void createReinforcedSlab(Block block, String doubleSlabModel, String texture) {
		Identifier textureLocation = mcBlock(texture);

		generateReinforcedSlab(block, modBlock(doubleSlabModel), textureLocation, textureLocation, textureLocation, ReinforcedTint.DEFAULT_BASE);
	}

	public static void createReinforcedSlab(Block block, String doubleSlabModel, String side, String end) {
		createReinforcedSlab(block, doubleSlabModel, side, end, ReinforcedTint.DEFAULT_BASE);
	}

	public static void createReinforcedSlab(Block block, String doubleSlabModel, String side, String end, ItemTintSource baseTint) {
		Identifier endTextureLocation = mcBlock(end);

		generateReinforcedSlab(block, modBlock(doubleSlabModel), mcBlock(side), endTextureLocation, endTextureLocation, baseTint);
	}

	public static void createTintedSlab(Block block, String doubleSlabModel, String side, String end, ItemTintSource tint) {
		Identifier endTextureLocation = mcBlock(end);

		generateTintedSlab(block, modBlock(doubleSlabModel), mcBlock(side), endTextureLocation, endTextureLocation, tint);
	}

	public static void createReinforcedSlab(Block block, String doubleSlabModel, String side, String bottom, String top) {
		generateReinforcedSlab(block, modBlock(doubleSlabModel), mcBlock(side), mcBlock(bottom), mcBlock(top), ReinforcedTint.DEFAULT_BASE);
	}

	public static void generateReinforcedSlab(Block block, MultiVariant doubleSlab, Identifier side, Identifier bottom, Identifier top, ItemTintSource baseTint) {
		createTintedSlab(block, doubleSlab, side, bottom, top);
		registerReinforcedItemModel(block, baseTint);
	}

	public static void generateTintedSlab(Block block, MultiVariant doubleSlab, Identifier side, Identifier bottom, Identifier top, ItemTintSource tint) {
		createTintedSlab(block, doubleSlab, side, bottom, top);
		itemInfo.accept(block.asItem(), ItemModelUtils.tintedModel(ModelLocationUtils.getModelLocation(block), tint));
	}

	public static void createTintedSlab(Block block, MultiVariant doubleSlab, Identifier side, Identifier bottom, Identifier top) {
		TextureMapping textureMapping = new TextureMapping().put(TextureSlot.BOTTOM, new Material(bottom)).put(TextureSlot.SIDE, new Material(side)).put(TextureSlot.TOP, new Material(top));
		MultiVariant bottomModel = BlockModelGenerators.plainVariant(SCModelTemplates.REINFORCED_SLAB_BOTTOM.create(block, textureMapping, modelOutput));
		MultiVariant topModel = BlockModelGenerators.plainVariant(SCModelTemplates.REINFORCED_SLAB_TOP.create(block, textureMapping, modelOutput));

		generate(block, BlockModelGenerators.createSlab(block, bottomModel, topModel, doubleSlab));
	}

	public static void createReinforcedStairs(Block block, String texture) {
		Identifier textureLocation = mcBlock(texture);

		generateReinforcedStairs(block, textureLocation, textureLocation, textureLocation, ReinforcedTint.DEFAULT_BASE);
	}

	public static void createReinforcedStairs(Block block, String side, String end) {
		createReinforcedStairs(block, side, end, ReinforcedTint.DEFAULT_BASE);
	}

	public static void createReinforcedStairs(Block block, String side, String end, ItemTintSource baseTint) {
		Identifier textureLocationEnd = mcBlock(end);

		generateReinforcedStairs(block, mcBlock(side), textureLocationEnd, textureLocationEnd, baseTint);
	}

	public static void createTintedStairs(Block block, String side, String end, ItemTintSource tint) {
		Identifier textureLocationEnd = mcBlock(end);

		generateTintedStairs(block, mcBlock(side), textureLocationEnd, textureLocationEnd, tint);
	}

	public static void createReinforcedStairs(Block block, String side, String bottom, String top) {
		generateReinforcedStairs(block, mcBlock(side), mcBlock(bottom), mcBlock(top), ReinforcedTint.DEFAULT_BASE);
	}

	public static void generateReinforcedStairs(Block block, Identifier side, Identifier bottom, Identifier top, ItemTintSource baseTint) {
		createTintedStairs(block, side, bottom, top);
		registerReinforcedItemModel(block, baseTint);
	}

	public static void createSecurityCamera() {
		Block cam = SCContent.SECURITY_CAMERA.get();
		Identifier baseModel = ModelLocationUtils.getModelLocation(cam.asItem());

		//@formatter:off
		itemInfo.accept(cam.asItem(),
				ItemModelUtils.specialModel(baseModel,
						new SecurityCameraSpecialRenderer.Unbaked(
								SecurityCraft.resLoc("security_camera"),
								0.0F,
								Optional.empty(),
								Optional.empty())));
		//@formatter:on
	}

	public static void generateTintedStairs(Block block, Identifier side, Identifier bottom, Identifier top, ItemTintSource tint) {
		createTintedStairs(block, side, bottom, top);
		itemInfo.accept(block.asItem(), ItemModelUtils.tintedModel(ModelLocationUtils.getModelLocation(block), tint));
	}

	public static void createTintedStairs(Block block, Identifier side, Identifier bottom, Identifier top) {
		TextureMapping textureMapping = new TextureMapping().put(TextureSlot.BOTTOM, new Material(bottom)).put(TextureSlot.SIDE, new Material(side)).put(TextureSlot.TOP, new Material(top));
		MultiVariant innerModel = BlockModelGenerators.plainVariant(SCModelTemplates.REINFORCED_STAIRS_INNER.create(block, textureMapping, modelOutput));
		MultiVariant straightModel = BlockModelGenerators.plainVariant(SCModelTemplates.REINFORCED_STAIRS_STRAIGHT.create(block, textureMapping, modelOutput));
		MultiVariant outerModel = BlockModelGenerators.plainVariant(SCModelTemplates.REINFORCED_STAIRS_OUTER.create(block, textureMapping, modelOutput));

		generate(block, BlockModelGenerators.createStairs(block, innerModel, straightModel, outerModel));
	}

	public static void createReinforcedWall(Block block, TextureMapping textureMapping) {
		MultiVariant postModel = BlockModelGenerators.plainVariant(SCModelTemplates.REINFORCED_WALL_POST.create(block, textureMapping, modelOutput));
		MultiVariant lowSideModel = BlockModelGenerators.plainVariant(SCModelTemplates.REINFORCED_WALL_LOW_SIDE.create(block, textureMapping, modelOutput));
		MultiVariant tallSideModel = BlockModelGenerators.plainVariant(SCModelTemplates.REINFORCED_WALL_TALL_SIDE.create(block, textureMapping, modelOutput));
		Identifier inventoryModel = SCModelTemplates.REINFORCED_WALL_INVENTORY.create(block, textureMapping, modelOutput);

		generate(block, BlockModelGenerators.createWall(block, postModel, lowSideModel, tallSideModel));
		registerReinforcedItemModel(block, inventoryModel);
	}

	public static void createSecureRedstoneInterface() {
		Block block = SCContent.SECURE_REDSTONE_INTERFACE.get();
		MultiVariant senderModel = BlockModelGenerators.plainVariant(TextureMapping.getBlockTexture(block, "_sender").sprite());
		MultiVariant receiverModel = BlockModelGenerators.plainVariant(TextureMapping.getBlockTexture(block, "_receiver").sprite());

		//@formatter:off
		generate(block, MultiVariantGenerator.dispatch(block)
				.with(BlockModelGenerators.createBooleanModelDispatch(SecureRedstoneInterfaceBlock.SENDER, senderModel, receiverModel))
				.with(BlockModelGenerators.ROTATIONS_COLUMN_WITH_FACING));
		//@formatter:on
		blockModelGenerators.registerSimpleItemModel(block, ModelLocationUtils.getModelLocation(block, "_sender"));
	}

	public static void createGlassBlocks(DeferredBlock<? extends Block> dGlass, DeferredBlock<? extends Block> dPane) {
		Block glass = dGlass.get();
		Block pane = dPane.get();

		blockModelGenerators.createGlassBlocks(glass, pane);
		itemInfo.accept(glass.asItem(), ItemModelUtils.plainModel(ModelLocationUtils.getModelLocation(glass)));
		generatedBlocks.add(glass);
		generatedBlocks.add(pane);
	}

	public static void createSecretSign(SignItem item, Block vanillaSign, Block vanillaWallSign) {
		Block secretSign = item.getBlock();
		Block secretWallSign = item.wallBlock;
		MultiVariant standingRot0 = BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(vanillaSign, "_rot_0"));
		MultiVariant standingRot1 = BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(vanillaSign, "_rot_1"));
		MultiVariant standingRot2 = BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(vanillaSign, "_rot_2"));
		MultiVariant standingRot3 = BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(vanillaSign, "_rot_3"));
		MultiVariant wallModel = BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(vanillaWallSign));

		generate(secretSign, BlockModelGenerators.createSign(secretSign, standingRot0, standingRot1, standingRot2, standingRot3));
		generate(secretWallSign, MultiVariantGenerator.dispatch(secretWallSign, wallModel).with(BlockModelGenerators.ROTATION_HORIZONTAL_FACING_ALT));
	}

	public static void createSecretHangingSign(SignItem item, Block vanillaHangingSign, Block vanillaWallHangingSign) {
		Block secretHangingSign = item.getBlock();
		Block secretWallHangingSign = item.wallBlock;

		MultiVariant hangingRot0 = BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(vanillaHangingSign, "_rot_0"));
		MultiVariant hangingRot1 = BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(vanillaHangingSign, "_rot_1"));
		MultiVariant hangingRot2 = BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(vanillaHangingSign, "_rot_2"));
		MultiVariant hangingRot3 = BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(vanillaHangingSign, "_rot_3"));
		MultiVariant attachedRot0 = BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(vanillaHangingSign, "_attached_rot_0"));
		MultiVariant attachedRot1 = BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(vanillaHangingSign, "_attached_rot_1"));
		MultiVariant attachedRot2 = BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(vanillaHangingSign, "_attached_rot_2"));
		MultiVariant attachedRot3 = BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(vanillaHangingSign, "_attached_rot_3"));
		MultiVariant wallModel = BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(vanillaWallHangingSign));

		generate(secretHangingSign, BlockModelGenerators.createHangingSign(secretHangingSign, hangingRot0, hangingRot1, hangingRot2, hangingRot3, attachedRot0, attachedRot1, attachedRot2, attachedRot3));
		generate(secretWallHangingSign, MultiVariantGenerator.dispatch(secretWallHangingSign, wallModel).with(BlockModelGenerators.ROTATION_HORIZONTAL_FACING_ALT));
	}

	public static void createFloorTrap() {
		Block block = SCContent.FLOOR_TRAP.get();

		blockModelGenerators.createTrivialCube(block);
		itemInfo.accept(block.asItem(), ItemModelUtils.plainModel(ModelLocationUtils.getModelLocation(block)));
		generatedBlocks.add(block);
	}

	public static BlockModelDefinitionGenerator createMirroredCubeGenerator(Block cubeBlock, Variant originalModel, TextureMapping textureMapping, BiConsumer<Identifier, ModelInstance> modelOutput) {
		Variant mirroredModel = BlockModelGenerators.plainModel(SCModelTemplates.REINFORCED_CUBE_MIRRORED_ALL.create(cubeBlock, textureMapping, modelOutput));

		return MultiVariantGenerator.dispatch(cubeBlock, BlockModelGenerators.createRotatedVariants(originalModel, mirroredModel));
	}

	public static BlockModelDefinitionGenerator createNorthWestMirroredCubeGenerator(Block cubeBlock, Variant originalModel, TextureMapping textureMapping, BiConsumer<Identifier, ModelInstance> modelOutput) {
		Variant mirroredModel = BlockModelGenerators.plainModel(SCModelTemplates.REINFORCED_CUBE_NORTH_WEST_MIRRORED_ALL.create(cubeBlock, textureMapping, modelOutput));

		return BlockModelGenerators.createSimpleBlock(cubeBlock, BlockModelGenerators.variant(mirroredModel));
	}

	public static BlockModelDefinitionGenerator createMirroredColumnGenerator(Block columnBlock, Variant originalModel, TextureMapping textureMapping, BiConsumer<Identifier, ModelInstance> modelOutput) {
		Variant mirroredModel = BlockModelGenerators.plainModel(SCModelTemplates.REINFORCED_CUBE_COLUMN_MIRRORED.create(columnBlock, textureMapping, modelOutput));

		return MultiVariantGenerator.dispatch(columnBlock, BlockModelGenerators.createRotatedVariants(originalModel, mirroredModel)).with(BlockModelGenerators.createRotatedPillar());
	}

	public static BlockModelDefinitionGenerator createReinforcedPillarBlockUVLocked(Block block, TextureMapping textureMapping, BiConsumer<Identifier, ModelInstance> modelOutput) {
		MultiVariant xModel = BlockModelGenerators.plainVariant(SCModelTemplates.REINFORCED_CUBE_COLUMN_UV_LOCKED_X.create(block, textureMapping, modelOutput));
		MultiVariant yModel = BlockModelGenerators.plainVariant(SCModelTemplates.REINFORCED_CUBE_COLUMN_UV_LOCKED_Y.create(block, textureMapping, modelOutput));
		MultiVariant zModel = BlockModelGenerators.plainVariant(SCModelTemplates.REINFORCED_CUBE_COLUMN_UV_LOCKED_Z.create(block, textureMapping, modelOutput));

		//@formatter:off
		return MultiVariantGenerator.dispatch(block)
			.with(
				PropertyDispatch.initial(BlockStateProperties.AXIS)
					.select(Direction.Axis.X, xModel)
					.select(Direction.Axis.Y, yModel)
					.select(Direction.Axis.Z, zModel)
			);
		//@formatter:on
	}

	public static void reinforcedFamily(BlockFamily family) {
		Block vanillaBlock = family.getBaseBlock();
		Block reinforcedBlock = IReinforcedBlock.VANILLA_TO_SECURITYCRAFT.get(vanillaBlock);

		if (reinforcedBlock == null)
			throw new IllegalStateException("Couldn't find reinforced block for " + Utils.getRegistryName(vanillaBlock));

		TexturedModel texturedModel = TEXTURED_MODELS.getOrDefault(vanillaBlock, SCTexturedModels.REINFORCED_CUBE.get(vanillaBlock));

		REINFORCED_FAMILIES.add(new ReinforcedBlockFamilyProvider(family, reinforcedBlock, texturedModel));
	}

	public static void generate(Block block, BlockModelDefinitionGenerator generator) {
		blockStateOutput.accept(generator);
		generatedBlocks.add(block);
	}

	public static <T extends Block & IReinforcedBlock> void registerReinforcedFlatItemModelFromBlock(T block) {
		registerReinforcedItemModel(block, blockModelGenerators.createFlatItemModelWithBlockTexture(block.asItem(), block.getVanillaBlock()));
	}

	public static <T extends Block & IReinforcedBlock> void registerReinforcedFlatItemModel(DeferredBlock<T> block) {
		registerReinforcedFlatItemModel(block.get());
	}

	public static <T extends Block & IReinforcedBlock> void registerReinforcedFlatItemModel(T block) {
		Item item = block.asItem();
		Identifier modelLocation = ModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation(item), TextureMapping.layer0(block.getVanillaBlock().asItem()), modelOutput);

		registerReinforcedItemModel(block, modelLocation);
	}

	public static void registerFlatItemModel(Block block) {
		blockModelGenerators.registerSimpleFlatItemModel(block);
		generatedBlocks.add(block);
	}

	public static void registerReinforcedItemModel(Block block) {
		registerReinforcedItemModel(block, ModelLocationUtils.getModelLocation(block));
	}

	public static void registerReinforcedItemModel(Block block, String suffix) {
		registerReinforcedItemModel(block, ModelLocationUtils.getModelLocation(block, suffix));
	}

	public static void registerReinforcedItemModel(Block block, Identifier model) {
		registerReinforcedItemModel(block, model, ReinforcedTint.DEFAULT_BASE);
	}

	public static void registerReinforcedItemModel(Block block, ItemTintSource base) {
		registerReinforcedItemModel(block, ModelLocationUtils.getModelLocation(block), base);
	}

	public static void registerReinforcedItemModel(Block block, String suffix, ItemTintSource base) {
		registerReinforcedItemModel(block, ModelLocationUtils.getModelLocation(block, suffix), base);
	}

	public static void registerReinforcedItemModel(Block block, Identifier model, ItemTintSource base) {
		itemInfo.accept(block.asItem(), ItemModelUtils.tintedModel(model, new ReinforcedTint(base)));
		generatedBlocks.add(block);
	}

	public static void registerSimpleItemModel(Block block) {
		registerSimpleItemModel(block, ModelLocationUtils.getModelLocation(block));
	}

	public static void registerSimpleItemModelFromItem(Block block) {
		registerSimpleItemModel(block, ModelLocationUtils.getModelLocation(block.asItem()));
	}

	public static void registerSimpleItemModel(Block block, String suffix) {
		registerSimpleItemModel(block, ModelLocationUtils.getModelLocation(block, suffix));
	}

	public static void registerSimpleItemModel(Block block, Identifier model) {
		itemInfo.accept(block.asItem(), ItemModelUtils.plainModel(model));
		generatedBlocks.add(block);
	}

	public static void registerTintedItemModel(Block block, ItemTintSource tint) {
		registerTintedItemModel(block, ModelLocationUtils.getModelLocation(block), tint);
	}

	public static void registerTintedItemModel(Block block, Identifier model, ItemTintSource tint) {
		itemInfo.accept(block.asItem(), ItemModelUtils.tintedModel(model, tint));
		generatedBlocks.add(block);
	}

	public static Identifier mcBlock(String path) {
		return ModelLocationUtils.decorateBlockModelLocation(SecurityCraft.mcResLoc(path).toString());
	}

	public static MultiVariant modBlock(String path) {
		return BlockModelGenerators.plainVariant(ModelLocationUtils.decorateBlockModelLocation(SecurityCraft.resLoc(path).toString()));
	}

	private static String name(Block block) {
		return Utils.getRegistryName(block).getPath();
	}
}
