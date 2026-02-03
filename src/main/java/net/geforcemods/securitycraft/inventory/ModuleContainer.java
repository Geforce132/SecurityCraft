package net.geforcemods.securitycraft.inventory;

import net.geforcemods.securitycraft.api.ILinkedAction;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.LinkableBlockEntity;
import net.geforcemods.securitycraft.items.ModuleItem;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

public class ModuleContainer extends SimpleContainer {
	private final IModuleInventory moduleInventory;

	public ModuleContainer(IModuleInventory moduleInventory) {
		super(moduleInventory.getMaxNumberOfModules());
		this.items = moduleInventory.getInventory();
		this.moduleInventory = moduleInventory;
	}

	@Override
	public ItemStack removeItem(int index, int count) {
		ItemStack removed = super.removeItem(index, count);

		if (!removed.isEmpty() && removed.getItem() instanceof ModuleItem module) {
			moduleInventory.onModuleRemoved(removed, module.getModuleType(), false);

			if (moduleInventory instanceof LinkableBlockEntity be)
				be.propagate(new ILinkedAction.ModuleRemoved(module.getModuleType(), false), be);
		}

		return removed;
	}

	@Override
	public ItemStack removeItemNoUpdate(int index) {
		ItemStack removed = super.removeItemNoUpdate(index);

		if (!removed.isEmpty() && removed.getItem() instanceof ModuleItem module) {
			moduleInventory.onModuleRemoved(removed, module.getModuleType(), false);

			if (moduleInventory instanceof LinkableBlockEntity be)
				be.propagate(new ILinkedAction.ModuleRemoved(module.getModuleType(), false), be);
		}

		return removed;
	}

	@Override
	public void setItem(int index, ItemStack stack) {
		ItemStack previous = getItem(index);

		super.setItem(index, stack);

		//Prevent module from being removed and re-added when the slot initializes
		if (ItemStack.matches(previous, stack))
			return;

		//call the correct methods, should there have been a module in the slot previously
		if (!previous.isEmpty() && previous.getItem() instanceof ModuleItem module) {
			moduleInventory.onModuleRemoved(previous, module.getModuleType(), false);

			if (moduleInventory instanceof LinkableBlockEntity be)
				be.propagate(new ILinkedAction.ModuleRemoved(module.getModuleType(), false), be);
		}

		if (stack.getItem() instanceof ModuleItem module) {
			moduleInventory.onModuleInserted(stack, module.getModuleType(), false);

			if (moduleInventory instanceof LinkableBlockEntity be)
				be.propagate(new ILinkedAction.ModuleInserted(stack, module, false), be);
		}
	}
}
