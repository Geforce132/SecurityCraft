package net.geforcemods.securitycraft.datagen;

import java.lang.reflect.Field;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blocks.mines.RedstoneOreMineBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedPaneBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedStainedGlassBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedStainedGlassPaneBlock;
import net.geforcemods.securitycraft.util.Reinforced;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.SixWayBlock;
import net.minecraft.block.WallBlock;
import net.minecraft.data.DataGenerator;
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
				}
			}
			catch(IllegalArgumentException | IllegalAccessException e)
			{
				e.printStackTrace();
			}
		}

		simpleBlock(SCContent.REINFORCED_GLASS.get());
		reinforcedPaneBlock((ReinforcedPaneBlock)SCContent.REINFORCED_GLASS_PANE.get());

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

	public void reinforcedPaneBlock(ReinforcedPaneBlock block)
	{
		String name = name(block);
		ResourceLocation noPane = modLoc(ModelProvider.BLOCK_FOLDER + "/" + name.replace("_pane", ""));

		reinforcedPaneBlock(block, block.getRegistryName().toString(), noPane, modLoc(ModelProvider.BLOCK_FOLDER + "/" + name + "_top"));
		itemModels().getBuilder(name).parent(new UncheckedModelFile("item/generated")).texture("layer0", noPane);
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
		reinforcedWallBlock(block, block.getRegistryName().getPath().replace("reinforced_", "").replace("_wall", ""));
	}

	public void reinforcedWallBlock(Block block, String textureName)
	{
		ResourceLocation texture = new ResourceLocation("block/" + textureName);
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
