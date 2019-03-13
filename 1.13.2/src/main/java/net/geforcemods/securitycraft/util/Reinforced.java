package net.geforcemods.securitycraft.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks that a block can be reinforced using the Universal Block Reinforcer.
 * This also automatically registers the block and its item block.
 * Only use on fields of type net.minecraft.Block
 *
 * @author bl4ckscor3
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Reinforced
{
	/**
	 * @return true if an SC Manual page for this block should be registered, false if not
	 */
	boolean hasPage() default false;

	/**
	 * @return true if this block should receive a tint ox 0x999999, false if not
	 */
	boolean hasTint() default true;
}
