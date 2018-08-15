package net.geforcemods.securitycraft.itemblocks;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockReinforcedStone extends ItemBlock
{
	public ItemBlockReinforcedStone(Block block)
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
			case 1: return name + "_granite";
			case 2: return name + "_smooth_granite";
			case 3: return name + "_diorite";
			case 4: return name + "_smooth_diorite";
			case 5: return name + "_andesite";
			case 6: return name + "_smooth_andesite";
			default: return name;
		}
	}
}