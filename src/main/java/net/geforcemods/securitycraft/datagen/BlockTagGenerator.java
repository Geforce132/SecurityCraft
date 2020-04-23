package net.geforcemods.securitycraft.datagen;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SCTags;
import net.minecraft.block.Block;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag.Builder;
import net.minecraftforge.fml.RegistryObject;

public class BlockTagGenerator extends BlockTagsProvider
{
	protected BlockTagGenerator(DataGenerator dataGenerator)
	{
		super(dataGenerator);
	}

	@Override
	protected void registerTags()
	{
		//securitycraft tags
		getBuilder(SCTags.Blocks.REINFORCED_ACACIA_LOGS).add(
				SCContent.REINFORCED_ACACIA_LOG.get(),
				SCContent.REINFORCED_ACACIA_WOOD.get(),
				SCContent.REINFORCED_STRIPPED_ACACIA_LOG.get(),
				SCContent.REINFORCED_STRIPPED_ACACIA_WOOD.get());
		getBuilder(SCTags.Blocks.REINFORCED_BIRCH_LOGS).add(
				SCContent.REINFORCED_BIRCH_LOG.get(),
				SCContent.REINFORCED_BIRCH_WOOD.get(),
				SCContent.REINFORCED_STRIPPED_BIRCH_LOG.get(),
				SCContent.REINFORCED_STRIPPED_BIRCH_WOOD.get());
		getBuilder(SCTags.Blocks.REINFORCED_BUTTONS).add(
				SCContent.REINFORCED_STONE_BUTTON.get(),
				SCContent.REINFORCED_OAK_BUTTON.get(),
				SCContent.REINFORCED_SPRUCE_BUTTON.get(),
				SCContent.REINFORCED_BIRCH_BUTTON.get(),
				SCContent.REINFORCED_JUNGLE_BUTTON.get(),
				SCContent.REINFORCED_ACACIA_BUTTON.get(),
				SCContent.REINFORCED_DARK_OAK_BUTTON.get());
		getBuilder(SCTags.Blocks.REINFORCED_CARPETS).add(
				SCContent.REINFORCED_WHITE_CARPET.get(),
				SCContent.REINFORCED_ORANGE_CARPET.get(),
				SCContent.REINFORCED_MAGENTA_CARPET.get(),
				SCContent.REINFORCED_LIGHT_BLUE_CARPET.get(),
				SCContent.REINFORCED_YELLOW_CARPET.get(),
				SCContent.REINFORCED_LIME_CARPET.get(),
				SCContent.REINFORCED_PINK_CARPET.get(),
				SCContent.REINFORCED_GRAY_CARPET.get(),
				SCContent.REINFORCED_LIGHT_GRAY_CARPET.get(),
				SCContent.REINFORCED_CYAN_CARPET.get(),
				SCContent.REINFORCED_PURPLE_CARPET.get(),
				SCContent.REINFORCED_BLUE_CARPET.get(),
				SCContent.REINFORCED_BROWN_CARPET.get(),
				SCContent.REINFORCED_GREEN_CARPET.get(),
				SCContent.REINFORCED_RED_CARPET.get(),
				SCContent.REINFORCED_BLACK_CARPET.get());
		getBuilder(SCTags.Blocks.REINFORCED_COBBLESTONE).add(
				SCContent.REINFORCED_COBBLESTONE.get(),
				SCContent.REINFORCED_MOSSY_COBBLESTONE.get());
		getBuilder(SCTags.Blocks.REINFORCED_DARK_OAK_LOGS).add(
				SCContent.REINFORCED_DARK_OAK_LOG.get(),
				SCContent.REINFORCED_DARK_OAK_WOOD.get(),
				SCContent.REINFORCED_STRIPPED_DARK_OAK_LOG.get(),
				SCContent.REINFORCED_STRIPPED_DARK_OAK_WOOD.get());
		getBuilder(SCTags.Blocks.REINFORCED_DIRT).add(
				SCContent.REINFORCED_GRASS_BLOCK.get(),
				SCContent.REINFORCED_DIRT.get(),
				SCContent.REINFORCED_COARSE_DIRT.get(),
				SCContent.REINFORCED_PODZOL.get(),
				SCContent.REINFORCED_MYCELIUM.get());
		getBuilder(SCTags.Blocks.REINFORCED_END_STONES).add(
				SCContent.REINFORCED_END_STONE.get());
		getBuilder(SCTags.Blocks.REINFORCED_ICE).add(
				SCContent.REINFORCED_ICE.get(),
				SCContent.REINFORCED_PACKED_ICE.get(),
				SCContent.REINFORCED_BLUE_ICE.get());
		getBuilder(SCTags.Blocks.REINFORCED_JUNGLE_LOGS).add(
				SCContent.REINFORCED_JUNGLE_LOG.get(),
				SCContent.REINFORCED_JUNGLE_WOOD.get(),
				SCContent.REINFORCED_STRIPPED_JUNGLE_LOG.get(),
				SCContent.REINFORCED_STRIPPED_JUNGLE_WOOD.get());
		getBuilder(SCTags.Blocks.REINFORCED_LOGS).add(
				SCTags.Blocks.REINFORCED_ACACIA_LOGS,
				SCTags.Blocks.REINFORCED_BIRCH_LOGS,
				SCTags.Blocks.REINFORCED_DARK_OAK_LOGS,
				SCTags.Blocks.REINFORCED_JUNGLE_LOGS,
				SCTags.Blocks.REINFORCED_OAK_LOGS,
				SCTags.Blocks.REINFORCED_SPRUCE_LOGS);
		getBuilder(SCTags.Blocks.REINFORCED_OAK_LOGS).add(
				SCContent.REINFORCED_OAK_LOG.get(),
				SCContent.REINFORCED_OAK_WOOD.get(),
				SCContent.REINFORCED_STRIPPED_OAK_LOG.get(),
				SCContent.REINFORCED_STRIPPED_OAK_WOOD.get());
		getBuilder(SCTags.Blocks.REINFORCED_PLANKS).add(
				SCContent.REINFORCED_ACACIA_PLANKS.get(),
				SCContent.REINFORCED_BIRCH_PLANKS.get(),
				SCContent.REINFORCED_DARK_OAK_PLANKS.get(),
				SCContent.REINFORCED_JUNGLE_PLANKS.get(),
				SCContent.REINFORCED_OAK_PLANKS.get(),
				SCContent.REINFORCED_SPRUCE_PLANKS.get());
		getBuilder(SCTags.Blocks.REINFORCED_SAND).add(
				SCContent.REINFORCED_RED_SAND.get(),
				SCContent.REINFORCED_SAND.get());
		getBuilder(SCTags.Blocks.REINFORCED_SLABS).add(SCTags.Blocks.REINFORCED_WOODEN_SLABS).add(
				SCContent.REINFORCED_NORMAL_STONE_SLAB.get(),
				SCContent.REINFORCED_SMOOTH_STONE_SLAB.get(),
				SCContent.REINFORCED_SANDSTONE_SLAB.get(),
				SCContent.REINFORCED_COBBLESTONE_SLAB.get(),
				SCContent.REINFORCED_BRICK_SLAB.get(),
				SCContent.REINFORCED_STONE_BRICK_SLAB.get(),
				SCContent.REINFORCED_NETHER_BRICK_SLAB.get(),
				SCContent.REINFORCED_QUARTZ_SLAB.get(),
				SCContent.REINFORCED_RED_SANDSTONE_SLAB.get(),
				SCContent.REINFORCED_PURPUR_SLAB.get(),
				SCContent.REINFORCED_PRISMARINE_SLAB.get(),
				SCContent.REINFORCED_PRISMARINE_BRICK_SLAB.get(),
				SCContent.REINFORCED_DARK_PRISMARINE_SLAB.get(),
				SCContent.REINFORCED_POLISHED_GRANITE_SLAB.get(),
				SCContent.REINFORCED_SMOOTH_RED_SANDSTONE_SLAB.get(),
				SCContent.REINFORCED_MOSSY_STONE_BRICK_SLAB.get(),
				SCContent.REINFORCED_POLISHED_DIORITE_SLAB.get(),
				SCContent.REINFORCED_MOSSY_COBBLESTONE_SLAB.get(),
				SCContent.REINFORCED_END_STONE_BRICK_SLAB.get(),
				SCContent.REINFORCED_SMOOTH_SANDSTONE_SLAB.get(),
				SCContent.REINFORCED_SMOOTH_QUARTZ_SLAB.get(),
				SCContent.REINFORCED_GRANITE_SLAB.get(),
				SCContent.REINFORCED_ANDESITE_SLAB.get(),
				SCContent.REINFORCED_RED_NETHER_BRICK_SLAB.get(),
				SCContent.REINFORCED_POLISHED_ANDESITE_SLAB.get(),
				SCContent.REINFORCED_DIORITE_SLAB.get(),
				SCContent.REINFORCED_CRYSTAL_QUARTZ_SLAB.get());
		getBuilder(SCTags.Blocks.REINFORCED_SPRUCE_LOGS).add(
				SCContent.REINFORCED_SPRUCE_LOG.get(),
				SCContent.REINFORCED_SPRUCE_WOOD.get(),
				SCContent.REINFORCED_STRIPPED_SPRUCE_LOG.get(),
				SCContent.REINFORCED_STRIPPED_SPRUCE_WOOD.get());
		getBuilder(SCTags.Blocks.REINFORCED_STAIRS).add(SCTags.Blocks.REINFORCED_WOODEN_STAIRS).add(
				SCContent.REINFORCED_PURPUR_STAIRS.get(),
				SCContent.REINFORCED_COBBLESTONE_STAIRS.get(),
				SCContent.REINFORCED_BRICK_STAIRS.get(),
				SCContent.REINFORCED_STONE_BRICK_STAIRS.get(),
				SCContent.REINFORCED_NETHER_BRICK_STAIRS.get(),
				SCContent.REINFORCED_SANDSTONE_STAIRS.get(),
				SCContent.REINFORCED_QUARTZ_STAIRS.get(),
				SCContent.REINFORCED_PRISMARINE_STAIRS.get(),
				SCContent.REINFORCED_PRISMARINE_BRICK_STAIRS.get(),
				SCContent.REINFORCED_DARK_PRISMARINE_STAIRS.get(),
				SCContent.REINFORCED_RED_SANDSTONE_STAIRS.get(),
				SCContent.REINFORCED_POLISHED_GRANITE_STAIRS.get(),
				SCContent.REINFORCED_SMOOTH_RED_SANDSTONE_STAIRS.get(),
				SCContent.REINFORCED_MOSSY_STONE_BRICK_STAIRS.get(),
				SCContent.REINFORCED_POLISHED_DIORITE_STAIRS.get(),
				SCContent.REINFORCED_MOSSY_COBBLESTONE_STAIRS.get(),
				SCContent.REINFORCED_END_STONE_BRICK_STAIRS.get(),
				SCContent.REINFORCED_STONE_STAIRS.get(),
				SCContent.REINFORCED_SMOOTH_SANDSTONE_STAIRS.get(),
				SCContent.REINFORCED_SMOOTH_QUARTZ_STAIRS.get(),
				SCContent.REINFORCED_GRANITE_STAIRS.get(),
				SCContent.REINFORCED_ANDESITE_STAIRS.get(),
				SCContent.REINFORCED_RED_NETHER_BRICK_STAIRS.get(),
				SCContent.REINFORCED_POLISHED_ANDESITE_STAIRS.get(),
				SCContent.REINFORCED_DIORITE_STAIRS.get(),
				SCContent.REINFORCED_CRYSTAL_QUARTZ_STAIRS.get());
		getBuilder(SCTags.Blocks.REINFORCED_STONE_BRICKS).add(
				SCContent.REINFORCED_STONE_BRICKS.get(),
				SCContent.REINFORCED_MOSSY_STONE_BRICKS.get(),
				SCContent.REINFORCED_CRACKED_STONE_BRICKS.get(),
				SCContent.REINFORCED_CHISELED_STONE_BRICKS.get());
		getBuilder(SCTags.Blocks.REINFORCED_WOODEN_SLABS).add(
				SCContent.REINFORCED_OAK_SLAB.get(),
				SCContent.REINFORCED_SPRUCE_SLAB.get(),
				SCContent.REINFORCED_BIRCH_SLAB.get(),
				SCContent.REINFORCED_JUNGLE_SLAB.get(),
				SCContent.REINFORCED_ACACIA_SLAB.get(),
				SCContent.REINFORCED_DARK_OAK_SLAB.get());
		getBuilder(SCTags.Blocks.REINFORCED_WOODEN_STAIRS).add(
				SCContent.REINFORCED_OAK_STAIRS.get(),
				SCContent.REINFORCED_SPRUCE_STAIRS.get(),
				SCContent.REINFORCED_BIRCH_STAIRS.get(),
				SCContent.REINFORCED_JUNGLE_STAIRS.get(),
				SCContent.REINFORCED_ACACIA_STAIRS.get(),
				SCContent.REINFORCED_DARK_OAK_STAIRS.get());
		getBuilder(SCTags.Blocks.REINFORCED_WOOL).add(
				SCContent.REINFORCED_WHITE_WOOL.get(),
				SCContent.REINFORCED_ORANGE_WOOL.get(),
				SCContent.REINFORCED_MAGENTA_WOOL.get(),
				SCContent.REINFORCED_LIGHT_BLUE_WOOL.get(),
				SCContent.REINFORCED_YELLOW_WOOL.get(),
				SCContent.REINFORCED_LIME_WOOL.get(),
				SCContent.REINFORCED_PINK_WOOL.get(),
				SCContent.REINFORCED_GRAY_WOOL.get(),
				SCContent.REINFORCED_LIGHT_GRAY_WOOL.get(),
				SCContent.REINFORCED_CYAN_WOOL.get(),
				SCContent.REINFORCED_PURPLE_WOOL.get(),
				SCContent.REINFORCED_BLUE_WOOL.get(),
				SCContent.REINFORCED_BROWN_WOOL.get(),
				SCContent.REINFORCED_GREEN_WOOL.get(),
				SCContent.REINFORCED_RED_WOOL.get(),
				SCContent.REINFORCED_BLACK_WOOL.get());
		getBuilder(SCTags.Blocks.SECRET_SIGNS).add(SCTags.Blocks.SECRET_STANDING_SIGNS, SCTags.Blocks.SECRET_WALL_SIGNS);
		getBuilder(SCTags.Blocks.SECRET_STANDING_SIGNS).add(
				SCContent.SECRET_ACACIA_SIGN.get(),
				SCContent.SECRET_BIRCH_SIGN.get(),
				SCContent.SECRET_DARK_OAK_SIGN.get(),
				SCContent.SECRET_JUNGLE_SIGN.get(),
				SCContent.SECRET_OAK_SIGN.get(),
				SCContent.SECRET_SPRUCE_SIGN.get());
		getBuilder(SCTags.Blocks.SECRET_WALL_SIGNS).add(
				SCContent.SECRET_ACACIA_WALL_SIGN.get(),
				SCContent.SECRET_BIRCH_WALL_SIGN.get(),
				SCContent.SECRET_DARK_OAK_WALL_SIGN.get(),
				SCContent.SECRET_JUNGLE_WALL_SIGN.get(),
				SCContent.SECRET_OAK_WALL_SIGN.get(),
				SCContent.SECRET_SPRUCE_WALL_SIGN.get());
		getBuilder(SCTags.Blocks.REINFORCED_STONE).add(
				SCContent.REINFORCED_ANDESITE.get(),
				SCContent.REINFORCED_DIORITE.get(),
				SCContent.REINFORCED_GRANITE.get(),
				SCContent.REINFORCED_STONE.get(),
				SCContent.REINFORCED_POLISHED_ANDESITE.get(),
				SCContent.REINFORCED_POLISHED_DIORITE.get(),
				SCContent.REINFORCED_POLISHED_GRANITE.get());

		//minecraft tags
		Builder<Block> dragonImmune = getBuilder(BlockTags.DRAGON_IMMUNE);
		Builder<Block> witherImmune = getBuilder(BlockTags.WITHER_IMMUNE);

		getBuilder(BlockTags.CARPETS).add(SCTags.Blocks.REINFORCED_CARPETS);
		getBuilder(BlockTags.FENCES).add(SCContent.IRON_FENCE.get());
		getBuilder(BlockTags.RAILS).add(SCContent.TRACK_MINE.get());
		getBuilder(BlockTags.SLABS).add(SCTags.Blocks.REINFORCED_SLABS).add(SCContent.CRYSTAL_QUARTZ_SLAB.get());
		getBuilder(BlockTags.STAIRS).add(SCTags.Blocks.REINFORCED_STAIRS).add(SCContent.STAIRS_CRYSTAL_QUARTZ.get());
		getBuilder(BlockTags.SIGNS).add(SCTags.Blocks.SECRET_SIGNS);
		getBuilder(BlockTags.STANDING_SIGNS).add(SCTags.Blocks.SECRET_STANDING_SIGNS);
		getBuilder(BlockTags.TRAPDOORS).add(SCContent.REINFORCED_IRON_TRAPDOOR.get());
		getBuilder(BlockTags.WALL_SIGNS).add(SCTags.Blocks.SECRET_WALL_SIGNS);
		getBuilder(BlockTags.WALLS).add(SCContent.REINFORCED_COBBLESTONE_WALL.get(),
				SCContent.REINFORCED_MOSSY_COBBLESTONE_WALL.get(),
				SCContent.REINFORCED_BRICK_WALL.get(),
				SCContent.REINFORCED_PRISMARINE_WALL.get(),
				SCContent.REINFORCED_RED_SANDSTONE_WALL.get(),
				SCContent.REINFORCED_MOSSY_STONE_BRICK_WALL.get(),
				SCContent.REINFORCED_GRANITE_WALL.get(),
				SCContent.REINFORCED_STONE_BRICK_WALL.get(),
				SCContent.REINFORCED_NETHER_BRICK_WALL.get(),
				SCContent.REINFORCED_ANDESITE_WALL.get(),
				SCContent.REINFORCED_RED_NETHER_BRICK_WALL.get(),
				SCContent.REINFORCED_SANDSTONE_WALL.get(),
				SCContent.REINFORCED_END_STONE_BRICK_WALL.get(),
				SCContent.REINFORCED_DIORITE_WALL.get());

		for(RegistryObject<Block> ro : SCContent.BLOCKS.getEntries())
		{
			Block block = ro.get();

			if(block != SCContent.CHISELED_CRYSTAL_QUARTZ.get() && block != SCContent.CRYSTAL_QUARTZ.get() && block != SCContent.CRYSTAL_QUARTZ_PILLAR.get() && block != SCContent.CRYSTAL_QUARTZ_SLAB.get() && block != SCContent.STAIRS_CRYSTAL_QUARTZ.get())
			{
				dragonImmune.add(block);
				witherImmune.add(block);
			}
		}
	}

	@Override
	public String getName()
	{
		return "SecurityCraft Block Tags";
	}
}
