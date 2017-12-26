package net.geforcemods.securitycraft.itemblocks;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlockWithMetadata;
import net.minecraft.item.ItemStack;

public class ItemBlockReinforcedCompressedBlocks extends ItemBlockWithMetadata
{
	public ItemBlockReinforcedCompressedBlocks(Block block)
	{
		super(block, block);
	}

	@Override
	public String getUnlocalizedName(ItemStack stack)
	{
		String name = getUnlocalizedName();

		switch(stack.getMetadata())
		{
			case 0: return name + "_lapis";
			case 1: return name + "_coal";
			default: return name;
		}
	}
}
