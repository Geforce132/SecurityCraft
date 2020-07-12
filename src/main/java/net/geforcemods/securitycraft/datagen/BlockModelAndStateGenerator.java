package net.geforcemods.securitycraft.datagen;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.WallBlock;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.ModelFile.UncheckedModelFile;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.client.model.generators.MultiPartBlockStateBuilder;

public class BlockModelAndStateGenerator extends BlockStateProvider
{
	public BlockModelAndStateGenerator(DataGenerator gen, ExistingFileHelper exFileHelper)
	{
		super(gen, SecurityCraft.MODID, exFileHelper);
	}

	@Override
	protected void registerStatesAndModels()
	{
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
		blockMine(Blocks.REDSTONE_ORE, SCContent.REDSTONE_ORE_MINE.get());
		blockMine(Blocks.SAND, SCContent.SAND_MINE.get());
		blockMine(Blocks.STONE, SCContent.STONE_MINE.get());
	}

	public void blockMine(Block vanillaBlock, Block block)
	{
		getVariantBuilder(block).forAllStates(state -> new ConfiguredModel[] {new ConfiguredModel(new UncheckedModelFile(mcLoc(ModelProvider.BLOCK_FOLDER + "/" + vanillaBlock.getRegistryName().getPath())))});
	}

	public void furnaceMine(Block vanillaBlock, Block block)
	{
		String baseName = block.getRegistryName().toString();
		ModelFile model = models().cubeAll(baseName, blockTexture(vanillaBlock));

		getVariantBuilder(block).forAllStates(state -> new ConfiguredModel[] {new ConfiguredModel(model)});
	}

	public void reinforcedWallBlock(Block block)
	{
		reinforcedWallBlock(block, block.getRegistryName().getPath().replace("reinforced_", "").replace("_wall", ""));
	}

	public void reinforcedWallBlock(Block block, String textureName)
	{
		ResourceLocation texture = new ResourceLocation("block/" + textureName);
		String baseName = block.getRegistryName().toString();
		ModelFile post = reinforcedWallPost(baseName + "_post", texture);
		ModelFile side = reinforcedWallSide(baseName + "_side", texture);
		MultiPartBlockStateBuilder builder = getMultipartBuilder(block)
				.part().modelFile(post).addModel()
				.condition(WallBlock.UP, true).end();

		fourWayMultipart(builder, side);
		reinforcedWallInventory(baseName + "_inventory", texture);
	}

	public BlockModelBuilder reinforcedWallPost(String name, ResourceLocation wall)
	{
		return uncheckedSingleTexture(name, modLoc(ModelProvider.BLOCK_FOLDER + "/reinforced_wall_post"), "wall", wall);
	}

	public BlockModelBuilder reinforcedWallSide(String name, ResourceLocation wall)
	{
		return uncheckedSingleTexture(name, modLoc(ModelProvider.BLOCK_FOLDER + "/reinforced_wall_side"), "wall", wall);
	}

	public BlockModelBuilder reinforcedWallInventory(String name, ResourceLocation wall)
	{
		return uncheckedSingleTexture(name, modLoc(ModelProvider.BLOCK_FOLDER + "/reinforced_wall_inventory"), "wall", wall);
	}

	public BlockModelBuilder uncheckedSingleTexture(String name, ResourceLocation parent, String textureKey, ResourceLocation texture)
	{
		return models().getBuilder(name).parent(new UncheckedModelFile(parent)).texture(textureKey, texture);
	}

	@Override
	public String getName()
	{
		return "SecurityCraft Block States/Models";
	}
}
