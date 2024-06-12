package net.geforcemods.securitycraft.datagen;

import java.util.Arrays;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blocks.DisguisableBlock;
import net.geforcemods.securitycraft.blocks.SecureRedstoneInterfaceBlock;
import net.geforcemods.securitycraft.blocks.mines.BaseFullMineBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedCarpetBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedSlabBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedStainedGlassBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedStainedGlassPaneBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedStairsBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedWallBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.FourWayBlock;
import net.minecraft.block.PaneBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.WallBlock;
import net.minecraft.block.WallHeight;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.Item;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.Half;
import net.minecraft.state.properties.SlabType;
import net.minecraft.state.properties.StairsShape;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.ModelFile.UncheckedModelFile;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.client.model.generators.MultiPartBlockStateBuilder;
import net.minecraftforge.client.model.generators.VariantBlockStateBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.fml.RegistryObject;

public class BlockModelAndStateGenerator extends BlockStateProvider {
	//@formatter:off
	private static final Map<Direction, EnumProperty<WallHeight>> DIR_TO_WALL_HEIGHT = ImmutableMap.of(
			Direction.EAST, BlockStateProperties.EAST_WALL,
			Direction.NORTH, BlockStateProperties.NORTH_WALL,
			Direction.SOUTH, BlockStateProperties.SOUTH_WALL,
			Direction.WEST, BlockStateProperties.WEST_WALL);
	//@formatter:on
	private final SCBlockModelProvider scModels;

	public BlockModelAndStateGenerator(DataGenerator gen, ExistingFileHelper exFileHelper) {
		super(gen, SecurityCraft.MODID, exFileHelper);

		scModels = new SCBlockModelProvider(gen, exFileHelper);
	}

