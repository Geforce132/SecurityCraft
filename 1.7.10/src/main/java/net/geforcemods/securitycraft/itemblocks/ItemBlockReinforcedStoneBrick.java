package net.geforcemods.securitycraft.itemblocks;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlockWithMetadata;
import net.minecraft.item.ItemStack;

public class ItemBlockReinforcedStoneBrick extends ItemBlockWithMetadata
{
	public ItemBlockReinforcedStoneBrick(Block par1)
	{
		super(par1, par1);
	}

	public String getUnlocalizedName(ItemStack stack)
	{
		if(stack.getItemDamage() == 0)
			return this.getUnlocalizedName() + "_default";
		else if(stack.getItemDamage() == 1)
			return this.getUnlocalizedName() + "_mossy";
		else if(stack.getItemDamage() == 2)
			return this.getUnlocalizedName() + "_cracked";
		else if(stack.getItemDamage() == 3)
			return this.getUnlocalizedName() + "_chiseled";
		else
			return this.getUnlocalizedName();
	}
}
