package net.geforcemods.securitycraft.api;

import java.util.EnumMap;

import net.geforcemods.securitycraft.misc.ModuleType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Extend this class in your TileEntity to make it customizable. You will be able to modify it with the various modules in
 * SecurityCraft, and have your block do different functions based on what modules are inserted.
 *
 * @author Geforce
 */
public abstract class CustomizableBlockEntity extends NamedBlockEntity implements IModuleInventory, ICustomizable {
	private NonNullList<ItemStack> modules = NonNullList.<ItemStack> withSize(getMaxNumberOfModules(), ItemStack.EMPTY);
	private EnumMap<ModuleType, Boolean> moduleStates = new EnumMap<>(ModuleType.class);

	public CustomizableBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		modules = loadModuleInventory(tag);
		moduleStates = loadModuleStates(tag);
		loadOptions(tag);
	}

	@Override
	public void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		saveModuleInventory(tag);
		saveModuleStates(tag);
		saveOptions(tag);
	}

	@Override
	public NonNullList<ItemStack> getInventory() {
		return modules;
	}

	@Override
	public boolean isModuleEnabled(ModuleType module) {
		return hasModule(module) && moduleStates.get(module);
	}

	@Override
	public void enableModule(ModuleType module) {
		moduleStates.put(module, hasModule(module)); //only enable if the module is present
		setChanged();
		level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
	}

	@Override
	public void disableModule(ModuleType module) {
		moduleStates.put(module, false);
		setChanged();
		level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
	}
}
