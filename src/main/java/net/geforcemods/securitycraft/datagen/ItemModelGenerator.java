package net.geforcemods.securitycraft.datagen;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile.UncheckedModelFile;

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
		reinforcedWallInventory(SCContent.REINFORCED_BRICK_WALL.get(), Blocks.BRICK_WALL, "bricks");
		reinforcedWallInventory(SCContent.REINFORCED_PRISMARINE_WALL.get(), Blocks.PRISMARINE_WALL);
		reinforcedWallInventory(SCContent.REINFORCED_RED_SANDSTONE_WALL.get(), Blocks.RED_SANDSTONE_WALL);
		reinforcedWallInventory(SCContent.REINFORCED_MOSSY_STONE_BRICK_WALL.get(), Blocks.MOSSY_STONE_BRICK_WALL, "mossy_stone_bricks");
		reinforcedWallInventory(SCContent.REINFORCED_GRANITE_WALL.get(), Blocks.GRANITE_WALL);
		reinforcedWallInventory(SCContent.REINFORCED_STONE_BRICK_WALL.get(), Blocks.STONE_BRICK_WALL, "stone_bricks");
		reinforcedWallInventory(SCContent.REINFORCED_NETHER_BRICK_WALL.get(), Blocks.NETHER_BRICK_WALL, "nether_bricks");
		reinforcedWallInventory(SCContent.REINFORCED_ANDESITE_WALL.get(), Blocks.ANDESITE_WALL);
		reinforcedWallInventory(SCContent.REINFORCED_RED_NETHER_BRICK_WALL.get(), Blocks.RED_NETHER_BRICK_WALL, "red_nether_bricks");
		reinforcedWallInventory(SCContent.REINFORCED_SANDSTONE_WALL.get(), Blocks.SANDSTONE_WALL);
		reinforcedWallInventory(SCContent.REINFORCED_END_STONE_BRICK_WALL.get(), Blocks.END_STONE_BRICK_WALL, "end_stone_bricks");
		reinforcedWallInventory(SCContent.REINFORCED_DIORITE_WALL.get(), Blocks.DIORITE_WALL);
		blockMine(Blocks.COAL_ORE, SCContent.COAL_ORE_MINE.get());
		blockMine(Blocks.COBBLESTONE, SCContent.COBBLESTONE_MINE.get());
		blockMine(Blocks.DIAMOND_ORE, SCContent.DIAMOND_ORE_MINE.get());
		blockMine(Blocks.DIRT, SCContent.DIRT_MINE.get());
		blockMine(Blocks.EMERALD_ORE, SCContent.EMERALD_ORE_MINE.get());
		blockMine(Blocks.FURNACE, SCContent.FURNACE_MINE.get());
		blockMine(Blocks.GRAVEL, SCContent.GRAVEL_MINE.get());
		blockMine(Blocks.GOLD_ORE, SCContent.GOLD_ORE_MINE.get());
		blockMine(Blocks.IRON_ORE, SCContent.IRON_ORE_MINE.get());
		blockMine(Blocks.LAPIS_ORE, SCContent.LAPIS_ORE_MINE.get());
		blockMine(Blocks.NETHER_QUARTZ_ORE, SCContent.QUARTZ_ORE_MINE.get());
		blockMine(Blocks.REDSTONE_ORE, SCContent.REDSTONE_ORE_MINE.get());
		blockMine(Blocks.SAND, SCContent.SAND_MINE.get());
		blockMine(Blocks.STONE, SCContent.STONE_MINE.get());
	}

	public ItemModelBuilder reinforcedWallInventory(Block block, Block vanillaBlock)
	{
		return reinforcedWallInventory(block, vanillaBlock, vanillaBlock.getRegistryName().getPath().replace("reinforced_", "").replace("_wall", ""));
	}

	public ItemModelBuilder reinforcedWallInventory(Block block, Block vanillaBlock, String textureName)
	{
		return uncheckedSingleTexture(block.getRegistryName().toString(), modLoc(BLOCK_FOLDER + "/reinforced_wall_inventory"), "wall", new ResourceLocation("block/" + textureName));
	}

	public ItemModelBuilder uncheckedSingleTexture(String name, ResourceLocation parent, String textureKey, ResourceLocation texture)
	{
		return parent(name, parent).texture(textureKey, texture);
	}

	public ItemModelBuilder blockMine(Block vanillaBlock, Block block)
	{
		return parent(block.getRegistryName().toString(), mcLoc(BLOCK_FOLDER + "/" + vanillaBlock.getRegistryName().getPath()));
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
