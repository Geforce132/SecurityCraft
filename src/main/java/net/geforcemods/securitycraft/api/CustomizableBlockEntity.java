package net.geforcemods.securitycraft.api;

import java.util.EnumMap;
import java.util.Map;

import net.geforcemods.securitycraft.misc.ModuleType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Extend this class in your TileEntity to make it customizable. You will be able to modify it with the various modules in
 * SecurityCraft, and have your block do different functions based on what modules are inserted.
 *
 * @author Geforce
 */
public abstract class CustomizableBlockEntity extends NamedBlockEntity implements IModuleInventory, ICustomizable {
	private NonNullList<ItemStack> modules = NonNullList.<ItemStack>withSize(getMaxNumberOfModules(), ItemStack.EMPTY);
	private Map<ModuleType, Boolean> moduleStates = new EnumMap<>(ModuleType.class);

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
	public NonNullList<ItemStack> getInventory() {
		return modules;
	}

	@Override
	public boolean isModuleEnabled(ModuleType module) {
		return hasModule(module) && moduleStates.get(module) == Boolean.TRUE; //prevent NPE
	}

	@Override
	public void toggleModuleState(ModuleType module, boolean shouldBeEnabled) {
		moduleStates.put(module, shouldBeEnabled);

		if (shouldBeEnabled)
			onModuleInserted(getModule(module), module, true);
		else
			onModuleRemoved(getModule(module), module, true);
	}

	@Override
	public World myLevel() {
		return world;
	}

	@Override
	public BlockPos myPos() {
		return pos;
	}
}
