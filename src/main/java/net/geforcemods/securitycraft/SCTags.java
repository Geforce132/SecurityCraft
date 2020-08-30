package net.geforcemods.securitycraft;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag.INamedTag;
import net.minecraft.tags.ItemTags;

public class SCTags
{
	public static class Blocks
	{
		public static final INamedTag<Block> REINFORCED_ACACIA_LOGS = tag("reinforced/acacia_logs");
		public static final INamedTag<Block> REINFORCED_BIRCH_LOGS = tag("reinforced/birch_logs");
		public static final INamedTag<Block> REINFORCED_BUTTONS = tag("reinforced/buttons");
		public static final INamedTag<Block> REINFORCED_CARPETS = tag("reinforced/carpets");
		public static final INamedTag<Block> REINFORCED_COBBLESTONE = tag("reinforced/cobblestone");
		public static final INamedTag<Block> REINFORCED_CRIMSON_STEMS = tag("reinforced/crimson_stems");
		public static final INamedTag<Block> REINFORCED_DARK_OAK_LOGS = tag("reinforced/dark_oak_logs");
		public static final INamedTag<Block> REINFORCED_DIRT = tag("reinforced/dirt");
		public static final INamedTag<Block> REINFORCED_END_STONES = tag("reinforced/end_stones");
		public static final INamedTag<Block> REINFORCED_ICE = tag("reinforced/ice");
		public static final INamedTag<Block> REINFORCED_JUNGLE_LOGS = tag("reinforced/jungle_logs");
		public static final INamedTag<Block> REINFORCED_LOGS = tag("reinforced/logs");
		public static final INamedTag<Block> REINFORCED_NYLIUM = tag("reinforced/nylium");
		public static final INamedTag<Block> REINFORCED_OAK_LOGS = tag("reinforced/oak_logs");
		public static final INamedTag<Block> REINFORCED_PLANKS = tag("reinforced/planks");
		public static final INamedTag<Block> REINFORCED_PRESSURE_PLATES = tag("reinforced/pressure_plates");
		public static final INamedTag<Block> REINFORCED_SAND = tag("reinforced/sand");
		public static final INamedTag<Block> REINFORCED_SLABS = tag("reinforced/slabs");
		public static final INamedTag<Block> REINFORCED_SPRUCE_LOGS = tag("reinforced/spruce_logs");
		public static final INamedTag<Block> REINFORCED_STAIRS = tag("reinforced/stairs");
		public static final INamedTag<Block> REINFORCED_STONE = tag("reinforced/stone");
		public static final INamedTag<Block> REINFORCED_STONE_BRICKS = tag("reinforced/stone_bricks");
		public static final INamedTag<Block> REINFORCED_WARPED_STEMS = tag("reinforced/warped_stems");
		public static final INamedTag<Block> REINFORCED_WOODEN_SLABS = tag("reinforced/wooden_slabs");
		public static final INamedTag<Block> REINFORCED_WOODEN_STAIRS = tag("reinforced/wooden_stairs");
		public static final INamedTag<Block> REINFORCED_WOOL = tag("reinforced/wool");
		public static final INamedTag<Block> SECRET_SIGNS = tag("secret_signs");
		public static final INamedTag<Block> SECRET_STANDING_SIGNS = tag("secret_standing_signs");
		public static final INamedTag<Block> SECRET_WALL_SIGNS = tag("secret_wall_signs");

		private static INamedTag<Block> tag(String name)
		{
			return BlockTags.makeWrapperTag(SecurityCraft.MODID + ":" + name);
		}
	}

	public static class Items
	{
		public static final INamedTag<Item> REINFORCED_ACACIA_LOGS = tag("reinforced/acacia_logs");
		public static final INamedTag<Item> REINFORCED_BIRCH_LOGS = tag("reinforced/birch_logs");
		public static final INamedTag<Item> REINFORCED_BUTTONS = tag("reinforced/buttons");
		public static final INamedTag<Item> REINFORCED_CARPETS = tag("reinforced/carpets");
		public static final INamedTag<Item> REINFORCED_COBBLESTONE = tag("reinforced/cobblestone");
		public static final INamedTag<Item> REINFORCED_CRIMSON_STEMS = tag("reinforced/crimson_stems");
		public static final INamedTag<Item> REINFORCED_DARK_OAK_LOGS = tag("reinforced/dark_oak_logs");
		public static final INamedTag<Item> REINFORCED_DIRT = tag("reinforced/dirt");
		public static final INamedTag<Item> REINFORCED_END_STONES = tag("reinforced/end_stones");
		public static final INamedTag<Item> REINFORCED_ICE = tag("reinforced/ice");
		public static final INamedTag<Item> REINFORCED_JUNGLE_LOGS = tag("reinforced/jungle_logs");
		public static final INamedTag<Item> REINFORCED_LOGS = tag("reinforced/logs");
		public static final INamedTag<Item> REINFORCED_NYLIUM = tag("reinforced/nylium");
		public static final INamedTag<Item> REINFORCED_OAK_LOGS = tag("reinforced/oak_logs");
		public static final INamedTag<Item> REINFORCED_PLANKS = tag("reinforced/planks");
		public static final INamedTag<Item> REINFORCED_PRESSURE_PLATES = tag("reinforced/pressure_plates");
		public static final INamedTag<Item> REINFORCED_SAND = tag("reinforced/sand");
		public static final INamedTag<Item> REINFORCED_SLABS = tag("reinforced/slabs");
		public static final INamedTag<Item> REINFORCED_SPRUCE_LOGS = tag("reinforced/spruce_logs");
		public static final INamedTag<Item> REINFORCED_STAIRS = tag("reinforced/stairs");
		public static final INamedTag<Item> REINFORCED_STONE = tag("reinforced/stone");
		public static final INamedTag<Item> REINFORCED_STONE_BRICKS = tag("reinforced/stone_bricks");
		public static final INamedTag<Item> REINFORCED_WARPED_STEMS = tag("reinforced/warped_stems");
		public static final INamedTag<Item> REINFORCED_WOODEN_SLABS = tag("reinforced/wooden_slabs");
		public static final INamedTag<Item> REINFORCED_WOODEN_STAIRS = tag("reinforced/wooden_stairs");
		public static final INamedTag<Item> REINFORCED_WOOL = tag("reinforced/wool");
		public static final INamedTag<Item> SECRET_SIGNS = tag("secret_signs");

		private static INamedTag<Item> tag(String name)
		{
			return ItemTags.makeWrapperTag(SecurityCraft.MODID + ":" + name);
		}
	}
}
