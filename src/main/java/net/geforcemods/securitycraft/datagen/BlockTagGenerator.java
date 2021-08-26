package net.geforcemods.securitycraft.datagen;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SCTags;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedSlabBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedStairsBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedWallBlock;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.fmllegacy.RegistryObject;

public class BlockTagGenerator extends BlockTagsProvider
{
	protected BlockTagGenerator(DataGenerator dataGenerator, ExistingFileHelper existingFileHelper)
	{
		super(dataGenerator, SecurityCraft.MODID, existingFileHelper);
	}

	//TODO: add reinforced moss to minecraft:small_dripleaf_placeable
	@Override
	protected void addTags()
	{
		//securitycraft tags
		tag(SCTags.Blocks.REINFORCED_ACACIA_LOGS).add(
				SCContent.REINFORCED_ACACIA_LOG.get(),
				SCContent.REINFORCED_ACACIA_WOOD.get(),
				SCContent.REINFORCED_STRIPPED_ACACIA_LOG.get(),
				SCContent.REINFORCED_STRIPPED_ACACIA_WOOD.get());
		tag(SCTags.Blocks.REINFORCED_BIRCH_LOGS).add(
				SCContent.REINFORCED_BIRCH_LOG.get(),
				SCContent.REINFORCED_BIRCH_WOOD.get(),
				SCContent.REINFORCED_STRIPPED_BIRCH_LOG.get(),
				SCContent.REINFORCED_STRIPPED_BIRCH_WOOD.get());
		tag(SCTags.Blocks.REINFORCED_BUTTONS).add(
				SCContent.REINFORCED_STONE_BUTTON.get(),
				SCContent.REINFORCED_OAK_BUTTON.get(),
				SCContent.REINFORCED_SPRUCE_BUTTON.get(),
				SCContent.REINFORCED_BIRCH_BUTTON.get(),
				SCContent.REINFORCED_JUNGLE_BUTTON.get(),
				SCContent.REINFORCED_ACACIA_BUTTON.get(),
				SCContent.REINFORCED_DARK_OAK_BUTTON.get(),
				SCContent.REINFORCED_CRIMSON_BUTTON.get(),
				SCContent.REINFORCED_WARPED_BUTTON.get(),
				SCContent.REINFORCED_POLISHED_BLACKSTONE_BUTTON.get());
		tag(SCTags.Blocks.REINFORCED_CARPETS).add(
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
		tag(SCTags.Blocks.REINFORCED_COBBLESTONE).add(
				SCContent.REINFORCED_COBBLESTONE.get(),
				SCContent.REINFORCED_MOSSY_COBBLESTONE.get());
		tag(SCTags.Blocks.REINFORCED_CRIMSON_STEMS).add(
				SCContent.REINFORCED_CRIMSON_STEM.get(),
				SCContent.REINFORCED_CRIMSON_HYPHAE.get(),
				SCContent.REINFORCED_STRIPPED_CRIMSON_STEM.get(),
				SCContent.REINFORCED_STRIPPED_CRIMSON_HYPHAE.get());
		tag(SCTags.Blocks.REINFORCED_DARK_OAK_LOGS).add(
				SCContent.REINFORCED_DARK_OAK_LOG.get(),
				SCContent.REINFORCED_DARK_OAK_WOOD.get(),
				SCContent.REINFORCED_STRIPPED_DARK_OAK_LOG.get(),
				SCContent.REINFORCED_STRIPPED_DARK_OAK_WOOD.get());
		tag(SCTags.Blocks.REINFORCED_DIRT).add(
				SCContent.REINFORCED_GRASS_BLOCK.get(),
				SCContent.REINFORCED_DIRT.get(),
				SCContent.REINFORCED_COARSE_DIRT.get(),
				SCContent.REINFORCED_PODZOL.get(),
				SCContent.REINFORCED_MOSS_BLOCK.get(),
				SCContent.REINFORCED_MYCELIUM.get());
		tag(SCTags.Blocks.REINFORCED_END_STONES).add(
				SCContent.REINFORCED_END_STONE.get());
		tag(SCTags.Blocks.REINFORCED_ICE).add(
				SCContent.REINFORCED_ICE.get(),
				SCContent.REINFORCED_PACKED_ICE.get(),
				SCContent.REINFORCED_BLUE_ICE.get());
		tag(SCTags.Blocks.REINFORCED_JUNGLE_LOGS).add(
				SCContent.REINFORCED_JUNGLE_LOG.get(),
				SCContent.REINFORCED_JUNGLE_WOOD.get(),
				SCContent.REINFORCED_STRIPPED_JUNGLE_LOG.get(),
				SCContent.REINFORCED_STRIPPED_JUNGLE_WOOD.get());
		tag(SCTags.Blocks.REINFORCED_LOGS)
		.addTag(SCTags.Blocks.REINFORCED_ACACIA_LOGS)
		.addTag(SCTags.Blocks.REINFORCED_BIRCH_LOGS)
		.addTag(SCTags.Blocks.REINFORCED_CRIMSON_STEMS)
		.addTag(SCTags.Blocks.REINFORCED_DARK_OAK_LOGS)
		.addTag(SCTags.Blocks.REINFORCED_JUNGLE_LOGS)
		.addTag(SCTags.Blocks.REINFORCED_OAK_LOGS)
		.addTag(SCTags.Blocks.REINFORCED_SPRUCE_LOGS)
		.addTag(SCTags.Blocks.REINFORCED_SPRUCE_LOGS)
		.addTag(SCTags.Blocks.REINFORCED_WARPED_STEMS);
		tag(SCTags.Blocks.REINFORCED_NYLIUM).add(
				SCContent.REINFORCED_CRIMSON_NYLIUM.get(),
				SCContent.REINFORCED_WARPED_NYLIUM.get());
		tag(SCTags.Blocks.REINFORCED_OAK_LOGS).add(
				SCContent.REINFORCED_OAK_LOG.get(),
				SCContent.REINFORCED_OAK_WOOD.get(),
				SCContent.REINFORCED_STRIPPED_OAK_LOG.get(),
				SCContent.REINFORCED_STRIPPED_OAK_WOOD.get());
		tag(SCTags.Blocks.REINFORCED_PLANKS).add(
				SCContent.REINFORCED_ACACIA_PLANKS.get(),
				SCContent.REINFORCED_BIRCH_PLANKS.get(),
				SCContent.REINFORCED_CRIMSON_PLANKS.get(),
				SCContent.REINFORCED_DARK_OAK_PLANKS.get(),
				SCContent.REINFORCED_JUNGLE_PLANKS.get(),
				SCContent.REINFORCED_OAK_PLANKS.get(),
				SCContent.REINFORCED_SPRUCE_PLANKS.get(),
				SCContent.REINFORCED_WARPED_PLANKS.get());
		tag(SCTags.Blocks.REINFORCED_PRESSURE_PLATES).add(
				SCContent.REINFORCED_OAK_PRESSURE_PLATE.get(),
				SCContent.REINFORCED_SPRUCE_PRESSURE_PLATE.get(),
				SCContent.REINFORCED_BIRCH_PRESSURE_PLATE.get(),
				SCContent.REINFORCED_JUNGLE_PRESSURE_PLATE.get(),
				SCContent.REINFORCED_ACACIA_PRESSURE_PLATE.get(),
				SCContent.REINFORCED_DARK_OAK_PRESSURE_PLATE.get(),
				SCContent.REINFORCED_CRIMSON_PRESSURE_PLATE.get(),
				SCContent.REINFORCED_WARPED_PRESSURE_PLATE.get(),
				SCContent.REINFORCED_POLISHED_BLACKSTONE_PRESSURE_PLATE.get());
		tag(SCTags.Blocks.REINFORCED_SAND).add(
				SCContent.REINFORCED_RED_SAND.get(),
				SCContent.REINFORCED_SAND.get());
		tag(SCTags.Blocks.REINFORCED_SLABS).addTag(SCTags.Blocks.REINFORCED_WOODEN_SLABS);
		tag(SCTags.Blocks.REINFORCED_SPRUCE_LOGS).add(
				SCContent.REINFORCED_SPRUCE_LOG.get(),
				SCContent.REINFORCED_SPRUCE_WOOD.get(),
				SCContent.REINFORCED_STRIPPED_SPRUCE_LOG.get(),
				SCContent.REINFORCED_STRIPPED_SPRUCE_WOOD.get());
		tag(SCTags.Blocks.REINFORCED_STAIRS).addTag(SCTags.Blocks.REINFORCED_WOODEN_STAIRS);
		tag(SCTags.Blocks.REINFORCED_STONE_BRICKS).add(
				SCContent.REINFORCED_STONE_BRICKS.get(),
				SCContent.REINFORCED_MOSSY_STONE_BRICKS.get(),
				SCContent.REINFORCED_CRACKED_STONE_BRICKS.get(),
				SCContent.REINFORCED_CHISELED_STONE_BRICKS.get());
		tag(SCTags.Blocks.REINFORCED_WARPED_STEMS).add(
				SCContent.REINFORCED_WARPED_STEM.get(),
				SCContent.REINFORCED_WARPED_HYPHAE.get(),
				SCContent.REINFORCED_STRIPPED_WARPED_STEM.get(),
				SCContent.REINFORCED_STRIPPED_WARPED_HYPHAE.get());
		tag(SCTags.Blocks.REINFORCED_WOODEN_SLABS).add(
				SCContent.REINFORCED_OAK_SLAB.get(),
				SCContent.REINFORCED_SPRUCE_SLAB.get(),
				SCContent.REINFORCED_BIRCH_SLAB.get(),
				SCContent.REINFORCED_JUNGLE_SLAB.get(),
				SCContent.REINFORCED_ACACIA_SLAB.get(),
				SCContent.REINFORCED_DARK_OAK_SLAB.get(),
				SCContent.REINFORCED_CRIMSON_SLAB.get(),
				SCContent.REINFORCED_WARPED_SLAB.get());
		tag(SCTags.Blocks.REINFORCED_WOODEN_STAIRS).add(
				SCContent.REINFORCED_OAK_STAIRS.get(),
				SCContent.REINFORCED_SPRUCE_STAIRS.get(),
				SCContent.REINFORCED_BIRCH_STAIRS.get(),
				SCContent.REINFORCED_JUNGLE_STAIRS.get(),
				SCContent.REINFORCED_ACACIA_STAIRS.get(),
				SCContent.REINFORCED_DARK_OAK_STAIRS.get(),
				SCContent.REINFORCED_CRIMSON_STAIRS.get(),
				SCContent.REINFORCED_WARPED_STAIRS.get());
		tag(SCTags.Blocks.REINFORCED_WOOL).add(
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
		tag(SCTags.Blocks.SECRET_SIGNS).addTag(SCTags.Blocks.SECRET_STANDING_SIGNS).addTag(SCTags.Blocks.SECRET_WALL_SIGNS);
		tag(SCTags.Blocks.SECRET_STANDING_SIGNS).add(
				SCContent.SECRET_ACACIA_SIGN.get(),
				SCContent.SECRET_BIRCH_SIGN.get(),
				SCContent.SECRET_CRIMSON_SIGN.get(),
				SCContent.SECRET_DARK_OAK_SIGN.get(),
				SCContent.SECRET_JUNGLE_SIGN.get(),
				SCContent.SECRET_OAK_SIGN.get(),
				SCContent.SECRET_SPRUCE_SIGN.get(),
				SCContent.SECRET_WARPED_SIGN.get());
		tag(SCTags.Blocks.SECRET_WALL_SIGNS).add(
				SCContent.SECRET_ACACIA_WALL_SIGN.get(),
				SCContent.SECRET_BIRCH_WALL_SIGN.get(),
				SCContent.SECRET_CRIMSON_WALL_SIGN.get(),
				SCContent.SECRET_DARK_OAK_WALL_SIGN.get(),
				SCContent.SECRET_JUNGLE_WALL_SIGN.get(),
				SCContent.SECRET_OAK_WALL_SIGN.get(),
				SCContent.SECRET_SPRUCE_WALL_SIGN.get(),
				SCContent.SECRET_WARPED_WALL_SIGN.get());
		tag(SCTags.Blocks.REINFORCED_STONE).add(
				SCContent.REINFORCED_ANDESITE.get(),
				SCContent.REINFORCED_DIORITE.get(),
				SCContent.REINFORCED_GRANITE.get(),
				SCContent.REINFORCED_STONE.get(),
				SCContent.REINFORCED_POLISHED_ANDESITE.get(),
				SCContent.REINFORCED_POLISHED_DIORITE.get(),
				SCContent.REINFORCED_POLISHED_GRANITE.get());

		//forge tags
		tag(Tags.Blocks.DIRT).addTag(SCTags.Blocks.REINFORCED_DIRT);

		//minecraft tags
		tag(BlockTags.BAMBOO_PLANTABLE_ON).addTag(SCTags.Blocks.REINFORCED_SAND).add(SCContent.REINFORCED_GRAVEL.get(),
				SCContent.REINFORCED_DIRT.get(),
				SCContent.REINFORCED_GRASS_BLOCK.get(),
				SCContent.REINFORCED_PODZOL.get(),
				SCContent.REINFORCED_COARSE_DIRT.get(),
				SCContent.REINFORCED_MOSS_BLOCK.get(),
				SCContent.REINFORCED_MYCELIUM.get());
		tag(BlockTags.BEACON_BASE_BLOCKS).add(
				SCContent.REINFORCED_DIAMOND_BLOCK.get(),
				SCContent.REINFORCED_EMERALD_BLOCK.get(),
				SCContent.REINFORCED_GOLD_BLOCK.get(),
				SCContent.REINFORCED_IRON_BLOCK.get(),
				SCContent.REINFORCED_NETHERITE_BLOCK.get());
		tag(BlockTags.CARPETS).addTag(SCTags.Blocks.REINFORCED_CARPETS);
		tag(BlockTags.CAULDRONS).add(
				SCContent.REINFORCED_CAULDRON.get(),
				SCContent.REINFORCED_LAVA_CAULDRON.get(),
				SCContent.REINFORCED_WATER_CAULDRON.get(),
				SCContent.REINFORCED_POWDER_SNOW_CAULDRON.get());
		tag(BlockTags.CRYSTAL_SOUND_BLOCKS).add(SCContent.REINFORCED_AMETHYST_BLOCK.get());
		tag(BlockTags.DIRT).addTag(SCTags.Blocks.REINFORCED_DIRT);
		tag(BlockTags.DOORS).add(SCContent.KEYPAD_DOOR.get(), SCContent.REINFORCED_DOOR.get(), SCContent.SCANNER_DOOR.get());
		tag(BlockTags.FENCES).add(SCContent.IRON_FENCE.get());
		tag(BlockTags.IMPERMEABLE).add(
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
		tag(BlockTags.INFINIBURN_OVERWORLD).add(SCContent.REINFORCED_NETHERRACK.get());
		tag(BlockTags.MINEABLE_WITH_PICKAXE).add(
				SCContent.CRYSTAL_QUARTZ.get(),
				SCContent.CHISELED_CRYSTAL_QUARTZ.get(),
				SCContent.CRYSTAL_QUARTZ_PILLAR.get(),
				SCContent.STAIRS_CRYSTAL_QUARTZ.get(),
				SCContent.CRYSTAL_QUARTZ_SLAB.get());
		tag(BlockTags.MUSHROOM_GROW_BLOCK).add(
				SCContent.REINFORCED_MYCELIUM.get(),
				SCContent.REINFORCED_PODZOL.get(),
				SCContent.REINFORCED_CRIMSON_NYLIUM.get(),
				SCContent.REINFORCED_WARPED_NYLIUM.get());
		tag(BlockTags.NYLIUM).addTag(SCTags.Blocks.REINFORCED_NYLIUM);
		tag(BlockTags.OCCLUDES_VIBRATION_SIGNALS).addTags(SCTags.Blocks.REINFORCED_WOOL);
		tag(BlockTags.PIGLIN_REPELLENTS).add(SCContent.REINFORCED_SOUL_LANTERN.get());
		tag(BlockTags.PRESSURE_PLATES).addTag(SCTags.Blocks.REINFORCED_PRESSURE_PLATES);
		tag(BlockTags.RAILS).add(SCContent.TRACK_MINE.get());
		tag(BlockTags.SLABS).addTag(SCTags.Blocks.REINFORCED_SLABS).add(SCContent.CRYSTAL_QUARTZ_SLAB.get());
		tag(BlockTags.SMALL_DRIPLEAF_PLACEABLE).add(SCContent.REINFORCED_CLAY.get());
		tag(BlockTags.STAIRS).addTag(SCTags.Blocks.REINFORCED_STAIRS).add(SCContent.STAIRS_CRYSTAL_QUARTZ.get());
		tag(BlockTags.STRIDER_WARM_BLOCKS).add(SCContent.FAKE_LAVA_BLOCK.get());
		tag(BlockTags.SIGNS).addTag(SCTags.Blocks.SECRET_SIGNS);
		tag(BlockTags.SNOW).add(SCContent.REINFORCED_SNOW_BLOCK.get());
		tag(BlockTags.SOUL_FIRE_BASE_BLOCKS).add(SCContent.REINFORCED_SOUL_SOIL.get());
		tag(BlockTags.SOUL_SPEED_BLOCKS).add(SCContent.REINFORCED_SOUL_SOIL.get());
		tag(BlockTags.STANDING_SIGNS).addTag(SCTags.Blocks.SECRET_STANDING_SIGNS);
		tag(BlockTags.TRAPDOORS).add(SCContent.REINFORCED_IRON_TRAPDOOR.get());
		tag(BlockTags.WALL_SIGNS).addTag(SCTags.Blocks.SECRET_WALL_SIGNS);
		tag(BlockTags.WITHER_SUMMON_BASE_BLOCKS).add(SCContent.REINFORCED_SOUL_SOIL.get());

		//automatic
		TagAppender<Block> dragonImmune = tag(BlockTags.DRAGON_IMMUNE);
		TagAppender<Block> witherImmune = tag(BlockTags.WITHER_IMMUNE);

		for(RegistryObject<Block> ro : SCContent.BLOCKS.getEntries())
		{
			Block block = ro.get();

			if(block != SCContent.CHISELED_CRYSTAL_QUARTZ.get() && block != SCContent.CRYSTAL_QUARTZ.get() && block != SCContent.CRYSTAL_QUARTZ_PILLAR.get() && block != SCContent.CRYSTAL_QUARTZ_SLAB.get() && block != SCContent.STAIRS_CRYSTAL_QUARTZ.get())
			{
				dragonImmune.add(block);
				witherImmune.add(block);
			}

			//ugly way of checking if slabs/stairs are wood. they do not need to be added to the tag explicitly, as they are already present in the wooden equivalent tag
			if(block instanceof ReinforcedSlabBlock && block.getSoundType(block.defaultBlockState()) != SoundType.WOOD)
				tag(SCTags.Blocks.REINFORCED_SLABS).add(block);
			else if(block instanceof ReinforcedStairsBlock && block.getSoundType(block.defaultBlockState()) != SoundType.WOOD)
				tag(SCTags.Blocks.REINFORCED_STAIRS).add(block);
			else if(block instanceof ReinforcedWallBlock)
				tag(BlockTags.WALLS).add(block);
		}
	}

	@Override
	public String getName()
	{
		return "SecurityCraft Block Tags";
	}
}
