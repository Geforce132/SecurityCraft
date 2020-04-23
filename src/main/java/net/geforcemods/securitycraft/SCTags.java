package net.geforcemods.securitycraft;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;

public class SCTags
{
	public static class Blocks
	{
		public static final Tag<Block> REINFORCED_ACACIA_LOGS = tag("reinforced/acacia_logs");
		public static final Tag<Block> REINFORCED_BIRCH_LOGS = tag("reinforced/birch_logs");
		public static final Tag<Block> REINFORCED_BUTTONS = tag("reinforced/buttons");
		public static final Tag<Block> REINFORCED_CARPETS = tag("reinforced/carpets");
		public static final Tag<Block> REINFORCED_COBBLESTONE = tag("reinforced/cobblestone");
		public static final Tag<Block> REINFORCED_DARK_OAK_LOGS = tag("reinforced/dark_oak_logs");
		public static final Tag<Block> REINFORCED_DIRT = tag("reinforced/dirt");
		public static final Tag<Block> REINFORCED_END_STONES = tag("reinforced/end_stones");
		public static final Tag<Block> REINFORCED_ICE = tag("reinforced/ice");
		public static final Tag<Block> REINFORCED_JUNGLE_LOGS = tag("reinforced/jungle_logs");
		public static final Tag<Block> REINFORCED_LOGS = tag("reinforced/logs");
		public static final Tag<Block> REINFORCED_OAK_LOGS = tag("reinforced/oak_logs");
		public static final Tag<Block> REINFORCED_PLANKS = tag("reinforced/planks");
		public static final Tag<Block> REINFORCED_SAND = tag("reinforced/sand");
		public static final Tag<Block> REINFORCED_SLABS = tag("reinforced/slabs");
		public static final Tag<Block> REINFORCED_SPRUCE_LOGS = tag("reinforced/spruce_logs");
		public static final Tag<Block> REINFORCED_STAIRS = tag("reinforced/stairs");
		public static final Tag<Block> REINFORCED_STONE = tag("reinforced/stone");
		public static final Tag<Block> REINFORCED_STONE_BRICKS = tag("reinforced/stone_bricks");
		public static final Tag<Block> REINFORCED_WOODEN_SLABS = tag("reinforced/wooden_slabs");
		public static final Tag<Block> REINFORCED_WOODEN_STAIRS = tag("reinforced/wooden_stairs");
		public static final Tag<Block> REINFORCED_WOOL = tag("reinforced/wool");
		public static final Tag<Block> SECRET_SIGNS = tag("secret_signs");
		public static final Tag<Block> SECRET_STANDING_SIGNS = tag("secret_standing_signs");
		public static final Tag<Block> SECRET_WALL_SIGNS = tag("secret_wall_signs");

		private static Tag<Block> tag(String name)
		{
			return new BlockTags.Wrapper(new ResourceLocation(SecurityCraft.MODID, name));
		}
	}

	public static class Items
	{
		public static final Tag<Item> REINFORCED_ACACIA_LOGS = tag("reinforced/acacia_logs");
		public static final Tag<Item> REINFORCED_BIRCH_LOGS = tag("reinforced/birch_logs");
		public static final Tag<Item> REINFORCED_BUTTONS = tag("reinforced/buttons");
		public static final Tag<Item> REINFORCED_CARPETS = tag("reinforced/carpets");
		public static final Tag<Item> REINFORCED_COBBLESTONE = tag("reinforced/cobblestone");
		public static final Tag<Item> REINFORCED_DARK_OAK_LOGS = tag("reinforced/dark_oak_logs");
		public static final Tag<Item> REINFORCED_DIRT = tag("reinforced/dirt");
		public static final Tag<Item> REINFORCED_END_STONES = tag("reinforced/end_stones");
		public static final Tag<Item> REINFORCED_ICE = tag("reinforced/ice");
		public static final Tag<Item> REINFORCED_JUNGLE_LOGS = tag("reinforced/jungle_logs");
		public static final Tag<Item> REINFORCED_LOGS = tag("reinforced/logs");
		public static final Tag<Item> REINFORCED_OAK_LOGS = tag("reinforced/oak_logs");
		public static final Tag<Item> REINFORCED_PLANKS = tag("reinforced/planks");
		public static final Tag<Item> REINFORCED_SAND = tag("reinforced/sand");
		public static final Tag<Item> REINFORCED_SLABS = tag("reinforced/slabs");
		public static final Tag<Item> REINFORCED_SPRUCE_LOGS = tag("reinforced/spruce_logs");
		public static final Tag<Item> REINFORCED_STAIRS = tag("reinforced/stairs");
		public static final Tag<Item> REINFORCED_STONE = tag("reinforced/stone");
		public static final Tag<Item> REINFORCED_STONE_BRICKS = tag("reinforced/stone_bricks");
		public static final Tag<Item> REINFORCED_WOODEN_SLABS = tag("reinforced/wooden_slabs");
		public static final Tag<Item> REINFORCED_WOODEN_STAIRS = tag("reinforced/wooden_stairs");
		public static final Tag<Item> REINFORCED_WOOL = tag("reinforced/wool");
		public static final Tag<Item> SECRET_SIGNS = tag("secret_signs");

		private static Tag<Item> tag(String name)
		{
			return new ItemTags.Wrapper(new ResourceLocation(SecurityCraft.MODID, name));
		}
	}
}
