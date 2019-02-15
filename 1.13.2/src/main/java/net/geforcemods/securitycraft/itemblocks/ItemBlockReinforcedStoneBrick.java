package net.geforcemods.securitycraft.itemblocks;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockReinforcedStoneBrick extends ItemBlock
{
	public ItemBlockReinforcedStoneBrick(Block block)
	{
		super(block);
		setHasSubtypes(true);
	}

	@Override
	public int getMetadata(int meta){
		return meta;
	}

	@Override
	public String getTranslationKey(ItemStack stack)
	{
		if(stack.getItemDamage() == 0)
			return this.getTranslationKey() + "_default";
		else if(stack.getItemDamage() == 1)
			return this.getTranslationKey() + "_mossy";
		else if(stack.getItemDamage() == 2)
			return this.getTranslationKey() + "_cracked";
		else if(stack.getItemDamage() == 3)
			return this.getTranslationKey() + "_chiseled";
		else
			return this.getTranslationKey();
	}
}
