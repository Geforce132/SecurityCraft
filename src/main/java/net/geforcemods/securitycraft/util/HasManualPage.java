package net.geforcemods.securitycraft.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks the annotated block/item as having a page in the SecurityCraft manual.
 * Also holds information that is specific to the page
 *
 * @author bl4ckscor3
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface HasManualPage
{
	/**
	 * @return A non-standard language key for the help text
	 */
	String specialInfoKey() default "";

	/**
	 * @return The author who designed this block/item
	 */
	String designedBy() default "";
}
