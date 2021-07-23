package net.geforcemods.securitycraft.compat.lycanitesmobs;

import net.geforcemods.securitycraft.api.IAttackTargetCheck;
import net.minecraft.world.entity.Entity;

public class LycanitesMobsCompat implements IAttackTargetCheck
{
	@Override
	public boolean canAttack(Entity potentialTarget)
	{
		return false;//potentialTarget instanceof BaseCreatureEntity && ((BaseCreatureEntity)potentialTarget).isAggressive();
	}
}
