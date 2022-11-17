package net.geforcemods.securitycraft.api;

import net.geforcemods.securitycraft.items.ItemModule;
import net.geforcemods.securitycraft.misc.EnumModuleType;
import net.minecraft.item.ItemStack;

/**
 * A simple interface which contains all the possible actions for LinkableBlockEntity.onLinkedBlockAction().
 *
 * @author Geforce, bl4ckscor3
 */
public interface ILinkedAction {
	/**
	 * Used when an {@link Option} in a TileEntity is changed.
	 */
	public static final class OptionChanged<T> implements ILinkedAction {
		public final Option<T> option;

		public OptionChanged(Option<T> option) {
			this.option = option;
		}
	}

	/**
	 * Used when a {@link EnumModuleType} is inserted into an {@link IModuleInventory}
	 */
	public static final class ModuleInserted implements ILinkedAction {
		public final ItemStack stack;
		public final ItemModule module;
		public final boolean wasModuleToggled;

		public ModuleInserted(ItemStack stack, ItemModule module, boolean wasModuleToggled) {
			this.stack = stack;
			this.module = module;
			this.wasModuleToggled = wasModuleToggled;
		}
	}

	/**
	 * Used when a {@link EnumModuleType} is removed from an {@link IModuleInventory}
	 */
	public static final class ModuleRemoved implements ILinkedAction {
		public final EnumModuleType moduleType;
		public final boolean wasModuleToggled;

		public ModuleRemoved(EnumModuleType moduleType, boolean wasModuleToggled) {
			this.moduleType = moduleType;
			this.wasModuleToggled = wasModuleToggled;
		}
	}

	/**
	 * Used when the {@link Owner} of a block entity changes
	 */
	public static final class OwnerChanged implements ILinkedAction {
		public final Owner newOwner;

		public OwnerChanged(Owner newOwner) {
			this.newOwner = newOwner;
		}
	}
}
