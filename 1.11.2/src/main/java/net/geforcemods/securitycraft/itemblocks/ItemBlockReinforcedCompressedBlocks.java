package net.geforcemods.securitycraft.itemblocks;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockReinforcedCompressedBlocks extends ItemBlock
{
	public ItemBlockReinforcedCompressedBlocks(Block block)
	{
		super(block);
		
		this.setHasSubtypes(true);
	}
	
	public int getMetadata(int meta)
	{
		return meta;
	}
	
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
