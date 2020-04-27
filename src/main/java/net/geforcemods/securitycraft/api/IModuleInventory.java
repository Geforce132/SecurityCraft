package net.geforcemods.securitycraft.api;

import java.util.ArrayList;

import net.geforcemods.securitycraft.blocks.SecurityCameraBlock;
import net.geforcemods.securitycraft.items.ModuleItem;
import net.geforcemods.securitycraft.misc.CustomModules;
import net.geforcemods.securitycraft.tileentity.SecurityCameraTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.util.Constants;

/**
 * Let your TileEntity implement this to be able to add modules to it
 * @author bl4ckscor3
 */
public interface IModuleInventory extends IInventory
{
	/**
	 * @return The list that holds the contents of this inventory
	 */
	public NonNullList<ItemStack> getInventory();

	/**
	 * @return An array of what {@link CustomModules} can be inserted into this inventory
	 */
	public CustomModules[] acceptedModules();

	/**
	 * @return The TileEntity this inventory is for
	 */
	public TileEntity getTileEntity();

	/**
	 * Called whenever a module is inserted into a slot in the "Customize" GUI.
	 *
	 * @param stack The raw ItemStack being inserted.
	 * @param module The CustomModules variant of stack.
	 */
	public default void onModuleInserted(ItemStack stack, CustomModules module) {}

	/**
	 * Called whenever a module is removed from a slot in the "Customize" GUI.
	 *
	 * @param stack The raw ItemStack being removed.
	 * @param module The CustomModules variant of stack.
	 */
	public default void onModuleRemoved(ItemStack stack, CustomModules module) {}

	@Override
	public default void clear()
	{
		getInventory().clear();
	}

	@Override
	public default int getSizeInventory()
	{
		return acceptedModules().length;
	}

	@Override
	public default boolean isEmpty()
	{
		for(ItemStack stack : getInventory())
		{
			if(!stack.isEmpty())
				return false;
		}

		return true;
	}

	@Override
	public default ItemStack getStackInSlot(int index)
	{
		return getInventory().get(index);
	}

	@Override
	public default ItemStack decrStackSize(int index, int count)
	{
		NonNullList<ItemStack> modules = getInventory();

		if(!modules.get(index).isEmpty())
		{
			ItemStack stack;

			if(modules.get(index).getCount() <= count)
			{
				stack = modules.get(index);
				modules.set(index, ItemStack.EMPTY);
				onModuleRemoved(stack, ((ModuleItem) stack.getItem()).getModule());

				TileEntity te = getTileEntity();

				if(te instanceof CustomizableTileEntity)
					((CustomizableTileEntity)te).createLinkedBlockAction(LinkedAction.MODULE_REMOVED, new Object[]{ stack, ((ModuleItem) stack.getItem()).getModule() }, (CustomizableTileEntity)te);

				if(te instanceof SecurityCameraTileEntity)
					te.getWorld().notifyNeighborsOfStateChange(te.getPos().offset(te.getWorld().getBlockState(te.getPos()).get(SecurityCameraBlock.FACING), -1), te.getWorld().getBlockState(te.getPos()).getBlock());

				return stack;
			}
			else
			{
				stack = modules.get(index).split(count);

				if(modules.get(index).getCount() == 0)
					modules.set(index, ItemStack.EMPTY);

				onModuleRemoved(stack, ((ModuleItem) stack.getItem()).getModule());

				TileEntity te = getTileEntity();

				if(te instanceof CustomizableTileEntity)
					((CustomizableTileEntity)te).createLinkedBlockAction(LinkedAction.MODULE_REMOVED, new Object[]{ stack, ((ModuleItem) stack.getItem()).getModule() }, (CustomizableTileEntity)te);

				if(te instanceof SecurityCameraTileEntity)
					te.getWorld().notifyNeighborsOfStateChange(te.getPos().offset(te.getWorld().getBlockState(te.getPos()).get(SecurityCameraBlock.FACING), -1), te.getWorld().getBlockState(te.getPos()).getBlock());

				return stack;
			}
		}
		else
			return ItemStack.EMPTY;
	}

	@Override
	public default ItemStack removeStackFromSlot(int index)
	{
		NonNullList<ItemStack> modules = getInventory();

		if(!modules.get(index).isEmpty())
		{
			ItemStack stack = modules.get(index);
			modules.set(index, ItemStack.EMPTY);
			return stack;
		}
		else return ItemStack.EMPTY;
	}

