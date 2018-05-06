package net.geforcemods.securitycraft.itemblocks;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlockWithMetadata;
import net.minecraft.item.ItemStack;

public class ItemBlockReinforcedSandstone extends ItemBlockWithMetadata {

	public ItemBlockReinforcedSandstone(Block block) {
		super(block, block);
	}

	@Override
	public String getUnlocalizedName(ItemStack stack){
		if(stack.getMetadata() == 0)
			return this.getUnlocalizedName() + "_normal";
		else if(stack.getMetadata() == 1)
			return this.getUnlocalizedName() + "_carved";
		else if(stack.getMetadata() == 2)
			return this.getUnlocalizedName() + "_smooth";
		else
			return this.getUnlocalizedName();
	}

}
