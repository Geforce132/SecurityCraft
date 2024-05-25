package net.geforcemods.securitycraft.entity.sentry;

import java.util.Collections;
import java.util.List;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.api.SecurityCraftAPI;
import net.geforcemods.securitycraft.entity.sentry.Sentry.SentryMode;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntityShulker;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;

/**
 * Attacks any player who is not the owner, or any mob
 */
public class TargetNearestPlayerOrMobGoal extends EntityAINearestAttackableTarget<EntityLivingBase> {
	private Sentry sentry;

	public TargetNearestPlayerOrMobGoal(Sentry sentry) {
		super(sentry, EntityLivingBase.class, true);

		this.sentry = sentry;
	}

	@Override
	public boolean shouldExecute() {
		if (sentry.isShutDown())
			return false;

		List<EntityLivingBase> list = taskOwner.world.<EntityLivingBase>getEntitiesWithinAABB(targetClass, getTargetableArea(getTargetDistance()), e -> sentry.getEntitySenses().canSee(e) && !e.isPotionActive(MobEffects.INVISIBILITY));

		if (!list.isEmpty()) {
			SentryMode sentryMode = sentry.getMode();
			int i;

			Collections.sort(list, sorter);

			//get the nearest target that is either a mob or a player
			for (i = 0; i < list.size(); i++) {
				EntityLivingBase potentialTarget = list.get(i);

				if (potentialTarget.getIsInvulnerable())
					continue;

				if (sentryMode.attacksPlayers()) {
					//@formatter:off
					if(potentialTarget instanceof EntityPlayer
							&& !((EntityPlayer) potentialTarget).isSpectator()
							&& !((EntityPlayer) potentialTarget).isCreative()
							&& !((Sentry) taskOwner).isOwnedBy((potentialTarget))
							&& !sentry.isTargetingAllowedPlayer(potentialTarget)
							&& !potentialTarget.isPotionActive(MobEffects.INVISIBILITY)) {
						break;
					}
					//@formatter:on
				}

				if (sentryMode.attacksHostile() && isSupportedTarget(potentialTarget))
					break;
			}

			if (i < list.size() && isCloseEnough(list.get(i))) {
				targetEntity = list.get(i);
				taskOwner.setAttackTarget(targetEntity);
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean shouldContinueExecuting() {
		return (isSupportedTarget(targetEntity) || targetEntity instanceof EntityPlayer) && isCloseEnough(targetEntity) && shouldExecute() && !sentry.isTargetingAllowedPlayer(targetEntity) && super.shouldContinueExecuting();
	}

	public boolean isCloseEnough(Entity entity) {
		return entity != null && taskOwner.getDistanceSq(entity) <= getTargetDistance() * getTargetDistance();
	}

	public boolean isSupportedTarget(EntityLivingBase potentialTarget) {
		//@formatter:off
		return potentialTarget.deathTime == 0 &&
				!isOnDenylist(potentialTarget) &&
				(potentialTarget instanceof EntityMob ||
						potentialTarget instanceof EntityFlying ||
						potentialTarget instanceof EntitySlime ||
						potentialTarget instanceof EntityShulker ||
						potentialTarget instanceof EntityDragon ||
						SecurityCraftAPI.getRegisteredSentryAttackTargetChecks().stream().anyMatch(check -> check.canAttack(potentialTarget)));
		//@formatter:on
	}

	private boolean isOnDenylist(Entity potentialTarget) {
		for (String id : ConfigHandler.sentryAttackableEntitiesDenylist) {
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

	@Override
	protected double getTargetDistance() {
		return Sentry.MAX_TARGET_DISTANCE;
	}
}
