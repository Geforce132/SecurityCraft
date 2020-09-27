package net.geforcemods.securitycraft.compat.inventory;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.VanillaDoubleChestItemHandler;

public class InsertOnlyDoubleChestHandler extends VanillaDoubleChestItemHandler
{
	public static final InsertOnlyDoubleChestHandler NO_ADJACENT_CHESTS = new InsertOnlyDoubleChestHandler(null, null, false);

	public InsertOnlyDoubleChestHandler(TileEntityChest mainChest, TileEntityChest other, boolean mainChestIsUpper)
	{
		super(mainChest, other, mainChestIsUpper);
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate)
	{
		return ItemStack.EMPTY;
	}

	@Nullable
	public static InsertOnlyDoubleChestHandler get(TileEntityChest chest) //copied from super class for changing the type
	{
		World world = chest.getWorld();
		BlockPos pos = chest.getPos();
		if (world == null || pos == null || !world.isBlockLoaded(pos))
			return null; // Still loading

		Block blockType = chest.getBlockType();

		EnumFacing[] horizontals = EnumFacing.HORIZONTALS;
		for (int i = horizontals.length - 1; i >= 0; i--)   // Use reverse order so we can return early
		{
			EnumFacing enumfacing = horizontals[i];
			BlockPos blockpos = pos.offset(enumfacing);
			Block block = world.getBlockState(blockpos).getBlock();

			if (block == blockType)
			{
				TileEntity otherTE = world.getTileEntity(blockpos);

				if (otherTE instanceof TileEntityChest)
				{
					TileEntityChest otherChest = (TileEntityChest) otherTE;
					return new InsertOnlyDoubleChestHandler(chest, otherChest,
							enumfacing != EnumFacing.WEST && enumfacing != EnumFacing.NORTH);

				}
			}
		}
		return NO_ADJACENT_CHESTS; //All alone
	}

	@Override
	public boolean needsRefresh()
	{
		if (this == NO_ADJACENT_CHESTS)
			return false;
		TileEntityChest tileEntityChest = get();
		return tileEntityChest == null || tileEntityChest.isInvalid();
	}
}

