package net.geforcemods.securitycraft.api;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Extend this class in your TileEntity to make it customizable. You will
 * be able to modify it with the various modules in SecurityCraft, and
 * have your block do different functions based on what modules are
 * inserted.
 *
 * @author Geforce
 */
public abstract class CustomizableBlockEntity extends NamedBlockEntity implements IModuleInventory, ICustomizable
{
	private NonNullList<ItemStack> modules = NonNullList.<ItemStack>withSize(getMaxNumberOfModules(), ItemStack.EMPTY);

	public CustomizableBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
	{
		super(type, pos, state);
	}

	@Override
	public void load(CompoundTag tag)
	{
		super.load(tag);
		modules = readModuleInventory(tag);
		readOptions(tag);
	}

	@Override
	public CompoundTag save(CompoundTag tag)
	{
		super.save(tag);
		writeModuleInventory(tag);
		writeOptions(tag);
		return tag;
	}

	@Override
	public NonNullList<ItemStack> getInventory()
	{
		return modules;
	}
}
