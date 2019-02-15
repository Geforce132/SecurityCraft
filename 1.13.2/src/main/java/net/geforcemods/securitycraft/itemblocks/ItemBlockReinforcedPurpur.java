package net.geforcemods.securitycraft.itemblocks;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockReinforcedPurpur extends ItemBlock
{
	public ItemBlockReinforcedPurpur(Block block)
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

		switch(stack.getDamage())
		{
			case 0: return name + "_default";
			case 1: case 2: case 3: return name + "_pillar";
			default: return name;
		}
	}
}
