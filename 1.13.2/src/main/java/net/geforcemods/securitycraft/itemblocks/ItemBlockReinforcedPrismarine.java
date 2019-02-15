package net.geforcemods.securitycraft.itemblocks;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockReinforcedPrismarine extends ItemBlock
{
	public ItemBlockReinforcedPrismarine(Block block)
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

		switch(stack.getDamage())
		{
			case 0: return name + "_default";
			case 1: return name + "_bricks";
			case 2: return name + "_dark";
			default: return name;
		}
	}
}