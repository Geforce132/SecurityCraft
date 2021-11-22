package net.geforcemods.securitycraft.api;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.NonNullList;

/**
 * Extend this class in your TileEntity to make it customizable. You will
 * be able to modify it with the various modules in SecurityCraft, and
 * have your block do different functions based on what modules are
 * inserted.
 *
 * @author Geforce
 */
public abstract class CustomizableTileEntity extends NamedTileEntity implements IModuleInventory, ICustomizable
{
	private NonNullList<ItemStack> modules = NonNullList.<ItemStack>withSize(getMaxNumberOfModules(), ItemStack.EMPTY);

	public CustomizableTileEntity(TileEntityType<?> type)
	{
		super(type);
	}

	@Override
	public void read(BlockState state, CompoundNBT tag)
	{
		super.read(state, tag);
		modules = readModuleInventory(tag);
		readOptions(tag);
	}

	@Override
	public CompoundNBT write(CompoundNBT tag)
	{
		super.write(tag);
		writeModuleInventory(tag);
		writeOptions(tag);
		return tag;
	}

	@Override
	public TileEntity getTileEntity()
	{
		return this;
	}

	@Override
	public NonNullList<ItemStack> getInventory()
	{
		return modules;
	}
}
