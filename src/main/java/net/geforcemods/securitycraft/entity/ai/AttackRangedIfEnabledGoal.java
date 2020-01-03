package net.geforcemods.securitycraft.entity.ai;

import java.util.EnumSet;

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
	private final int attackIntervalMin;
	private final int maxRangedAttackTime;
	private final float attackRadius;

	public AttackRangedIfEnabledGoal(IRangedAttackMob attacker, double movespeed, int maxAttackTime, float maxAttackDistance)
	{
		sentry = (SentryEntity)attacker;
		rangedAttackTime = -1;
		attackIntervalMin = maxAttackTime;
		maxRangedAttackTime = maxAttackTime;
		attackRadius = maxAttackDistance;
		setMutexFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
	}

	@Override
	public boolean shouldExecute()
	{
		LivingEntity potentialTarget = sentry.getAttackTarget();

		if(potentialTarget == null)
			return false;
		else if(sentry.isTargetingWhitelistedPlayer(potentialTarget))
		{
			sentry.setAttackTarget(null);
			return false;
		}
		else
		{
			attackTarget = potentialTarget;
			return sentry.getMode() != SentryMode.IDLE;
		}
	}

	@Override
	public void resetTask()
	{
		attackTarget = null;
		rangedAttackTime = -3;
	}

	@Override
	public void tick() //copied from vanilla to remove pathfinding code
	{
		double targetDistance = sentry.getDistanceSq(attackTarget.func_226277_ct_(), attackTarget.getBoundingBox().minY, attackTarget.func_226281_cx_());

		sentry.getLookController().setLookPositionWithEntity(attackTarget, 30.0F, 30.0F);

		if(--rangedAttackTime == 0)
		{
			if(!sentry.getEntitySenses().canSee(attackTarget))
				return;

			float f = MathHelper.sqrt(targetDistance) / attackRadius;
			float lvt_5_1_ = MathHelper.clamp(f, 0.1F, 1.0F);

			sentry.attackEntityWithRangedAttack(attackTarget, lvt_5_1_);
			rangedAttackTime = MathHelper.floor(f * (maxRangedAttackTime - attackIntervalMin) + attackIntervalMin);
		}
		else if(rangedAttackTime < 0)
			rangedAttackTime = MathHelper.floor((MathHelper.sqrt(targetDistance) / attackRadius) * (maxRangedAttackTime - attackIntervalMin) + attackIntervalMin);
	}
}
