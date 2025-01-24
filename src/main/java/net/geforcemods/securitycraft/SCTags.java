package net.geforcemods.securitycraft;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class SCTags {
	private SCTags() {}

	public static class Blocks {
		private Blocks() {}

		public static final TagKey<Block> ALL_SECRET_SIGNS = tag("all_secret_signs");
		public static final TagKey<Block> REINFORCED_ACACIA_LOGS = tag("reinforced/acacia_logs");
		public static final TagKey<Block> REINFORCED_BAMBOO_BLOCKS = tag("reinforced/bamboo_blocks");
		public static final TagKey<Block> REINFORCED_BIRCH_LOGS = tag("reinforced/birch_logs");
		public static final TagKey<Block> REINFORCED_BUTTONS = tag("reinforced/buttons");
		public static final TagKey<Block> REINFORCED_CHERRY_LOGS = tag("reinforced/cherry_logs");
		public static final TagKey<Block> REINFORCED_COBBLESTONE = tag("reinforced/cobblestone");
		public static final TagKey<Block> REINFORCED_CONCRETE = tag("reinforced/concrete");
		public static final TagKey<Block> REINFORCED_CRIMSON_STEMS = tag("reinforced/crimson_stems");
		public static final TagKey<Block> REINFORCED_DARK_OAK_LOGS = tag("reinforced/dark_oak_logs");
		public static final TagKey<Block> REINFORCED_DIRT = tag("reinforced/dirt");
		public static final TagKey<Block> REINFORCED_END_STONES = tag("reinforced/end_stones");
		public static final TagKey<Block> REINFORCED_FENCES = tag("reinforced/fences");
		public static final TagKey<Block> REINFORCED_FENCE_GATES = tag("reinforced/fence_gates");
		public static final TagKey<Block> REINFORCED_GLASS_PANES = tag("reinforced/glass_panes");
		public static final TagKey<Block> REINFORCED_GLAZED_TERRACOTTA = tag("reinforced/glazed_terracotta");
		public static final TagKey<Block> REINFORCED_ICE = tag("reinforced/ice");
		public static final TagKey<Block> REINFORCED_JUNGLE_LOGS = tag("reinforced/jungle_logs");
		public static final TagKey<Block> REINFORCED_LOGS = tag("reinforced/logs");
		public static final TagKey<Block> REINFORCED_MANGROVE_LOGS = tag("reinforced/mangrove_logs");
		public static final TagKey<Block> REINFORCED_NYLIUM = tag("reinforced/nylium");
		public static final TagKey<Block> REINFORCED_OAK_LOGS = tag("reinforced/oak_logs");
		public static final TagKey<Block> REINFORCED_PALE_OAK_LOGS = tag("reinforced/pale_oak_logs");
		public static final TagKey<Block> REINFORCED_PLANKS = tag("reinforced/planks");
		public static final TagKey<Block> REINFORCED_PRESSURE_PLATES = tag("reinforced/pressure_plates");
		public static final TagKey<Block> REINFORCED_SAND = tag("reinforced/sand");
		public static final TagKey<Block> REINFORCED_SLABS = tag("reinforced/slabs");
		public static final TagKey<Block> REINFORCED_SPRUCE_LOGS = tag("reinforced/spruce_logs");
		public static final TagKey<Block> REINFORCED_STAIRS = tag("reinforced/stairs");
		public static final TagKey<Block> REINFORCED_STONE = tag("reinforced/stone");
		public static final TagKey<Block> REINFORCED_STONE_BRICKS = tag("reinforced/stone_bricks");
		public static final TagKey<Block> REINFORCED_STONE_PRESSURE_PLATES = tag("reinforced/stone_pressure_plates");
		public static final TagKey<Block> REINFORCED_TERRACOTTA = tag("reinforced/terracotta");
		public static final TagKey<Block> REINFORCED_WARPED_STEMS = tag("reinforced/warped_stems");
		public static final TagKey<Block> REINFORCED_WOODEN_BUTTONS = tag("reinforced/wooden_buttons");
		public static final TagKey<Block> REINFORCED_WOODEN_FENCES = tag("reinforced/wooden_fences");
		public static final TagKey<Block> REINFORCED_WOODEN_FENCE_GATES = tag("reinforced/wooden_fence_gates");
		public static final TagKey<Block> REINFORCED_WOODEN_PRESSURE_PLATES = tag("reinforced/wooden_pressure_plates");
		public static final TagKey<Block> REINFORCED_WOODEN_SLABS = tag("reinforced/wooden_slabs");
		public static final TagKey<Block> REINFORCED_WOODEN_STAIRS = tag("reinforced/wooden_stairs");
		public static final TagKey<Block> REINFORCED_WOOL = tag("reinforced/wool");
		public static final TagKey<Block> REINFORCED_WOOL_CARPETS = tag("reinforced/wool_carpets");
		public static final TagKey<Block> SECRET_CEILING_HANGING_SIGNS = tag("secret_ceiling_hanging_signs");
		public static final TagKey<Block> SECRET_HANGING_SIGNS = tag("secret_hanging_signs");
		public static final TagKey<Block> SECRET_SIGNS = tag("secret_signs");
		public static final TagKey<Block> SECRET_STANDING_SIGNS = tag("secret_standing_signs");
		public static final TagKey<Block> SECRET_WALL_HANGING_SIGNS = tag("secret_wall_hanging_signs");
		public static final TagKey<Block> SECRET_WALL_SIGNS = tag("secret_wall_signs");
		public static final TagKey<Block> SUSPICIOUS_MINES = tag("suspicious_mines");

		private static TagKey<Block> tag(String name) {
			return BlockTags.create(SecurityCraft.resLoc(name));
		}
	}

	public static class DamageTypes {
		private DamageTypes() {}

		public static final TagKey<DamageType> SECURITY_SEA_BOAT_VULNERABLE_TO = tag("security_sea_boat_vulnerable_to");

		private static TagKey<DamageType> tag(String name) {
			return TagKey.create(Registries.DAMAGE_TYPE, SecurityCraft.resLoc(name));
		}
	}

	public static class Items {
		private Items() {}

		public static final TagKey<Item> CAN_INTERACT_WITH_DOORS = tag("can_interact_with_doors");
		public static final TagKey<Item> KEYCARDS = tag("keycards");
		public static final TagKey<Item> KEYCARD_HOLDER_CAN_HOLD = tag("keycard_holder_can_hold");
		public static final TagKey<Item> MODULES = tag("modules");
		public static final TagKey<Item> REINFORCED_ACACIA_LOGS = tag("reinforced/acacia_logs");
		public static final TagKey<Item> REINFORCED_BAMBOO_BLOCKS = tag("reinforced/bamboo_blocks");
		public static final TagKey<Item> REINFORCED_BIRCH_LOGS = tag("reinforced/birch_logs");
		public static final TagKey<Item> REINFORCED_BUTTONS = tag("reinforced/buttons");
		public static final TagKey<Item> REINFORCED_CHERRY_LOGS = tag("reinforced/cherry_logs");
		public static final TagKey<Item> REINFORCED_COBBLESTONE = tag("reinforced/cobblestone");
		public static final TagKey<Item> REINFORCED_CONCRETE = tag("reinforced/concrete");
		public static final TagKey<Item> REINFORCED_CRIMSON_STEMS = tag("reinforced/crimson_stems");
		public static final TagKey<Item> REINFORCED_DARK_OAK_LOGS = tag("reinforced/dark_oak_logs");
		public static final TagKey<Item> REINFORCED_DIRT = tag("reinforced/dirt");
		public static final TagKey<Item> REINFORCED_END_STONES = tag("reinforced/end_stones");
		public static final TagKey<Item> REINFORCED_FENCES = tag("reinforced/fences");
		public static final TagKey<Item> REINFORCED_FENCE_GATES = tag("reinforced/fence_gates");
		public static final TagKey<Item> REINFORCED_GLASS_PANES = tag("reinforced/glass_panes");
		public static final TagKey<Item> REINFORCED_GLAZED_TERRACOTTA = tag("reinforced/glazed_terracotta");
		public static final TagKey<Item> REINFORCED_ICE = tag("reinforced/ice");
		public static final TagKey<Item> REINFORCED_JUNGLE_LOGS = tag("reinforced/jungle_logs");
		public static final TagKey<Item> REINFORCED_LOGS = tag("reinforced/logs");
		public static final TagKey<Item> REINFORCED_MANGROVE_LOGS = tag("reinforced/mangrove_logs");
		public static final TagKey<Item> REINFORCED_MOSS = tag("reinforced/moss");
		public static final TagKey<Item> REINFORCED_NYLIUM = tag("reinforced/nylium");
		public static final TagKey<Item> REINFORCED_OAK_LOGS = tag("reinforced/oak_logs");
		public static final TagKey<Item> REINFORCED_PALE_OAK_LOGS = tag("reinforced/pale_oak_logs");
		public static final TagKey<Item> REINFORCED_PLANKS = tag("reinforced/planks");
		public static final TagKey<Item> REINFORCED_PRESSURE_PLATES = tag("reinforced/pressure_plates");
		public static final TagKey<Item> REINFORCED_SAND = tag("reinforced/sand");
		public static final TagKey<Item> REINFORCED_SLABS = tag("reinforced/slabs");
		public static final TagKey<Item> REINFORCED_SPRUCE_LOGS = tag("reinforced/spruce_logs");
		public static final TagKey<Item> REINFORCED_STAIRS = tag("reinforced/stairs");
		public static final TagKey<Item> REINFORCED_STONE = tag("reinforced/stone");
		public static final TagKey<Item> REINFORCED_STONE_BRICKS = tag("reinforced/stone_bricks");
		public static final TagKey<Item> REINFORCED_STONE_CRAFTING_MATERIALS = tag("reinforced/stone_crafting_materials");
		public static final TagKey<Item> REINFORCED_STONE_PRESSURE_PLATES = tag("reinforced/stone_pressure_plates");
		public static final TagKey<Item> REINFORCED_TERRACOTTA = tag("reinforced/terracotta");
		public static final TagKey<Item> REINFORCED_WARPED_STEMS = tag("reinforced/warped_stems");
		public static final TagKey<Item> REINFORCED_WOODEN_BUTTONS = tag("reinforced/wooden_buttons");
		public static final TagKey<Item> REINFORCED_WOODEN_FENCES = tag("reinforced/wooden_fences");
		public static final TagKey<Item> REINFORCED_WOODEN_FENCE_GATES = tag("reinforced/wooden_fence_gates");
		public static final TagKey<Item> REINFORCED_WOODEN_PRESSURE_PLATES = tag("reinforced/wooden_pressure_plates");
		public static final TagKey<Item> REINFORCED_WOODEN_SLABS = tag("reinforced/wooden_slabs");
		public static final TagKey<Item> REINFORCED_WOODEN_STAIRS = tag("reinforced/wooden_stairs");
		public static final TagKey<Item> REINFORCED_WOOL = tag("reinforced/wool");
		public static final TagKey<Item> REINFORCED_WOOL_CARPETS = tag("reinforced/wool_carpets");
		public static final TagKey<Item> SECRET_HANGING_SIGNS = tag("secret_hanging_signs");
		public static final TagKey<Item> SECRET_SIGNS = tag("secret_signs");

		private static TagKey<Item> tag(String name) {
			return ItemTags.create(SecurityCraft.resLoc(name));
		}
	}
}
