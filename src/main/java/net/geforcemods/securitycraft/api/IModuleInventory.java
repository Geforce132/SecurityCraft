package net.geforcemods.securitycraft.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;

import net.geforcemods.securitycraft.items.ModuleItem;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.IItemHandlerModifiable;

/**
 * Let your block entity implement this to be able to add modules to it
 *
 * @author bl4ckscor3
 */
public interface IModuleInventory extends IItemHandlerModifiable {
	/**
	 * @return The list that holds the contents of this inventory
	 */
	public NonNullList<ItemStack> getInventory();

	/**
	 * @return An array of what {@link ModuleType} can be inserted into this inventory
	 */
	public ModuleType[] acceptedModules();

	/**
	 * Checks whether the given module's functionality is enabled
	 *
	 * @param module The module
	 * @return true if the given module is enabled, false otherwise. If the module does not exist, this should return false
	 *         as well.
	 */
	public boolean isModuleEnabled(ModuleType module);

	/**
	 * Enables the given module's functionality
	 *
	 * @param module The module
	 */
	public void enableModule(ModuleType module);

	/**
	 * Disables the given module's functionality
	 *
	 * @param module The module
	 */
	public void disableModule(ModuleType module);

	/**
	 * @return The block entity this inventory is for
	 */
	public default BlockEntity getBlockEntity() {
		return (BlockEntity) this;
	}

	/**
	 * @return The amount of modules that can be inserted
	 */
	public default int getMaxNumberOfModules() {
		return acceptedModules().length;
	}

	/**
	 * Called whenever a module is enabled in the "Customize" GUI.
	 *
	 * @param stack The raw ItemStack of the module being enabled.
	 * @param module The ModuleType variant of stack.
	 */
	public default void onModuleEnabled(ItemStack stack, ModuleType module) {
		BlockEntity be = getBlockEntity();

		if (!be.getLevel().isClientSide) {
			be.setChanged();
			be.getLevel().sendBlockUpdated(be.getBlockPos(), be.getBlockState(), be.getBlockState(), 3);
		}
	}

	/**
	 * Called whenever a module is disabled in the "Customize" GUI.
	 *
	 * @param stack The raw ItemStack of the module being disabled.
	 * @param module The ModuleType variant of stack.
	 */
	public default void onModuleDisabled(ItemStack stack, ModuleType module) {
		BlockEntity be = getBlockEntity();

		if (!be.getLevel().isClientSide) {
			be.setChanged();
			be.getLevel().sendBlockUpdated(be.getBlockPos(), be.getBlockState(), be.getBlockState(), 3);
		}
	}

	/**
	 * Used for enabling differentiation between module slots and slots that are handled by IInventory. This is needed
	 * because of the duplicate getStackInSlot method.
	 *
	 * @return true if the slot ids are not starting with 0, false otherwise
	 */
	public default boolean enableHack() {
		return false;
	}

	/**
	 * Only override if enableHack returns true and your ids don't start at 100. Used to convert the slot ids to inventory
	 * indices
	 *
	 * @param id The slot id to convert
	 * @return The inventory index corresponding to the slot id
	 */
	public default int fixSlotId(int id) {
		return id >= 100 ? id - 100 : id;
	}

	@Override
	public default int getSlots() {
		return acceptedModules().length;
	}

	@Override
	public default ItemStack getStackInSlot(int slot) {
		return getModuleInSlot(slot);
	}

	public default ItemStack getModuleInSlot(int slot) {
		slot = fixSlotId(slot);
		return slot < 0 || slot >= getSlots() ? ItemStack.EMPTY : getInventory().get(slot);
	}

	@Override
	public default ItemStack extractItem(int slot, int amount, boolean simulate) {
		slot = fixSlotId(slot);

		ItemStack stack = getModuleInSlot(slot).copy();

		if (stack.isEmpty())
			return ItemStack.EMPTY;
		else {
			if (!simulate) {
				if (!getBlockEntity().getLevel().isClientSide && stack.getItem() instanceof ModuleItem module) {
					disableModule(module.getModuleType());
					onModuleDisabled(stack, module.getModuleType());

					if (getBlockEntity() instanceof LinkableBlockEntity be)
						ModuleUtils.createLinkedAction(LinkedAction.MODULE_REMOVED, stack, be);
				}

				return getInventory().set(slot, ItemStack.EMPTY).copy();
			}
			else
				return stack.copy();
		}
	}

	@Override
	public default ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		slot = fixSlotId(slot);

