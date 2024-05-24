package net.geforcemods.securitycraft.entity.sentry;

import java.util.Collections;
import java.util.List;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.api.SecurityCraftAPI;
import net.geforcemods.securitycraft.entity.sentry.Sentry.SentryMode;
import net.minecraft.entity.Entity;
import net.minecraft.entity.FlyingEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.monster.ShulkerEntity;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effects;

/**
 * Attacks any player who is not the owner, or any mob
 */
public class TargetNearestPlayerOrMobGoal extends NearestAttackableTargetGoal<LivingEntity> {
	private Sentry sentry;

	public TargetNearestPlayerOrMobGoal(Sentry sentry) {
		super(sentry, LivingEntity.class, true);

		this.sentry = sentry;
	}

	@Override
	public boolean canUse() {
		if (sentry.isShutDown())
			return false;

		List<LivingEntity> list = mob.level.<LivingEntity>getEntitiesOfClass(targetType, getTargetSearchArea(getFollowDistance()), e -> sentry.getSensing().canSee(e) && !e.hasEffect(Effects.INVISIBILITY));

		if (!list.isEmpty()) {
			SentryMode sentryMode = sentry.getMode();
			int i;

			Collections.sort(list, (e1, e2) -> {
				double distTo1 = mob.distanceToSqr(e1);
				double distTo2 = mob.distanceToSqr(e2);

				if (distTo1 < distTo2)
					return -1;
				else
					return distTo1 > distTo2 ? 1 : 0;
			});

			//get the nearest target that is either a mob or a player
			for (i = 0; i < list.size(); i++) {
				LivingEntity potentialTarget = list.get(i);

				if (potentialTarget.isInvulnerable())
					continue;

				//@formatter:off
				if(sentryMode.attacksPlayers() && potentialTarget instanceof PlayerEntity
						&& !potentialTarget.isSpectator()
						&& !((PlayerEntity) potentialTarget).isCreative()
						&& !((Sentry) mob).isOwnedBy((potentialTarget))
						&& !sentry.isTargetingAllowedPlayer(potentialTarget)) {
					break;
				}
				//@formatter:on

				if (sentryMode.attacksHostile() && isSupportedTarget(potentialTarget))
					break;
			}

			if (i < list.size() && isCloseEnough(list.get(i))) {
				target = list.get(i);
				mob.setTarget(target);
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean canContinueToUse() {
		return (isSupportedTarget(target) || target instanceof PlayerEntity) && isCloseEnough(target) && canUse() && !sentry.isTargetingAllowedPlayer(targetMob) && super.canContinueToUse();
	}

	public boolean isCloseEnough(Entity entity) {
		return entity != null && mob.distanceToSqr(entity) <= getFollowDistance() * getFollowDistance();
	}

	public boolean isSupportedTarget(LivingEntity potentialTarget) {
		//@formatter:off
		return potentialTarget.deathTime == 0 &&
				!ConfigHandler.SERVER.sentryAttackableEntitiesDenylist.get().contains(potentialTarget.getType().getRegistryName().toString()) &&
				(potentialTarget instanceof MonsterEntity ||
						potentialTarget instanceof FlyingEntity ||
						potentialTarget instanceof SlimeEntity ||
						potentialTarget instanceof ShulkerEntity ||
						potentialTarget instanceof EnderDragonEntity ||
						SecurityCraftAPI.getRegisteredSentryAttackTargetChecks().stream().anyMatch(check -> check.canAttack(potentialTarget)));
		//@formatter:on
	}

	@Override
	protected double getFollowDistance() {
		return Sentry.MAX_TARGET_DISTANCE;
	}
}
