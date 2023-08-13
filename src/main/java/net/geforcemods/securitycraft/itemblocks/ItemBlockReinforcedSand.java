package net.geforcemods.securitycraft.itemblocks;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockReinforcedSand extends ItemBlock {
	public ItemBlockReinforcedSand(Block block) {
		super(block);

		setHasSubtypes(true);
	}

	@Override
	public int getMetadata(int meta) {
		return meta;
	}

	@Override
	public String getTranslationKey(ItemStack stack) {
		String name = getTranslationKey();

		if (stack.getItemDamage() == 1)
			return name.replace("Sand", "RedSand");
		else
			return name;
	}
}