package net.geforcemods.securitycraft.datagen;

import java.lang.reflect.Field;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blocks.mines.RedstoneOreMineBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedPaneBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedStainedGlassBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedStainedGlassPaneBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedStairsBlock;
import net.geforcemods.securitycraft.util.Reinforced;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.SixWayBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.WallBlock;
import net.minecraft.data.DataGenerator;
import net.minecraft.state.properties.Half;
import net.minecraft.state.properties.StairsShape;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.ModelFile.UncheckedModelFile;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.client.model.generators.MultiPartBlockStateBuilder;
import net.minecraftforge.client.model.generators.VariantBlockStateBuilder;
import net.minecraftforge.fml.RegistryObject;

public class BlockModelAndStateGenerator extends BlockStateProvider
{
	private final SCBlockModelProvider scModels;

	public BlockModelAndStateGenerator(DataGenerator gen, ExistingFileHelper exFileHelper)
	{
		super(gen, SecurityCraft.MODID, exFileHelper);

		scModels = new SCBlockModelProvider(gen, exFileHelper);
	}

	@Override
	protected void registerStatesAndModels()
	{
		for(Field field : SCContent.class.getFields())
		{
			try
			{
				if(field.isAnnotationPresent(Reinforced.class))
				{
					RegistryObject<Block> obj = ((RegistryObject<Block>)field.get(null));
					Block block = obj.get();

					if(block instanceof ReinforcedStainedGlassBlock)
						simpleBlock(block);
					else if(block instanceof ReinforcedStainedGlassPaneBlock)
						reinforcedPaneBlock((ReinforcedPaneBlock)block);
					else if(block instanceof ReinforcedStairsBlock)
						reinforcedStairsBlock(block);
				}
			}
			catch(IllegalArgumentException | IllegalAccessException e)
			{
				e.printStackTrace();
			}
		}

		simpleBlock(SCContent.REINFORCED_GLASS.get());
		reinforcedPaneBlock((ReinforcedPaneBlock)SCContent.REINFORCED_GLASS_PANE.get());

		reinforcedStairsBlock(SCContent.REINFORCED_PURPUR_STAIRS.get(), "purpur_block");
		reinforcedStairsBlock(SCContent.REINFORCED_OAK_STAIRS.get(), "oak_planks");
		reinforcedStairsBlock(SCContent.REINFORCED_BRICK_STAIRS.get(), "bricks");
		reinforcedStairsBlock(SCContent.REINFORCED_STONE_BRICK_STAIRS.get(), "stone_bricks");
		reinforcedStairsBlock(SCContent.REINFORCED_NETHER_BRICK_STAIRS.get(), "nether_bricks");
		reinforcedStairsBlock(SCContent.REINFORCED_SANDSTONE_STAIRS.get(), "sandstone", "sandstone_bottom", "sandstone_top");
		reinforcedStairsBlock(SCContent.REINFORCED_SPRUCE_STAIRS.get(), "spruce_planks");
		reinforcedStairsBlock(SCContent.REINFORCED_BIRCH_STAIRS.get(), "birch_planks");
		reinforcedStairsBlock(SCContent.REINFORCED_JUNGLE_STAIRS.get(), "jungle_planks");
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
		reinforcedStairsBlock(SCContent.REINFORCED_CRYSTAL_QUARTZ_STAIRS.get(), "quartz_block_side", "quartz_block_top");
		reinforcedStairsBlock(SCContent.STAIRS_CRYSTAL_QUARTZ.get(), "quartz_block_side", "quartz_block_top");

		reinforcedWallBlock(SCContent.REINFORCED_COBBLESTONE_WALL.get());
		reinforcedWallBlock(SCContent.REINFORCED_MOSSY_COBBLESTONE_WALL.get());
		reinforcedWallBlock(SCContent.REINFORCED_BRICK_WALL.get(), "bricks");
		reinforcedWallBlock(SCContent.REINFORCED_PRISMARINE_WALL.get());
		reinforcedWallBlock(SCContent.REINFORCED_RED_SANDSTONE_WALL.get());
		reinforcedWallBlock(SCContent.REINFORCED_MOSSY_STONE_BRICK_WALL.get(), "mossy_stone_bricks");
		reinforcedWallBlock(SCContent.REINFORCED_GRANITE_WALL.get());
		reinforcedWallBlock(SCContent.REINFORCED_STONE_BRICK_WALL.get(), "stone_bricks");
		reinforcedWallBlock(SCContent.REINFORCED_NETHER_BRICK_WALL.get(), "nether_bricks");
		reinforcedWallBlock(SCContent.REINFORCED_ANDESITE_WALL.get());
		reinforcedWallBlock(SCContent.REINFORCED_RED_NETHER_BRICK_WALL.get(), "red_nether_bricks");
		reinforcedWallBlock(SCContent.REINFORCED_SANDSTONE_WALL.get());
		reinforcedWallBlock(SCContent.REINFORCED_END_STONE_BRICK_WALL.get(), "end_stone_bricks");
		reinforcedWallBlock(SCContent.REINFORCED_DIORITE_WALL.get());

		blockMine(Blocks.COAL_ORE, SCContent.COAL_ORE_MINE.get());
		blockMine(Blocks.COBBLESTONE, SCContent.COBBLESTONE_MINE.get());
		blockMine(Blocks.DIAMOND_ORE, SCContent.DIAMOND_ORE_MINE.get());
		blockMine(Blocks.DIRT, SCContent.DIRT_MINE.get());
		blockMine(Blocks.EMERALD_ORE, SCContent.EMERALD_ORE_MINE.get());
		horizontalBlock(SCContent.FURNACE_MINE.get(), new ResourceLocation(ModelProvider.BLOCK_FOLDER + "/furnace_side"), new ResourceLocation(ModelProvider.BLOCK_FOLDER + "/furnace_front"), new ResourceLocation(ModelProvider.BLOCK_FOLDER + "/furnace_top"));
		blockMine(Blocks.GRAVEL, SCContent.GRAVEL_MINE.get());
		blockMine(Blocks.GOLD_ORE, SCContent.GOLD_ORE_MINE.get());
		blockMine(Blocks.IRON_ORE, SCContent.IRON_ORE_MINE.get());
		blockMine(Blocks.LAPIS_ORE, SCContent.LAPIS_ORE_MINE.get());
		blockMine(Blocks.NETHER_QUARTZ_ORE, SCContent.QUARTZ_ORE_MINE.get());
		getVariantBuilder(SCContent.REDSTONE_ORE_MINE.get()).forAllStates(state -> {
			if(state.get(RedstoneOreMineBlock.LIT))
				return new ConfiguredModel[] {new ConfiguredModel(models().getExistingFile(mcLoc(ModelProvider.BLOCK_FOLDER + "/redstone_ore_on")))};
			else
				return new ConfiguredModel[] {new ConfiguredModel(models().getExistingFile(mcLoc(ModelProvider.BLOCK_FOLDER + "/redstone_ore")))};
		});
		blockMine(Blocks.SAND, SCContent.SAND_MINE.get());
		blockMine(Blocks.STONE, SCContent.STONE_MINE.get());
	}

