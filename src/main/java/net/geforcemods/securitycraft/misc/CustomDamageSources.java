package net.geforcemods.securitycraft.misc;

import net.geforcemods.securitycraft.SecurityCraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;

public class CustomDamageSources {
	public static final ResourceKey<DamageType> LASER = ResourceKey.create(Registries.DAMAGE_TYPE, SecurityCraft.resLoc("laser"));
	public static final ResourceKey<DamageType> FAKE_WATER = ResourceKey.create(Registries.DAMAGE_TYPE, SecurityCraft.resLoc("fake_water"));
	public static final ResourceKey<DamageType> ELECTRICITY = ResourceKey.create(Registries.DAMAGE_TYPE, SecurityCraft.resLoc("electricity"));
	public static final ResourceKey<DamageType> TASER = ResourceKey.create(Registries.DAMAGE_TYPE, SecurityCraft.resLoc("taser"));
	public static final ResourceKey<DamageType> INCORRECT_PASSCODE = ResourceKey.create(Registries.DAMAGE_TYPE, SecurityCraft.resLoc("incorrect_passcode"));
	public static final ResourceKey<DamageType> IN_REINFORCED_WALL = ResourceKey.create(Registries.DAMAGE_TYPE, SecurityCraft.resLoc("in_reinforced_wall"));

	private CustomDamageSources() {}

	public static DamageSource laser(RegistryAccess registryAccess) {
		return new DamageSource(registryAccess.lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(LASER));
	}

	public static DamageSource fakeWater(RegistryAccess registryAccess) {
		return new DamageSource(registryAccess.lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(FAKE_WATER));
	}

	public static DamageSource electricity(RegistryAccess registryAccess) {
		return new DamageSource(registryAccess.lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(ELECTRICITY));
	}

	public static DamageSource taser(Entity shooter) {
		return new DamageSource(shooter.level().registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(TASER), null, shooter);
	}

	public static DamageSource incorrectPasscode(RegistryAccess registryAccess) {
		return new DamageSource(registryAccess.lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(INCORRECT_PASSCODE));
	}

	public static DamageSource inReinforcedWall(RegistryAccess registryAccess) {
		return new DamageSource(registryAccess.lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(IN_REINFORCED_WALL));
	}
}
