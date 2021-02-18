package net.geforcemods.securitycraft.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a SecurityCraft block that is not reinforced as having an item block.
 * This automatically registers the item block.
 * Only use on fields of type net.minecraft.Block
 *
 * @author bl4ckscor3
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface RegisterItemBlock
{
	/**
	 * The SecurityCraft creative tab to put the item block in
	 *
	 * @return The creative tab
	 */
	SCItemGroup value() default SCItemGroup.TECHNICAL;

	public static enum SCItemGroup
	{
		TECHNICAL, EXPLOSIVES, DECORATION;
	}
}
