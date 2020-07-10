package net.geforcemods.securitycraft.datagen;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SCTags;
import net.minecraft.block.Block;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.fml.RegistryObject;

//func_240522_a_ = func_240522_a_
//func_240531_a_ = add
//func_240534_a_ = add
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
		func_240522_a_(SCTags.Blocks.REINFORCED_ACACIA_LOGS).func_240534_a_(
				SCContent.REINFORCED_ACACIA_LOG.get(),
				SCContent.REINFORCED_ACACIA_WOOD.get(),
				SCContent.REINFORCED_STRIPPED_ACACIA_LOG.get(),
				SCContent.REINFORCED_STRIPPED_ACACIA_WOOD.get());
		func_240522_a_(SCTags.Blocks.REINFORCED_BIRCH_LOGS).func_240534_a_(
				SCContent.REINFORCED_BIRCH_LOG.get(),
				SCContent.REINFORCED_BIRCH_WOOD.get(),
				SCContent.REINFORCED_STRIPPED_BIRCH_LOG.get(),
				SCContent.REINFORCED_STRIPPED_BIRCH_WOOD.get());
		func_240522_a_(SCTags.Blocks.REINFORCED_BUTTONS).func_240534_a_(
				SCContent.REINFORCED_STONE_BUTTON.get(),
				SCContent.REINFORCED_OAK_BUTTON.get(),
				SCContent.REINFORCED_SPRUCE_BUTTON.get(),
				SCContent.REINFORCED_BIRCH_BUTTON.get(),
				SCContent.REINFORCED_JUNGLE_BUTTON.get(),
				SCContent.REINFORCED_ACACIA_BUTTON.get(),
				SCContent.REINFORCED_DARK_OAK_BUTTON.get());
		func_240522_a_(SCTags.Blocks.REINFORCED_CARPETS).func_240534_a_(
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
		func_240522_a_(SCTags.Blocks.REINFORCED_COBBLESTONE).func_240534_a_(
				SCContent.REINFORCED_COBBLESTONE.get(),
				SCContent.REINFORCED_MOSSY_COBBLESTONE.get());
		func_240522_a_(SCTags.Blocks.REINFORCED_DARK_OAK_LOGS).func_240534_a_(
				SCContent.REINFORCED_DARK_OAK_LOG.get(),
				SCContent.REINFORCED_DARK_OAK_WOOD.get(),
				SCContent.REINFORCED_STRIPPED_DARK_OAK_LOG.get(),
				SCContent.REINFORCED_STRIPPED_DARK_OAK_WOOD.get());
		func_240522_a_(SCTags.Blocks.REINFORCED_DIRT).func_240534_a_(
				SCContent.REINFORCED_GRASS_BLOCK.get(),
				SCContent.REINFORCED_DIRT.get(),
				SCContent.REINFORCED_COARSE_DIRT.get(),
				SCContent.REINFORCED_PODZOL.get(),
				SCContent.REINFORCED_MYCELIUM.get());
		func_240522_a_(SCTags.Blocks.REINFORCED_END_STONES).func_240534_a_(
				SCContent.REINFORCED_END_STONE.get());
		func_240522_a_(SCTags.Blocks.REINFORCED_ICE).func_240534_a_(
				SCContent.REINFORCED_ICE.get(),
				SCContent.REINFORCED_PACKED_ICE.get(),
				SCContent.REINFORCED_BLUE_ICE.get());
		func_240522_a_(SCTags.Blocks.REINFORCED_JUNGLE_LOGS).func_240534_a_(
				SCContent.REINFORCED_JUNGLE_LOG.get(),
				SCContent.REINFORCED_JUNGLE_WOOD.get(),
				SCContent.REINFORCED_STRIPPED_JUNGLE_LOG.get(),
				SCContent.REINFORCED_STRIPPED_JUNGLE_WOOD.get());
		func_240522_a_(SCTags.Blocks.REINFORCED_LOGS)
		.func_240531_a_(SCTags.Blocks.REINFORCED_ACACIA_LOGS)
		.func_240531_a_(SCTags.Blocks.REINFORCED_BIRCH_LOGS)
		.func_240531_a_(SCTags.Blocks.REINFORCED_DARK_OAK_LOGS)
		.func_240531_a_(SCTags.Blocks.REINFORCED_JUNGLE_LOGS)
		.func_240531_a_(SCTags.Blocks.REINFORCED_OAK_LOGS)
		.func_240531_a_(SCTags.Blocks.REINFORCED_SPRUCE_LOGS);
		func_240522_a_(SCTags.Blocks.REINFORCED_OAK_LOGS).func_240534_a_(
				SCContent.REINFORCED_OAK_LOG.get(),
				SCContent.REINFORCED_OAK_WOOD.get(),
				SCContent.REINFORCED_STRIPPED_OAK_LOG.get(),
				SCContent.REINFORCED_STRIPPED_OAK_WOOD.get());
		func_240522_a_(SCTags.Blocks.REINFORCED_PLANKS).func_240534_a_(
				SCContent.REINFORCED_ACACIA_PLANKS.get(),
				SCContent.REINFORCED_BIRCH_PLANKS.get(),
				SCContent.REINFORCED_DARK_OAK_PLANKS.get(),
				SCContent.REINFORCED_JUNGLE_PLANKS.get(),
				SCContent.REINFORCED_OAK_PLANKS.get(),
				SCContent.REINFORCED_SPRUCE_PLANKS.get());
		func_240522_a_(SCTags.Blocks.REINFORCED_SAND).func_240534_a_(
				SCContent.REINFORCED_RED_SAND.get(),
				SCContent.REINFORCED_SAND.get());
		func_240522_a_(SCTags.Blocks.REINFORCED_SLABS).func_240531_a_(SCTags.Blocks.REINFORCED_WOODEN_SLABS).func_240534_a_(
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
		func_240522_a_(SCTags.Blocks.REINFORCED_SPRUCE_LOGS).func_240534_a_(
				SCContent.REINFORCED_SPRUCE_LOG.get(),
				SCContent.REINFORCED_SPRUCE_WOOD.get(),
				SCContent.REINFORCED_STRIPPED_SPRUCE_LOG.get(),
				SCContent.REINFORCED_STRIPPED_SPRUCE_WOOD.get());
		func_240522_a_(SCTags.Blocks.REINFORCED_STAIRS).func_240531_a_(SCTags.Blocks.REINFORCED_WOODEN_STAIRS).func_240534_a_(
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
		func_240522_a_(SCTags.Blocks.REINFORCED_STONE_BRICKS).func_240534_a_(
				SCContent.REINFORCED_STONE_BRICKS.get(),
				SCContent.REINFORCED_MOSSY_STONE_BRICKS.get(),
				SCContent.REINFORCED_CRACKED_STONE_BRICKS.get(),
				SCContent.REINFORCED_CHISELED_STONE_BRICKS.get());
		func_240522_a_(SCTags.Blocks.REINFORCED_WOODEN_SLABS).func_240534_a_(
				SCContent.REINFORCED_OAK_SLAB.get(),
				SCContent.REINFORCED_SPRUCE_SLAB.get(),
				SCContent.REINFORCED_BIRCH_SLAB.get(),
				SCContent.REINFORCED_JUNGLE_SLAB.get(),
				SCContent.REINFORCED_ACACIA_SLAB.get(),
				SCContent.REINFORCED_DARK_OAK_SLAB.get());
		func_240522_a_(SCTags.Blocks.REINFORCED_WOODEN_STAIRS).func_240534_a_(
				SCContent.REINFORCED_OAK_STAIRS.get(),
				SCContent.REINFORCED_SPRUCE_STAIRS.get(),
				SCContent.REINFORCED_BIRCH_STAIRS.get(),
				SCContent.REINFORCED_JUNGLE_STAIRS.get(),
				SCContent.REINFORCED_ACACIA_STAIRS.get(),
				SCContent.REINFORCED_DARK_OAK_STAIRS.get());
		func_240522_a_(SCTags.Blocks.REINFORCED_WOOL).func_240534_a_(
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
		func_240522_a_(SCTags.Blocks.SECRET_SIGNS).func_240531_a_(SCTags.Blocks.SECRET_STANDING_SIGNS).func_240531_a_(SCTags.Blocks.SECRET_WALL_SIGNS);
		func_240522_a_(SCTags.Blocks.SECRET_STANDING_SIGNS).func_240534_a_(
				SCContent.SECRET_ACACIA_SIGN.get(),
				SCContent.SECRET_BIRCH_SIGN.get(),
				SCContent.SECRET_DARK_OAK_SIGN.get(),
				SCContent.SECRET_JUNGLE_SIGN.get(),
				SCContent.SECRET_OAK_SIGN.get(),
				SCContent.SECRET_SPRUCE_SIGN.get());
		func_240522_a_(SCTags.Blocks.SECRET_WALL_SIGNS).func_240534_a_(
				SCContent.SECRET_ACACIA_WALL_SIGN.get(),
				SCContent.SECRET_BIRCH_WALL_SIGN.get(),
				SCContent.SECRET_DARK_OAK_WALL_SIGN.get(),
				SCContent.SECRET_JUNGLE_WALL_SIGN.get(),
				SCContent.SECRET_OAK_WALL_SIGN.get(),
				SCContent.SECRET_SPRUCE_WALL_SIGN.get());
		func_240522_a_(SCTags.Blocks.REINFORCED_STONE).func_240534_a_(
				SCContent.REINFORCED_ANDESITE.get(),
				SCContent.REINFORCED_DIORITE.get(),
				SCContent.REINFORCED_GRANITE.get(),
				SCContent.REINFORCED_STONE.get(),
				SCContent.REINFORCED_POLISHED_ANDESITE.get(),
				SCContent.REINFORCED_POLISHED_DIORITE.get(),
				SCContent.REINFORCED_POLISHED_GRANITE.get());

		//minecraft tags
		Builder<Block> dragonImmune = func_240522_a_(BlockTags.DRAGON_IMMUNE);
		Builder<Block> witherImmune = func_240522_a_(BlockTags.WITHER_IMMUNE);

		func_240522_a_(BlockTags.BAMBOO_PLANTABLE_ON).func_240531_a_(SCTags.Blocks.REINFORCED_SAND).func_240534_a_(SCContent.REINFORCED_GRAVEL.get(),
				SCContent.REINFORCED_DIRT.get(),
				SCContent.REINFORCED_GRASS_BLOCK.get(),
				SCContent.REINFORCED_PODZOL.get(),
				SCContent.REINFORCED_COARSE_DIRT.get(),
				SCContent.REINFORCED_MYCELIUM.get());
		func_240522_a_(BlockTags.BEACON_BASE_BLOCKS).func_240534_a_(
				SCContent.REINFORCED_DIAMOND_BLOCK.get(),
				SCContent.REINFORCED_EMERALD_BLOCK.get(),
				SCContent.REINFORCED_GOLD_BLOCK.get(),
				SCContent.REINFORCED_IRON_BLOCK.get());
		func_240522_a_(BlockTags.CARPETS).func_240531_a_(SCTags.Blocks.REINFORCED_CARPETS);
		func_240522_a_(BlockTags.DOORS).func_240534_a_(SCContent.REINFORCED_DOOR.get(), SCContent.SCANNER_DOOR.get());
		func_240522_a_(BlockTags.FENCES).func_240534_a_(SCContent.IRON_FENCE.get());
		func_240522_a_(BlockTags.IMPERMEABLE).func_240534_a_(
				SCContent.REINFORCED_GLASS.get(),
				SCContent.REINFORCED_WHITE_STAINED_GLASS.get(),
				SCContent.REINFORCED_ORANGE_STAINED_GLASS.get(),
				SCContent.REINFORCED_MAGENTA_STAINED_GLASS.get(),
				SCContent.REINFORCED_LIGHT_BLUE_STAINED_GLASS.get(),
				SCContent.REINFORCED_YELLOW_STAINED_GLASS.get(),
				SCContent.REINFORCED_LIME_STAINED_GLASS.get(),
				SCContent.REINFORCED_PINK_STAINED_GLASS.get(),
				SCContent.REINFORCED_GRAY_STAINED_GLASS.get(),
				SCContent.REINFORCED_LIGHT_GRAY_STAINED_GLASS.get(),
				SCContent.REINFORCED_CYAN_STAINED_GLASS.get(),
				SCContent.REINFORCED_PURPLE_STAINED_GLASS.get(),
				SCContent.REINFORCED_BLUE_STAINED_GLASS.get(),
				SCContent.REINFORCED_BROWN_STAINED_GLASS.get(),
				SCContent.REINFORCED_GREEN_STAINED_GLASS.get(),
				SCContent.REINFORCED_RED_STAINED_GLASS.get(),
				SCContent.REINFORCED_BLACK_STAINED_GLASS.get());
		func_240522_a_(BlockTags.RAILS).func_240534_a_(SCContent.TRACK_MINE.get());
		func_240522_a_(BlockTags.SLABS).func_240531_a_(SCTags.Blocks.REINFORCED_SLABS).func_240534_a_(SCContent.CRYSTAL_QUARTZ_SLAB.get());
		func_240522_a_(BlockTags.STAIRS).func_240531_a_(SCTags.Blocks.REINFORCED_STAIRS).func_240534_a_(SCContent.STAIRS_CRYSTAL_QUARTZ.get());
		func_240522_a_(BlockTags.SIGNS).func_240531_a_(SCTags.Blocks.SECRET_SIGNS);
		func_240522_a_(BlockTags.STANDING_SIGNS).func_240531_a_(SCTags.Blocks.SECRET_STANDING_SIGNS);
		func_240522_a_(BlockTags.TRAPDOORS).func_240534_a_(SCContent.REINFORCED_IRON_TRAPDOOR.get());
		func_240522_a_(BlockTags.WALL_SIGNS).func_240531_a_(SCTags.Blocks.SECRET_WALL_SIGNS);
		func_240522_a_(BlockTags.WALLS).func_240534_a_(
				SCContent.REINFORCED_COBBLESTONE_WALL.get(),
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
				dragonImmune.func_240534_a_(block);
				witherImmune.func_240534_a_(block);
			}
		}
	}

	@Override
	public String getName()
	{
		return "SecurityCraft Block Tags";
	}
}
