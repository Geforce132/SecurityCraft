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
	}

	public ItemModelBuilder reinforcedWallInventory(Block block, Block vanillaBlock)
	{
		return reinforcedWallInventory(block, vanillaBlock,vanillaBlock.getRegistryName().getPath().replace("reinforced_", "").replace("_wall", ""));
	}

	public ItemModelBuilder reinforcedWallInventory(Block block, Block vanillaBlock, String textureName)
	{
		return uncheckedSingleTexture(block.getRegistryName().toString(), modLoc(BLOCK_FOLDER + "/reinforced_wall_inventory"), "wall", new ResourceLocation("block/" + textureName));
	}

	public ItemModelBuilder uncheckedSingleTexture(String name, ResourceLocation parent, String textureKey, ResourceLocation texture)
	{
		return getBuilder(name).parent(new UncheckedModelFile(parent)).texture(textureKey, texture);
	}

	@Override
	public String getName()
	{
		return "SecurityCraft Item Models";
	}
}
