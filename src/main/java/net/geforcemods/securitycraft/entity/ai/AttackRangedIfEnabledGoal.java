package net.geforcemods.securitycraft.entity.ai;

import java.util.EnumSet;
import java.util.function.Supplier;

import net.geforcemods.securitycraft.entity.SentryEntity;
import net.geforcemods.securitycraft.entity.SentryEntity.SentryMode;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.MathHelper;

public class AttackRangedIfEnabledGoal extends Goal
{
	private SentryEntity sentry;
	private LivingEntity attackTarget;
	private int rangedAttackTime;
	private final Supplier<Integer> maxAttackTime;
	private final float attackRadius;

	public AttackRangedIfEnabledGoal(IRangedAttackMob attacker, Supplier<Integer> maxAttackTime, float maxAttackDistance)
	{
		sentry = (SentryEntity)attacker;
		rangedAttackTime = -1;
		this.maxAttackTime = maxAttackTime;
		attackRadius = maxAttackDistance;
		setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
	}

	@Override
	public boolean canUse()
	{
		LivingEntity potentialTarget = sentry.getTarget();

		if(potentialTarget == null)
			return false;
		else
		{
			attackTarget = potentialTarget;
			return sentry.getMode() != SentryMode.IDLE;
		}
	}

	@Override
	public void stop()
	{
		attackTarget = null;
		rangedAttackTime = -3;
	}

	@Override
	public void tick() //copied from vanilla to remove pathfinding code
	{
		double targetDistance = sentry.distanceToSqr(attackTarget.getX(), attackTarget.getBoundingBox().minY, attackTarget.getZ());

		sentry.getLookControl().setLookAt(attackTarget, 30.0F, 30.0F);

		if(--rangedAttackTime == 0)
		{
			if(!sentry.getSensing().canSee(attackTarget))
				return;

			float f = MathHelper.sqrt(targetDistance) / attackRadius;
			float distanceFactor = MathHelper.clamp(f, 0.1F, 1.0F);

			sentry.performRangedAttack(attackTarget, distanceFactor);
			rangedAttackTime = MathHelper.floor(maxAttackTime.get());
		}
		else if(rangedAttackTime < 0)
			rangedAttackTime = MathHelper.floor(maxAttackTime.get());
	}
}
