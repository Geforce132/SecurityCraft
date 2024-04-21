package net.geforcemods.securitycraft.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks that a block can be reinforced using the Universal Block Reinforcer. This also automatically registers its item
 * block. Only use on RegistryObjects of blocks
 *
 * @author bl4ckscor3
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Reinforced {
	/**
	 * @return true if this block should receive a reinforcing tint, false if not
	 */
	boolean hasReinforcedTint() default true;

	/**
	 * @return The custom tint of this block which is mixed with the reinforcing tint. 0xFFFFFF (no custom tint) by default
	 */
	int customTint() default 0xFFFFFFFF;

	/**
	 * @return true if a BlockItem should be automatically registered for this block, false if not
	 */
	boolean registerBlockItem() default true;

	/**
	 * The SecurityCraft creative tab to automatically put the item block in, if {@link #registerBlockItem()} returns true
	 *
	 * @return The creative tab. If this returns {@link SCItemGroup#MANUAL}, then the item is not automatically registered to any
	 *         tab
	 */
	SCItemGroup itemGroup() default SCItemGroup.DECORATION;
}
