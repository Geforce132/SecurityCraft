package net.geforcemods.securitycraft.api;

/**
 * A simple enum which contains all the possible actions for CustomizableSCTE.onLinkedBlockAction(). Each action has
 * different parameters which is passed in onLinkedBlockAction().
 *
 * @author Geforce
 */
public enum LinkedAction {
	/**
	 * Used when an {@link Option} in a TileEntity is changed.
	 */
	OPTION_CHANGED,

	/**
	 * Used when a {@link EnumCustomModules} is inserted into an IModuleInventory.
	 */
	MODULE_INSERTED,

	/**
	 * Used when a {@link EnumCustomModules} is removed from an IModuleInventory.
	 */
	MODULE_REMOVED;
}
