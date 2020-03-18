package net.geforcemods.securitycraft.datagen;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.minecraft.block.Block;
import net.minecraft.block.WallBlock;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockStateProvider;
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
