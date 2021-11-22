package net.geforcemods.securitycraft.api;

import java.util.ArrayList;

import net.geforcemods.securitycraft.items.ModuleItem;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.IItemHandlerModifiable;

/**
 * Let your TileEntity implement this to be able to add modules to it
 * @author bl4ckscor3
 */
public interface IModuleInventory extends IItemHandlerModifiable
{
	/**
	 * @return The list that holds the contents of this inventory
	 */
	public NonNullList<ItemStack> getInventory();

	/**
	 * @return An array of what {@link ModuleType} can be inserted into this inventory
	 */
	public ModuleType[] acceptedModules();

	/**
	 * @return The TileEntity this inventory is for
	 */
	public BlockEntity getTileEntity();

	/**
	 * @return The amount of modules that can be inserted
	 */
	public default int getMaxNumberOfModules()
	{
		return acceptedModules().length;
	}

	/**
	 * Called whenever a module is inserted into a slot in the "Customize" GUI.
	 *
	 * @param stack The raw ItemStack being inserted.
	 * @param module The ModuleType variant of stack.
	 */
	public default void onModuleInserted(ItemStack stack, ModuleType module)
	{
		BlockEntity te = getTileEntity();

		if(!te.getLevel().isClientSide)
			te.getLevel().sendBlockUpdated(te.getBlockPos(), te.getBlockState(), te.getBlockState(), 3);
	}

	/**
	 * Called whenever a module is removed from a slot in the "Customize" GUI.
	 *
	 * @param stack The raw ItemStack being removed.
	 * @param module The ModuleType variant of stack.
	 */
	public default void onModuleRemoved(ItemStack stack, ModuleType module)
	{
		BlockEntity te = getTileEntity();

		if(!te.getLevel().isClientSide)
			te.getLevel().sendBlockUpdated(te.getBlockPos(), te.getBlockState(), te.getBlockState(), 3);
	}

	/**
	 * Used for enabling differentiation between module slots and slots that are handled by IInventory.
	 * This is needed because of the duplicate getStackInSlot method.
	 * @return true if the slot ids are not starting with 0, false otherwise
	 */
	public default boolean enableHack()
	{
		return false;
	}

	/**
	 * Only override if enableHack returns true and your ids don't start at 100. Used to convert the slot ids to inventory indices
	 * @param id The slot id to convert
	 * @return The inventory index corresponding to the slot id
	 */
	public default int fixSlotId(int id)
	{
		return id >= 100 ? id - 100 : id;
	}

	@Override
	public default int getSlots()
	{
		return acceptedModules().length;
	}

	@Override
	public default ItemStack getStackInSlot(int slot)
	{
		return getModuleInSlot(slot);
	}

	public default ItemStack getModuleInSlot(int slot)
	{
		slot = fixSlotId(slot);
		return slot < 0 || slot >= getSlots() ? ItemStack.EMPTY : getInventory().get(slot);
	}

	@Override
	public default ItemStack extractItem(int slot, int amount, boolean simulate)
	{
		slot = fixSlotId(slot);

		ItemStack stack = getModuleInSlot(slot).copy();

		if(stack.isEmpty())
			return ItemStack.EMPTY;
		else
		{
			if(!simulate)
			{
				BlockEntity tile = getTileEntity();

				if(stack.getItem() instanceof ModuleItem module)
				{
					onModuleRemoved(stack, module.getModuleType());

					if(tile instanceof LinkableBlockEntity te)
						ModuleUtils.createLinkedAction(LinkedAction.MODULE_REMOVED, stack, te);
				}

				return getInventory().set(slot, ItemStack.EMPTY).copy();
			}
			else return stack.copy();
		}
	}

