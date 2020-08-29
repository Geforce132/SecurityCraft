package net.geforcemods.securitycraft.util;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

public interface IBlockWithNoDrops
{
	public default ItemStack getUniversalBlockRemoverDrop()
	{
		return new ItemStack((Block)this);
	}
}
