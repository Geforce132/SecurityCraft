package net.geforcemods.securitycraft.entity.ai;

import java.util.Collections;
import java.util.List;

import net.geforcemods.securitycraft.entity.SentryEntity;
import net.geforcemods.securitycraft.entity.SentryEntity.SentryMode;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;

/**
 * Attacks any player who is not the owner, or any mob
 */
public class TargetNearestPlayerOrMobGoal extends NearestAttackableTargetGoal<LivingEntity>
{
	private SentryEntity sentry;

	public TargetNearestPlayerOrMobGoal(SentryEntity sentry)
	{
		super(sentry, LivingEntity.class, true);

		this.sentry = sentry;
	}

	@Override
	public boolean shouldExecute()
	{
		List<LivingEntity> list = goalOwner.world.<LivingEntity>getEntitiesWithinAABB(targetClass, getTargetableArea(getTargetDistance()), e -> true);

		if(list.isEmpty())
			return false;
		else
		{
			int i;

			Collections.sort(list, (e1, e2) -> {
				double distTo1 = goalOwner.getDistanceSq(e1);
				double distTo2 = goalOwner.getDistanceSq(e2);

				if(distTo1 < distTo2)
					return -1;
				else return distTo1 > distTo2 ? 1 : 0;
			});

			//get the nearest target that is either a mob or a player
			for(i = 0; i < list.size(); i++)
			{
				LivingEntity potentialTarget = list.get(i);

				if(potentialTarget instanceof PlayerEntity && !((PlayerEntity)potentialTarget).isSpectator() && !((PlayerEntity)potentialTarget).isCreative() && !((SentryEntity)goalOwner).getOwner().isOwner(((PlayerEntity)potentialTarget)))
					break;
				else if(sentry.isTargetingWhitelistedPlayer(potentialTarget))
					break;
				else if(potentialTarget instanceof MonsterEntity && sentry.getMode() == SentryMode.AGGRESSIVE)
					break;
			}

			if(i < list.size())
			{
				if(isCloseEnough(list.get(i)))
				{
					nearestTarget = list.get(i);
					goalOwner.setAttackTarget(nearestTarget);
					return true;
				}
			}

			return false;
		}
	}

	@Override
	public boolean shouldContinueExecuting()
	{
		return (nearestTarget instanceof MonsterEntity || nearestTarget instanceof PlayerEntity) && isCloseEnough(nearestTarget) && shouldExecute() && !sentry.isTargetingWhitelistedPlayer(target) && super.shouldContinueExecuting();
	}

	public boolean isCloseEnough(Entity entity)
	{
		return entity != null && goalOwner.getDistanceSq(entity) <= getTargetDistance() * getTargetDistance();
	}

	@Override
	protected double getTargetDistance()
	{
		return SentryEntity.MAX_TARGET_DISTANCE;
	}
}
