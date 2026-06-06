package net.geforcemods.securitycraft.datagen;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ColorCollection;
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
				SCContent.REINFORCED_COBBLESTONE.get().asItem().builtInRegistryHolder().key(),
				SCContent.REINFORCED_BLACKSTONE.get().asItem().builtInRegistryHolder().key(),
				SCContent.REINFORCED_COBBLED_DEEPSLATE.get().asItem().builtInRegistryHolder().key());
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
		tag(ItemTags.FENCE_GATES).add(SCContent.ELECTRIFIED_IRON_FENCE_GATE.get().asItem().builtInRegistryHolder().key());
		tag(BlockItemTags.FENCES.item()).add(SCContent.ELECTRIFIED_IRON_FENCE.get().asItem().builtInRegistryHolder().key());
		tag(ItemTags.HANGING_SIGNS).addTag(SCTags.Items.SECRET_HANGING_SIGNS);
		tag(ItemTags.PIGLIN_LOVED).add(
				SCContent.DEEPSLATE_GOLD_ORE_MINE.get().asItem().builtInRegistryHolder().key(),
				SCContent.GILDED_BLACKSTONE_MINE.get().asItem().builtInRegistryHolder().key(),
				SCContent.GOLD_ORE_MINE.get().asItem().builtInRegistryHolder().key(),
				SCContent.NETHER_GOLD_ORE_MINE.get().asItem().builtInRegistryHolder().key(),
				SCContent.REINFORCED_GOLD_BLOCK.get().asItem().builtInRegistryHolder().key(),
				SCContent.REINFORCED_RAW_GOLD_BLOCK.get().asItem().builtInRegistryHolder().key());
		tag(ItemTags.PIGLIN_REPELLENTS).add(SCContent.REINFORCED_SOUL_LANTERN.get().asItem().builtInRegistryHolder().key());
		tag(ItemTags.RAILS).add(SCContent.TRACK_MINE.get().asItem().builtInRegistryHolder().key());
		tag(BlockItemTags.SLABS.item()).addTag(SCTags.Items.REINFORCED_SLABS).add(SCContent.CRYSTAL_QUARTZ_SLAB.get().asItem().builtInRegistryHolder().key(), SCContent.SMOOTH_CRYSTAL_QUARTZ_SLAB.get().asItem().builtInRegistryHolder().key());
		tag(BlockItemTags.STAIRS.item()).addTag(SCTags.Items.REINFORCED_STAIRS).add(SCContent.CRYSTAL_QUARTZ_STAIRS.get().asItem().builtInRegistryHolder().key(), SCContent.SMOOTH_CRYSTAL_QUARTZ_STAIRS.get().asItem().builtInRegistryHolder().key());
		tag(ItemTags.SIGNS).addTag(SCTags.Items.SECRET_SIGNS);
		tag(ItemTags.TERRACOTTA).addTag(SCTags.Items.REINFORCED_TERRACOTTA);
		tag(BlockItemTags.TRAPDOORS.item()).add(SCContent.REINFORCED_IRON_TRAPDOOR.get().asItem().builtInRegistryHolder().key(), SCContent.KEYPAD_TRAPDOOR.get().asItem().builtInRegistryHolder().key(), SCContent.SCANNER_TRAPDOOR.get().asItem().builtInRegistryHolder().key());
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
				list.add(entry.asItem().builtInRegistryHolder().key());
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
}
