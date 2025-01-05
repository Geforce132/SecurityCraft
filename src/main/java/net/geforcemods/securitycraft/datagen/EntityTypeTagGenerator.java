package net.geforcemods.securitycraft.datagen;

import java.util.concurrent.CompletableFuture;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.tags.EntityTypeTags;
import net.neoforged.neoforge.common.Tags;

public class EntityTypeTagGenerator extends EntityTypeTagsProvider {
	protected EntityTypeTagGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
		super(output, lookupProvider, SecurityCraft.MODID);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		tag(EntityTypeTags.IMMUNE_TO_INFESTED).add(SCContent.SENTRY_ENTITY.get());
		tag(EntityTypeTags.IMMUNE_TO_OOZING).add(SCContent.SENTRY_ENTITY.get());
		tag(Tags.EntityTypes.CAPTURING_NOT_SUPPORTED).add(SCContent.SENTRY_ENTITY.get());
		tag(Tags.EntityTypes.TELEPORTING_NOT_SUPPORTED).add(SCContent.SENTRY_ENTITY.get());
	}
}
