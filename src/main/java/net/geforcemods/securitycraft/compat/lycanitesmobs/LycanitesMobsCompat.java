package net.geforcemods.securitycraft.compat.lycanitesmobs;

import java.util.function.Function;

import com.lycanitesmobs.core.entity.BaseCreatureEntity;

import net.geforcemods.securitycraft.api.IAttackTargetCheck;
import net.minecraft.entity.Entity;

public class LycanitesMobsCompat implements IAttackTargetCheck, Function<Object,IAttackTargetCheck>
{
	@Override
	public IAttackTargetCheck apply(Object t)
	{
		return this;
	}

	@Override
	public boolean canAttack(Entity potentialTarget)
	{
		return potentialTarget instanceof BaseCreatureEntity && ((BaseCreatureEntity)potentialTarget).isHostile();
	}
}