		if (!getModuleInSlot(slot).isEmpty())
			return stack;
		else {
			int returnSize = 0;

			//the max stack size is one, so in order to provide the correct return value, the count after insertion is calculated here
			if (stack.getCount() > 1)
				returnSize = stack.getCount() - 1;

			if (!simulate) {
				ItemStack copy = stack.copy();

				copy.setCount(1);
				getInventory().set(slot, copy);

				if (!getBlockEntity().getLevel().isClientSide && stack.getItem() instanceof ModuleItem module) {
					enableModule(module.getModuleType());
					onModuleEnabled(stack, module.getModuleType());

					if (getBlockEntity() instanceof LinkableBlockEntity be)
						ModuleUtils.createLinkedAction(LinkedAction.MODULE_INSERTED, copy, be);
				}
			}

			if (returnSize != 0) {
				ItemStack toReturn = stack.copy();

				toReturn.setCount(returnSize);
				return toReturn;
			}
			else
				return ItemStack.EMPTY;
		}
	}

	@Override
	public default void setStackInSlot(int slot, ItemStack stack) {
		slot = fixSlotId(slot);

		ItemStack previous = getModuleInSlot(slot);

		//call the correct methods, should there have been a module in the slot previously
		if (!getBlockEntity().getLevel().isClientSide && stack.isEmpty() && !previous.isEmpty()) {
			ModuleType moduleType = ((ModuleItem) previous.getItem()).getModuleType();

			disableModule(moduleType);
			onModuleDisabled(previous, moduleType);

			if (getBlockEntity() instanceof LinkableBlockEntity be)
				ModuleUtils.createLinkedAction(LinkedAction.MODULE_REMOVED, previous, be);
		}

		getInventory().set(slot, stack);

		if (!getBlockEntity().getLevel().isClientSide && stack.getItem() instanceof ModuleItem module) {
			enableModule(module.getModuleType());
			onModuleEnabled(stack, module.getModuleType());

			if (getBlockEntity() instanceof LinkableBlockEntity be)
				ModuleUtils.createLinkedAction(LinkedAction.MODULE_INSERTED, stack, be);
		}
	}

	@Override
	public default int getSlotLimit(int slot) {
		return 1;
	}

	@Override
	public default boolean isItemValid(int slot, ItemStack stack) {
		slot = fixSlotId(slot);
		return getModuleInSlot(slot).isEmpty() && !stack.isEmpty() && stack.getItem() instanceof ModuleItem module && getAcceptedModules().contains(module.getModuleType()) && !hasModule(module.getModuleType());
	}

	/**
	 * @return A list of all {@link ModuleType} that can be inserted into this inventory
	 */
	public default ArrayList<ModuleType> getAcceptedModules() {
		ArrayList<ModuleType> list = new ArrayList<>();

		for (ModuleType module : acceptedModules()) {
			list.add(module);
		}

		return list;
	}

	/**
	 * @return A List of all ModuleType currently inserted in the TileEntity.
	 */
	public default ArrayList<ModuleType> getInsertedModules() {
		ArrayList<ModuleType> modules = new ArrayList<>();

		for (ItemStack stack : getInventory()) {
			if (!stack.isEmpty() && stack.getItem() instanceof ModuleItem module)
				modules.add(module.getModuleType());
		}

		return modules;
	}

	/**
	 * @param module The module type of the stack to get
	 * @return The ItemStack for the given ModuleType type. If there is no ItemStack for that type, returns ItemStack.EMPTY.
	 */
	public default ItemStack getModule(ModuleType module) {
		NonNullList<ItemStack> modules = getInventory();

		for (int i = 0; i < modules.size(); i++) {
			if (!modules.get(i).isEmpty() && modules.get(i).getItem() instanceof ModuleItem moduleItem && moduleItem.getModuleType() == module)
				return modules.get(i);
		}

		return ItemStack.EMPTY;
	}

	/**
	 * Inserts an exact copy of the given item into the customization inventory, if it is not empty and a module.
	 *
	 * @param module The stack to insert
	 */
	public default void insertModule(ItemStack module) {
		if (module.isEmpty() || !(module.getItem() instanceof ModuleItem moduleItem))
			return;

		NonNullList<ItemStack> modules = getInventory();

		for (int i = 0; i < modules.size(); i++) {
			if (!modules.get(i).isEmpty()) {
				if (modules.get(i).getItem() == module.getItem())
					return;
			}
		}

		for (int i = 0; i < modules.size(); i++) {
			if (modules.get(i).isEmpty()) {
				ItemStack toInsert = module.copy();

				modules.set(i, toInsert);
				enableModule(moduleItem.getModuleType());
				onModuleEnabled(toInsert, moduleItem.getModuleType());
				break;
			}
		}
	}

	/**
	 * Removes the first item with the given module type from the inventory.
	 *
	 * @param module The module type to remove
	 */
	public default void removeModule(ModuleType module) {
		NonNullList<ItemStack> modules = getInventory();

		for (int i = 0; i < modules.size(); i++) {
			if (!modules.get(i).isEmpty() && modules.get(i).getItem() instanceof ModuleItem moduleItem && moduleItem.getModuleType() == module) {
				ItemStack removed = modules.get(i).copy();

				modules.set(i, ItemStack.EMPTY);
				disableModule(module);
				onModuleDisabled(removed, module);
			}
		}
	}

	/**
	 * @param module The type to check if it is present in this inventory
	 * @return true if the given module type is present in this inventory, false otherwise
	 */
	public default boolean hasModule(ModuleType module) {
		NonNullList<ItemStack> modules = getInventory();

		if (module == null) {
			for (int i = 0; i < modules.size(); i++) {
				if (modules.get(i).isEmpty())
					return true;
			}
		}
		else {
			for (int i = 0; i < modules.size(); i++) {
				if (!modules.get(i).isEmpty() && modules.get(i).getItem() instanceof ModuleItem moduleItem && moduleItem.getModuleType() == module)
					return true;
			}
		}

		return false;
	}

	/**
	 * Call this from your load method. Used for loading the module inventory from a tag. Use in conjunction with
	 * saveModuleInventory.
	 *
	 * @param tag The tag to read the inventory from
	 * @return A NonNullList of ItemStacks that were read from the given tag
	 */
	public default NonNullList<ItemStack> loadModuleInventory(CompoundTag tag) {
		ListTag list = tag.getList("Modules", Tag.TAG_COMPOUND);
		NonNullList<ItemStack> modules = NonNullList.withSize(getMaxNumberOfModules(), ItemStack.EMPTY);

		for (int i = 0; i < list.size(); ++i) {
			CompoundTag stackTag = list.getCompound(i);
			byte slot = stackTag.getByte("ModuleSlot");

			if (slot >= 0 && slot < modules.size())
				modules.set(slot, ItemStack.of(stackTag));
		}

		return modules;
	}

	/**
	 * Call this from your load method after loadModuleInventory. Used for loading which modules are enabled from a tag. Use
	 * in conjunction with saveModuleStates.
	 *
	 * @param tag The tag to read the states from
	 * @return An EnumMap of all module types with the enabled flag set as read from the tag
	 */
	public default EnumMap<ModuleType, Boolean> loadModuleStates(CompoundTag tag) {
		EnumMap<ModuleType, Boolean> moduleStates = new EnumMap<>(ModuleType.class);
		List<ModuleType> acceptedModules = Arrays.asList(acceptedModules());

		for (ModuleType module : ModuleType.values()) {
			if (acceptedModules.contains(module)) {
				String key = module.name().toLowerCase() + "Enabled";

				if (tag.contains(key))
					moduleStates.put(module, tag.getBoolean(key));
				else
					moduleStates.put(module, hasModule(module)); //if the module is accepted, but no state was saved yet, revert to whether the module is installed
			}
			else
				moduleStates.put(module, false); //module is not accepted, so disable it right away
		}

		return moduleStates;
	}

	/**
	 * Call this from your save method. Used for writing the module inventory to a tag. Use in conjunction with
	 * loadModuleInventory.
	 *
	 * @param tag The tag to save the inventory to
	 * @return The modified tag
	 */
	public default CompoundTag saveModuleInventory(CompoundTag tag) {
		ListTag list = new ListTag();
		NonNullList<ItemStack> modules = getInventory();

		for (int i = 0; i < modules.size(); i++) {
			if (!modules.get(i).isEmpty()) {
				CompoundTag stackTag = new CompoundTag();

				stackTag.putByte("ModuleSlot", (byte) i);
				modules.get(i).save(stackTag);
				list.add(stackTag);
			}
		}

		tag.put("Modules", list);
		return tag;
	}

	/**
	 * Call this from your save method. Used for writing which modules are enabled to a tag. Use in conjunction with
	 * loadModuleStates.
	 *
	 * @param tag The tag to save the module enabled states to
	 * @return The modified tag
	 */
	public default CompoundTag saveModuleStates(CompoundTag tag) {
		for (ModuleType module : acceptedModules()) {
			tag.putBoolean(module.name().toLowerCase() + "Enabled", isModuleEnabled(module));
		}

		return tag;
	}
}