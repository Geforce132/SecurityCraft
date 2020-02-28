package net.geforcemods.securitycraft.datagen;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SCTags;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;
import net.minecraft.tags.ItemTags;

public class ItemTagGenerator extends ItemTagsProvider
{
	protected ItemTagGenerator(DataGenerator dataGenerator)
	{
		super(dataGenerator);
	}

	@Override
	protected void registerTags()
	{
		//securitycraft tags
		copy(SCTags.Blocks.REINFORCED_ACACIA_LOGS, SCTags.Items.REINFORCED_ACACIA_LOGS);
		copy(SCTags.Blocks.REINFORCED_BIRCH_LOGS, SCTags.Items.REINFORCED_BIRCH_LOGS);
		copy(SCTags.Blocks.REINFORCED_CARPETS, SCTags.Items.REINFORCED_CARPETS);
		copy(SCTags.Blocks.REINFORCED_DARK_OAK_LOGS, SCTags.Items.REINFORCED_DARK_OAK_LOGS);
		copy(SCTags.Blocks.REINFORCED_JUNGLE_LOGS, SCTags.Items.REINFORCED_JUNGLE_LOGS);
		copy(SCTags.Blocks.REINFORCED_LOGS, SCTags.Items.REINFORCED_LOGS);
		copy(SCTags.Blocks.REINFORCED_OAK_LOGS, SCTags.Items.REINFORCED_OAK_LOGS);
		copy(SCTags.Blocks.REINFORCED_PLANKS, SCTags.Items.REINFORCED_PLANKS);
		copy(SCTags.Blocks.REINFORCED_SLABS, SCTags.Items.REINFORCED_SLABS);
		copy(SCTags.Blocks.REINFORCED_SPRUCE_LOGS, SCTags.Items.REINFORCED_SPRUCE_LOGS);
		copy(SCTags.Blocks.REINFORCED_STAIRS, SCTags.Items.REINFORCED_STAIRS);
		copy(SCTags.Blocks.REINFORCED_STONE, SCTags.Items.REINFORCED_STONE);
		copy(SCTags.Blocks.REINFORCED_STONE_BRICKS, SCTags.Items.REINFORCED_STONE_BRICKS);
		copy(SCTags.Blocks.REINFORCED_WOODEN_SLABS, SCTags.Items.REINFORCED_WOODEN_SLABS);
		copy(SCTags.Blocks.REINFORCED_WOODEN_STAIRS, SCTags.Items.REINFORCED_WOODEN_STAIRS);
		copy(SCTags.Blocks.REINFORCED_WOOL, SCTags.Items.REINFORCED_WOOL);
		getBuilder(SCTags.Items.SECRET_SIGNS).add(
				SCContent.SECRET_ACACIA_SIGN_ITEM.get(),
				SCContent.SECRET_BIRCH_SIGN_ITEM.get(),
				SCContent.SECRET_DARK_OAK_SIGN_ITEM.get(),
				SCContent.SECRET_JUNGLE_SIGN_ITEM.get(),
				SCContent.SECRET_OAK_SIGN_ITEM.get(),
				SCContent.SECRET_SPRUCE_SIGN_ITEM.get());

		//minecraft tags
		getBuilder(ItemTags.CARPETS).add(SCTags.Items.REINFORCED_CARPETS);
		getBuilder(ItemTags.FENCES).add(SCContent.IRON_FENCE.get().asItem());
		getBuilder(ItemTags.RAILS).add(SCContent.TRACK_MINE.get().asItem());
		getBuilder(ItemTags.SLABS).add(SCTags.Items.REINFORCED_SLABS).add(SCContent.CRYSTAL_QUARTZ_SLAB.get().asItem());
		getBuilder(ItemTags.STAIRS).add(SCTags.Items.REINFORCED_STAIRS).add(SCContent.STAIRS_CRYSTAL_QUARTZ.get().asItem());
		getBuilder(ItemTags.SIGNS).add(SCTags.Items.SECRET_SIGNS);
		getBuilder(ItemTags.TRAPDOORS).add(SCContent.REINFORCED_IRON_TRAPDOOR.get().asItem());
	}

	@Override
	public String getName()
	{
		return "SecurityCraft Item Tags";
	}
}
