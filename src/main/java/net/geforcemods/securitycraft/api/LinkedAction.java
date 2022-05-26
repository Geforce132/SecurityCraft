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
	 * Used when an {@link Option} in a block entity is changed
	 */
	OPTION_CHANGED,

	/**
	 * Used when a {@link ModuleType} is inserted into an IModuleInventory
	 */
	MODULE_INSERTED,

	/**
	 * Used when a {@link ModuleType} is removed from an IModuleInventory
	 */
	MODULE_REMOVED,

	/**
	 * Used when the {@link Owner} of a block entity changes;
	 */
	OWNER_CHANGED;
}