	@Override
	protected void registerStatesAndModels() {
		for (RegistryObject<Block> obj : SCContent.BLOCKS.getEntries()) {
			Block block = obj.get();
			Item item = block.asItem();

			if (item.getCreativeTabs().contains(SecurityCraft.DECORATION_TAB)) {
				if (block instanceof ReinforcedSlabBlock)
					reinforcedSlabBlock(block);
				else if (block instanceof ReinforcedStainedGlassBlock)
					simpleBlock(block);
				else if (block instanceof ReinforcedStainedGlassPaneBlock)
					reinforcedPaneBlock((PaneBlock) block);
				else if (block instanceof ReinforcedStairsBlock)
					reinforcedStairsBlock(block);
				else if (block instanceof ReinforcedWallBlock)
					reinforcedWallBlock(block);
				else if (block instanceof ReinforcedCarpetBlock)
					reinforcedCarpetBlock(block);
			}
			else if (item.getCreativeTabs().contains(SecurityCraft.MINE_TAB) && block instanceof BaseFullMineBlock)
				blockMine(((BaseFullMineBlock) block).getBlockDisguisedAs(), block);
		}

		blockMine(Blocks.ANCIENT_DEBRIS, SCContent.ANCIENT_DEBRIS_MINE.get());
		horizontalBlock(SCContent.FURNACE_MINE.get(), mcBlock("furnace_side"), mcBlock("furnace_front"), mcBlock("furnace_top"));
		horizontalBlock(SCContent.SMOKER_MINE.get(), mcBlock("smoker_side"), mcBlock("smoker_front"), mcBlock("smoker_top"));
		horizontalBlock(SCContent.BLAST_FURNACE_MINE.get(), mcBlock("blast_furnace_side"), mcBlock("blast_furnace_front"), mcBlock("blast_furnace_top"));
		secureRedstoneInterface();

		simpleBlock(SCContent.FLOOR_TRAP.get());
		simpleBlock(SCContent.REINFORCED_GLASS.get());
		reinforcedPaneBlock((PaneBlock) SCContent.REINFORCED_GLASS_PANE.get());

		reinforcedFenceBlock(SCContent.REINFORCED_OAK_FENCE.get(), "oak_planks");
		reinforcedFenceBlock(SCContent.REINFORCED_SPRUCE_FENCE.get(), "spruce_planks");
		reinforcedFenceBlock(SCContent.REINFORCED_BIRCH_FENCE.get(), "birch_planks");
		reinforcedFenceBlock(SCContent.REINFORCED_JUNGLE_FENCE.get(), "jungle_planks");
		reinforcedFenceBlock(SCContent.REINFORCED_ACACIA_FENCE.get(), "acacia_planks");
		reinforcedFenceBlock(SCContent.REINFORCED_DARK_OAK_FENCE.get(), "dark_oak_planks");
		//bamboo fence is handled manually
		reinforcedFenceBlock(SCContent.REINFORCED_CRIMSON_FENCE.get(), "crimson_planks");
		reinforcedFenceBlock(SCContent.REINFORCED_WARPED_FENCE.get(), "warped_planks");
		reinforcedFenceBlock(SCContent.REINFORCED_NETHER_BRICK_FENCE.get(), "nether_bricks");
		reinforcedFenceGateBlock(SCContent.REINFORCED_OAK_FENCE_GATE.get(), "oak_planks");
		reinforcedFenceGateBlock(SCContent.REINFORCED_SPRUCE_FENCE_GATE.get(), "spruce_planks");
		reinforcedFenceGateBlock(SCContent.REINFORCED_BIRCH_FENCE_GATE.get(), "birch_planks");
		reinforcedFenceGateBlock(SCContent.REINFORCED_JUNGLE_FENCE_GATE.get(), "jungle_planks");
		reinforcedFenceGateBlock(SCContent.REINFORCED_ACACIA_FENCE_GATE.get(), "acacia_planks");
		reinforcedFenceGateBlock(SCContent.REINFORCED_DARK_OAK_FENCE_GATE.get(), "dark_oak_planks");
		//bamboo fence gate is handled manually
		reinforcedFenceGateBlock(SCContent.REINFORCED_CRIMSON_FENCE_GATE.get(), "crimson_planks");
		reinforcedFenceGateBlock(SCContent.REINFORCED_WARPED_FENCE_GATE.get(), "warped_planks");

		models().reinforcedColumn("reinforced_smooth_stone_slab_double", "smooth_stone_slab_side", "smooth_stone");
		reinforcedSlabBlock(SCContent.REINFORCED_OAK_SLAB.get(), "reinforced_oak_planks", "oak_planks");
		reinforcedSlabBlock(SCContent.REINFORCED_SPRUCE_SLAB.get(), "reinforced_spruce_planks", "spruce_planks");
		reinforcedSlabBlock(SCContent.REINFORCED_BIRCH_SLAB.get(), "reinforced_birch_planks", "birch_planks");
		reinforcedSlabBlock(SCContent.REINFORCED_JUNGLE_SLAB.get(), "reinforced_jungle_planks", "jungle_planks");
		reinforcedSlabBlock(SCContent.REINFORCED_ACACIA_SLAB.get(), "reinforced_acacia_planks", "acacia_planks");
		reinforcedSlabBlock(SCContent.REINFORCED_DARK_OAK_SLAB.get(), "reinforced_dark_oak_planks", "dark_oak_planks");
		reinforcedSlabBlock(SCContent.REINFORCED_CRIMSON_SLAB.get(), "reinforced_crimson_planks", "crimson_planks");
		reinforcedSlabBlock(SCContent.REINFORCED_WARPED_SLAB.get(), "reinforced_warped_planks", "warped_planks");
		reinforcedSlabBlock(SCContent.REINFORCED_NORMAL_STONE_SLAB.get(), "reinforced_stone", "stone");
		reinforcedSlabBlock(SCContent.REINFORCED_SMOOTH_STONE_SLAB.get(), "reinforced_smooth_stone_slab_double", "smooth_stone_slab_side", "smooth_stone");
		reinforcedSlabBlock(SCContent.REINFORCED_SANDSTONE_SLAB.get(), "reinforced_sandstone", "sandstone", "sandstone_bottom", "sandstone_top");
		reinforcedSlabBlock(SCContent.REINFORCED_CUT_SANDSTONE_SLAB.get(), "reinforced_cut_sandstone", "cut_sandstone", "sandstone_top");
		reinforcedSlabBlock(SCContent.REINFORCED_BRICK_SLAB.get(), "reinforced_bricks", "bricks");
		reinforcedSlabBlock(SCContent.REINFORCED_STONE_BRICK_SLAB.get(), "reinforced_stone_bricks", "stone_bricks");
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

		reinforcedStairsBlock(SCContent.REINFORCED_PURPUR_STAIRS.get(), "purpur_block");
		reinforcedStairsBlock(SCContent.REINFORCED_OAK_STAIRS.get(), "oak_planks");
		reinforcedStairsBlock(SCContent.REINFORCED_BRICK_STAIRS.get(), "bricks");
		reinforcedStairsBlock(SCContent.REINFORCED_STONE_BRICK_STAIRS.get(), "stone_bricks");
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

		reinforcedWallBlock(SCContent.REINFORCED_BRICK_WALL.get(), "bricks");
		reinforcedWallBlock(SCContent.REINFORCED_MOSSY_STONE_BRICK_WALL.get(), "mossy_stone_bricks");
		reinforcedWallBlock(SCContent.REINFORCED_STONE_BRICK_WALL.get(), "stone_bricks");
		reinforcedWallBlock(SCContent.REINFORCED_NETHER_BRICK_WALL.get(), "nether_bricks");
		reinforcedWallBlock(SCContent.REINFORCED_RED_NETHER_BRICK_WALL.get(), "red_nether_bricks");
		reinforcedWallBlock(SCContent.REINFORCED_END_STONE_BRICK_WALL.get(), "end_stone_bricks");
		reinforcedWallBlock(SCContent.REINFORCED_POLISHED_BLACKSTONE_BRICK_WALL.get(), "polished_blackstone_bricks");
	}

