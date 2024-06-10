package net.geforcemods.securitycraft.datagen;

import java.util.concurrent.CompletableFuture;

import net.geforcemods.securitycraft.SCTags;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.misc.CustomDamageSources;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.DamageTypeTagsProvider;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageTypes;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class DamageTypeTagGenerator extends DamageTypeTagsProvider {
	protected DamageTypeTagGenerator(PackOutput output, CompletableFuture<Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
		super(output, lookupProvider, SecurityCraft.MODID, existingFileHelper);
	}

	@Override
	protected void addTags(Provider provider) {
		tag(SCTags.DamageTypes.SECURITY_SEA_BOAT_VULNERABLE_TO).add(DamageTypes.PLAYER_ATTACK);

		//minecraft tags
		tag(DamageTypeTags.BYPASSES_ARMOR).add(CustomDamageSources.FAKE_WATER, CustomDamageSources.ELECTRICITY, CustomDamageSources.IN_REINFORCED_WALL);
		tag(DamageTypeTags.BYPASSES_EFFECTS).add(CustomDamageSources.IN_REINFORCED_WALL);

		//NeoForge tags
		tag(Tags.DamageTypes.IS_ENVIRONMENT).add(CustomDamageSources.ELECTRICITY, CustomDamageSources.IN_REINFORCED_WALL, CustomDamageSources.LASER, CustomDamageSources.TASER);
		tag(Tags.DamageTypes.IS_PHYSICAL).add(CustomDamageSources.IN_REINFORCED_WALL);
		tag(Tags.DamageTypes.IS_POISON).add(CustomDamageSources.FAKE_WATER);
	}
}
