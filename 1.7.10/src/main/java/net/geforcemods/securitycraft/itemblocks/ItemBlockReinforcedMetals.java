package net.geforcemods.securitycraft.itemblocks;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlockWithMetadata;
import net.minecraft.item.ItemStack;

public class ItemBlockReinforcedMetals extends ItemBlockWithMetadata
{
	public ItemBlockReinforcedMetals(Block block)
	{
		super(block, block);
	}
	
	public String getUnlocalizedName(ItemStack stack)
	{
		String name = getUnlocalizedName();
		
		switch(stack.getItemDamage())
		{
			case 0: return name + "_gold";
			case 1: return name + "_iron";
			case 2: return name + "_diamond";
			case 3: return name + "_emerald";
			default: return name;
		}
	}
}
