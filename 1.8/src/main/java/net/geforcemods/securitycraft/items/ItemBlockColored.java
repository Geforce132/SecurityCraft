package net.geforcemods.securitycraft.items;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockColored extends ItemBlock
{
	public ItemBlockColored(Block block)
	{
		super(block);
	}
	
	@Override
	public int getColorFromItemStack(ItemStack stack, int renderPass)
	{
		return 0x999999;
	}
}
