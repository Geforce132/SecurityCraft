package net.geforcemods.securitycraft.datagen;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedStainedGlassBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedStainedGlassPaneBlock;
import net.geforcemods.securitycraft.util.Reinforced;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.PaneBlock;
import net.minecraft.block.WallBlock;
import net.minecraft.block.WallHeight;
import net.minecraft.data.DataGenerator;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.ModelFile.UncheckedModelFile;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.client.model.generators.MultiPartBlockStateBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.fml.RegistryObject;

public class BlockModelAndStateGenerator extends BlockStateProvider
{
	private static final Map<Direction,EnumProperty<WallHeight>> DIR_TO_WALL_HEIGHT = ImmutableMap.of(
			Direction.EAST, BlockStateProperties.WALL_HEIGHT_EAST,
			Direction.NORTH, BlockStateProperties.WALL_HEIGHT_NORTH,
			Direction.SOUTH, BlockStateProperties.WALL_HEIGHT_SOUTH,
			Direction.WEST, BlockStateProperties.WALL_HEIGHT_WEST);
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
						reinforcedPaneBlock((PaneBlock)block);
				}
			}
			catch(IllegalArgumentException | IllegalAccessException e)
			{
				e.printStackTrace();
			}
		}

		simpleBlock(SCContent.REINFORCED_GLASS.get());
		reinforcedPaneBlock((PaneBlock)SCContent.REINFORCED_GLASS_PANE.get());

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
		reinforcedWallBlock(SCContent.REINFORCED_BLACKSTONE_WALL.get());
		reinforcedWallBlock(SCContent.REINFORCED_POLISHED_BLACKSTONE_WALL.get());
		reinforcedWallBlock(SCContent.REINFORCED_POLISHED_BLACKSTONE_BRICK_WALL.get(), "polished_blackstone_bricks");

		blockMine(Blocks.ANCIENT_DEBRIS, SCContent.ANCIENT_DEBRIS_MINE.get());
		blockMine(Blocks.COAL_ORE, SCContent.COAL_ORE_MINE.get());
		blockMine(Blocks.COBBLESTONE, SCContent.COBBLESTONE_MINE.get());
		blockMine(Blocks.DIAMOND_ORE, SCContent.DIAMOND_ORE_MINE.get());
		blockMine(Blocks.DIRT, SCContent.DIRT_MINE.get());
		blockMine(Blocks.EMERALD_ORE, SCContent.EMERALD_ORE_MINE.get());
		horizontalBlock(SCContent.FURNACE_MINE.get(), new ResourceLocation(ModelProvider.BLOCK_FOLDER + "/furnace_side"), new ResourceLocation(ModelProvider.BLOCK_FOLDER + "/furnace_front"), new ResourceLocation(ModelProvider.BLOCK_FOLDER + "/furnace_top"));
		blockMine(Blocks.GRAVEL, SCContent.GRAVEL_MINE.get());
		blockMine(Blocks.GOLD_ORE, SCContent.GOLD_ORE_MINE.get());
		blockMine(Blocks.GILDED_BLACKSTONE, SCContent.GILDED_BLACKSTONE_MINE.get());
		blockMine(Blocks.IRON_ORE, SCContent.IRON_ORE_MINE.get());
		blockMine(Blocks.LAPIS_ORE, SCContent.LAPIS_ORE_MINE.get());
		blockMine(Blocks.NETHER_GOLD_ORE, SCContent.NETHER_GOLD_ORE_MINE.get());
		blockMine(Blocks.NETHER_QUARTZ_ORE, SCContent.QUARTZ_ORE_MINE.get());
		blockMine(Blocks.REDSTONE_ORE, SCContent.REDSTONE_ORE_MINE.get());
		blockMine(Blocks.SAND, SCContent.SAND_MINE.get());
		blockMine(Blocks.STONE, SCContent.STONE_MINE.get());
	}

	public void blockMine(Block vanillaBlock, Block block)
	{
		getVariantBuilder(block).forAllStates(state -> new ConfiguredModel[] {new ConfiguredModel(new UncheckedModelFile(mcLoc(ModelProvider.BLOCK_FOLDER + "/" + vanillaBlock.getRegistryName().getPath())))});
	}

	public void fourWayWallHeight(MultiPartBlockStateBuilder builder, ModelFile model, WallHeight height)
	{
		Arrays.stream(Direction.values()).filter(dir -> dir.getAxis().isHorizontal()).forEach(dir -> {
			builder.part().modelFile(model).rotationY((((int) dir.getHorizontalAngle()) + 180) % 360).uvLock(true).addModel()
			.condition(DIR_TO_WALL_HEIGHT.get(dir), height);
		});
	}

	public void reinforcedPaneBlock(PaneBlock block)
	{
		String name = name(block);
		ResourceLocation noPane = modLoc(ModelProvider.BLOCK_FOLDER + "/" + name.replace("_pane", ""));

		paneBlock(block, noPane, modLoc(ModelProvider.BLOCK_FOLDER + "/" + name + "_top"));
		itemModels().getBuilder(name).parent(new UncheckedModelFile("item/generated")).texture("layer0", noPane);
	}

	public void reinforcedWallBlock(Block block)
	{
		reinforcedWallBlock(block, block.getRegistryName().getPath().replace("reinforced_", "").replace("_wall", ""));
	}

	public void reinforcedWallBlock(Block block, String textureName)
	{
		ResourceLocation texture = new ResourceLocation("block/" + textureName);
		String baseName = block.getRegistryName().toString();
		ModelFile post = models().reinforcedWallPost(baseName + "_post", texture);
		ModelFile side = models().reinforcedWallSide(baseName + "_side", texture, false);
		ModelFile sideTall = models().reinforcedWallSide(baseName + "_side_tall", texture, true);
		MultiPartBlockStateBuilder builder = getMultipartBuilder(block)
				.part().modelFile(post).addModel()
				.condition(WallBlock.UP, true).end();

		fourWayWallHeight(builder, side, WallHeight.LOW);
		fourWayWallHeight(builder, sideTall, WallHeight.TALL);
		models().reinforcedWallInventory(baseName + "_inventory", texture);
	}

	@Override
	public void simpleBlock(Block block)
	{
		String name = name(block);

		super.simpleBlock(block);
		itemModels().withExistingParent(name, modLoc(ModelProvider.BLOCK_FOLDER + "/" + name));
	}

	@Override
	public String getName()
	{
		return "SecurityCraft Block States/Models";
	}

	private String name(Block block)
	{
		return block.getRegistryName().getPath();
	}

	@Override
	public SCBlockModelProvider models()
	{
		return scModels;
	}
}