	public void blockMine(Block vanillaBlock, Block block)
	{
		getVariantBuilder(block).forAllStates(state -> new ConfiguredModel[] {new ConfiguredModel(new UncheckedModelFile(mcLoc(ModelProvider.BLOCK_FOLDER + "/" + vanillaBlock.getRegistryName().getPath())))});
	}

	public void reinforcedStairsBlock(Block block)
	{
		reinforcedStairsBlock(block, name(block).replace("reinforced_", "").replace("_stairs", ""));
	}

	public void reinforcedStairsBlock(Block block, String textureName)
	{
		ResourceLocation textureLocation = mcLoc(ModelProvider.BLOCK_FOLDER + "/" + textureName);

		reinforcedStairsBlock(block, block.getRegistryName().toString(), textureLocation, textureLocation, textureLocation);
	}

	public void reinforcedStairsBlock(Block block, String side, String end)
	{
		ResourceLocation textureLocationEnd = mcLoc(ModelProvider.BLOCK_FOLDER + "/" + end);

		reinforcedStairsBlock(block, block.getRegistryName().toString(), mcLoc(ModelProvider.BLOCK_FOLDER + "/" + side), textureLocationEnd, textureLocationEnd);
	}

	public void reinforcedStairsBlock(Block block, String side, String bottom, String top)
	{
		reinforcedStairsBlock(block, block.getRegistryName().toString(), mcLoc(ModelProvider.BLOCK_FOLDER + "/" + side), mcLoc(ModelProvider.BLOCK_FOLDER + "/" + bottom), mcLoc(ModelProvider.BLOCK_FOLDER + "/" + top));
	}

