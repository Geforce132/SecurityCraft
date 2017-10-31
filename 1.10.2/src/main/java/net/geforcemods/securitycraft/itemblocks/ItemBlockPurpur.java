package net.geforcemods.securitycraft.itemblocks;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockPurpur extends ItemBlock
{
	public ItemBlockPurpur(Block block)
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
			case 0: return name + "_default";
			case 1: case 2: case 3: return name + "_pillar";
			default: return name;
		}
	}
}