	@Override
	public default void setInventorySlotContents(int index, ItemStack stack)
	{
		NonNullList<ItemStack> modules = getInventory();

		modules.set(index, stack);

		if(!stack.isEmpty() && stack.getCount() > getInventoryStackLimit())
			stack = new ItemStack(stack.getItem(), getInventoryStackLimit());

		if(!stack.isEmpty())
		{
			onModuleInserted(stack, ((ModuleItem) stack.getItem()).getModule());

			TileEntity te = getTileEntity();

			if(te instanceof CustomizableTileEntity)
				((CustomizableTileEntity)te).createLinkedBlockAction(LinkedAction.MODULE_INSERTED, new Object[]{ stack, ((ModuleItem) stack.getItem()).getModule() }, (CustomizableTileEntity)te);

			if(getTileEntity() instanceof SecurityCameraTileEntity)
				te.getWorld().notifyNeighborsOfStateChange(te.getPos().offset(te.getWorld().getBlockState(te.getPos()).get(SecurityCameraBlock.FACING), -1), te.getWorld().getBlockState(te.getPos()).getBlock());
		}
	}

	@Override
	public default void markDirty()
	{
		getTileEntity().markDirty();
	}

	@Override
	public default boolean isUsableByPlayer(PlayerEntity player)
	{
		return true;
	}

	@Override
	public default int getInventoryStackLimit()
	{
		return 1;
	}

	@Override
	public default boolean isItemValidForSlot(int index, ItemStack stack)
	{
		return stack.getItem() instanceof ModuleItem;
	}

	/**
	 * @return A list of all {@link CustomModules} that can be inserted into this inventory
	 */
	public default ArrayList<CustomModules> getAcceptedModules()
	{
		ArrayList<CustomModules> list = new ArrayList<>();

		for(CustomModules module : acceptedModules())
			list.add(module);

		return list;
	}

	/**
	 * @return A List of all CustomModules currently inserted in the TileEntity.
	 */
	public default ArrayList<CustomModules> getInsertedModules()
	{
		ArrayList<CustomModules> modules = new ArrayList<>();

		for(ItemStack stack : getInventory())
		{
			if(!stack.isEmpty() && stack.getItem() instanceof ModuleItem)
				modules.add(((ModuleItem) stack.getItem()).getModule());
		}

		return modules;
	}

	/**
	 * @param The module type of the stack to get
	 * @return The ItemStack for the given CustomModules type.
	 * If there is no ItemStack for that type, returns ItemStack.EMPTY.
	 */
	public default ItemStack getModule(CustomModules module)
	{
		NonNullList<ItemStack> modules = getInventory();

		for(int i = 0; i < modules.size(); i++)
		{
			if(!modules.get(i).isEmpty() && modules.get(i).getItem() instanceof ModuleItem && ((ModuleItem) modules.get(i).getItem()).getModule() == module)
				return modules.get(i);
		}

		return ItemStack.EMPTY;
	}

	/**
	 * Inserts a generic copy of the given module type into the customization inventory.
	 * @param module The module type to insert
	 */
	public default void insertModule(CustomModules module)
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
	public default void removeModule(CustomModules module)
	{
		NonNullList<ItemStack> modules = getInventory();

		for(int i = 0; i < modules.size(); i++)
		{
			if(!modules.get(i).isEmpty() && modules.get(i).getItem() instanceof ModuleItem && ((ModuleItem) modules.get(i).getItem()).getModule() == module)
				modules.set(i, ItemStack.EMPTY);
		}
	}

	/**
	 * @param module The type to check if it is present in this inventory
	 * @return true if the given module type is present in this inventory, false otherwise
	 */
	public default boolean hasModule(CustomModules module)
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
				if(!modules.get(i).isEmpty() && modules.get(i).getItem() instanceof ModuleItem && ((ModuleItem) modules.get(i).getItem()).getModule() == module)
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
	public default NonNullList<ItemStack> readModuleInventory(CompoundNBT tag)
	{
		ListNBT list = tag.getList("Modules", Constants.NBT.TAG_LIST);
		NonNullList<ItemStack> modules = NonNullList.withSize(getSizeInventory(), ItemStack.EMPTY);

		for(int i = 0; i < list.size(); ++i)
		{
			CompoundNBT stackTag = list.getCompound(i);
			byte slot = stackTag.getByte("ModuleSlot");

			if(slot >= 0 && slot < modules.size())
				modules.set(slot, ItemStack.read(stackTag));
		}

		return modules;
	}

	/**
	 * Call this from your write method. Used for write the module inventory to a tag. Use in conjunction with readModuleInventory.
	 * @param tag The tag to write the inventory to
	 * @return The modified CompoundNBT
	 */
	public default CompoundNBT writeModuleInventory(CompoundNBT tag)
	{
		ListNBT list = new ListNBT();
		NonNullList<ItemStack> modules = getInventory();

		for(int i = 0; i < modules.size(); i++)
		{
			if(!modules.get(i).isEmpty())
			{
				CompoundNBT stackTag = new CompoundNBT();

				stackTag.putByte("ModuleSlot", (byte)i);
				modules.get(i).write(stackTag);
				list.add(stackTag);
			}
		}

		tag.put("Modules", list);
		return tag;
	}
}