package net.geforcemods.securitycraft.datagen;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile.UncheckedModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ItemModelGenerator extends ItemModelProvider
{
	public ItemModelGenerator(DataGenerator generator, ExistingFileHelper existingFileHelper)
	{
		super(generator, SecurityCraft.MODID, existingFileHelper);
	}

	@Override
	protected void registerModels()
	{
		reinforcedWallInventory(SCContent.REINFORCED_COBBLESTONE_WALL.get(), Blocks.COBBLESTONE_WALL);
		reinforcedWallInventory(SCContent.REINFORCED_MOSSY_COBBLESTONE_WALL.get(), Blocks.MOSSY_COBBLESTONE_WALL);
		reinforcedWallInventory(SCContent.REINFORCED_BRICK_WALL.get(), "bricks");
		reinforcedWallInventory(SCContent.REINFORCED_PRISMARINE_WALL.get(), Blocks.PRISMARINE_WALL);
		reinforcedWallInventory(SCContent.REINFORCED_RED_SANDSTONE_WALL.get(), Blocks.RED_SANDSTONE_WALL);
		reinforcedWallInventory(SCContent.REINFORCED_MOSSY_STONE_BRICK_WALL.get(), "mossy_stone_bricks");
		reinforcedWallInventory(SCContent.REINFORCED_GRANITE_WALL.get(), Blocks.GRANITE_WALL);
		reinforcedWallInventory(SCContent.REINFORCED_STONE_BRICK_WALL.get(), "stone_bricks");
		reinforcedWallInventory(SCContent.REINFORCED_NETHER_BRICK_WALL.get(), "nether_bricks");
		reinforcedWallInventory(SCContent.REINFORCED_ANDESITE_WALL.get(), Blocks.ANDESITE_WALL);
		reinforcedWallInventory(SCContent.REINFORCED_RED_NETHER_BRICK_WALL.get(), "red_nether_bricks");
		reinforcedWallInventory(SCContent.REINFORCED_SANDSTONE_WALL.get(), Blocks.SANDSTONE_WALL);
		reinforcedWallInventory(SCContent.REINFORCED_END_STONE_BRICK_WALL.get(), "end_stone_bricks");
		reinforcedWallInventory(SCContent.REINFORCED_DIORITE_WALL.get(), Blocks.DIORITE_WALL);
		reinforcedWallInventory(SCContent.REINFORCED_BLACKSTONE_WALL.get(), Blocks.BLACKSTONE_WALL);
		reinforcedWallInventory(SCContent.REINFORCED_POLISHED_BLACKSTONE_WALL.get(), Blocks.POLISHED_BLACKSTONE_WALL);
		reinforcedWallInventory(SCContent.REINFORCED_POLISHED_BLACKSTONE_BRICK_WALL.get(), "polished_blackstone_bricks");

		//gui block mine model
		getBuilder("template_block_mine")
		.parent(new UncheckedModelFile(BLOCK_FOLDER + "/block"))
		.texture("overlay", modLoc(ITEM_FOLDER + "/block_mine_overlay"))
		.texture("particle", "#north")
		//normal block
		.element().from(0, 0, 0).to(16, 16, 16).allFaces((dir, builder) -> builder.cullface(dir).texture("#" + dir.getName2()).end()).end()
		//overlay
		.element().from(0, 0, 0).to(16, 16, 16).face(Direction.UP).cullface(Direction.UP).texture("#overlay").end().end();

		blockMine(Blocks.ANCIENT_DEBRIS, SCContent.ANCIENT_DEBRIS_MINE.get(), mcLoc(BLOCK_FOLDER + "/ancient_debris_side"), mcLoc(BLOCK_FOLDER + "/ancient_debris_side"), mcLoc(BLOCK_FOLDER + "/ancient_debris_top"));
		blockMine(Blocks.COAL_ORE, SCContent.COAL_ORE_MINE.get());
		blockMine(Blocks.COBBLESTONE, SCContent.COBBLESTONE_MINE.get());
		blockMine(Blocks.DIAMOND_ORE, SCContent.DIAMOND_ORE_MINE.get());
		blockMine(Blocks.DIRT, SCContent.DIRT_MINE.get());
		blockMine(Blocks.EMERALD_ORE, SCContent.EMERALD_ORE_MINE.get());
		blockMine(Blocks.FURNACE, SCContent.FURNACE_MINE.get(), mcLoc(BLOCK_FOLDER + "/furnace_side"), mcLoc(BLOCK_FOLDER + "/furnace_front"), mcLoc(BLOCK_FOLDER + "/furnace_top"));
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

	public ItemModelBuilder reinforcedWallInventory(Block block, Block vanillaBlock)
	{
		return reinforcedWallInventory(block, vanillaBlock.getRegistryName().getPath().replace("reinforced_", "").replace("_wall", ""));
	}

	public ItemModelBuilder reinforcedWallInventory(Block block, String textureName)
	{
		return uncheckedSingleTexture(block.getRegistryName().toString(), modLoc(BLOCK_FOLDER + "/reinforced_wall_inventory"), "wall", new ResourceLocation("block/" + textureName));
	}

	public ItemModelBuilder uncheckedSingleTexture(String name, ResourceLocation parent, String textureKey, ResourceLocation texture)
	{
		return parent(name, parent).texture(textureKey, texture);
	}

	public ItemModelBuilder blockMine(Block vanillaBlock, Block block)
	{
		ResourceLocation texture = mcLoc(BLOCK_FOLDER + "/" + vanillaBlock.getRegistryName().getPath());

		return blockMine(vanillaBlock, block, texture, texture, texture);
	}

	public ItemModelBuilder blockMine(Block vanillaBlock, Block block, ResourceLocation sideTexture, ResourceLocation frontTexture, ResourceLocation bottomTopTexture)
	{
		return parent(block.getRegistryName().toString(), modLoc(ITEM_FOLDER + "/template_block_mine"))
				.texture("down", bottomTopTexture)
				.texture("up", bottomTopTexture)
				.texture("north", frontTexture)
				.texture("east", sideTexture)
				.texture("south", sideTexture)
				.texture("west", sideTexture);
	}

	public ItemModelBuilder simpleParent(Block block)
	{
		return simpleParent(block.getRegistryName());
	}

	public ItemModelBuilder simpleParent(ResourceLocation name)
	{
		return parent(name.toString(), modLoc(BLOCK_FOLDER + "/" + name.getPath()));
	}

	public ItemModelBuilder parent(String name, ResourceLocation parent)
	{
		return getBuilder(name).parent(new UncheckedModelFile(parent));
	}

	@Override
	public String getName()
	{
		return "SecurityCraft Item Models";
	}
}
