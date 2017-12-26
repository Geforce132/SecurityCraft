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

	@Override
	public String getUnlocalizedName(ItemStack stack)
	{
		if(stack.getMetadata() == 0)
			return this.getUnlocalizedName() + "_default";
		else if(stack.getMetadata() == 1)
			return this.getUnlocalizedName() + "_mossy";
		else if(stack.getMetadata() == 2)
			return this.getUnlocalizedName() + "_cracked";
		else if(stack.getMetadata() == 3)
			return this.getUnlocalizedName() + "_chiseled";
		else
			return this.getUnlocalizedName();
	}
}
