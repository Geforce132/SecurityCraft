package net.geforcemods.securitycraft.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import net.geforcemods.securitycraft.items.ModuleItem;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.IItemHandlerModifiable;

/**
 * Let your TileEntity implement this to be able to add modules to it
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
	 * @return true if the given module is enabled, false otherwise. If the module does not exist, this should return false as
	 *         well.
	 */
	public boolean isModuleEnabled(ModuleType module);

	/**
	 * Turns the given module type on or off, depending on shouldBeEnabled. The module needs to be present in the inventory in
	 * order for toggling to work.
	 *
	 * @param module The type of the module to toggle
	 * @param shouldBeEnabled Whether the state of this module type should be set to enabled
	 */
	public void toggleModuleState(ModuleType module, boolean shouldBeEnabled);

	/**
	 * @return The TileEntity this inventory is for
	 */
	public default TileEntity getBlockEntity() {
		return (TileEntity) this;
	}

	/**
	 * @return The amount of modules that can be inserted
	 */
	public default int getMaxNumberOfModules() {
		return acceptedModules().length;
	}

	/**
	 * Called whenever a module is inserted into a slot in the "Customize" GUI.
	 *
	 * @param stack The raw ItemStack being inserted.
	 * @param module The ModuleType variant of stack.
	 * @param toggled false if the actual item changed, true if the enabled state of the module changed
	 */
	public default void onModuleInserted(ItemStack stack, ModuleType module, boolean toggled) {
		TileEntity be = getBlockEntity();

		if (!be.getLevel().isClientSide) {
			if (!toggled)
				toggleModuleState(module, true);

			be.setChanged();
			be.getLevel().sendBlockUpdated(be.getBlockPos(), be.getBlockState(), be.getBlockState(), 3);
		}
	}

	/**
	 * Called whenever a module is removed from a slot in the "Customize" GUI.
	 *
	 * @param stack The raw ItemStack being removed.
	 * @param module The ModuleType variant of stack.
	 * @param toggled false if the actual item changed, true if the enabled state of the module changed
	 */
	public default void onModuleRemoved(ItemStack stack, ModuleType module, boolean toggled) {
		TileEntity be = getBlockEntity();

		if (!be.getLevel().isClientSide) {
			if (!toggled)
				toggleModuleState(module, false);

			be.setChanged();
			be.getLevel().sendBlockUpdated(be.getBlockPos(), be.getBlockState(), be.getBlockState(), 3);
		}
	}

	/**
	 * Used for enabling differentiation between module slots and slots that are handled by IInventory. This is needed because of
	 * the duplicate getStackInSlot method.
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

	public default void dropAllModules() {
		TileEntity be = getBlockEntity();
		World level = be.getLevel();
		BlockPos pos = be.getBlockPos();
		LinkableBlockEntity linkable = be instanceof LinkableBlockEntity ? (LinkableBlockEntity) be : null;

		for (ItemStack module : getInventory()) {
			if (!(module.getItem() instanceof ModuleItem))
				continue;

			if (linkable != null)
				linkable.propagate(new ILinkedAction.ModuleRemoved(((ModuleItem) module.getItem()).getModuleType(), false), linkable);

			Block.popResource(level, pos, module);
		}

		getInventory().clear();
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
				TileEntity te = getBlockEntity();

				if (stack.getItem() instanceof ModuleItem) {
					onModuleRemoved(stack, ((ModuleItem) stack.getItem()).getModuleType(), false);

					if (te instanceof LinkableBlockEntity) {
						LinkableBlockEntity be = (LinkableBlockEntity) te;

						be.propagate(new ILinkedAction.ModuleRemoved(((ModuleItem) stack.getItem()).getModuleType(), false), be);
					}
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
				TileEntity te = getBlockEntity();

				copy.setCount(1);
				getInventory().set(slot, copy);

				if (stack.getItem() instanceof ModuleItem) {
					onModuleInserted(stack, ((ModuleItem) stack.getItem()).getModuleType(), false);

					if (te instanceof LinkableBlockEntity) {
						LinkableBlockEntity be = (LinkableBlockEntity) te;

						be.propagate(new ILinkedAction.ModuleInserted(copy, (ModuleItem) copy.getItem(), false), be);
					}
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

		TileEntity te = getBlockEntity();
		ItemStack previous = getModuleInSlot(slot);

		//call the correct methods, should there have been a module in the slot previously
		if (!previous.isEmpty()) {
			onModuleRemoved(previous, ((ModuleItem) previous.getItem()).getModuleType(), false);

			if (te instanceof LinkableBlockEntity) {
				LinkableBlockEntity be = (LinkableBlockEntity) te;

				be.propagate(new ILinkedAction.ModuleRemoved(((ModuleItem) previous.getItem()).getModuleType(), false), be);
			}
		}

		getInventory().set(slot, stack);

		if (stack.getItem() instanceof ModuleItem) {
			onModuleInserted(stack, ((ModuleItem) stack.getItem()).getModuleType(), false);

			if (te instanceof LinkableBlockEntity) {
				LinkableBlockEntity be = (LinkableBlockEntity) te;

				be.propagate(new ILinkedAction.ModuleInserted(stack, (ModuleItem) stack.getItem(), false), be);
			}
		}
	}

	@Override
	public default int getSlotLimit(int slot) {
		return 1;
	}

	@Override
	public default boolean isItemValid(int slot, ItemStack stack) {
		slot = fixSlotId(slot);
		return getModuleInSlot(slot).isEmpty() && !stack.isEmpty() && stack.getItem() instanceof ModuleItem && acceptsModule(((ModuleItem) stack.getItem()).getModuleType()) && !hasModule(((ModuleItem) stack.getItem()).getModuleType());
	}

	/**
	 * @return true if the inventory accepts the given {@link ModuleType}, false otherwise
	 */
	public default boolean acceptsModule(ModuleType type) {
		for (ModuleType module : acceptedModules()) {
			if (module == type)
				return true;
		}

		return false;
	}

	/**
	 * @return A List of all ModuleType currently inserted in the TileEntity.
	 */
	public default List<ModuleType> getInsertedModules() {
		List<ModuleType> modules = new ArrayList<>();

		for (ItemStack stack : getInventory()) {
			if (!stack.isEmpty() && stack.getItem() instanceof ModuleItem)
				modules.add(((ModuleItem) stack.getItem()).getModuleType());
		}

		return modules;
	}

	/**
	 * @param The module type of the stack to get
	 * @return The ItemStack for the given ModuleType type. If there is no ItemStack for that type, returns ItemStack.EMPTY.
	 */
	public default ItemStack getModule(ModuleType module) {
		NonNullList<ItemStack> modules = getInventory();

		for (int i = 0; i < modules.size(); i++) {
			if (!modules.get(i).isEmpty() && modules.get(i).getItem() instanceof ModuleItem && ((ModuleItem) modules.get(i).getItem()).getModuleType() == module)
				return modules.get(i);
		}

		return ItemStack.EMPTY;
	}

	/**
	 * Inserts an exact copy of the given item into the customization inventory, if it is not empty and a module.
	 *
	 * @param module The stack to insert
	 * @param toggled false if the actual item changed, true if the enabled state of the module changed
	 */
	public default void insertModule(ItemStack module, boolean toggled) {
		if (module.isEmpty() || !(module.getItem() instanceof ModuleItem))
			return;

		ModuleItem moduleItem = (ModuleItem) module.getItem();
		NonNullList<ItemStack> modules = getInventory();

		//if the module is being toggled, then there should not be a check for whether the module already exists
		if (!toggled) {
			for (int i = 0; i < modules.size(); i++) {
				if (!modules.get(i).isEmpty() && modules.get(i).getItem() == module.getItem()) {
					return;
				}
			}
		}

		//if the module is being toggled, the test should be for the stack that matches the module. if not, the test should look for the first empty slot
		Predicate<ItemStack> predicate = toggled ? stack -> stack.getItem() == moduleItem : ItemStack::isEmpty;

		for (int i = 0; i < modules.size(); i++) {
			if (predicate.test(modules.get(i))) {
				ItemStack toInsert = module.copy();

				if (toggled)
					toggleModuleState(moduleItem.getModuleType(), true);
				else {
					modules.set(i, toInsert);
					onModuleInserted(toInsert, moduleItem.getModuleType(), toggled);
				}

				break;
			}
		}
	}

	/**
	 * Removes the first item with the given module type from the inventory.
	 *
	 * @param module The module type to remove
	 * @param toggled false if the actual item changed, true if the enabled state of the module changed
	 */
	public default void removeModule(ModuleType module, boolean toggled) {
		NonNullList<ItemStack> modules = getInventory();

		for (int i = 0; i < modules.size(); i++) {
			if (!modules.get(i).isEmpty() && modules.get(i).getItem() instanceof ModuleItem && ((ModuleItem) modules.get(i).getItem()).getModuleType() == module) {
				if (toggled)
					toggleModuleState(module, false);
				else
					modules.set(i, ItemStack.EMPTY);
			}
		}
	}

	/**
	 * @param module The type to check if it is present in this inventory
	 * @return true if the given module type is present in this inventory, false otherwise
	 * @deprecated Prefer using {@link #isModuleEnabled(ModuleType)}
	 */
	@Deprecated
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
				if (!modules.get(i).isEmpty() && modules.get(i).getItem() instanceof ModuleItem && ((ModuleItem) modules.get(i).getItem()).getModuleType() == module)
					return true;
			}
		}

		return false;
	}

	/**
	 * Call this from your read method. Used for reading the module inventory from a tag. Use in conjunction with
	 * writeModuleInventory.
	 *
	 * @param tag The tag to read the inventory from
	 * @return A NonNullList of ItemStacks that were read from the given tag
	 */
	public default NonNullList<ItemStack> readModuleInventory(CompoundNBT tag) {
		ListNBT list = tag.getList("Modules", Constants.NBT.TAG_COMPOUND);
		NonNullList<ItemStack> modules = NonNullList.withSize(getMaxNumberOfModules(), ItemStack.EMPTY);

		for (int i = 0; i < list.size(); ++i) {
			CompoundNBT stackTag = list.getCompound(i);
			byte slot = stackTag.getByte("ModuleSlot");

			if (slot >= 0 && slot < modules.size())
				modules.set(slot, ItemStack.of(stackTag));
		}

		return modules;
	}

	/**
	 * Call this from your load method after loadModuleInventory. Used for loading which modules are enabled from a tag. Use in
	 * conjunction with saveModuleStates.
	 *
	 * @param tag The tag to read the states from
	 * @return An EnumMap of all module types with the enabled flag set as read from the tag
	 */
	public default Map<ModuleType, Boolean> readModuleStates(CompoundNBT tag) {
		Map<ModuleType, Boolean> moduleStates = new EnumMap<>(ModuleType.class);
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
	 * Call this from your write method. Used for writing the module inventory to a tag. Use in conjunction with
	 * readModuleInventory.
	 *
	 * @param tag The tag to write the inventory to
	 * @return The modified tag
	 */
	public default CompoundNBT writeModuleInventory(CompoundNBT tag) {
		ListNBT list = new ListNBT();
		NonNullList<ItemStack> modules = getInventory();

		for (int i = 0; i < modules.size(); i++) {
			if (!modules.get(i).isEmpty()) {
				CompoundNBT stackTag = new CompoundNBT();

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
	public default CompoundNBT writeModuleStates(CompoundNBT tag) {
		for (ModuleType module : acceptedModules()) {
			tag.putBoolean(module.name().toLowerCase() + "Enabled", isModuleEnabled(module));
		}

		return tag;
	}

	/**
	 * Checks whether the entity is listed on the allowlist of this block, if an allowlist module exists
	 *
	 * @param entity The entity to check
	 * @return true if the entity is listed on the allowlist module, false otherwise
	 */
	public default boolean isAllowed(Entity entity) {
		return isAllowed(entity.getName().getString());
	}

	/**
	 * Checks whether the name of the entity is listed on the allowlist of this block, if an allowlist module exists
	 *
	 * @param entity The name of the to check
	 * @return true if the name of the entity is listed on the allowlist module, false otherwise
	 */
	public default boolean isAllowed(String name) {
		if (!isModuleEnabled(ModuleType.ALLOWLIST))
			return false;

		ItemStack stack = getModule(ModuleType.ALLOWLIST);

		if (stack.hasTag() && stack.getTag().getBoolean("affectEveryone"))
			return true;

		//IModuleInventory#getModule returns ItemStack.EMPTY when the module does not exist, and getPlayersFromModule will then have an empty list
		return ModuleItem.doesModuleHaveTeamOf(stack, name, getBlockEntity().getLevel()) || ModuleItem.getPlayersFromModule(stack).contains(name.toLowerCase());
	}

	/**
	 * Checks whether the entity is listed on the denylist of this block, if a denylist module exists
	 *
	 * @param entity The entity to check
	 * @return true if the entity is listed on the denylist module, false otherwise
	 */
	public default boolean isDenied(Entity entity) {
		if (!isModuleEnabled(ModuleType.DENYLIST))
			return false;

		ItemStack stack = getModule(ModuleType.DENYLIST);

		if (stack.hasTag() && stack.getTag().getBoolean("affectEveryone")) {
			if (getBlockEntity() instanceof IOwnable) {
				//only deny players that are not the owner
				if (entity instanceof PlayerEntity) {
					//if the player IS the owner, fall back to the default handling (check if the name is on the list)
					if (!((IOwnable) getBlockEntity()).isOwnedBy((PlayerEntity) entity))
						return true;
				}
				else
					return true;
			}
			else
				return true;
		}

		String name = entity.getName().getString();

		//IModuleInventory#getModule returns ItemStack.EMPTY when the module does not exist, and getPlayersFromModule will then have an empty list
		return ModuleItem.doesModuleHaveTeamOf(stack, name, getBlockEntity().getLevel()) || ModuleItem.getPlayersFromModule(stack).contains(name.toLowerCase());
	}

	/**
	 * Determine whether the modules in this inventory should be dropped when the block is broken
	 *
	 * @return true if the modules should be dropped, false if not
	 */
	public default boolean shouldDropModules() {
		return true;
	}

	/**
	 * Get the description text's translation key that is shown in the customize screen tooltip when hovering over a module
	 * button
	 *
	 * @param blockName The name of the block that is being customized
	 * @param module The type of the module whose module button is being hovered
	 * @return The translation key to use for the description
	 */
	public default String getModuleDescriptionId(String blockName, ModuleType module) {
		return "module." + blockName + "." + module.getTranslationKey().substring(5).replace("securitycraft.", "") + ".description";
	}
}