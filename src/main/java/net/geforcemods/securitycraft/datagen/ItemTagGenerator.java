package net.geforcemods.securitycraft.datagen;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SCTags;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;
import net.minecraft.tags.ItemTags;

public class ItemTagGenerator extends ItemTagsProvider
{
	protected ItemTagGenerator(DataGenerator dataGenerator, BlockTagsProvider blockTagsProvider)
	{
		super(dataGenerator, blockTagsProvider);
	}

	@Override
	protected void registerTags()
	{
		//securitycraft tags
		copy(SCTags.Blocks.REINFORCED_ACACIA_LOGS, SCTags.Items.REINFORCED_ACACIA_LOGS);
		copy(SCTags.Blocks.REINFORCED_BIRCH_LOGS, SCTags.Items.REINFORCED_BIRCH_LOGS);
		copy(SCTags.Blocks.REINFORCED_BUTTONS, SCTags.Items.REINFORCED_BUTTONS);
		copy(SCTags.Blocks.REINFORCED_CARPETS, SCTags.Items.REINFORCED_CARPETS);
		copy(SCTags.Blocks.REINFORCED_COBBLESTONE, SCTags.Items.REINFORCED_COBBLESTONE);
		copy(SCTags.Blocks.REINFORCED_DARK_OAK_LOGS, SCTags.Items.REINFORCED_DARK_OAK_LOGS);
		copy(SCTags.Blocks.REINFORCED_DIRT, SCTags.Items.REINFORCED_DIRT);
		copy(SCTags.Blocks.REINFORCED_END_STONES, SCTags.Items.REINFORCED_END_STONES);
		copy(SCTags.Blocks.REINFORCED_ICE, SCTags.Items.REINFORCED_ICE);
		copy(SCTags.Blocks.REINFORCED_JUNGLE_LOGS, SCTags.Items.REINFORCED_JUNGLE_LOGS);
		copy(SCTags.Blocks.REINFORCED_LOGS, SCTags.Items.REINFORCED_LOGS);
		copy(SCTags.Blocks.REINFORCED_OAK_LOGS, SCTags.Items.REINFORCED_OAK_LOGS);
		copy(SCTags.Blocks.REINFORCED_PLANKS, SCTags.Items.REINFORCED_PLANKS);
		copy(SCTags.Blocks.REINFORCED_SAND, SCTags.Items.REINFORCED_SAND);
		copy(SCTags.Blocks.REINFORCED_SLABS, SCTags.Items.REINFORCED_SLABS);
		copy(SCTags.Blocks.REINFORCED_SPRUCE_LOGS, SCTags.Items.REINFORCED_SPRUCE_LOGS);
		copy(SCTags.Blocks.REINFORCED_STAIRS, SCTags.Items.REINFORCED_STAIRS);
		copy(SCTags.Blocks.REINFORCED_STONE, SCTags.Items.REINFORCED_STONE);
		copy(SCTags.Blocks.REINFORCED_STONE_BRICKS, SCTags.Items.REINFORCED_STONE_BRICKS);
		copy(SCTags.Blocks.REINFORCED_WOODEN_SLABS, SCTags.Items.REINFORCED_WOODEN_SLABS);
		copy(SCTags.Blocks.REINFORCED_WOODEN_STAIRS, SCTags.Items.REINFORCED_WOODEN_STAIRS);
		copy(SCTags.Blocks.REINFORCED_WOOL, SCTags.Items.REINFORCED_WOOL);
		getOrCreateBuilder(SCTags.Items.SECRET_SIGNS).add(
				SCContent.SECRET_ACACIA_SIGN_ITEM.get(),
				SCContent.SECRET_BIRCH_SIGN_ITEM.get(),
				SCContent.SECRET_DARK_OAK_SIGN_ITEM.get(),
				SCContent.SECRET_JUNGLE_SIGN_ITEM.get(),
				SCContent.SECRET_OAK_SIGN_ITEM.get(),
				SCContent.SECRET_SPRUCE_SIGN_ITEM.get());

		//minecraft tags
		getOrCreateBuilder(ItemTags.CARPETS).addTag(SCTags.Items.REINFORCED_CARPETS);
		getOrCreateBuilder(ItemTags.FENCES).add(SCContent.IRON_FENCE.get().asItem());
		getOrCreateBuilder(ItemTags.RAILS).add(SCContent.TRACK_MINE.get().asItem());
		getOrCreateBuilder(ItemTags.SLABS).addTag(SCTags.Items.REINFORCED_SLABS).add(SCContent.CRYSTAL_QUARTZ_SLAB.get().asItem());
		getOrCreateBuilder(ItemTags.STAIRS).addTag(SCTags.Items.REINFORCED_STAIRS).add(SCContent.STAIRS_CRYSTAL_QUARTZ.get().asItem());
		getOrCreateBuilder(ItemTags.SIGNS).addTag(SCTags.Items.SECRET_SIGNS);
		getOrCreateBuilder(ItemTags.TRAPDOORS).add(SCContent.REINFORCED_IRON_TRAPDOOR.get().asItem());
	}

	@Override
	public String getName()
	{
		return "SecurityCraft Item Tags";
	}
}
