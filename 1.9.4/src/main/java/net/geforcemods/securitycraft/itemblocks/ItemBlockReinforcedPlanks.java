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
	public String getUnlocalizedName(ItemStack stack){
		if(stack.getItemDamage() == 0)
			return this.getUnlocalizedName() + "_oak";
		else if(stack.getItemDamage() == 1)
			return this.getUnlocalizedName() + "_spruce";
		else if(stack.getItemDamage() == 2)
			return this.getUnlocalizedName() + "_birch";
		else if(stack.getItemDamage() == 3)
			return this.getUnlocalizedName() + "_jungle";
		else if(stack.getItemDamage() == 4)
			return this.getUnlocalizedName() + "_acacia";
		else if(stack.getItemDamage() == 5)
			return this.getUnlocalizedName() + "_darkoak";
		else
			return this.getUnlocalizedName();
	}

}
