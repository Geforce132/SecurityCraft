package net.geforcemods.securitycraft.itemblocks;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

public class ItemBlockReinforcedPrismarine extends ItemBlockTinted
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
	public String getUnlocalizedName(ItemStack stack)
	{
		String name = getUnlocalizedName();

		switch(stack.getItemDamage())
		{
			case 0: return name + "_default";
			case 1: return name + "_bricks";
			case 2: return name + "_dark";
			default: return name;
		}
	}
}