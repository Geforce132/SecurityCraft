package net.geforcemods.securitycraft.itemblocks;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockReinforcedSandstone extends ItemBlock {

	public ItemBlockReinforcedSandstone(Block block) {
		super(block);
		setHasSubtypes(true);
	}

	@Override
	public int getMetadata(int meta){
		return meta;
	}

	@Override
	public String getTranslationKey(ItemStack stack){
		if(stack.getDamage() == 0)
			return this.getTranslationKey() + "_normal";
		else if(stack.getDamage() == 1)
			return this.getTranslationKey() + "_chiseled";
		else if(stack.getDamage() == 2)
			return this.getTranslationKey() + "_smooth";
		else
			return this.getTranslationKey();
	}

}