	@Override
	public default ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
	{
		slot = fixSlotId(slot);

		if(!getModuleInSlot(slot).isEmpty())
			return stack;
		else
		{
			int returnSize = 0;

			//the max stack size is one, so in order to provide the correct return value, the count after insertion is calculated here
			if(stack.getCount() > 1)
				returnSize = stack.getCount() - 1;

			if(!simulate)
			{
				ItemStack copy = stack.copy();
				BlockEntity tile = getTileEntity();

				copy.setCount(1);
				getInventory().set(slot, copy);

				if(stack.getItem() instanceof ModuleItem module)
				{
					onModuleInserted(stack, module.getModuleType());

					if(tile instanceof LinkableBlockEntity te)
						ModuleUtils.createLinkedAction(LinkedAction.MODULE_INSERTED, copy, te);
				}
			}

			if(returnSize != 0)
			{
				ItemStack toReturn = stack.copy();

				toReturn.setCount(returnSize);
				return toReturn;
			}
			else return ItemStack.EMPTY;
		}
	}

	@Override
	public default void setStackInSlot(int slot, ItemStack stack)
	{
		slot = fixSlotId(slot);

		BlockEntity tile = getTileEntity();
		ItemStack previous = getModuleInSlot(slot);

		//call the correct methods, should there have been a module in the slot previously
		if(!previous.isEmpty())
		{
			onModuleRemoved(previous, ((ModuleItem)previous.getItem()).getModuleType());

			if(tile instanceof LinkableBlockEntity te)
				ModuleUtils.createLinkedAction(LinkedAction.MODULE_REMOVED, previous, te);
		}

		getInventory().set(slot, stack);

		if(stack.getItem() instanceof ModuleItem module)
		{
			onModuleInserted(stack, module.getModuleType());

			if(tile instanceof LinkableBlockEntity te)
				ModuleUtils.createLinkedAction(LinkedAction.MODULE_INSERTED, stack, te);
		}
	}

	@Override
	public default int getSlotLimit(int slot)
	{
		return 1;
	}

	@Override
	public default boolean isItemValid(int slot, ItemStack stack)
	{
		slot = fixSlotId(slot);
		return getModuleInSlot(slot).isEmpty() && !stack.isEmpty() && stack.getItem() instanceof ModuleItem module && getAcceptedModules().contains(module.getModuleType()) && !hasModule(module.getModuleType());
	}

	/**
	 * @return A list of all {@link ModuleType} that can be inserted into this inventory
	 */
	public default ArrayList<ModuleType> getAcceptedModules()
	{
		ArrayList<ModuleType> list = new ArrayList<>();

		for(ModuleType module : acceptedModules())
			list.add(module);

		return list;
	}

	/**
	 * @return A List of all ModuleType currently inserted in the TileEntity.
	 */
	public default ArrayList<ModuleType> getInsertedModules()
	{
		ArrayList<ModuleType> modules = new ArrayList<>();

		for(ItemStack stack : getInventory())
		{
			if(!stack.isEmpty() && stack.getItem() instanceof ModuleItem module)
				modules.add(module.getModuleType());
		}

		return modules;
	}

	/**
	 * @param module The module type of the stack to get
	 * @return The ItemStack for the given ModuleType type.
	 * If there is no ItemStack for that type, returns ItemStack.EMPTY.
	 */
	public default ItemStack getModule(ModuleType module)
	{
		NonNullList<ItemStack> modules = getInventory();

		for(int i = 0; i < modules.size(); i++)
		{
			if(!modules.get(i).isEmpty() && modules.get(i).getItem() instanceof ModuleItem moduleItem && moduleItem.getModuleType() == module)
				return modules.get(i);
		}

		return ItemStack.EMPTY;
	}

	/**
	 * Inserts a generic copy of the given module type into the customization inventory.
	 * @param module The module type to insert
	 */
	public default void insertModule(ModuleType module)
	{
		NonNullList<ItemStack> modules = getInventory();

		for(int i = 0; i < modules.size(); i++)
		{
			if(!modules.get(i).isEmpty())
			{
				if(modules.get(i).getItem() == module.getItem())
					return;
			}
		}

		for(int i = 0; i < modules.size(); i++)
		{
			if(!modules.get(i).isEmpty() && module != null)
			{
				modules.set(i, new ItemStack(module.getItem()));
				break;
			}
			else if(!modules.get(i).isEmpty() && module == null)
				modules.set(i, ItemStack.EMPTY);
		}
	}

