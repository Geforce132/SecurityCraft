package net.geforcemods.securitycraft.itemblocks;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

public class ItemBlockReinforcedSandstone extends ItemBlockTinted {

	public ItemBlockReinforcedSandstone(Block block) {
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
			return this.getUnlocalizedName() + "_normal";
		else if(stack.getItemDamage() == 1)
			return this.getUnlocalizedName() + "_chiseled";
		else if(stack.getItemDamage() == 2)
			return this.getUnlocalizedName() + "_smooth";
		else
			return this.getUnlocalizedName();
	}

}
