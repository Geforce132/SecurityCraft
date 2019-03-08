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
	 * @return Whether or not to register an SC Manual page for this block
	 */
	boolean value() default false;
}
