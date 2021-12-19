package net.geforcemods.securitycraft.itemblocks;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockReinforcedStoneBrick extends ItemBlock {
	public ItemBlockReinforcedStoneBrick(Block block) {
		super(block);
		setHasSubtypes(true);
	}

	@Override
	public int getMetadata(int meta) {
		return meta;
	}

	@Override
	public String getTranslationKey(ItemStack stack) {
		if (stack.getItemDamage() == 0)
			return getTranslationKey() + "_default";
		else if (stack.getItemDamage() == 1)
			return getTranslationKey() + "_mossy";
		else if (stack.getItemDamage() == 2)
			return getTranslationKey() + "_cracked";
		else if (stack.getItemDamage() == 3)
			return getTranslationKey() + "_chiseled";
		else
			return getTranslationKey();
	}
}
