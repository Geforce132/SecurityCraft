package net.geforcemods.securitycraft.itemblocks;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockTinted extends ItemBlock
{
	public ItemBlockTinted(Block block)
	{
		super(block);
	}

	@Override
	public int getColorFromItemStack(ItemStack stack, int renderPass)
	{
		return 0x999999;
	}
}
