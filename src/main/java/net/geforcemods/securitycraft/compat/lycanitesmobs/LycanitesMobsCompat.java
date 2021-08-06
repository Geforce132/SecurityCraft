package net.geforcemods.securitycraft.compat.lycanitesmobs;

import com.lycanitesmobs.core.entity.BaseCreatureEntity;

import net.geforcemods.securitycraft.api.IAttackTargetCheck;
import net.minecraft.entity.Entity;

public class LycanitesMobsCompat implements IAttackTargetCheck
{
	@Override
	public boolean canAttack(Entity potentialTarget)
	{
		return potentialTarget instanceof BaseCreatureEntity && ((BaseCreatureEntity)potentialTarget).isAggressive();
	}
}
