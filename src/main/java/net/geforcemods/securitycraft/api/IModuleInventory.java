package net.geforcemods.securitycraft.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import com.mojang.serialization.Codec;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.components.ListModuleData;
import net.geforcemods.securitycraft.items.ModuleItem;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.ItemStackWithSlot;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;

import static com.mojang.serialization.codecs.RecordCodecBuilder.create;

/**
 * Let your object implement this to be able to add modules to it
 *
 * @author bl4ckscor3
 */
public interface IModuleInventory extends ResourceHandler<ItemResource> {
	Codec<ItemStackWithSlot> MODULE_SLOT_CODEC = create(i -> i.group(
					ExtraCodecs.UNSIGNED_BYTE.fieldOf("ModuleSlot").orElse(0).forGetter(ItemStackWithSlot::slot),
					ItemStack.MAP_CODEC.forGetter(ItemStackWithSlot::stack))
			.apply(i, ItemStackWithSlot::new));

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
	 * well.
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
	 * @return The level of this object
	 */
	public Level myLevel();

	/**
	 * @return The position of this object
	 */
	public BlockPos myPos();

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
		toggleModuleState(module, true);

		if (this instanceof BlockEntity be && !be.getLevel().isClientSide()) {
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
		toggleModuleState(module, false);

		if (this instanceof BlockEntity be && !be.getLevel().isClientSide()) {
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

	/**
	 * Drops all modules in this inventory at the position in the world
	 */
	public default void dropAllModules() {
		for (ItemStack module : getInventory()) {
			if (!(module.getItem() instanceof ModuleItem))
				continue;

			if (this instanceof LinkableBlockEntity be)
				be.propagate(new ILinkedAction.ModuleRemoved(((ModuleItem) module.getItem()).getModuleType(), false), be);

			Block.popResource(myLevel(), myPos(), module);
		}

		getInventory().clear();
	}

	@Override
	default long getAmountAsLong(int index) {
		return getResource(index).isEmpty() ? 0 : 1;
	}

	@Override
	public default int size() {
		return acceptedModules().length;
	}

	@Override
	public default ItemResource getResource(int slot) {
		return ItemResource.of(getModuleInSlot(slot));
	}

	public default ItemStack getModuleInSlot(int slot) {
		slot = fixSlotId(slot);
		return slot < 0 || slot >= size() ? ItemStack.EMPTY : getInventory().get(slot);
	}

	@Override
	public default ItemStack extractItem(int slot, int amount, boolean simulate) {
		slot = fixSlotId(slot);

		ItemStack stack = getModuleInSlot(slot).copy();

		if (stack.isEmpty())
			return ItemStack.EMPTY;
		else {
			if (!simulate) {
				getInventory().set(slot, ItemStack.EMPTY);

				if (stack.getItem() instanceof ModuleItem module) {
					onModuleRemoved(stack, module.getModuleType(), false);

					if (this instanceof LinkableBlockEntity be)
						be.propagate(new ILinkedAction.ModuleRemoved(((ModuleItem) stack.getItem()).getModuleType(), false), be);
				}

				return stack;
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

				if (stack.getItem() instanceof ModuleItem module) {
					onModuleInserted(stack, module.getModuleType(), false);

					if (this instanceof LinkableBlockEntity be)
						be.propagate(new ILinkedAction.ModuleInserted(copy, (ModuleItem) copy.getItem(), false), be);
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

		//Prevent module from being removed and re-added when the slot initializes
		if (ItemStack.matches(previous, stack))
			return;

		//call the correct methods, should there have been a module in the slot previously
		if (!previous.isEmpty()) {
			onModuleRemoved(previous, ((ModuleItem) previous.getItem()).getModuleType(), false);

			if (this instanceof LinkableBlockEntity be)
				be.propagate(new ILinkedAction.ModuleRemoved(((ModuleItem) previous.getItem()).getModuleType(), false), be);
		}

		getInventory().set(slot, stack);

		if (stack.getItem() instanceof ModuleItem module) {
			onModuleInserted(stack, module.getModuleType(), false);

			if (this instanceof LinkableBlockEntity be)
				be.propagate(new ILinkedAction.ModuleInserted(stack, (ModuleItem) stack.getItem(), false), be);
		}
	}

	@Override
	public default long getCapacityAsLong(int slot, ItemResource resource) {
		return 1;
	}

	@Override
	public default boolean isValid(int slot, ItemResource resource) {
		slot = fixSlotId(slot);
		return getModuleInSlot(slot).isEmpty() && !resource.isEmpty() && resource.getItem() instanceof ModuleItem module && acceptsModule(module.getModuleType()) && !hasModule(module.getModuleType());
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
	 * @return A List of all ModuleType currently inserted in the object
	 */
	public default List<ModuleType> getInsertedModules() {
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
	 * @param toggled false if the actual item changed, true if the enabled state of the module changed
	 */
	public default void insertModule(ItemStack module, boolean toggled) {
		if (module.isEmpty() || !(module.getItem() instanceof ModuleItem moduleItem))
			return;

		NonNullList<ItemStack> modules = getInventory();

		//if the module is being toggled, then there should not be a check for whether the module already exists
		if (!toggled) {
			for (int i = 0; i < modules.size(); i++) {
				if (!modules.get(i).isEmpty() && modules.get(i).getItem() == moduleItem)
					return;
			}
		}

		//if the module is being toggled, the test should be for the stack that matches the module. if not, the test should look for the first empty slot
		Predicate<ItemStack> predicate = toggled ? stack -> stack.getItem() == moduleItem : ItemStack::isEmpty;

		for (int i = 0; i < modules.size(); i++) {
			if (predicate.test(modules.get(i))) {
				ItemStack toInsert = module.copy();

				if (!toggled)
					modules.set(i, toInsert);

				onModuleInserted(toInsert, moduleItem.getModuleType(), toggled);
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
			ItemStack moduleStack = modules.get(i);

			if (!moduleStack.isEmpty() && moduleStack.getItem() instanceof ModuleItem moduleItem && moduleItem.getModuleType() == module) {
				if (!toggled)
					modules.set(i, ItemStack.EMPTY);

				onModuleRemoved(moduleStack, module, toggled);
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
				if (!modules.get(i).isEmpty() && modules.get(i).getItem() instanceof ModuleItem moduleItem && moduleItem.getModuleType() == module)
					return true;
			}
		}

		return false;
	}

	/**
	 * Used for reading the module inventory from a tag. Use in conjunction with writeModuleInventory.
	 *
	 * @param tag The tag to read the inventory from
	 * @return A NonNullList of ItemStacks that were read from the given tag
	 */
	public default NonNullList<ItemStack> readModuleInventory(ValueInput tag) {
		NonNullList<ItemStack> modules = NonNullList.withSize(getMaxNumberOfModules(), ItemStack.EMPTY);

		for (ItemStackWithSlot itemstackwithslot : tag.listOrEmpty("Modules", MODULE_SLOT_CODEC)) {
			if (itemstackwithslot.isValidInContainer(modules.size()))
				modules.set(itemstackwithslot.slot(), itemstackwithslot.stack());
		}

		return modules;
	}

	/**
	 * Used for loading which modules are enabled from a tag. Use in conjunction with writeModuleStates and call after
	 * readModuleInventory.
	 *
	 * @param tag The tag to read the states from
	 * @return An EnumMap of all module types with the enabled flag set as read from the tag
	 */
	public default Map<ModuleType, Boolean> readModuleStates(ValueInput tag) {
		EnumMap<ModuleType, Boolean> moduleStates = new EnumMap<>(ModuleType.class);
		List<ModuleType> acceptedModules = Arrays.asList(acceptedModules());

		for (ModuleType module : ModuleType.values()) {
			if (acceptedModules.contains(module)) {
				String key = module.name().toLowerCase() + "Enabled";

				moduleStates.put(module, tag.getBooleanOr(key, hasModule(module))); //if the module is accepted, but no state was saved yet, revert to whether the module is installed
			}
			else
				moduleStates.put(module, false); //module is not accepted, so disable it right away
		}

		return moduleStates;
	}

	/**
	 * Used for writing the module inventory to a tag. Use in conjunction with readModuleInventory.
	 *
	 * @param tag The tag to write the inventory to
	 */
	public default void writeModuleInventory(ValueOutput tag) {
		ValueOutput.TypedOutputList<ItemStackWithSlot> list = tag.list("Modules", MODULE_SLOT_CODEC);
		NonNullList<ItemStack> modules = getInventory();

		for (int i = 0; i < modules.size(); i++) {
			ItemStack stack = modules.get(i);

			if (!stack.isEmpty())
				list.add(new ItemStackWithSlot(i, stack));
		}
	}

	/**
	 * Used for writing which modules are enabled to a tag. Use in conjunction with readModuleStates and call after
	 * writeModuleInventory.
	 *
	 * @param tag The tag to save the module enabled states to
	 */
	public default void writeModuleStates(ValueOutput tag) {
		for (ModuleType module : acceptedModules()) {
			tag.putBoolean(module.name().toLowerCase() + "Enabled", isModuleEnabled(module));
		}
	}

	/**
	 * Checks whether the entity is listed on the allowlist of this object, if an allowlist module exists
	 *
	 * @param entity The entity to check
	 * @return true if the entity is listed on the allowlist module, false otherwise
	 */
	public default boolean isAllowed(Entity entity) {
		String name;

		if (this instanceof IOwnable ownable && entity instanceof Player player && ownable.isOwnedBy(player, true))
			name = PlayerUtils.getNameFromPlayerOrMask(player);
		else
			name = entity.getName().getString();

		return isAllowed(name);
	}

	/**
	 * Checks whether the name of the entity is listed on the allowlist of this object, if an allowlist module exists
	 *
	 * @param name The name of the entity to check
	 * @return true if the name of the entity is listed on the allowlist module, false otherwise
	 */
	public default boolean isAllowed(String name) {
		if (!isModuleEnabled(ModuleType.ALLOWLIST))
			return false;

		ListModuleData listModuleData = getModule(ModuleType.ALLOWLIST).get(SCContent.LIST_MODULE_DATA);

		return listModuleData != null && (listModuleData.affectEveryone() || listModuleData.isTeamOfPlayerOnList(myLevel(), name) || listModuleData.isPlayerOnList(name));
	}

	/**
	 * Checks whether the entity is listed on the denylist of this object, if a denylist module exists
	 *
	 * @param entity The entity to check
	 * @return true if the entity is listed on the denylist module, false otherwise
	 */
	public default boolean isDenied(Entity entity) {
		if (!isModuleEnabled(ModuleType.DENYLIST))
			return false;

		ListModuleData listModuleData = getModule(ModuleType.DENYLIST).get(SCContent.LIST_MODULE_DATA);
		String name;

		if (listModuleData != null) {
			if (listModuleData.affectEveryone()) {
				if (this instanceof IOwnable ownable) {
					//only deny players that are not the owner
					if (entity instanceof Player player) {
						//if the player IS the owner, fall back to the default handling (check if the name is on the list)
						if (!ownable.isOwnedBy(player))
							return true;
					}
					else
						return true;
				}
				else
					return true;
			}

			if (this instanceof IOwnable ownable && entity instanceof Player player && ownable.isOwnedBy(player, true))
				name = PlayerUtils.getNameFromPlayerOrMask(player);
			else
				name = entity.getName().getString();

			return listModuleData.isTeamOfPlayerOnList(myLevel(), name) || listModuleData.isPlayerOnList(name);
		}

		return false;
	}

	/**
	 * Determine whether the modules in this inventory should be dropped when the object is broken
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
	 * @param denotation The denotation to use for the key, usually the block's name
	 * @param module The type of the module whose module button is being hovered
	 * @return The translation key to use for the description
	 */
	public default String getModuleDescriptionId(String denotation, ModuleType module) {
		if (module == ModuleType.DISGUISE)
			return "module.generic.disguise_module.description";

		return getBaseModuleDescriptionId(denotation, module);
	}

	public static String getBaseModuleDescriptionId(String denotation, ModuleType module) {
		return "module." + denotation + "." + module.getTranslationKey().substring(5).replace("securitycraft.", "") + ".description";
	}
}