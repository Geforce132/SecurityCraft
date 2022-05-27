package net.geforcemods.securitycraft.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.function.Predicate;

import net.geforcemods.securitycraft.items.ItemModule;
import net.geforcemods.securitycraft.misc.EnumModuleType;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.FMLCommonHandler;
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
	 * @return An array of what {@link EnumModuleType} can be inserted into this inventory
	 */
	public EnumModuleType[] acceptedModules();

	/**
	 * Checks whether the given module's functionality is enabled
	 *
	 * @param module The module
	 * @return true if the given module is enabled, false otherwise. If the module does not exist, this should return false
	 *         as well.
	 */
	public boolean isModuleEnabled(EnumModuleType module);

	/**
	 * Turns the given module type on or off, depending on shouldBeEnabled. The module needs to be present in the inventory
	 * in order for toggling to work.
	 *
	 * @param module The type of the module to toggle
	 * @param shouldBeEnabled Whether the state of this module type should be set to enabled
	 */
	public void toggleModuleState(EnumModuleType module, boolean shouldBeEnabled);

	/**
	 * @return The TileEntity this inventory is for
	 */
	public default TileEntity getTileEntity() {
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
	 * @param module The EnumModuleType variant of stack.
	 * @param toggled false if the actual item changed, true if the enabled state of the module changed
	 */
	public default void onModuleInserted(ItemStack stack, EnumModuleType module, boolean toggled) {
		TileEntity te = getTileEntity();

		if (!te.getWorld().isRemote) {
			IBlockState state = te.getWorld().getBlockState(te.getPos());

			if (!toggled)
				toggleModuleState(module, true);

			te.markDirty();
			te.getWorld().notifyBlockUpdate(te.getPos(), state, state, 3);
			te.getWorld().notifyNeighborsOfStateChange(te.getPos(), te.getBlockType(), false);
			FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().sendPacketToAllPlayers(getTileEntity().getUpdatePacket());
		}
	}

	/**
	 * Called whenever a module is removed from a slot in the "Customize" GUI.
	 *
	 * @param stack The raw ItemStack being removed.
	 * @param module The EnumModuleType variant of stack.
	 * @param toggled false if the actual item changed, true if the enabled state of the module changed
	 */
	public default void onModuleRemoved(ItemStack stack, EnumModuleType module, boolean toggled) {
		TileEntity te = getTileEntity();

		if (!te.getWorld().isRemote) {
			IBlockState state = te.getWorld().getBlockState(te.getPos());
			if (!toggled)
				toggleModuleState(module, false);

			te.markDirty();
			te.getWorld().notifyBlockUpdate(te.getPos(), state, state, 3);
			te.getWorld().notifyNeighborsOfStateChange(te.getPos(), te.getBlockType(), false);
			FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().sendPacketToAllPlayers(getTileEntity().getUpdatePacket());
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

	public default void dropAllModules() {
		TileEntity te = getTileEntity();
		World level = te.getWorld();
		BlockPos pos = te.getPos();
		TileEntityLinkable linkable = te instanceof TileEntityLinkable ? (TileEntityLinkable) te : null;

		for (ItemStack module : getInventory()) {
			if (!(module.getItem() instanceof ItemModule))
				continue;

			if (linkable != null)
				ModuleUtils.createLinkedAction(EnumLinkedAction.MODULE_REMOVED, module, linkable, false);

			Block.spawnAsEntity(level, pos, module);
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
				TileEntity te = getTileEntity();

				if (stack.getItem() instanceof ItemModule) {
					onModuleRemoved(stack, ((ItemModule) stack.getItem()).getModuleType(), false);

					if (te instanceof TileEntityLinkable)
						ModuleUtils.createLinkedAction(EnumLinkedAction.MODULE_REMOVED, stack, (TileEntityLinkable) te, false);
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
				TileEntity te = getTileEntity();

				copy.setCount(1);
				getInventory().set(slot, copy);

				if (stack.getItem() instanceof ItemModule) {
					onModuleInserted(stack, ((ItemModule) stack.getItem()).getModuleType(), false);

					if (te instanceof TileEntityLinkable)
						ModuleUtils.createLinkedAction(EnumLinkedAction.MODULE_INSERTED, copy, (TileEntityLinkable) te, false);
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

		TileEntity te = getTileEntity();
		ItemStack previous = getModuleInSlot(slot);

		//call the correct methods, should there have been a module in the slot previously
		if (!previous.isEmpty()) {
			onModuleRemoved(previous, ((ItemModule) previous.getItem()).getModuleType(), false);

			if (te instanceof TileEntityLinkable)
				ModuleUtils.createLinkedAction(EnumLinkedAction.MODULE_REMOVED, previous, (TileEntityLinkable) te, false);
		}

		getInventory().set(slot, stack);

		if (stack.getItem() instanceof ItemModule) {
			onModuleInserted(stack, ((ItemModule) stack.getItem()).getModuleType(), false);

			if (te instanceof TileEntityLinkable)
				ModuleUtils.createLinkedAction(EnumLinkedAction.MODULE_INSERTED, stack, (TileEntityLinkable) te, false);
		}
	}

	@Override
	public default int getSlotLimit(int slot) {
		return 1;
	}

	@Override
	public default boolean isItemValid(int slot, ItemStack stack) {
		slot = fixSlotId(slot);
		return getModuleInSlot(slot).isEmpty() && !stack.isEmpty() && stack.getItem() instanceof ItemModule && acceptsModule(((ItemModule) stack.getItem()).getModuleType()) && !hasModule(((ItemModule) stack.getItem()).getModuleType());
	}

	/**
	 * @return true if the inventory accepts the given {@link EnumModuleType}, false otherwise
	 */
	public default boolean acceptsModule(EnumModuleType type) {
		for (EnumModuleType module : acceptedModules()) {
			if (module == type)
				return true;
		}

		return false;
	}

	/**
	 * @return A List of all EnumModuleType currently inserted in the TileEntity.
	 */
	public default ArrayList<EnumModuleType> getInsertedModules() {
		ArrayList<EnumModuleType> modules = new ArrayList<>();

		for (ItemStack stack : getInventory()) {
			if (!stack.isEmpty() && stack.getItem() instanceof ItemModule)
				modules.add(((ItemModule) stack.getItem()).getModuleType());
		}

		return modules;
	}

	/**
	 * @param The module type of the stack to get
	 * @return The ItemStack for the given EnumModuleType type. If there is no ItemStack for that type, returns
	 *         ItemStack.EMPTY.
	 */
	public default ItemStack getModule(EnumModuleType module) {
		NonNullList<ItemStack> modules = getInventory();

		for (int i = 0; i < modules.size(); i++) {
			if (!modules.get(i).isEmpty() && modules.get(i).getItem() instanceof ItemModule && ((ItemModule) modules.get(i).getItem()).getModuleType() == module)
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
		if (module.isEmpty() || !(module.getItem() instanceof ItemModule))
			return;

		ItemModule moduleItem = (ItemModule) module.getItem();
		NonNullList<ItemStack> modules = getInventory();

		//if the module is being toggled, then there should not be a check for whether the module already exists
		if (!toggled) {
			for (int i = 0; i < modules.size(); i++) {
				if (!modules.get(i).isEmpty()) {
					if (modules.get(i).getItem() == module.getItem())
						return;
				}
			}
		}

		//if the module is being toggled, the test should be for the stack that matches the module. if not, the test should look for the first empty slot
		Predicate<ItemStack> predicate = toggled ? stack -> stack.getItem() == moduleItem : stack -> stack.isEmpty();

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
	public default void removeModule(EnumModuleType module, boolean toggled) {
		NonNullList<ItemStack> modules = getInventory();

		for (int i = 0; i < modules.size(); i++) {
			if (!modules.get(i).isEmpty() && modules.get(i).getItem() instanceof ItemModule && ((ItemModule) modules.get(i).getItem()).getModuleType() == module) {
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
	 */
	public default boolean hasModule(EnumModuleType module) {
		NonNullList<ItemStack> modules = getInventory();

		if (module == null) {
			for (int i = 0; i < modules.size(); i++) {
				if (modules.get(i).isEmpty())
					return true;
			}
		}
		else {
			for (int i = 0; i < modules.size(); i++) {
				if (!modules.get(i).isEmpty() && modules.get(i).getItem() instanceof ItemModule && ((ItemModule) modules.get(i).getItem()).getModuleType() == module)
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
	public default NonNullList<ItemStack> readModuleInventory(NBTTagCompound tag) {
		NBTTagList list = tag.getTagList("Modules", Constants.NBT.TAG_COMPOUND);
		NonNullList<ItemStack> modules = NonNullList.withSize(getMaxNumberOfModules(), ItemStack.EMPTY);

		for (int i = 0; i < list.tagCount(); ++i) {
			NBTTagCompound stackTag = list.getCompoundTagAt(i);
			byte slot = stackTag.getByte("ModuleSlot");

			if (slot >= 0 && slot < modules.size())
				modules.set(slot, new ItemStack(stackTag));
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
	public default EnumMap<EnumModuleType, Boolean> readModuleStates(NBTTagCompound tag) {
		EnumMap<EnumModuleType, Boolean> moduleStates = new EnumMap<>(EnumModuleType.class);
		List<EnumModuleType> acceptedModules = Arrays.asList(acceptedModules());

		for (EnumModuleType module : EnumModuleType.values()) {
			if (acceptedModules.contains(module)) {
				String key = module.name().toLowerCase() + "Enabled";

				if (tag.hasKey(key))
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
	public default NBTTagCompound writeModuleInventory(NBTTagCompound tag) {
		NBTTagList list = new NBTTagList();
		NonNullList<ItemStack> modules = getInventory();

		for (int i = 0; i < modules.size(); i++) {
			if (!modules.get(i).isEmpty()) {
				NBTTagCompound stackTag = new NBTTagCompound();

				stackTag.setByte("ModuleSlot", (byte) i);
				modules.get(i).writeToNBT(stackTag);
				list.appendTag(stackTag);
			}
		}

		tag.setTag("Modules", list);
		return tag;
	}

	/**
	 * Call this from your save method. Used for writing which modules are enabled to a tag. Use in conjunction with
	 * loadModuleStates.
	 *
	 * @param tag The tag to save the module enabled states to
	 * @return The modified tag
	 */
	public default NBTTagCompound writeModuleStates(NBTTagCompound tag) {
		for (EnumModuleType module : acceptedModules()) {
			tag.setBoolean(module.name().toLowerCase() + "Enabled", isModuleEnabled(module));
		}

		return tag;
	}
}