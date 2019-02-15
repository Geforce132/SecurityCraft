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
	public int getMetadata(int meta){
		return meta;
	}

	@Override
	public String getTranslationKey(ItemStack stack){
		if(stack.getDamage() == 0)
			return this.getTranslationKey() + "_white";
		else if(stack.getDamage() == 1)
			return this.getTranslationKey() + "_orange";
		else if(stack.getDamage() == 2)
			return this.getTranslationKey() + "_magenta";
		else if(stack.getDamage() == 3)
			return this.getTranslationKey() + "_light_blue";
		else if(stack.getDamage() == 4)
			return this.getTranslationKey() + "_yellow";
		else if(stack.getDamage() == 5)
			return this.getTranslationKey() + "_lime";
		else if(stack.getDamage() == 6)
			return this.getTranslationKey() + "_pink";
		else if(stack.getDamage() == 7)
			return this.getTranslationKey() + "_gray";
		else if(stack.getDamage() == 8)
			return this.getTranslationKey() + "_silver";
		else if(stack.getDamage() == 9)
			return this.getTranslationKey() + "_cyan";
		else if(stack.getDamage() == 10)
			return this.getTranslationKey() + "_purple";
		else if(stack.getDamage() == 11)
			return this.getTranslationKey() + "_blue";
		else if(stack.getDamage() == 12)
			return this.getTranslationKey() + "_brown";
		else if(stack.getDamage() == 13)
			return this.getTranslationKey() + "_green";
		else if(stack.getDamage() == 14)
			return this.getTranslationKey() + "_red";
		else if(stack.getDamage() == 15)
			return this.getTranslationKey() + "_black";
		else
			return this.getTranslationKey();
	}

}
