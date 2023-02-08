package net.geforcemods.securitycraft.misc;

import java.util.function.Function;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.api.IAttackTargetCheck;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class ConfigAttackTargetCheck implements IAttackTargetCheck, Function<Object, IAttackTargetCheck> {
	@Override
	public IAttackTargetCheck apply(Object o) {
		return this;
	}

	@Override
	public boolean canAttack(Entity potentialTarget) {
		for (String id : ConfigHandler.sentryAttackableEntitiesAllowlist) {
			if (id == null || id.isEmpty())
				continue;

			EntityEntry entry = EntityRegistry.getEntry(potentialTarget.getClass());

			if (entry == null)
				continue;

			if (id.equals(entry.getRegistryName().toString()))
				return true;
		}

		return false;
	}
}
