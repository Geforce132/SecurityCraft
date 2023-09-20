package net.geforcemods.securitycraft.datagen;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SCTags;
import net.geforcemods.securitycraft.SecurityCraft;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ItemTagGenerator extends ItemTagsProvider {
	protected ItemTagGenerator(DataGenerator dataGenerator, BlockTagsProvider blockTagsProvider, ExistingFileHelper existingFileHelper) {
		super(dataGenerator, blockTagsProvider, SecurityCraft.MODID, existingFileHelper);
	}

	@Override
	protected void addTags() {
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
		tag(SCTags.Items.MODULES).add(
				SCContent.ALLOWLIST_MODULE.get(),
				SCContent.DENYLIST_MODULE.get(),
				SCContent.DISGUISE_MODULE.get(),
				SCContent.HARMING_MODULE.get(),
				SCContent.REDSTONE_MODULE.get(),
				SCContent.SMART_MODULE.get(),
				SCContent.SPEED_MODULE.get(),
				SCContent.STORAGE_MODULE.get());
		tag(SCTags.Items.REINFORCED_STONE_CRAFTING_MATERIALS).add(
				SCContent.REINFORCED_COBBLESTONE.get().asItem(),
				SCContent.REINFORCED_BLACKSTONE.get().asItem(),
				SCContent.REINFORCED_COBBLED_DEEPSLATE.get().asItem());
		copy(SCTags.Blocks.REINFORCED_ACACIA_LOGS, SCTags.Items.REINFORCED_ACACIA_LOGS);
		copy(SCTags.Blocks.REINFORCED_BIRCH_LOGS, SCTags.Items.REINFORCED_BIRCH_LOGS);
		copy(SCTags.Blocks.REINFORCED_BUTTONS, SCTags.Items.REINFORCED_BUTTONS);
		copy(SCTags.Blocks.REINFORCED_CARPETS, SCTags.Items.REINFORCED_CARPETS);
		copy(SCTags.Blocks.REINFORCED_COBBLESTONE, SCTags.Items.REINFORCED_COBBLESTONE);
		copy(SCTags.Blocks.REINFORCED_CRIMSON_STEMS, SCTags.Items.REINFORCED_CRIMSON_STEMS);
		copy(SCTags.Blocks.REINFORCED_DARK_OAK_LOGS, SCTags.Items.REINFORCED_DARK_OAK_LOGS);
		copy(SCTags.Blocks.REINFORCED_DIRT, SCTags.Items.REINFORCED_DIRT);
		copy(SCTags.Blocks.REINFORCED_END_STONES, SCTags.Items.REINFORCED_END_STONES);
		copy(SCTags.Blocks.REINFORCED_GLASS_PANES, SCTags.Items.REINFORCED_GLASS_PANES);
		copy(SCTags.Blocks.REINFORCED_ICE, SCTags.Items.REINFORCED_ICE);
		copy(SCTags.Blocks.REINFORCED_JUNGLE_LOGS, SCTags.Items.REINFORCED_JUNGLE_LOGS);
		copy(SCTags.Blocks.REINFORCED_LOGS, SCTags.Items.REINFORCED_LOGS);
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
		copy(SCTags.Blocks.REINFORCED_WOODEN_PRESSURE_PLATES, SCTags.Items.REINFORCED_WOODEN_PRESSURE_PLATES);
		copy(SCTags.Blocks.REINFORCED_WOODEN_SLABS, SCTags.Items.REINFORCED_WOODEN_SLABS);
		copy(SCTags.Blocks.REINFORCED_WOODEN_STAIRS, SCTags.Items.REINFORCED_WOODEN_STAIRS);
		copy(SCTags.Blocks.REINFORCED_WOOL, SCTags.Items.REINFORCED_WOOL);
		tag(SCTags.Items.SECRET_SIGNS).add(
				SCContent.SECRET_ACACIA_SIGN_ITEM.get(),
				SCContent.SECRET_BIRCH_SIGN_ITEM.get(),
				SCContent.SECRET_CRIMSON_SIGN_ITEM.get(),
				SCContent.SECRET_DARK_OAK_SIGN_ITEM.get(),
				SCContent.SECRET_JUNGLE_SIGN_ITEM.get(),
				SCContent.SECRET_OAK_SIGN_ITEM.get(),
				SCContent.SECRET_SPRUCE_SIGN_ITEM.get(),
				SCContent.SECRET_WARPED_SIGN_ITEM.get());

		//minecraft tags
		tag(ItemTags.BUTTONS).addTag(SCTags.Items.REINFORCED_BUTTONS);
		tag(ItemTags.CARPETS).addTag(SCTags.Items.REINFORCED_CARPETS);
		tag(ItemTags.FENCES).add(SCContent.IRON_FENCE.get().asItem());
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
		//@formatter:on
	}

	@Override
	public String getName() {
		return "SecurityCraft Item Tags";
	}
}
