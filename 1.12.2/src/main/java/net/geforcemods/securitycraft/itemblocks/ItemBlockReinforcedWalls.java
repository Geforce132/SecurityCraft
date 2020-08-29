package net.geforcemods.securitycraft.itemblocks;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockReinforcedWalls extends ItemBlock
{
	public ItemBlockReinforcedWalls(Block block)
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
		return getTranslationKey() + (stack.getItemDamage() == 0 ? "_cobble" : "_mossy");
	}
}