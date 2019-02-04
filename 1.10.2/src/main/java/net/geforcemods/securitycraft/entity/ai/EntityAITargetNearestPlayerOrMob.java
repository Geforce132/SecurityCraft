package net.geforcemods.securitycraft.entity.ai;

import java.util.Collections;
import java.util.List;

import net.geforcemods.securitycraft.entity.EntitySentry;
import net.geforcemods.securitycraft.entity.EntitySentry.EnumSentryMode;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Attacks any player who is not the owner, or any mob
 */
public class EntityAITargetNearestPlayerOrMob extends EntityAINearestAttackableTarget<EntityLivingBase>
{
	private EntitySentry sentry;

	public EntityAITargetNearestPlayerOrMob(EntitySentry sentry)
	{
		super(sentry, EntityLivingBase.class, true);

		this.sentry = sentry;
	}

	@Override
	public boolean shouldExecute()
	{
		List<EntityLivingBase> list = taskOwner.world.<EntityLivingBase>getEntitiesWithinAABB(targetClass, getTargetableArea(getTargetDistance()), targetEntitySelector);

		if(list.isEmpty())
			return false;
		else
		{
			int i;

			Collections.sort(list, theNearestAttackableTargetSorter);

			//get the nearest target that is either a mob or a player
			for(i = 0; i < list.size(); i++)
			{
				EntityLivingBase potentialTarget = list.get(i);

				if(potentialTarget instanceof EntityPlayer && !((EntityPlayer)potentialTarget).isSpectator() && !((EntityPlayer)potentialTarget).isCreative() && !((EntitySentry)taskOwner).getOwner().isOwner(((EntityPlayer)potentialTarget)))
					break;
				else if(potentialTarget instanceof EntityMob && sentry.getMode() == EnumSentryMode.AGGRESSIVE)
					break;
			}

			if(i < list.size())
			{
				if(isCloseEnough(list.get(i)))
				{
					targetEntity = list.get(i);
					taskOwner.setAttackTarget(targetEntity);
					return true;
				}
			}

			return false;
		}
	}

	@Override
	public boolean continueExecuting()
	{
		return (targetEntity instanceof EntityMob || targetEntity instanceof EntityPlayer) && isCloseEnough(targetEntity) && shouldExecute() && super.continueExecuting();
	}

	public boolean isCloseEnough(Entity entity)
	{
		return entity != null && taskOwner.getDistanceSqToEntity(entity) <= getTargetDistance() * getTargetDistance();
	}

	@Override
	protected double getTargetDistance()
	{
		return EntitySentry.MAX_TARGET_DISTANCE;
	}
}
