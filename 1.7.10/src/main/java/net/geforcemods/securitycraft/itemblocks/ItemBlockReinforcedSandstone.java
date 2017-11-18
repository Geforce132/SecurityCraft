package net.geforcemods.securitycraft.itemblocks;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlockWithMetadata;
import net.minecraft.item.ItemStack;

public class ItemBlockReinforcedSandstone extends ItemBlockWithMetadata {

	public ItemBlockReinforcedSandstone(Block par1) {
		super(par1, par1);
	}

	@Override
	public String getUnlocalizedName(ItemStack stack){
		if(stack.getItemDamage() == 0)
			return this.getUnlocalizedName() + "_normal";
		else if(stack.getItemDamage() == 1)
			return this.getUnlocalizedName() + "_carved";
		else if(stack.getItemDamage() == 2)
			return this.getUnlocalizedName() + "_smooth";
		else
			return this.getUnlocalizedName();
	}

}
