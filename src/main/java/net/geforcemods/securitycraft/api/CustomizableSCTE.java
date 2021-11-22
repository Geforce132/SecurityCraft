package net.geforcemods.securitycraft.api;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

/**
 * Extend this class in your TileEntity to make it customizable. You will
 * be able to modify it with the various modules in SecurityCraft, and
 * have your block do different functions based on what modules are
 * inserted.
 *
 * @author Geforce
 */
public abstract class CustomizableSCTE extends TileEntityNamed implements IModuleInventory, ICustomizable {
	private NonNullList<ItemStack> modules = NonNullList.<ItemStack>withSize(getMaxNumberOfModules(), ItemStack.EMPTY);

	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);
		modules = readModuleInventory(tag);
		readOptions(tag);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag)
	{
		super.writeToNBT(tag);
		writeModuleInventory(tag);
		writeOptions(tag);
		return tag;
	}

	@Override
	public ITextComponent getDefaultName() {
		return new TextComponentString("Customize");
	}

	@Override
	public NonNullList<ItemStack> getInventory()
	{
		return modules;
	}
}
