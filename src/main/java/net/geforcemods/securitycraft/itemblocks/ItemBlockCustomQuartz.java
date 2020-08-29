package net.geforcemods.securitycraft.itemblocks;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockCustomQuartz extends ItemBlock
{
	public ItemBlockCustomQuartz(Block block)
	{
		super(block);

		setHasSubtypes(true);
	}

	@Override
	public int getMetadata(int damage)
	{
		return damage;
	}

	@Override
	public String getTranslationKey(ItemStack stack)
	{
		String name = getTranslationKey();

		switch(stack.getItemDamage())
		{
			case 0: return name + "_default";
			case 1: return name + "_chiseled";
			case 2: return name + "_pillar";
			default: return name;
		}
	}
}