package net.geforcemods.securitycraft.api;

import net.geforcemods.securitycraft.api.ILinkedAction.ModuleInserted;
import net.geforcemods.securitycraft.api.ILinkedAction.ModuleRemoved;
import net.geforcemods.securitycraft.api.ILinkedAction.OptionChanged;
import net.geforcemods.securitycraft.api.ILinkedAction.OwnerChanged;
import net.geforcemods.securitycraft.items.ModuleItem;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.minecraft.world.item.ItemStack;

/**
 * A simple interface which contains all the possible actions for LinkableBlockEntity.onLinkedBlockAction().
 *
 * @author Geforce, bl4ckscor3
 */
public sealed interface ILinkedAction permits OptionChanged, ModuleInserted, ModuleRemoved, OwnerChanged {
	/**
	 * Used when an {@link Option} in a block entity is changed
	 */
	public static final record OptionChanged<T> (Option<T> option) implements ILinkedAction {}

	/**
	 * Used when a {@link ModuleType} is inserted into an {@link IModuleInventory}
	 */
	public static final record ModuleInserted(ItemStack stack, ModuleItem module, boolean wasModuleToggled) implements ILinkedAction {}

	/**
	 * Used when a {@link ModuleType} is removed from an {@link IModuleInventory}
	 */
	public static final record ModuleRemoved(ModuleType moduleType, boolean wasModuleToggled) implements ILinkedAction {}

	/**
	 * Used when the {@link Owner} of a block entity changes
	 */
	public static final record OwnerChanged(Owner newOwner) implements ILinkedAction {}
}
