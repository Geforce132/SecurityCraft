package net.geforcemods.securitycraft.datagen;

import java.util.concurrent.CompletableFuture;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SCTags;
import net.geforcemods.securitycraft.SecurityCraft;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ItemTagGenerator extends ItemTagsProvider {
	public ItemTagGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTagsProvider, ExistingFileHelper existingFileHelper) {
		super(output, lookupProvider, blockTagsProvider, SecurityCraft.MODID, existingFileHelper);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		//@formatter:off
		//securitycraft tags
		tag(SCTags.Items.CAN_INTERACT_WITH_DOORS).add(
				SCContent.ADMIN_TOOL.get(),
				SCContent.CODEBREAKER.get(),
				SCContent.SONIC_SECURITY_SYSTEM_ITEM.get(),
				SCContent.UNIVERSAL_BLOCK_MODIFIER.get(),
				SCContent.UNIVERSAL_BLOCK_REMOVER.get(),
				SCContent.UNIVERSAL_KEY_CHANGER.get(),
				SCContent.UNIVERSAL_OWNER_CHANGER.get())
		.addTag(SCTags.Items.MODULES);
		tag(SCTags.Items.KEYCARDS).add(
				SCContent.KEYCARD_LVL_1.get(),
				SCContent.KEYCARD_LVL_2.get(),
				SCContent.KEYCARD_LVL_3.get(),
				SCContent.KEYCARD_LVL_4.get(),
				SCContent.KEYCARD_LVL_5.get());
		tag(SCTags.Items.KEYCARD_HOLDER_CAN_HOLD).addTag(SCTags.Items.KEYCARDS).add(SCContent.LIMITED_USE_KEYCARD.getKey());
		tag(SCTags.Items.MODULES).add(
				SCContent.ALLOWLIST_MODULE.get(),
				SCContent.DENYLIST_MODULE.get(),
				SCContent.DISGUISE_MODULE.get(),
				SCContent.HARMING_MODULE.get(),
				SCContent.REDSTONE_MODULE.get(),
				SCContent.SMART_MODULE.get(),
				SCContent.SPEED_MODULE.get(),
				SCContent.STORAGE_MODULE.get());
		tag(SCTags.Items.REINFORCED_MOSS).add(SCContent.REINFORCED_MOSS_BLOCK.get().asItem());
		tag(SCTags.Items.REINFORCED_STONE_CRAFTING_MATERIALS).add(
				SCContent.REINFORCED_COBBLESTONE.get().asItem(),
				SCContent.REINFORCED_BLACKSTONE.get().asItem(),
				SCContent.REINFORCED_COBBLED_DEEPSLATE.get().asItem());
		tag(SCTags.Items.SECRET_HANGING_SIGNS).add(
				SCContent.SECRET_ACACIA_HANGING_SIGN_ITEM.get(),
				SCContent.SECRET_BAMBOO_HANGING_SIGN_ITEM.get(),
				SCContent.SECRET_BIRCH_HANGING_SIGN_ITEM.get(),
				SCContent.SECRET_CHERRY_HANGING_SIGN_ITEM.get(),
				SCContent.SECRET_CRIMSON_HANGING_SIGN_ITEM.get(),
				SCContent.SECRET_DARK_OAK_HANGING_SIGN_ITEM.get(),
				SCContent.SECRET_JUNGLE_HANGING_SIGN_ITEM.get(),
				SCContent.SECRET_MANGROVE_HANGING_SIGN_ITEM.get(),
				SCContent.SECRET_OAK_HANGING_SIGN_ITEM.get(),
				SCContent.SECRET_SPRUCE_HANGING_SIGN_ITEM.get(),
				SCContent.SECRET_WARPED_HANGING_SIGN_ITEM.get());
		tag(SCTags.Items.SECRET_SIGNS).add(
				SCContent.SECRET_ACACIA_SIGN_ITEM.get(),
				SCContent.SECRET_BAMBOO_SIGN_ITEM.get(),
				SCContent.SECRET_BIRCH_SIGN_ITEM.get(),
				SCContent.SECRET_CHERRY_SIGN_ITEM.get(),
				SCContent.SECRET_CRIMSON_SIGN_ITEM.get(),
				SCContent.SECRET_DARK_OAK_SIGN_ITEM.get(),
				SCContent.SECRET_JUNGLE_SIGN_ITEM.get(),
				SCContent.SECRET_MANGROVE_SIGN_ITEM.get(),
				SCContent.SECRET_OAK_SIGN_ITEM.get(),
				SCContent.SECRET_SPRUCE_SIGN_ITEM.get(),
				SCContent.SECRET_WARPED_SIGN_ITEM.get());
		copy(SCTags.Blocks.REINFORCED_ACACIA_LOGS, SCTags.Items.REINFORCED_ACACIA_LOGS);
		copy(SCTags.Blocks.REINFORCED_BAMBOO_BLOCKS, SCTags.Items.REINFORCED_BAMBOO_BLOCKS);
		copy(SCTags.Blocks.REINFORCED_BIRCH_LOGS, SCTags.Items.REINFORCED_BIRCH_LOGS);
		copy(SCTags.Blocks.REINFORCED_BUTTONS, SCTags.Items.REINFORCED_BUTTONS);
		copy(SCTags.Blocks.REINFORCED_CHERRY_LOGS, SCTags.Items.REINFORCED_CHERRY_LOGS);
		copy(SCTags.Blocks.REINFORCED_COBBLESTONE, SCTags.Items.REINFORCED_COBBLESTONE);
		copy(SCTags.Blocks.REINFORCED_CRIMSON_STEMS, SCTags.Items.REINFORCED_CRIMSON_STEMS);
		copy(SCTags.Blocks.REINFORCED_DARK_OAK_LOGS, SCTags.Items.REINFORCED_DARK_OAK_LOGS);
		copy(SCTags.Blocks.REINFORCED_DIRT, SCTags.Items.REINFORCED_DIRT);
		copy(SCTags.Blocks.REINFORCED_END_STONES, SCTags.Items.REINFORCED_END_STONES);
		copy(SCTags.Blocks.REINFORCED_FENCES, SCTags.Items.REINFORCED_FENCES);
		copy(SCTags.Blocks.REINFORCED_GLASS_PANES, SCTags.Items.REINFORCED_GLASS_PANES);
		copy(SCTags.Blocks.REINFORCED_ICE, SCTags.Items.REINFORCED_ICE);
		copy(SCTags.Blocks.REINFORCED_JUNGLE_LOGS, SCTags.Items.REINFORCED_JUNGLE_LOGS);
		copy(SCTags.Blocks.REINFORCED_LOGS, SCTags.Items.REINFORCED_LOGS);
		copy(SCTags.Blocks.REINFORCED_MANGROVE_LOGS, SCTags.Items.REINFORCED_MANGROVE_LOGS);
		copy(SCTags.Blocks.REINFORCED_NYLIUM, SCTags.Items.REINFORCED_NYLIUM);
		copy(SCTags.Blocks.REINFORCED_OAK_LOGS, SCTags.Items.REINFORCED_OAK_LOGS);
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
		tag(ItemTags.BOOKSHELF_BOOKS).add(SCContent.SC_MANUAL.get());
		tag(ItemTags.BUTTONS).addTag(SCTags.Items.REINFORCED_BUTTONS);
		tag(ItemTags.DAMPENS_VIBRATIONS).addTag(SCTags.Items.REINFORCED_WOOL);
		tag(ItemTags.FENCE_GATES).add(SCContent.ELECTRIFIED_IRON_FENCE_GATE.get().asItem());
		tag(ItemTags.FENCES).add(SCContent.ELECTRIFIED_IRON_FENCE.get().asItem());
		tag(ItemTags.PIGLIN_LOVED).add(
				SCContent.DEEPSLATE_GOLD_ORE_MINE.get().asItem(),
				SCContent.GILDED_BLACKSTONE_MINE.get().asItem(),
				SCContent.GOLD_ORE_MINE.get().asItem(),
				SCContent.NETHER_GOLD_ORE_MINE.get().asItem(),
				SCContent.REINFORCED_GOLD_BLOCK.get().asItem(),
				SCContent.REINFORCED_RAW_GOLD_BLOCK.get().asItem());
		tag(ItemTags.PIGLIN_REPELLENTS).add(SCContent.REINFORCED_SOUL_LANTERN.get().asItem());
		tag(ItemTags.RAILS).add(SCContent.TRACK_MINE.get().asItem());
		tag(ItemTags.SLABS).addTag(SCTags.Items.REINFORCED_SLABS).add(SCContent.CRYSTAL_QUARTZ_SLAB.get().asItem(), SCContent.SMOOTH_CRYSTAL_QUARTZ_SLAB.get().asItem());
		tag(ItemTags.STAIRS).addTag(SCTags.Items.REINFORCED_STAIRS).add(SCContent.CRYSTAL_QUARTZ_STAIRS.get().asItem(), SCContent.SMOOTH_CRYSTAL_QUARTZ_STAIRS.get().asItem());
		tag(ItemTags.SIGNS).addTag(SCTags.Items.SECRET_SIGNS);
		tag(ItemTags.TERRACOTTA).addTag(SCTags.Items.REINFORCED_TERRACOTTA);
		tag(ItemTags.TRAPDOORS).add(SCContent.REINFORCED_IRON_TRAPDOOR.get().asItem(), SCContent.KEYPAD_TRAPDOOR.get().asItem(), SCContent.SCANNER_TRAPDOOR.get().asItem());
		tag(ItemTags.WOODEN_FENCES).addTag(SCTags.Items.REINFORCED_WOODEN_FENCES);
		tag(ItemTags.WOOL_CARPETS).addTag(SCTags.Items.REINFORCED_WOOL_CARPETS);

		//Forge tags
		tag(Tags.Items.FENCE_GATES_WOODEN).addTag(SCTags.Items.REINFORCED_WOODEN_FENCE_GATES);
		//@formatter:on
	}

	@Override
	public String getName() {
		return "SecurityCraft Item Tags";
	}
}
