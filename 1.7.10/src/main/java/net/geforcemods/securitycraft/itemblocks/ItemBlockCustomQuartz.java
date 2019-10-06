package net.geforcemods.securitycraft.itemblocks;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlockWithMetadata;
import net.minecraft.item.ItemStack;

public class ItemBlockCustomQuartz extends ItemBlockWithMetadata
{
	public ItemBlockCustomQuartz(Block block)
	{
		super(block, block);
	}

	@Override
	public String getUnlocalizedName(ItemStack stack)
	{
		String name = getUnlocalizedName();

		switch(stack.getMetadata())
		{
			case 0: return name + "_default";
			case 1: return name + "_chiseled";
			case 2: return name + "_pillar";
			default: return name;
		}
	}
}
