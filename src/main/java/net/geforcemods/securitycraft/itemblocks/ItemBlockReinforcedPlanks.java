package net.geforcemods.securitycraft.itemblocks;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlockWithMetadata;
import net.minecraft.item.ItemStack;

public class ItemBlockReinforcedPlanks extends ItemBlockWithMetadata{

	public ItemBlockReinforcedPlanks(Block block) {
		super(block, block);
	}

	@Override
	public String getUnlocalizedName(ItemStack stack){
		if(stack.getMetadata() == 0)
			return this.getUnlocalizedName() + "_oak";
		else if(stack.getMetadata() == 1)
			return this.getUnlocalizedName() + "_spruce";
		else if(stack.getMetadata() == 2)
			return this.getUnlocalizedName() + "_birch";
		else if(stack.getMetadata() == 3)
			return this.getUnlocalizedName() + "_jungle";
		else if(stack.getMetadata() == 4)
			return this.getUnlocalizedName() + "_acacia";
		else if(stack.getMetadata() == 5)
			return this.getUnlocalizedName() + "_darkoak";
		else
			return this.getUnlocalizedName();
	}

}
