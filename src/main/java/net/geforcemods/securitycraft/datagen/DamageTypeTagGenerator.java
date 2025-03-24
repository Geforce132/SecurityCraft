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

public class DamageTypeTagGenerator extends DamageTypeTagsProvider {
	protected DamageTypeTagGenerator(PackOutput output, CompletableFuture<Provider> lookupProvider) {
		super(output, lookupProvider, SecurityCraft.MODID);
	}

	@Override
	protected void addTags(Provider provider) {
		tag(SCTags.DamageTypes.SECURITY_SEA_BOAT_VULNERABLE_TO).add(DamageTypes.PLAYER_ATTACK);

		//@formatter:off
		//minecraft tags
		tag(DamageTypeTags.BYPASSES_ARMOR)
				.addOptional(CustomDamageSources.FAKE_WATER.location())
				.addOptional(CustomDamageSources.ELECTRICITY.location())
				.addOptional(CustomDamageSources.IN_REINFORCED_WALL.location());
		tag(DamageTypeTags.BYPASSES_EFFECTS)
				.addOptional(CustomDamageSources.IN_REINFORCED_WALL.location());

		//NeoForge tags
		tag(Tags.DamageTypes.IS_ENVIRONMENT)
				.addOptional(CustomDamageSources.ELECTRICITY.location())
				.addOptional(CustomDamageSources.IN_REINFORCED_WALL.location())
				.addOptional(CustomDamageSources.LASER.location())
				.addOptional(CustomDamageSources.TASER.location());
		tag(Tags.DamageTypes.IS_PHYSICAL)
				.addOptional(CustomDamageSources.IN_REINFORCED_WALL.location());
		tag(Tags.DamageTypes.IS_POISON)
				.addOptional(CustomDamageSources.FAKE_WATER.location());
	}
}
