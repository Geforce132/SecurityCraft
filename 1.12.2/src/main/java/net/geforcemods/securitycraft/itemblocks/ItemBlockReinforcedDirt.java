package net.geforcemods.securitycraft.itemblocks;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockReinforcedDirt extends ItemBlock
{
	public ItemBlockReinforcedDirt(Block block)
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
			case 0: return name;
			case 1: return name.replace("Dirt", "_coarse_dirt");
			case 2: return name.replace("Dirt", "_podzol");
			default: return name;
		}
	}
}