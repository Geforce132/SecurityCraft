package net.geforcemods.securitycraft.api;

import net.minecraft.world.entity.Entity;

/**
 * Defines a callback that the Sentry checks before trying to attack an entity.
 * Call <pre>InterModComms.sendTo("securitycraft", SecurityCraftAPI.IMC_SENTRY_ATTACK_TARGET_MSG, ClassThatImplementsIAttackTargetCheck::new);</pre>
 * during InterModEnqueueEvent to register this with SecurityCraft.
 *
 * @author bl4ckscor3
 */
public interface IAttackTargetCheck
{
	/**
	 * Checks if the Sentry is allowed to attack the given entity
	 * @param potentialTarget The entity that the Sentry wants to attack
	 * @return true if the Sentry is allowed to attack this entity, false otherwise
	 */
	public boolean canAttack(Entity potentialTarget);
}
