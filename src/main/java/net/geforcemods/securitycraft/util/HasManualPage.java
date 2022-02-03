package net.geforcemods.securitycraft.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.geforcemods.securitycraft.misc.PageGroup;

/**
 * Marks the annotated block/item as having a page in the SecurityCraft manual. Also holds information that is specific to
 * the page
 *
 * @author bl4ckscor3
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface HasManualPage {
	/**
	 * @return The group this page belongs to. The block/item annotatated will not have a separate page, but will be included
	 *         in the page defined by the first block/item to be in this group
	 */
	PageGroup value() default PageGroup.NONE;

	/**
	 * @return A language key for the title of the page
	 */
	String title() default "";

	/**
	 * @return A non-standard language key for the help text
	 */
	String specialInfoKey() default "";

	/**
	 * @return The author who designed this block/item
	 */
	String designedBy() default "";

	/**
	 * @return Whether this page displays information instead of a recipe
	 */
	boolean hasRecipeDescription() default false;
}
