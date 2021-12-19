package net.geforcemods.securitycraft.itemblocks;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockReinforcedStainedBlock extends ItemBlock {
	public ItemBlockReinforcedStainedBlock(Block block) {
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
			return getTranslationKey() + "_white";
		else if (stack.getItemDamage() == 1)
			return getTranslationKey() + "_orange";
		else if (stack.getItemDamage() == 2)
			return getTranslationKey() + "_magenta";
		else if (stack.getItemDamage() == 3)
			return getTranslationKey() + "_light_blue";
		else if (stack.getItemDamage() == 4)
			return getTranslationKey() + "_yellow";
		else if (stack.getItemDamage() == 5)
			return getTranslationKey() + "_lime";
		else if (stack.getItemDamage() == 6)
			return getTranslationKey() + "_pink";
		else if (stack.getItemDamage() == 7)
			return getTranslationKey() + "_gray";
		else if (stack.getItemDamage() == 8)
			return getTranslationKey() + "_silver";
		else if (stack.getItemDamage() == 9)
			return getTranslationKey() + "_cyan";
		else if (stack.getItemDamage() == 10)
			return getTranslationKey() + "_purple";
		else if (stack.getItemDamage() == 11)
			return getTranslationKey() + "_blue";
		else if (stack.getItemDamage() == 12)
			return getTranslationKey() + "_brown";
		else if (stack.getItemDamage() == 13)
			return getTranslationKey() + "_green";
		else if (stack.getItemDamage() == 14)
			return getTranslationKey() + "_red";
		else if (stack.getItemDamage() == 15)
			return getTranslationKey() + "_black";
		else
			return getTranslationKey();
	}
}
