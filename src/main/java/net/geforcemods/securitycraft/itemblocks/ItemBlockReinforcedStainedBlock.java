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
		if(stack.getItemDamage() == 0)
			return this.getTranslationKey() + "_white";
		else if(stack.getItemDamage() == 1)
			return this.getTranslationKey() + "_orange";
		else if(stack.getItemDamage() == 2)
			return this.getTranslationKey() + "_magenta";
		else if(stack.getItemDamage() == 3)
			return this.getTranslationKey() + "_light_blue";
		else if(stack.getItemDamage() == 4)
			return this.getTranslationKey() + "_yellow";
		else if(stack.getItemDamage() == 5)
			return this.getTranslationKey() + "_lime";
		else if(stack.getItemDamage() == 6)
			return this.getTranslationKey() + "_pink";
		else if(stack.getItemDamage() == 7)
			return this.getTranslationKey() + "_gray";
		else if(stack.getItemDamage() == 8)
			return this.getTranslationKey() + "_silver";
		else if(stack.getItemDamage() == 9)
			return this.getTranslationKey() + "_cyan";
		else if(stack.getItemDamage() == 10)
			return this.getTranslationKey() + "_purple";
		else if(stack.getItemDamage() == 11)
			return this.getTranslationKey() + "_blue";
		else if(stack.getItemDamage() == 12)
			return this.getTranslationKey() + "_brown";
		else if(stack.getItemDamage() == 13)
			return this.getTranslationKey() + "_green";
		else if(stack.getItemDamage() == 14)
			return this.getTranslationKey() + "_red";
		else if(stack.getItemDamage() == 15)
			return this.getTranslationKey() + "_black";
		else
			return this.getTranslationKey();
	}

}
