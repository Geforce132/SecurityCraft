package net.geforcemods.securitycraft.containers;

import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;

public abstract class ContainerStateSelectorAccess extends Container {
	public abstract ItemStack getStateStack();

	public abstract IBlockState getSavedState();

	public void onStateChange(IBlockState state) {}
}