	public void reinforcedStairsBlock(Block block, String baseName, ResourceLocation side, ResourceLocation bottom, ResourceLocation top)
	{
		ModelFile stairs = models().reinforcedStairs(baseName, side, bottom, top);
		ModelFile stairsInner = models().reinforcedStairsInner(baseName + "_inner", side, bottom, top);
		ModelFile stairsOuter = models().reinforcedStairsOuter(baseName + "_outer", side, bottom, top);

		getVariantBuilder(block).forAllStatesExcept(state -> {
			Direction facing = state.get(StairsBlock.FACING);
			Half half = state.get(StairsBlock.HALF);
			StairsShape shape = state.get(StairsBlock.SHAPE);
			int yRot = (int)facing.rotateY().getHorizontalAngle();

			if(shape == StairsShape.INNER_LEFT || shape == StairsShape.OUTER_LEFT)
				yRot += 270;

			if(shape != StairsShape.STRAIGHT && half == Half.TOP)
				yRot += 90;

			yRot %= 360;

			return ConfiguredModel.builder()
					.modelFile(shape == StairsShape.STRAIGHT ? stairs : shape == StairsShape.INNER_LEFT || shape == StairsShape.INNER_RIGHT ? stairsInner : stairsOuter)
					.rotationX(half == Half.BOTTOM ? 0 : 180)
					.rotationY(yRot)
					.uvLock(yRot != 0 || half == Half.TOP)
					.build();
		}, StairsBlock.WATERLOGGED);
	}

	public void reinforcedPaneBlock(ReinforcedPaneBlock block)
	{
		String name = name(block);

		reinforcedPaneBlock(block, block.getRegistryName().toString(), modLoc(ModelProvider.BLOCK_FOLDER + "/" + name.replace("_pane", "")), modLoc(ModelProvider.BLOCK_FOLDER + "/" + name + "_top"));
	}

	public void reinforcedPaneBlock(ReinforcedPaneBlock block, String name, ResourceLocation pane, ResourceLocation edge)
	{
		ModelFile post = models().panePost(name + "_post", pane, edge);
		ModelFile side = models().paneSide(name + "_side", pane, edge);
		ModelFile sideAlt = models().paneSideAlt(name + "_side_alt", pane, edge);
		ModelFile noSide = models().paneNoSide(name + "_noside", pane);
		ModelFile noSideAlt = models().paneNoSideAlt(name + "_noside_alt", pane);
		MultiPartBlockStateBuilder builder = getMultipartBuilder(block).part().modelFile(post).addModel().end();

		SixWayBlock.FACING_TO_PROPERTY_MAP.entrySet().forEach(e -> {
			Direction dir = e.getKey();

			if(dir.getAxis().isHorizontal())
			{
				builder.part().modelFile(dir == Direction.SOUTH || dir == Direction.WEST ? sideAlt : side).rotationY(dir.getAxis() == Axis.X ? 90 : 0).addModel()
				.condition(e.getValue(), true).end()
				.part().modelFile(dir == Direction.SOUTH || dir == Direction.EAST ? noSideAlt : noSide).rotationY(dir == Direction.WEST ? 270 : dir == Direction.SOUTH ? 90 : 0).addModel()
				.condition(e.getValue(), false);
			}
		});
	}

	public void reinforcedWallBlock(Block block)
	{
		reinforcedWallBlock(block, name(block).replace("reinforced_", "").replace("_wall", ""));
	}

	public void reinforcedWallBlock(Block block, String textureName)
	{
		ResourceLocation texture = new ResourceLocation(ModelProvider.BLOCK_FOLDER + "/" + textureName);
		String baseName = block.getRegistryName().toString();
		ModelFile post = models().reinforcedWallPost(baseName + "_post", texture);
		ModelFile side = models().reinforcedWallSide(baseName + "_side", texture);
		MultiPartBlockStateBuilder builder = getMultipartBuilder(block)
				.part().modelFile(post).addModel()
				.condition(WallBlock.UP, true).end();

		fourWayMultipart(builder, side);
		models().reinforcedWallInventory(baseName + "_inventory", texture);
	}

	@Override
	public String getName()
	{
		return "SecurityCraft Block States/Models";
	}

	@Override
	public VariantBlockStateBuilder getVariantBuilder(Block b)
	{
		//because some blocks' states get set twice (once during scanning SCContent, then actually generating the proper model),
		//and the super method checking if the state has already been generated, and erroring if so, the key has to be removed so it can be remade
		if(registeredBlocks.containsKey(b))
			registeredBlocks.remove(b);

		return super.getVariantBuilder(b);
	}

	@Override
	public SCBlockModelProvider models()
	{
		return scModels;
	}

	private String name(Block block)
	{
		return block.getRegistryName().getPath();
	}
}
