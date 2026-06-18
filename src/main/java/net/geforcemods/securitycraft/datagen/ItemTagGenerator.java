package net.geforcemods.securitycraft.datagen;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableList;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SCTags;
import net.geforcemods.securitycraft.SecurityCraft;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockItemTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ColorCollection;
import net.minecraft.world.level.block.WeatheringCopperCollection;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagCopyingItemTagProvider;
import net.neoforged.neoforge.registries.DeferredBlock;

public class ItemTagGenerator extends BlockTagCopyingItemTagProvider {
	public ItemTagGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTagsProvider) {
		super(output, lookupProvider, blockTagsProvider, SecurityCraft.MODID);
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void addTags(HolderLookup.Provider provider) {
		//@formatter:off
		//securitycraft tags
		tag(SCTags.Items.CAN_INTERACT_WITH_DOORS).add(
				SCContent.ADMIN_TOOL.getKey(),
				SCContent.CODEBREAKER.getKey(),
				SCContent.SONIC_SECURITY_SYSTEM_ITEM.getKey(),
				SCContent.UNIVERSAL_BLOCK_MODIFIER.getKey(),
				SCContent.UNIVERSAL_BLOCK_REMOVER.getKey(),
				SCContent.UNIVERSAL_KEY_CHANGER.getKey(),
				SCContent.UNIVERSAL_OWNER_CHANGER.getKey())
		.addTag(SCTags.Items.MODULES);
		tag(SCTags.Items.KEYCARDS).add(
				SCContent.KEYCARD_LVL_1.getKey(),
				SCContent.KEYCARD_LVL_2.getKey(),
				SCContent.KEYCARD_LVL_3.getKey(),
				SCContent.KEYCARD_LVL_4.getKey(),
				SCContent.KEYCARD_LVL_5.getKey());
		tag(SCTags.Items.KEYCARD_HOLDER_CAN_HOLD).addTag(SCTags.Items.KEYCARDS).add(SCContent.LIMITED_USE_KEYCARD.getKey());
		tag(SCTags.Items.MODULES).add(
				SCContent.ALLOWLIST_MODULE.getKey(),
				SCContent.DENYLIST_MODULE.getKey(),
				SCContent.DISGUISE_MODULE.getKey(),
				SCContent.HARMING_MODULE.getKey(),
				SCContent.REDSTONE_MODULE.getKey(),
				SCContent.SMART_MODULE.getKey(),
				SCContent.SPEED_MODULE.getKey(),
				SCContent.STORAGE_MODULE.getKey());
		tag(SCTags.Items.REINFORCED_STONE_CRAFTING_MATERIALS).add(
				keyFromItem(SCContent.REINFORCED_COBBLESTONE.get()),
				keyFromItem(SCContent.REINFORCED_BLACKSTONE.get()),
				keyFromItem(SCContent.REINFORCED_COBBLED_DEEPSLATE.get()));
		tag(SCTags.Items.SECRET_HANGING_SIGNS).add(
				SCContent.SECRET_ACACIA_HANGING_SIGN_ITEM.getKey(),
				SCContent.SECRET_BAMBOO_HANGING_SIGN_ITEM.getKey(),
				SCContent.SECRET_BIRCH_HANGING_SIGN_ITEM.getKey(),
				SCContent.SECRET_CHERRY_HANGING_SIGN_ITEM.getKey(),
				SCContent.SECRET_CRIMSON_HANGING_SIGN_ITEM.getKey(),
				SCContent.SECRET_DARK_OAK_HANGING_SIGN_ITEM.getKey(),
				SCContent.SECRET_JUNGLE_HANGING_SIGN_ITEM.getKey(),
				SCContent.SECRET_MANGROVE_HANGING_SIGN_ITEM.getKey(),
				SCContent.SECRET_OAK_HANGING_SIGN_ITEM.getKey(),
				SCContent.SECRET_PALE_OAK_HANGING_SIGN_ITEM.getKey(),
				SCContent.SECRET_SPRUCE_HANGING_SIGN_ITEM.getKey(),
				SCContent.SECRET_WARPED_HANGING_SIGN_ITEM.getKey());
		tag(SCTags.Items.SECRET_SIGNS).add(
				SCContent.SECRET_ACACIA_SIGN_ITEM.getKey(),
				SCContent.SECRET_BAMBOO_SIGN_ITEM.getKey(),
				SCContent.SECRET_BIRCH_SIGN_ITEM.getKey(),
				SCContent.SECRET_CHERRY_SIGN_ITEM.getKey(),
				SCContent.SECRET_CRIMSON_SIGN_ITEM.getKey(),
				SCContent.SECRET_DARK_OAK_SIGN_ITEM.getKey(),
				SCContent.SECRET_JUNGLE_SIGN_ITEM.getKey(),
				SCContent.SECRET_MANGROVE_SIGN_ITEM.getKey(),
				SCContent.SECRET_OAK_SIGN_ITEM.getKey(),
				SCContent.SECRET_PALE_OAK_SIGN_ITEM.getKey(),
				SCContent.SECRET_SPRUCE_SIGN_ITEM.getKey(),
				SCContent.SECRET_WARPED_SIGN_ITEM.getKey());
		tag(SCTags.Items.SULFUR_CUBE_ARCHETYPE_REINFORCED).add(
				keyFromItem(SCContent.REINFORCED_ACACIA_LOG.get()),
				keyFromItem(SCContent.REINFORCED_ACACIA_PLANKS.get()),
				keyFromItem(SCContent.REINFORCED_ACACIA_WOOD.get()),
				keyFromItem(SCContent.REINFORCED_AMETHYST_BLOCK.get()),
				keyFromItem(SCContent.REINFORCED_ANDESITE.get()),
				keyFromItem(SCContent.REINFORCED_BAMBOO_BLOCK.get()),
				keyFromItem(SCContent.REINFORCED_BAMBOO_MOSAIC.get()),
				keyFromItem(SCContent.REINFORCED_BAMBOO_PLANKS.get()),
				keyFromItem(SCContent.REINFORCED_BASALT.get()),
				keyFromItem(SCContent.REINFORCED_BIRCH_LOG.get()),
				keyFromItem(SCContent.REINFORCED_BIRCH_PLANKS.get()),
				keyFromItem(SCContent.REINFORCED_BIRCH_WOOD.get()),
				keyFromItem(SCContent.REINFORCED_BLACKSTONE.get()),
				keyFromItem(SCContent.REINFORCED_BLUE_ICE.get()),
				keyFromItem(SCContent.REINFORCED_BONE_BLOCK.get()),
				keyFromItem(SCContent.REINFORCED_BRICKS.get()),
				keyFromItem(SCContent.REINFORCED_CALCITE.get()),
				keyFromItem(SCContent.REINFORCED_CHERRY_LOG.get()),
				keyFromItem(SCContent.REINFORCED_CHERRY_PLANKS.get()),
				keyFromItem(SCContent.REINFORCED_CHERRY_WOOD.get()),
				keyFromItem(SCContent.REINFORCED_CHISELED_CINNABAR.get()),
				keyFromItem(SCContent.REINFORCED_CHISELED_DEEPSLATE.get()),
				keyFromItem(SCContent.REINFORCED_CHISELED_NETHER_BRICKS.get()),
				keyFromItem(SCContent.REINFORCED_CHISELED_POLISHED_BLACKSTONE.get()),
				keyFromItem(SCContent.REINFORCED_CHISELED_QUARTZ.get()),
				keyFromItem(SCContent.REINFORCED_CHISELED_RED_SANDSTONE.get()),
				keyFromItem(SCContent.REINFORCED_CHISELED_RESIN_BRICKS.get()),
				keyFromItem(SCContent.REINFORCED_CHISELED_SANDSTONE.get()),
				keyFromItem(SCContent.REINFORCED_CHISELED_STONE_BRICKS.get()),
				keyFromItem(SCContent.REINFORCED_CHISELED_SULFUR.get()),
				keyFromItem(SCContent.REINFORCED_CHISELED_TUFF.get()),
				keyFromItem(SCContent.REINFORCED_CHISELED_TUFF_BRICKS.get()),
				keyFromItem(SCContent.REINFORCED_CINNABAR.get()),
				keyFromItem(SCContent.REINFORCED_CINNABAR_BRICKS.get()),
				keyFromItem(SCContent.REINFORCED_CLAY.get()),
				keyFromItem(SCContent.REINFORCED_COAL_BLOCK.get()),
				keyFromItem(SCContent.REINFORCED_COARSE_DIRT.get()),
				keyFromItem(SCContent.REINFORCED_COBBLED_DEEPSLATE.get()),
				keyFromItem(SCContent.REINFORCED_COBBLESTONE.get()),
				keyFromItem(SCContent.REINFORCED_CRACKED_DEEPSLATE_BRICKS.get()),
				keyFromItem(SCContent.REINFORCED_CRACKED_DEEPSLATE_TILES.get()),
				keyFromItem(SCContent.REINFORCED_CRACKED_NETHER_BRICKS.get()),
				keyFromItem(SCContent.REINFORCED_CRACKED_POLISHED_BLACKSTONE_BRICKS.get()),
				keyFromItem(SCContent.REINFORCED_CRACKED_STONE_BRICKS.get()),
				keyFromItem(SCContent.REINFORCED_CRIMSON_HYPHAE.get()),
				keyFromItem(SCContent.REINFORCED_CRIMSON_NYLIUM.get()),
				keyFromItem(SCContent.REINFORCED_CRIMSON_PLANKS.get()),
				keyFromItem(SCContent.REINFORCED_CRIMSON_STEM.get()),
				keyFromItem(SCContent.REINFORCED_CRYING_OBSIDIAN.get()),
				keyFromItem(SCContent.REINFORCED_CUT_RED_SANDSTONE.get()),
				keyFromItem(SCContent.REINFORCED_CUT_SANDSTONE.get()),
				keyFromItem(SCContent.REINFORCED_DARK_OAK_LOG.get()),
				keyFromItem(SCContent.REINFORCED_DARK_OAK_PLANKS.get()),
				keyFromItem(SCContent.REINFORCED_DARK_OAK_WOOD.get()),
				keyFromItem(SCContent.REINFORCED_DARK_PRISMARINE.get()),
				keyFromItem(SCContent.REINFORCED_DEEPSLATE.get()),
				keyFromItem(SCContent.REINFORCED_DEEPSLATE_BRICKS.get()),
				keyFromItem(SCContent.REINFORCED_DEEPSLATE_TILES.get()),
				keyFromItem(SCContent.REINFORCED_DIAMOND_BLOCK.get()),
				keyFromItem(SCContent.REINFORCED_DIORITE.get()),
				keyFromItem(SCContent.REINFORCED_DIRT.get()),
				keyFromItem(SCContent.REINFORCED_DRIPSTONE_BLOCK.get()),
				keyFromItem(SCContent.REINFORCED_EMERALD_BLOCK.get()),
				keyFromItem(SCContent.REINFORCED_END_STONE.get()),
				keyFromItem(SCContent.REINFORCED_END_STONE_BRICKS.get()),
				keyFromItem(SCContent.REINFORCED_GLOWSTONE.get()),
				keyFromItem(SCContent.REINFORCED_GOLD_BLOCK.get()),
				keyFromItem(SCContent.REINFORCED_GRANITE.get()),
				keyFromItem(SCContent.REINFORCED_GRASS_BLOCK.get()),
				keyFromItem(SCContent.REINFORCED_TERRACOTTA.get()),
				keyFromItem(SCContent.REINFORCED_IRON_BLOCK.get()),
				keyFromItem(SCContent.REINFORCED_JUNGLE_LOG.get()),
				keyFromItem(SCContent.REINFORCED_JUNGLE_PLANKS.get()),
				keyFromItem(SCContent.REINFORCED_JUNGLE_WOOD.get()),
				keyFromItem(SCContent.REINFORCED_LAPIS_BLOCK.get()),
				keyFromItem(SCContent.REINFORCED_MAGMA_BLOCK.get()),
				keyFromItem(SCContent.REINFORCED_MANGROVE_LOG.get()),
				keyFromItem(SCContent.REINFORCED_MANGROVE_PLANKS.get()),
				keyFromItem(SCContent.REINFORCED_MANGROVE_WOOD.get()),
				keyFromItem(SCContent.REINFORCED_MOSSY_COBBLESTONE.get()),
				keyFromItem(SCContent.REINFORCED_MOSSY_STONE_BRICKS.get()),
				keyFromItem(SCContent.REINFORCED_MOSS_BLOCK.get()),
				keyFromItem(SCContent.REINFORCED_MUD.get()),
				keyFromItem(SCContent.REINFORCED_MUD_BRICKS.get()),
				keyFromItem(SCContent.REINFORCED_MYCELIUM.get()),
				keyFromItem(SCContent.REINFORCED_NETHERITE_BLOCK.get()),
				keyFromItem(SCContent.REINFORCED_NETHERRACK.get()),
				keyFromItem(SCContent.REINFORCED_NETHER_BRICKS.get()),
				keyFromItem(SCContent.REINFORCED_NETHER_WART_BLOCK.get()),
				keyFromItem(SCContent.REINFORCED_OAK_LOG.get()),
				keyFromItem(SCContent.REINFORCED_OAK_PLANKS.get()),
				keyFromItem(SCContent.REINFORCED_OAK_WOOD.get()),
				keyFromItem(SCContent.REINFORCED_OBSERVER.get()),
				keyFromItem(SCContent.REINFORCED_OBSIDIAN.get()),
				keyFromItem(SCContent.REINFORCED_OCHRE_FROGLIGHT.get()),
				keyFromItem(SCContent.REINFORCED_PACKED_ICE.get()),
				keyFromItem(SCContent.REINFORCED_PACKED_MUD.get()),
				keyFromItem(SCContent.REINFORCED_PALE_MOSS_BLOCK.get()),
				keyFromItem(SCContent.REINFORCED_PALE_OAK_LOG.get()),
				keyFromItem(SCContent.REINFORCED_PALE_OAK_PLANKS.get()),
				keyFromItem(SCContent.REINFORCED_PALE_OAK_WOOD.get()),
				keyFromItem(SCContent.REINFORCED_PEARLESCENT_FROGLIGHT.get()),
				keyFromItem(SCContent.REINFORCED_PODZOL.get()),
				keyFromItem(SCContent.REINFORCED_POLISHED_ANDESITE.get()),
				keyFromItem(SCContent.REINFORCED_POLISHED_BASALT.get()),
				keyFromItem(SCContent.REINFORCED_POLISHED_BLACKSTONE.get()),
				keyFromItem(SCContent.REINFORCED_POLISHED_BLACKSTONE_BRICKS.get()),
				keyFromItem(SCContent.REINFORCED_POLISHED_CINNABAR.get()),
				keyFromItem(SCContent.REINFORCED_POLISHED_DEEPSLATE.get()),
				keyFromItem(SCContent.REINFORCED_POLISHED_DIORITE.get()),
				keyFromItem(SCContent.REINFORCED_POLISHED_GRANITE.get()),
				keyFromItem(SCContent.REINFORCED_POLISHED_SULFUR.get()),
				keyFromItem(SCContent.REINFORCED_POLISHED_TUFF.get()),
				keyFromItem(SCContent.REINFORCED_PRISMARINE.get()),
				keyFromItem(SCContent.REINFORCED_PRISMARINE_BRICKS.get()),
				keyFromItem(SCContent.REINFORCED_PURPUR_BLOCK.get()),
				keyFromItem(SCContent.REINFORCED_PURPUR_PILLAR.get()),
				keyFromItem(SCContent.REINFORCED_QUARTZ_BLOCK.get()),
				keyFromItem(SCContent.REINFORCED_QUARTZ_BRICKS.get()),
				keyFromItem(SCContent.REINFORCED_QUARTZ_PILLAR.get()),
				keyFromItem(SCContent.REINFORCED_RAW_COPPER_BLOCK.get()),
				keyFromItem(SCContent.REINFORCED_RAW_GOLD_BLOCK.get()),
				keyFromItem(SCContent.REINFORCED_RAW_IRON_BLOCK.get()),
				keyFromItem(SCContent.REINFORCED_REDSTONE_LAMP.get()),
				keyFromItem(SCContent.REINFORCED_RED_NETHER_BRICKS.get()),
				keyFromItem(SCContent.REINFORCED_RED_SANDSTONE.get()),
				keyFromItem(SCContent.REINFORCED_RESIN_BLOCK.get()),
				keyFromItem(SCContent.REINFORCED_RESIN_BRICKS.get()),
				keyFromItem(SCContent.REINFORCED_ROOTED_DIRT.get()),
				keyFromItem(SCContent.REINFORCED_SANDSTONE.get()),
				keyFromItem(SCContent.REINFORCED_SEA_LANTERN.get()),
				keyFromItem(SCContent.REINFORCED_SHROOMLIGHT.get()),
				keyFromItem(SCContent.REINFORCED_SMOOTH_BASALT.get()),
				keyFromItem(SCContent.REINFORCED_SMOOTH_QUARTZ.get()),
				keyFromItem(SCContent.REINFORCED_SMOOTH_RED_SANDSTONE.get()),
				keyFromItem(SCContent.REINFORCED_SMOOTH_SANDSTONE.get()),
				keyFromItem(SCContent.REINFORCED_SMOOTH_STONE.get()),
				keyFromItem(SCContent.REINFORCED_SNOW_BLOCK.get()),
				keyFromItem(SCContent.REINFORCED_SOUL_SAND.get()),
				keyFromItem(SCContent.REINFORCED_SOUL_SOIL.get()),
				keyFromItem(SCContent.REINFORCED_SPRUCE_LOG.get()),
				keyFromItem(SCContent.REINFORCED_SPRUCE_PLANKS.get()),
				keyFromItem(SCContent.REINFORCED_SPRUCE_WOOD.get()),
				keyFromItem(SCContent.REINFORCED_STONE.get()),
				keyFromItem(SCContent.REINFORCED_STONE_BRICKS.get()),
				keyFromItem(SCContent.REINFORCED_STRIPPED_ACACIA_LOG.get()),
				keyFromItem(SCContent.REINFORCED_STRIPPED_ACACIA_WOOD.get()),
				keyFromItem(SCContent.REINFORCED_STRIPPED_BAMBOO_BLOCK.get()),
				keyFromItem(SCContent.REINFORCED_STRIPPED_BIRCH_LOG.get()),
				keyFromItem(SCContent.REINFORCED_STRIPPED_BIRCH_WOOD.get()),
				keyFromItem(SCContent.REINFORCED_STRIPPED_CHERRY_LOG.get()),
				keyFromItem(SCContent.REINFORCED_STRIPPED_CHERRY_WOOD.get()),
				keyFromItem(SCContent.REINFORCED_STRIPPED_CRIMSON_HYPHAE.get()),
				keyFromItem(SCContent.REINFORCED_STRIPPED_CRIMSON_STEM.get()),
				keyFromItem(SCContent.REINFORCED_STRIPPED_DARK_OAK_LOG.get()),
				keyFromItem(SCContent.REINFORCED_STRIPPED_DARK_OAK_WOOD.get()),
				keyFromItem(SCContent.REINFORCED_STRIPPED_JUNGLE_LOG.get()),
				keyFromItem(SCContent.REINFORCED_STRIPPED_JUNGLE_WOOD.get()),
				keyFromItem(SCContent.REINFORCED_STRIPPED_MANGROVE_LOG.get()),
				keyFromItem(SCContent.REINFORCED_STRIPPED_MANGROVE_WOOD.get()),
				keyFromItem(SCContent.REINFORCED_STRIPPED_OAK_LOG.get()),
				keyFromItem(SCContent.REINFORCED_STRIPPED_OAK_WOOD.get()),
				keyFromItem(SCContent.REINFORCED_STRIPPED_PALE_OAK_LOG.get()),
				keyFromItem(SCContent.REINFORCED_STRIPPED_PALE_OAK_WOOD.get()),
				keyFromItem(SCContent.REINFORCED_STRIPPED_SPRUCE_LOG.get()),
				keyFromItem(SCContent.REINFORCED_STRIPPED_SPRUCE_WOOD.get()),
				keyFromItem(SCContent.REINFORCED_STRIPPED_WARPED_HYPHAE.get()),
				keyFromItem(SCContent.REINFORCED_STRIPPED_WARPED_STEM.get()),
				keyFromItem(SCContent.REINFORCED_SULFUR.get()),
				keyFromItem(SCContent.REINFORCED_SULFUR_BRICKS.get()),
				keyFromItem(SCContent.REINFORCED_TUFF.get()),
				keyFromItem(SCContent.REINFORCED_TUFF_BRICKS.get()),
				keyFromItem(SCContent.REINFORCED_VERDANT_FROGLIGHT.get()),
				keyFromItem(SCContent.REINFORCED_WARPED_HYPHAE.get()),
				keyFromItem(SCContent.REINFORCED_WARPED_NYLIUM.get()),
				keyFromItem(SCContent.REINFORCED_WARPED_PLANKS.get()),
				keyFromItem(SCContent.REINFORCED_WARPED_STEM.get()),
				keyFromItem(SCContent.REINFORCED_WARPED_WART_BLOCK.get()))
				.addAll(keysFromCollection(SCContent.REINFORCED_CHISELED_COPPER)).addAll(keysFromCollection(SCContent.REINFORCED_CONCRETE))
				.addAll(keysFromCollection(SCContent.REINFORCED_COPPER_BLOCK)).addAll(keysFromCollection(SCContent.REINFORCED_COPPER_BULB))
				.addAll(keysFromCollection(SCContent.REINFORCED_CUT_COPPER)).addAll(keysFromCollection(SCContent.REINFORCED_DYED_TERRACOTTA))
				.addAll(keysFromCollection(SCContent.REINFORCED_GLAZED_TERRACOTTA)).addAll(keysFromCollection(SCContent.REINFORCED_WOOL));
		copy(SCTags.Blocks.REINFORCED_ACACIA_LOGS, SCTags.Items.REINFORCED_ACACIA_LOGS);
		copy(SCTags.Blocks.REINFORCED_BAMBOO_BLOCKS, SCTags.Items.REINFORCED_BAMBOO_BLOCKS);
		copy(SCTags.Blocks.REINFORCED_BARS, SCTags.Items.REINFORCED_BARS);
		copy(SCTags.Blocks.REINFORCED_BIRCH_LOGS, SCTags.Items.REINFORCED_BIRCH_LOGS);
		copy(SCTags.Blocks.REINFORCED_BUTTONS, SCTags.Items.REINFORCED_BUTTONS);
		copy(SCTags.Blocks.REINFORCED_CHERRY_LOGS, SCTags.Items.REINFORCED_CHERRY_LOGS);
		copy(SCTags.Blocks.REINFORCED_COBBLESTONE, SCTags.Items.REINFORCED_COBBLESTONE);
		copy(SCTags.Blocks.REINFORCED_CONCRETE, SCTags.Items.REINFORCED_CONCRETE);
		copy(SCTags.Blocks.REINFORCED_CRIMSON_STEMS, SCTags.Items.REINFORCED_CRIMSON_STEMS);
		copy(SCTags.Blocks.REINFORCED_DARK_OAK_LOGS, SCTags.Items.REINFORCED_DARK_OAK_LOGS);
		copy(SCTags.Blocks.REINFORCED_DIRT, SCTags.Items.REINFORCED_DIRT);
		copy(SCTags.Blocks.REINFORCED_END_STONES, SCTags.Items.REINFORCED_END_STONES);
		copy(SCTags.Blocks.REINFORCED_FENCES, SCTags.Items.REINFORCED_FENCES);
		copy(SCTags.Blocks.REINFORCED_GLASS_PANES, SCTags.Items.REINFORCED_GLASS_PANES);
		copy(SCTags.Blocks.REINFORCED_GLAZED_TERRACOTTA, SCTags.Items.REINFORCED_GLAZED_TERRACOTTA);
		copy(SCTags.Blocks.REINFORCED_GRASS_BLOCKS, SCTags.Items.REINFORCED_GRASS_BLOCKS);
		copy(SCTags.Blocks.REINFORCED_ICE, SCTags.Items.REINFORCED_ICE);
		copy(SCTags.Blocks.REINFORCED_JUNGLE_LOGS, SCTags.Items.REINFORCED_JUNGLE_LOGS);
		copy(SCTags.Blocks.REINFORCED_LOGS, SCTags.Items.REINFORCED_LOGS);
		copy(SCTags.Blocks.REINFORCED_MANGROVE_LOGS, SCTags.Items.REINFORCED_MANGROVE_LOGS);
		copy(SCTags.Blocks.REINFORCED_MOSS_BLOCKS, SCTags.Items.REINFORCED_MOSS_BLOCKS);
		copy(SCTags.Blocks.REINFORCED_MUD, SCTags.Items.REINFORCED_MUD);
		copy(SCTags.Blocks.REINFORCED_NYLIUM, SCTags.Items.REINFORCED_NYLIUM);
		copy(SCTags.Blocks.REINFORCED_OAK_LOGS, SCTags.Items.REINFORCED_OAK_LOGS);
		copy(SCTags.Blocks.REINFORCED_PALE_OAK_LOGS, SCTags.Items.REINFORCED_PALE_OAK_LOGS);
		copy(SCTags.Blocks.REINFORCED_PLANKS, SCTags.Items.REINFORCED_PLANKS);
		copy(SCTags.Blocks.REINFORCED_PRESSURE_PLATES, SCTags.Items.REINFORCED_PRESSURE_PLATES);
		copy(SCTags.Blocks.REINFORCED_SAND, SCTags.Items.REINFORCED_SAND);
		copy(SCTags.Blocks.REINFORCED_SLABS, SCTags.Items.REINFORCED_SLABS);
		copy(SCTags.Blocks.REINFORCED_SPRUCE_LOGS, SCTags.Items.REINFORCED_SPRUCE_LOGS);
		copy(SCTags.Blocks.REINFORCED_STAIRS, SCTags.Items.REINFORCED_STAIRS);
		copy(SCTags.Blocks.REINFORCED_STONE, SCTags.Items.REINFORCED_STONE);
		copy(SCTags.Blocks.REINFORCED_STONE_BRICKS, SCTags.Items.REINFORCED_STONE_BRICKS);
		copy(SCTags.Blocks.REINFORCED_STONE_PRESSURE_PLATES, SCTags.Items.REINFORCED_STONE_PRESSURE_PLATES);
		copy(SCTags.Blocks.REINFORCED_TERRACOTTA, SCTags.Items.REINFORCED_TERRACOTTA);
		copy(SCTags.Blocks.REINFORCED_WARPED_STEMS, SCTags.Items.REINFORCED_WARPED_STEMS);
		copy(SCTags.Blocks.REINFORCED_WOODEN_BUTTONS, SCTags.Items.REINFORCED_WOODEN_BUTTONS);
		copy(SCTags.Blocks.REINFORCED_WOODEN_FENCES, SCTags.Items.REINFORCED_WOODEN_FENCES);
		copy(SCTags.Blocks.REINFORCED_WOODEN_FENCE_GATES, SCTags.Items.REINFORCED_WOODEN_FENCE_GATES);
		copy(SCTags.Blocks.REINFORCED_WOODEN_PRESSURE_PLATES, SCTags.Items.REINFORCED_WOODEN_PRESSURE_PLATES);
		copy(SCTags.Blocks.REINFORCED_WOODEN_SHELVES, SCTags.Items.REINFORCED_WOODEN_SHELVES);
		copy(SCTags.Blocks.REINFORCED_WOODEN_SLABS, SCTags.Items.REINFORCED_WOODEN_SLABS);
		copy(SCTags.Blocks.REINFORCED_WOODEN_STAIRS, SCTags.Items.REINFORCED_WOODEN_STAIRS);
		copy(SCTags.Blocks.REINFORCED_WOOL, SCTags.Items.REINFORCED_WOOL);
		copy(SCTags.Blocks.REINFORCED_WOOL_CARPETS, SCTags.Items.REINFORCED_WOOL_CARPETS);

		//minecraft tags
		tag(ItemTags.BOOKSHELF_BOOKS).add(SCContent.SC_MANUAL.getKey());
		tag(BlockItemTags.BUTTONS.item()).addTag(SCTags.Items.REINFORCED_BUTTONS);
		tag(ItemTags.DAMPENS_VIBRATIONS).addTag(SCTags.Items.REINFORCED_WOOL);
		tag(ItemTags.DURABILITY_ENCHANTABLE).add(
				SCContent.WIRE_CUTTERS.getKey(),
				SCContent.UNIVERSAL_BLOCK_REINFORCER_LVL_1.getKey(),
				SCContent.UNIVERSAL_BLOCK_REINFORCER_LVL_2.getKey(),
				SCContent.UNIVERSAL_BLOCK_REMOVER.getKey(),
				SCContent.UNIVERSAL_OWNER_CHANGER.getKey());
		tag(ItemTags.CAULDRON_CAN_REMOVE_DYE).add(SCContent.BRIEFCASE.getKey(), SCContent.LENS.getKey());
		tag(ItemTags.FENCE_GATES).add(keyFromItem(SCContent.ELECTRIFIED_IRON_FENCE_GATE.get()));
		tag(BlockItemTags.FENCES.item()).add(keyFromItem(SCContent.ELECTRIFIED_IRON_FENCE.get()));
		tag(ItemTags.HANGING_SIGNS).addTag(SCTags.Items.SECRET_HANGING_SIGNS);
		tag(ItemTags.PIGLIN_LOVED).add(
				keyFromItem(SCContent.DEEPSLATE_GOLD_ORE_MINE.get()),
				keyFromItem(SCContent.GILDED_BLACKSTONE_MINE.get()),
				keyFromItem(SCContent.GOLD_ORE_MINE.get()),
				keyFromItem(SCContent.NETHER_GOLD_ORE_MINE.get()),
				keyFromItem(SCContent.REINFORCED_GOLD_BLOCK.get()),
				keyFromItem(SCContent.REINFORCED_RAW_GOLD_BLOCK.get()));
		tag(ItemTags.PIGLIN_REPELLENTS).add(keyFromItem(SCContent.REINFORCED_SOUL_LANTERN.get()));
		tag(ItemTags.RAILS).add(keyFromItem(SCContent.TRACK_MINE.get()));
		tag(BlockItemTags.SLABS.item()).addTag(SCTags.Items.REINFORCED_SLABS).add(keyFromItem(SCContent.CRYSTAL_QUARTZ_SLAB.get()), keyFromItem(SCContent.SMOOTH_CRYSTAL_QUARTZ_SLAB.get()));
		tag(BlockItemTags.STAIRS.item()).addTag(SCTags.Items.REINFORCED_STAIRS).add(keyFromItem(SCContent.CRYSTAL_QUARTZ_STAIRS.get()), keyFromItem(SCContent.SMOOTH_CRYSTAL_QUARTZ_STAIRS.get()));
		tag(ItemTags.SIGNS).addTag(SCTags.Items.SECRET_SIGNS);
		tag(ItemTags.SULFUR_CUBE_SWALLOWABLE).addTag(SCTags.Items.SULFUR_CUBE_ARCHETYPE_REINFORCED);
		tag(ItemTags.TERRACOTTA).addTag(SCTags.Items.REINFORCED_TERRACOTTA);
		tag(BlockItemTags.TRAPDOORS.item()).add(keyFromItem(SCContent.REINFORCED_IRON_TRAPDOOR.get()), keyFromItem(SCContent.KEYPAD_TRAPDOOR.get()), keyFromItem(SCContent.SCANNER_TRAPDOOR.get()));
		tag(ItemTags.VANISHING_ENCHANTABLE).add(
				SCContent.BRIEFCASE.getKey(),
				SCContent.WIRE_CUTTERS.getKey(),
				SCContent.UNIVERSAL_BLOCK_REINFORCER_LVL_1.getKey(),
				SCContent.UNIVERSAL_BLOCK_REINFORCER_LVL_2.getKey(),
				SCContent.UNIVERSAL_BLOCK_REMOVER.getKey(),
				SCContent.UNIVERSAL_OWNER_CHANGER.getKey());
		tag(ItemTags.WOODEN_FENCES).addTag(SCTags.Items.REINFORCED_WOODEN_FENCES);
		tag(ItemTags.WOOL_CARPETS).addTag(SCTags.Items.REINFORCED_WOOL_CARPETS);

		//NeoForge tags
		tag(Tags.Items.BUCKETS).add(SCContent.FAKE_LAVA_BUCKET.getKey(), SCContent.FAKE_WATER_BUCKET.getKey());
		tag(Tags.Items.CONCRETES).addTag(SCTags.Items.REINFORCED_CONCRETE);

		List<ColorCollection<? extends DeferredBlock<? extends Block>>> allColoredCollections =  List.of(
				SCContent.REINFORCED_CARPET,
				SCContent.REINFORCED_CONCRETE,
				SCContent.REINFORCED_GLAZED_TERRACOTTA,
				SCContent.REINFORCED_STAINED_GLASS,
				SCContent.REINFORCED_STAINED_GLASS_PANE,
				SCContent.REINFORCED_DYED_TERRACOTTA,
				SCContent.REINFORCED_WOOL
		);
		ColorCollection<TagKey<Item>> dyedTags = new ColorCollection<>(
				Tags.Items.DYED_WHITE,
				Tags.Items.DYED_ORANGE,
				Tags.Items.DYED_MAGENTA,
				Tags.Items.DYED_LIGHT_BLUE,
				Tags.Items.DYED_YELLOW,
				Tags.Items.DYED_LIME,
				Tags.Items.DYED_PINK,
				Tags.Items.DYED_GRAY,
				Tags.Items.DYED_LIGHT_GRAY,
				Tags.Items.DYED_CYAN,
				Tags.Items.DYED_PURPLE,
				Tags.Items.DYED_BLUE,
				Tags.Items.DYED_BROWN,
				Tags.Items.DYED_GREEN,
				Tags.Items.DYED_RED,
				Tags.Items.DYED_BLACK
		);
		ColorCollection<List<ResourceKey<Item>>> allColoredEntries = new ColorCollection<>(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

		for (ColorCollection<? extends DeferredBlock<? extends Block>> collection : allColoredCollections){
			allColoredEntries = ColorCollection.zipMap(allColoredEntries, collection, (list, entry) -> {
				list.add(keyFromItem(entry));
				return list;
			});
		}

		ColorCollection.zipApply(dyedTags, allColoredEntries, (tag, blockList) -> tag(tag).addAll(blockList));
		tag(Tags.Items.FENCE_GATES_WOODEN).addTag(SCTags.Items.REINFORCED_WOODEN_FENCE_GATES);
		tag(Tags.Items.GLAZED_TERRACOTTAS).addTag(SCTags.Items.REINFORCED_GLAZED_TERRACOTTA);
		//@formatter:on
	}

	@Override
	public String getName() {
		return "SecurityCraft Item Tags";
	}

	private static Stream<ResourceKey<Item>> keysFromCollection(ColorCollection<? extends DeferredBlock<? extends Block>> collection) {
		return collection.asList().stream().map(ItemTagGenerator::keyFromItem);
	}

	private static Stream<ResourceKey<Item>> keysFromCollection(WeatheringCopperCollection.ByState<? extends DeferredBlock<? extends Block>> collection) {
		ImmutableList.Builder<ResourceKey<Item>> builder = ImmutableList.builderWithExpectedSize(4);

		collection.forEach(e -> builder.add(keyFromItem(e)));
		return builder.build().stream();
	}

	private static ResourceKey<Item> keyFromItem(ItemLike item) {
		return item.asItem().builtInRegistryHolder().key();
	}
}
