package net.geforcemods.securitycraft.api;

import net.geforcemods.securitycraft.api.ILinkedAction.ModuleInserted;
import net.geforcemods.securitycraft.api.ILinkedAction.ModuleRemoved;
import net.geforcemods.securitycraft.api.ILinkedAction.OptionChanged;
import net.geforcemods.securitycraft.api.ILinkedAction.OwnerChanged;
import net.geforcemods.securitycraft.api.ILinkedAction.StateChanged;
import net.geforcemods.securitycraft.items.ModuleItem;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

/**
 * A simple interface which contains all the possible actions for LinkableBlockEntity.onLinkedBlockAction().
 *
 * @author Geforce, bl4ckscor3
 */
public sealed interface ILinkedAction permits OptionChanged, ModuleInserted, ModuleRemoved, OwnerChanged, StateChanged {
	/**
	 * Used when an {@link Option} in a block entity is changed
	 */
	public static final record OptionChanged<T>(Option<T> option) implements ILinkedAction {}

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

	/**
	 * Used when a property of the {@link BlockState} at the block entity's position changes
	 */
	public static final record StateChanged<T extends Comparable<T>>(Property<T> property, T oldValue, T newValue) implements ILinkedAction {}
}
