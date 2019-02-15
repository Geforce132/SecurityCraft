package net.geforcemods.securitycraft.itemblocks;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockReinforcedPlanks extends ItemBlock {

	public ItemBlockReinforcedPlanks(Block block) {
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
			return this.getTranslationKey() + "_oak";
		else if(stack.getDamage() == 1)
			return this.getTranslationKey() + "_spruce";
		else if(stack.getDamage() == 2)
			return this.getTranslationKey() + "_birch";
		else if(stack.getDamage() == 3)
			return this.getTranslationKey() + "_jungle";
		else if(stack.getDamage() == 4)
			return this.getTranslationKey() + "_acacia";
		else if(stack.getDamage() == 5)
			return this.getTranslationKey() + "_darkoak";
		else
			return this.getTranslationKey();
	}

}
