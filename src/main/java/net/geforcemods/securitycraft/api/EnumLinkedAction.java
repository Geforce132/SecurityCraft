package net.geforcemods.securitycraft.api;

/**
 * A simple enum which contains all the possible actions for CustomizableSCTE.onLinkedBlockAction(). Each action has
 * different parameters which is passed in onLinkedBlockAction().
 *
 * @author Geforce
 */
public enum EnumLinkedAction {
	/**
	 * Used when an {@link Option} in a TileEntity is changed.
	 */
	OPTION_CHANGED,

	/**
	 * Used when a {@link EnumCustomModules} is inserted into a IModuleInventory.
	 */
	MODULE_INSERTED,

	/**
	 * Used when a {@link EnumCustomModules} is removed from an IModuleInventory.
	 */
	MODULE_REMOVED,

	/**
	 * Used when the {@link Owner} of a block entity changes;
	 */
	OWNER_CHANGED;
}
