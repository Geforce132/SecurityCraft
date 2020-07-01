package net.geforcemods.securitycraft.datagen;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SCTags;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;
import net.minecraft.tags.ItemTags;

//func_240521_a_ = copy
//func_240522_a_ = getBuilder
//func_240531_a_ = add
//func_240534_a_ = add
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
		func_240521_a_(SCTags.Blocks.REINFORCED_ACACIA_LOGS, SCTags.Items.REINFORCED_ACACIA_LOGS);
		func_240521_a_(SCTags.Blocks.REINFORCED_BIRCH_LOGS, SCTags.Items.REINFORCED_BIRCH_LOGS);
		func_240521_a_(SCTags.Blocks.REINFORCED_BUTTONS, SCTags.Items.REINFORCED_BUTTONS);
		func_240521_a_(SCTags.Blocks.REINFORCED_CARPETS, SCTags.Items.REINFORCED_CARPETS);
		func_240521_a_(SCTags.Blocks.REINFORCED_COBBLESTONE, SCTags.Items.REINFORCED_COBBLESTONE);
		func_240521_a_(SCTags.Blocks.REINFORCED_DARK_OAK_LOGS, SCTags.Items.REINFORCED_DARK_OAK_LOGS);
		func_240521_a_(SCTags.Blocks.REINFORCED_DIRT, SCTags.Items.REINFORCED_DIRT);
		func_240521_a_(SCTags.Blocks.REINFORCED_END_STONES, SCTags.Items.REINFORCED_END_STONES);
		func_240521_a_(SCTags.Blocks.REINFORCED_ICE, SCTags.Items.REINFORCED_ICE);
		func_240521_a_(SCTags.Blocks.REINFORCED_JUNGLE_LOGS, SCTags.Items.REINFORCED_JUNGLE_LOGS);
		func_240521_a_(SCTags.Blocks.REINFORCED_LOGS, SCTags.Items.REINFORCED_LOGS);
		func_240521_a_(SCTags.Blocks.REINFORCED_OAK_LOGS, SCTags.Items.REINFORCED_OAK_LOGS);
		func_240521_a_(SCTags.Blocks.REINFORCED_PLANKS, SCTags.Items.REINFORCED_PLANKS);
		func_240521_a_(SCTags.Blocks.REINFORCED_SAND, SCTags.Items.REINFORCED_SAND);
		func_240521_a_(SCTags.Blocks.REINFORCED_SLABS, SCTags.Items.REINFORCED_SLABS);
		func_240521_a_(SCTags.Blocks.REINFORCED_SPRUCE_LOGS, SCTags.Items.REINFORCED_SPRUCE_LOGS);
		func_240521_a_(SCTags.Blocks.REINFORCED_STAIRS, SCTags.Items.REINFORCED_STAIRS);
		func_240521_a_(SCTags.Blocks.REINFORCED_STONE, SCTags.Items.REINFORCED_STONE);
		func_240521_a_(SCTags.Blocks.REINFORCED_STONE_BRICKS, SCTags.Items.REINFORCED_STONE_BRICKS);
		func_240521_a_(SCTags.Blocks.REINFORCED_WOODEN_SLABS, SCTags.Items.REINFORCED_WOODEN_SLABS);
		func_240521_a_(SCTags.Blocks.REINFORCED_WOODEN_STAIRS, SCTags.Items.REINFORCED_WOODEN_STAIRS);
		func_240521_a_(SCTags.Blocks.REINFORCED_WOOL, SCTags.Items.REINFORCED_WOOL);
		func_240522_a_(SCTags.Items.SECRET_SIGNS).func_240534_a_(
				SCContent.SECRET_ACACIA_SIGN_ITEM.get(),
				SCContent.SECRET_BIRCH_SIGN_ITEM.get(),
				SCContent.SECRET_DARK_OAK_SIGN_ITEM.get(),
				SCContent.SECRET_JUNGLE_SIGN_ITEM.get(),
				SCContent.SECRET_OAK_SIGN_ITEM.get(),
				SCContent.SECRET_SPRUCE_SIGN_ITEM.get());

		//minecraft tags
		func_240522_a_(ItemTags.CARPETS).func_240531_a_(SCTags.Items.REINFORCED_CARPETS);
		func_240522_a_(ItemTags.FENCES).func_240534_a_(SCContent.IRON_FENCE.get().asItem());
		func_240522_a_(ItemTags.RAILS).func_240534_a_(SCContent.TRACK_MINE.get().asItem());
		func_240522_a_(ItemTags.SLABS).func_240531_a_(SCTags.Items.REINFORCED_SLABS).func_240534_a_(SCContent.CRYSTAL_QUARTZ_SLAB.get().asItem());
		func_240522_a_(ItemTags.STAIRS).func_240531_a_(SCTags.Items.REINFORCED_STAIRS).func_240534_a_(SCContent.STAIRS_CRYSTAL_QUARTZ.get().asItem());
		func_240522_a_(ItemTags.SIGNS).func_240531_a_(SCTags.Items.SECRET_SIGNS);
		func_240522_a_(ItemTags.TRAPDOORS).func_240534_a_(SCContent.REINFORCED_IRON_TRAPDOOR.get().asItem());
	}

	@Override
	public String getName()
	{
		return "SecurityCraft Item Tags";
	}
}
