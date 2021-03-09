package net.geforcemods.securitycraft.datagen;

import java.lang.reflect.Field;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blocks.mines.BaseFullMineBlock;
import net.geforcemods.securitycraft.blocks.reinforced.IReinforcedBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedSlabBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedStainedGlassBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedStainedGlassPaneBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedStairsBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedWallBlock;
import net.geforcemods.securitycraft.util.RegisterItemBlock;
import net.geforcemods.securitycraft.util.RegisterItemBlock.SCItemGroup;
import net.geforcemods.securitycraft.util.Reinforced;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile.UncheckedModelFile;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.fml.RegistryObject;

public class ItemModelGenerator extends ItemModelProvider
{
	public ItemModelGenerator(DataGenerator generator, ExistingFileHelper existingFileHelper)
	{
		super(generator, SecurityCraft.MODID, existingFileHelper);
	}

	@Override
	protected void registerModels()
	{
		for(Field field : SCContent.class.getFields())
		{
			try
			{
				if(field.isAnnotationPresent(Reinforced.class))
				{
					Block block = ((RegistryObject<Block>)field.get(null)).get();

					if(block instanceof ReinforcedSlabBlock || block instanceof ReinforcedStainedGlassBlock || block instanceof ReinforcedStairsBlock)
						simpleParent(block);
					else if(block instanceof ReinforcedStainedGlassPaneBlock)
						reinforcedPane(block);
					else if(block instanceof ReinforcedWallBlock)
						reinforcedWallInventory(block, ((IReinforcedBlock)block).getVanillaBlock());
				}
				else if(field.isAnnotationPresent(RegisterItemBlock.class) && field.getAnnotation(RegisterItemBlock.class).value() == SCItemGroup.EXPLOSIVES)
				{
					Block block = ((RegistryObject<Block>)field.get(null)).get();

					if(block instanceof BaseFullMineBlock)
						blockMine(((BaseFullMineBlock)block).getBlockDisguisedAs(), block);
				}
			}
			catch(IllegalArgumentException | IllegalAccessException e)
			{
				e.printStackTrace();
			}
		}


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
		blockMine(Blocks.FURNACE, SCContent.FURNACE_MINE.get(), mcLoc(BLOCK_FOLDER + "/furnace_side"), mcLoc(BLOCK_FOLDER + "/furnace_front"), mcLoc(BLOCK_FOLDER + "/furnace_top"));
		simpleParent(SCContent.CRYSTAL_QUARTZ_SLAB.get());
		simpleParent(SCContent.STAIRS_CRYSTAL_QUARTZ.get());
		simpleParent(SCContent.REINFORCED_GLASS.get());
		reinforcedPane(SCContent.REINFORCED_GLASS_PANE.get());
		reinforcedWallInventory(SCContent.REINFORCED_BRICK_WALL.get(), "bricks");
		reinforcedWallInventory(SCContent.REINFORCED_MOSSY_STONE_BRICK_WALL.get(), "mossy_stone_bricks");
		reinforcedWallInventory(SCContent.REINFORCED_STONE_BRICK_WALL.get(), "stone_bricks");
		reinforcedWallInventory(SCContent.REINFORCED_NETHER_BRICK_WALL.get(), "nether_bricks");
		reinforcedWallInventory(SCContent.REINFORCED_RED_NETHER_BRICK_WALL.get(), "red_nether_bricks");
		reinforcedWallInventory(SCContent.REINFORCED_END_STONE_BRICK_WALL.get(), "end_stone_bricks");
		reinforcedWallInventory(SCContent.REINFORCED_POLISHED_BLACKSTONE_BRICK_WALL.get(), "polished_blackstone_bricks");
	}

	public ItemModelBuilder reinforcedPane(Block block)
	{
		String name = name(block);

		return getBuilder(name).parent(new UncheckedModelFile("item/generated")).texture("layer0", modLoc(ModelProvider.BLOCK_FOLDER + "/" + name.replace("_pane", "")));
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
		String name = name(block);

		return parent(name, modLoc(BLOCK_FOLDER + "/" + name));
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

	private String name(Block block)
	{
		return block.getRegistryName().getPath();
	}
}
