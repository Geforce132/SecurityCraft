package net.geforcemods.securitycraft.api;

import net.minecraft.entity.Entity;

/**
 * Defines a callback that the Sentry checks before trying to attack an entity.
 * Call <pre>FMLInterModComms.sendFunctionMessage("securitycraft", "registerSentryAttackTargetCheck", "your.package.ClassThatImplementsIAttackTargetCheck");</pre>
 * during InterModEnqueueEvent to register this with SecurityCraft.
 * Do note, that you also need to implement Function<Object,IAttackTargetCheck> on the class that you send via IMC. You can just return <code>this</code>
 * in the apply method. The Object argument is unused and will always be null.
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