	/**
	 * Inserts an exact copy of the given item into the customization inventory, if it is not empty and a module.
	 * @param module The stack to insert
	 */
	public default void insertModule(ItemStack module)
	{
		if(module.isEmpty() || !(module.getItem() instanceof ModuleItem))
			return;

		NonNullList<ItemStack> modules = getInventory();

		for(int i = 0; i < modules.size(); i++)
		{
			if(!modules.get(i).isEmpty())
			{
				if(modules.get(i).getItem() == module.getItem())
					return;
			}
		}

		for(int i = 0; i < modules.size(); i++)
		{
			if(modules.get(i).isEmpty())
			{
				modules.set(i, module.copy());
				break;
			}
		}
	}

	/**
	 * Removes the first item with the given module type from the inventory.
	 * @param module The module type to remove
	 */
	public default void removeModule(ModuleType module)
	{
		NonNullList<ItemStack> modules = getInventory();

		for(int i = 0; i < modules.size(); i++)
		{
			if(!modules.get(i).isEmpty() && modules.get(i).getItem() instanceof ModuleItem moduleItem && moduleItem.getModuleType() == module)
				modules.set(i, ItemStack.EMPTY);
		}
	}

	/**
	 * @param module The type to check if it is present in this inventory
	 * @return true if the given module type is present in this inventory, false otherwise
	 */
	public default boolean hasModule(ModuleType module)
	{
		NonNullList<ItemStack> modules = getInventory();

		if(module == null)
		{
			for(int i = 0; i < modules.size(); i++)
			{
				if(modules.get(i).isEmpty())
					return true;
			}
		}
		else
		{
			for(int i = 0; i < modules.size(); i++)
			{
				if(!modules.get(i).isEmpty() && modules.get(i).getItem() instanceof ModuleItem moduleItem && moduleItem.getModuleType() == module)
					return true;
			}
		}

		return false;
	}

	/**
	 * Call this from your read method. Used for reading the module inventory from a tag. Use in conjunction with writeModuleInventory.
	 * @param tag The tag to read the inventory from
	 * @return A NonNullList of ItemStacks that were read from the given tag
	 */
	public default NonNullList<ItemStack> readModuleInventory(CompoundTag tag)
	{
		ListTag list = tag.getList("Modules", Constants.NBT.TAG_COMPOUND);
		NonNullList<ItemStack> modules = NonNullList.withSize(getMaxNumberOfModules(), ItemStack.EMPTY);

		for(int i = 0; i < list.size(); ++i)
		{
			CompoundTag stackTag = list.getCompound(i);
			byte slot = stackTag.getByte("ModuleSlot");

			if(slot >= 0 && slot < modules.size())
				modules.set(slot, ItemStack.of(stackTag));
		}

		return modules;
	}

	/**
	 * Call this from your write method. Used for writing the module inventory to a tag. Use in conjunction with readModuleInventory.
	 * @param tag The tag to write the inventory to
	 * @return The modified CompoundNBT
	 */
	public default CompoundTag writeModuleInventory(CompoundTag tag)
	{
		ListTag list = new ListTag();
		NonNullList<ItemStack> modules = getInventory();

		for(int i = 0; i < modules.size(); i++)
		{
			if(!modules.get(i).isEmpty())
			{
				CompoundTag stackTag = new CompoundTag();

				stackTag.putByte("ModuleSlot", (byte)i);
				modules.get(i).save(stackTag);
				list.add(stackTag);
			}
		}

		tag.put("Modules", list);
		return tag;
	}
}