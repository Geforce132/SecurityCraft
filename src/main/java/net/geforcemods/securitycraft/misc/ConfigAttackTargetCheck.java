package net.geforcemods.securitycraft.misc;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.api.IAttackTargetCheck;
import net.minecraft.entity.Entity;

public class ConfigAttackTargetCheck implements IAttackTargetCheck {
	@Override
	public boolean canAttack(Entity potentialTarget) {
		return ConfigHandler.SERVER.sentryAttackableEntitiesAllowlist.get().contains(potentialTarget.getType().getRegistryName().toString());
	}
}
