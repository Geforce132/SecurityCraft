package net.geforcemods.securitycraft.datagen;

import java.util.concurrent.CompletableFuture;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class EntityTypeTagGenerator extends EntityTypeTagsProvider {
	protected EntityTypeTagGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
		super(output, lookupProvider, SecurityCraft.MODID, existingFileHelper);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		tag(Tags.EntityTypes.CAPTURING_NOT_SUPPORTED).add(SCContent.SENTRY_ENTITY.get());
		tag(Tags.EntityTypes.TELEPORTING_NOT_SUPPORTED).add(SCContent.SENTRY_ENTITY.get());
	}
}
