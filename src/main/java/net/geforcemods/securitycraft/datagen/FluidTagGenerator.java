package net.geforcemods.securitycraft.datagen;

import java.util.concurrent.CompletableFuture;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraft.tags.FluidTags;
import net.neoforged.neoforge.common.Tags;

public class FluidTagGenerator extends FluidTagsProvider {
	protected FluidTagGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
		super(output, lookupProvider, SecurityCraft.MODID);
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void addTags(HolderLookup.Provider provider) {
		//vanilla tags
		tag(FluidTags.LAVA).add(SCContent.FAKE_LAVA.key(), SCContent.FLOWING_FAKE_LAVA.key());
		tag(FluidTags.WATER).add(SCContent.FAKE_WATER.key(), SCContent.FLOWING_FAKE_WATER.key());

		//neoforge tags
		tag(Tags.Fluids.LAVA).add(SCContent.FAKE_LAVA.key());
		tag(Tags.Fluids.WATER).add(SCContent.FAKE_WATER.key());
	}
}
