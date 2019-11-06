package net.geforcemods.securitycraft.api;

import net.geforcemods.securitycraft.misc.CustomModules;

/**
 * A simple enum which contains all the possible actions for
 * CustomizableSCTE.onLinkedBlockAction(). Each action has different
 * parameters which is passed in onLinkedBlockAction().
 *
 * @author Geforce
 */
public enum LinkedAction {

	/**
	 * Used when an {@link Option} in a TileEntity is changed. <p>
	 *
	 * Parameters: [0] The Option which changed
	 */
	OPTION_CHANGED,

	/**
	 * Used when a {@link EnumCustomModules} is inserted into a CustomizableSCTE. <p>
	 *
	 * Parameters: [0] The module's ItemStack, [1] the module in EnumCustomModules-form
	 */
	MODULE_INSERTED,

	/**
	 * Used when a {@link EnumCustomModules} is removed from a CustomizableSCTE. <p>
	 *
	 * Parameters: [0] The module's ItemStack, [1] the module in EnumCustomModules-form
	 */
	MODULE_REMOVED;

}
