package net.geforcemods.securitycraft.itemblocks;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockReinforcedCompressedBlocks extends ItemBlock
{
	public ItemBlockReinforcedCompressedBlocks(Block block)
	{
		super(block);

		setHasSubtypes(true);
	}

	@Override
	public int getMetadata(int meta)
	{
		return meta;
	}

	@Override
	public String getTranslationKey(ItemStack stack)
	{
		String name = getTranslationKey();

		switch(stack.getItemDamage())
		{
			case 0: return name + "_lapis";
			case 1: return name + "_coal";
			default: return name;
		}
	}
}
