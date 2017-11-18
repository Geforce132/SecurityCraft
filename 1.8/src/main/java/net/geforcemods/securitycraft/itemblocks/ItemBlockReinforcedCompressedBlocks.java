package net.geforcemods.securitycraft.itemblocks;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

public class ItemBlockReinforcedCompressedBlocks extends ItemBlockTinted
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
	public String getUnlocalizedName(ItemStack stack)
	{
		String name = getUnlocalizedName();

		switch(stack.getItemDamage())
		{
			case 0: return name + "_lapis";
			case 1: return name + "_coal";
			default: return name;
		}
	}
}
