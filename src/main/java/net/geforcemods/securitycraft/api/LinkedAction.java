package net.geforcemods.securitycraft.api;

import net.geforcemods.securitycraft.misc.ModuleType;

/**
 * A simple enum which contains all the possible actions for LinkableBlockEntity.onLinkedBlockAction(). Each action has
 * different parameters which is passed in onLinkedBlockAction().
 *
 * @author Geforce
 */
public enum LinkedAction {

	/**
	 * Used when an {@link Option} in a block entity is changed. <p> Parameters: [0] The Option which changed
	 */
	OPTION_CHANGED,

	/**
	 * Used when a {@link ModuleType} is inserted into an IModuleInventory. <p> Parameters: [0] The module's ItemStack, [1]
	 * the module in ModuleType-form
	 */
	MODULE_INSERTED,

	/**
	 * Used when a {@link ModuleType} is removed from an IModuleInventory. <p> Parameters: [0] The module's ItemStack, [1]
	 * the module in ModuleType-form
	 */
	MODULE_REMOVED;

}
