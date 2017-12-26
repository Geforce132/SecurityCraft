package net.geforcemods.securitycraft.itemblocks;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlockWithMetadata;
import net.minecraft.item.ItemStack;

public class ItemBlockReinforcedColoredBlock extends ItemBlockWithMetadata {

	public ItemBlockReinforcedColoredBlock(Block block) {
		super(block, block);
	}

	@Override
	public String getUnlocalizedName(ItemStack stack){
		if(stack.getMetadata() == 0)
			return this.getUnlocalizedName() + "_white";
		else if(stack.getMetadata() == 1)
			return this.getUnlocalizedName() + "_orange";
		else if(stack.getMetadata() == 2)
			return this.getUnlocalizedName() + "_magenta";
		else if(stack.getMetadata() == 3)
			return this.getUnlocalizedName() + "_light_blue";
		else if(stack.getMetadata() == 4)
			return this.getUnlocalizedName() + "_yellow";
		else if(stack.getMetadata() == 5)
			return this.getUnlocalizedName() + "_lime";
		else if(stack.getMetadata() == 6)
			return this.getUnlocalizedName() + "_pink";
		else if(stack.getMetadata() == 7)
			return this.getUnlocalizedName() + "_gray";
		else if(stack.getMetadata() == 8)
			return this.getUnlocalizedName() + "_silver";
		else if(stack.getMetadata() == 9)
			return this.getUnlocalizedName() + "_cyan";
		else if(stack.getMetadata() == 10)
			return this.getUnlocalizedName() + "_purple";
		else if(stack.getMetadata() == 11)
			return this.getUnlocalizedName() + "_blue";
		else if(stack.getMetadata() == 12)
			return this.getUnlocalizedName() + "_brown";
		else if(stack.getMetadata() == 13)
			return this.getUnlocalizedName() + "_green";
		else if(stack.getMetadata() == 14)
			return this.getUnlocalizedName() + "_red";
		else if(stack.getMetadata() == 15)
			return this.getUnlocalizedName() + "_black";
		else
			return this.getUnlocalizedName();
	}

}
