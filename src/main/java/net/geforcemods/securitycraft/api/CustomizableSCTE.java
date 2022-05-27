package net.geforcemods.securitycraft.api;

import java.util.EnumMap;

import net.geforcemods.securitycraft.misc.EnumModuleType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

/**
 * Extend this class in your TileEntity to make it customizable. You will be able to modify it with the various modules in
 * SecurityCraft, and have your block do different functions based on what modules are inserted.
 *
 * @author Geforce
 */
public abstract class CustomizableSCTE extends TileEntityNamed implements IModuleInventory, ICustomizable {
	private NonNullList<ItemStack> modules = NonNullList.<ItemStack> withSize(getMaxNumberOfModules(), ItemStack.EMPTY);
	private EnumMap<EnumModuleType, Boolean> moduleStates = new EnumMap<>(EnumModuleType.class);

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		modules = readModuleInventory(tag);
		moduleStates = readModuleStates(tag);
		readOptions(tag);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		writeModuleInventory(tag);
		writeModuleStates(tag);
		writeOptions(tag);
		return tag;
	}

	@Override
	public ITextComponent getDefaultName() {
		return new TextComponentString("Customize");
	}

	@Override
	public NonNullList<ItemStack> getInventory() {
		return modules;
	}

	@Override
	public boolean isModuleEnabled(EnumModuleType module) {
		return hasModule(module) && moduleStates.get(module) == Boolean.TRUE; //prevent NPE
	}

	@Override
	public void toggleModuleState(EnumModuleType module, boolean shouldBeEnabled) {
		moduleStates.put(module, shouldBeEnabled);

		if (shouldBeEnabled)
			onModuleInserted(getModule(module), module, true);
		else
			onModuleRemoved(getModule(module), module, true);
	}
}