	public void blockMine(Block vanillaBlock, Block block) {
		getVariantBuilder(block).forAllStates(state -> new ConfiguredModel[] {
				new ConfiguredModel(new UncheckedModelFile(mcBlock(vanillaBlock.getRegistryName().getPath())))
		});
	}

	public void fourWayWallHeight(MultiPartBlockStateBuilder builder, ModelFile model, WallHeight height) {
		//@formatter:off
		Arrays.stream(Direction.values()).filter(dir -> dir.getAxis().isHorizontal()).forEach(dir -> {
			builder.part()
			.modelFile(model)
			.rotationY((((int) dir.toYRot()) + 180) % 360)
			.uvLock(true)
			.addModel()
			.condition(DIR_TO_WALL_HEIGHT.get(dir), height);
		});
		//@formatter:on
	}

	public void reinforcedCarpetBlock(Block block) {
		String name = name(block);
		ModelFile model = models().reinforcedCarpet(name, mcBlock(name.replace("reinforced_", "").replace("carpet", "wool")));

		getVariantBuilder(block).forAllStates(state -> new ConfiguredModel[] {
				new ConfiguredModel(model)
		});
	}

	public void reinforcedFenceBlock(Block block, String textureName) {
		String baseName = block.getRegistryName().toString();
		ResourceLocation texture = mcBlock(textureName);

		fourWayBlock((FourWayBlock) block, models().fencePost(baseName + "_post", texture), models().fenceSide(baseName + "_side", texture));
		models().singleTexture(baseName + "_inventory", modBlock("reinforced_fence_inventory"), texture);
	}

	public void reinforcedFenceGateBlock(Block block, String textureName) {
		String baseName = block.getRegistryName().toString();
		ResourceLocation texture = mcBlock(textureName);
		ModelFile gate = models().fenceGate(baseName, texture);
		ModelFile gateOpen = models().fenceGateOpen(baseName + "_open", texture);
		ModelFile gateWall = models().fenceGateWall(baseName + "_wall", texture);
		ModelFile gateWallOpen = models().fenceGateWallOpen(baseName + "_wall_open", texture);

		fenceGateBlock((FenceGateBlock) block, gate, gateOpen, gateWall, gateWallOpen);
	}

	public void reinforcedPaneBlock(PaneBlock block) {
		String name = name(block);

		paneBlock(block, modBlock(name.replace("_pane", "")), modBlock(name + "_top"));
	}

	public void reinforcedSlabBlock(Block block) {
		String name = name(block).replace("_slab", "");

		reinforcedSlabBlock(block, name, name.replace("reinforced_", ""));
	}

	public void reinforcedSlabBlock(Block block, String doubleSlabModel, String texture) {
		ResourceLocation textureLocation = mcBlock(texture);

		reinforcedSlabBlock(block, name(block), modBlock(doubleSlabModel), textureLocation, textureLocation, textureLocation);
	}

	public void reinforcedSlabBlock(Block block, String doubleSlabModel, String side, String end) {
		ResourceLocation endTextureLocation = mcBlock(end);

		reinforcedSlabBlock(block, name(block), modBlock(doubleSlabModel), mcBlock(side), endTextureLocation, endTextureLocation);
	}

