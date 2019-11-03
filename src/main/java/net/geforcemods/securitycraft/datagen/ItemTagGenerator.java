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
		copy(SCTags.Blocks.REINFORCED_STONE_BRICKS, SCTags.Items.REINFORCED_STONE_BRICKS);
		copy(SCTags.Blocks.REINFORCED_WOODEN_SLABS, SCTags.Items.REINFORCED_WOODEN_SLABS);
		copy(SCTags.Blocks.REINFORCED_WOODEN_STAIRS, SCTags.Items.REINFORCED_WOODEN_STAIRS);
		copy(SCTags.Blocks.REINFORCED_WOOL, SCTags.Items.REINFORCED_WOOL);
		getBuilder(SCTags.Items.SECRET_SIGNS).add(
				SCContent.secretAcaciaSignItem,
				SCContent.secretBirchSignItem,
				SCContent.secretDarkOakSignItem,
				SCContent.secretJungleSignItem,
				SCContent.secretOakSignItem,
				SCContent.secretSpruceSignItem);

		//minecraft tags
		getBuilder(ItemTags.CARPETS).add(SCTags.Items.REINFORCED_CARPETS);
		getBuilder(ItemTags.FENCES).add(SCContent.ironFence.asItem());
		getBuilder(ItemTags.RAILS).add(SCContent.trackMine.asItem());
		getBuilder(ItemTags.SLABS).add(SCTags.Items.REINFORCED_SLABS).add(SCContent.crystalQuartzSlab.asItem());
		getBuilder(ItemTags.STAIRS).add(SCTags.Items.REINFORCED_STAIRS).add(SCContent.stairsCrystalQuartz.asItem());
		getBuilder(ItemTags.SIGNS).add(SCTags.Items.SECRET_SIGNS);
		getBuilder(ItemTags.TRAPDOORS).add(SCContent.reinforcedIronTrapdoor.asItem());
	}

	@Override
	public String getName()
	{
		return "SecurityCraft Item Tags";
	}
}
