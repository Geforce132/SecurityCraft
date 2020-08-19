package net.geforcemods.securitycraft.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks that a block can be reinforced using the Universal Block Reinforcer.
 * This also automatically registers its item block.
 * Only use on RegistryObjects of blocks
 *
 * @author bl4ckscor3
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Reinforced
{
	/**
	 * @return true if this block should receive a tint of whatever tint is set to, false if not
	 */
	boolean hasTint() default true;

	/**
	 * @return The tint of this block, if hasTint is true. 0x999999 by default
	 */
	int tint() default 0x999999;
}