	public void reinforcedSlabBlock(Block block, String doubleSlabModel, String side, String bottom, String top) {
		reinforcedSlabBlock(block, name(block), modBlock(doubleSlabModel), mcBlock(side), mcBlock(bottom), mcBlock(top));
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
		ResourceLocation textureLocation = mcBlock(texture);

		reinforcedStairsBlock(block, block.getRegistryName().toString(), textureLocation, textureLocation, textureLocation);
	}

	public void reinforcedStairsBlock(Block block, String side, String end) {
		ResourceLocation textureLocationEnd = mcBlock(end);

		reinforcedStairsBlock(block, block.getRegistryName().toString(), mcBlock(side), textureLocationEnd, textureLocationEnd);
	}

	public void reinforcedStairsBlock(Block block, String side, String bottom, String top) {
		reinforcedStairsBlock(block, block.getRegistryName().toString(), mcBlock(side), mcBlock(bottom), mcBlock(top));
	}

	public void reinforcedStairsBlock(Block block, String baseName, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
		ModelFile stairs = models().reinforcedStairs(baseName, side, bottom, top);
		ModelFile stairsInner = models().reinforcedStairsInner(baseName + "_inner", side, bottom, top);
		ModelFile stairsOuter = models().reinforcedStairsOuter(baseName + "_outer", side, bottom, top);

		getVariantBuilder(block).forAllStatesExcept(state -> {
			Direction facing = state.getValue(StairsBlock.FACING);
			Half half = state.getValue(StairsBlock.HALF);
			StairsShape shape = state.getValue(StairsBlock.SHAPE);
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
		}, StairsBlock.WATERLOGGED);
	}

	public void reinforcedWallBlock(Block block) {
		reinforcedWallBlock(block, name(block).replace("reinforced_", "").replace("_wall", ""));
	}

	public void reinforcedWallBlock(Block block, String textureName) {
		ResourceLocation texture = mcBlock(textureName);
		String baseName = block.getRegistryName().toString();
		ModelFile post = models().reinforcedWallPost(baseName + "_post", texture);
		ModelFile side = models().reinforcedWallSide(baseName + "_side", texture, false);
		ModelFile sideTall = models().reinforcedWallSide(baseName + "_side_tall", texture, true);
		//@formatter:off
		MultiPartBlockStateBuilder builder = getMultipartBuilder(block)
				.part().modelFile(post).addModel()
				.condition(WallBlock.UP, true).end();
		//@formatter:on

		fourWayWallHeight(builder, side, WallHeight.LOW);
		fourWayWallHeight(builder, sideTall, WallHeight.TALL);
		models().reinforcedWallInventory(baseName + "_inventory", texture);
	}

	public void secureRedstoneInterface() {
		Block block = SCContent.SECURE_REDSTONE_INTERFACE.get();

		getVariantBuilder(block).forAllStatesExcept(state -> {
			int x = getXRotationBasedOnFacing(state);
			int y = getYRotationBasedOnFacing(state);
			String baseName = blockTexture(block).toString();

			if (state.getValue(SecureRedstoneInterfaceBlock.SENDER))
				baseName += "_sender";
			else
				baseName += "_receiver";

			return new ConfiguredModel[] {
					new ConfiguredModel(new UncheckedModelFile(baseName), x, y, false)
			};
		}, DisguisableBlock.WATERLOGGED);
	}

	public ResourceLocation mcBlock(String path) {
		return mcLoc(ModelProvider.BLOCK_FOLDER + "/" + path);
	}

	public ResourceLocation modBlock(String path) {
		return modLoc(ModelProvider.BLOCK_FOLDER + "/" + path);
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
		return block.getRegistryName().getPath();
	}

	private int getXRotationBasedOnFacing(BlockState state) {
		switch (state.getValue(BlockStateProperties.FACING)) {
			case DOWN:
				return 180;
			case UP:
				return 0;
			default:
				return 90;
		}
	}

	private int getYRotationBasedOnFacing(BlockState state) {
		switch (state.getValue(BlockStateProperties.FACING)) {
			case EAST:
				return 90;
			case SOUTH:
				return 180;
			case WEST:
				return 270;
			default:
				return 0;
		}
	}
}
