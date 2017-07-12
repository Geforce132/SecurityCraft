package net.geforcemods.securitycraft.items;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

public class ItemBlockReinforcedSandstone extends ItemBlockTinted {

	public ItemBlockReinforcedSandstone(Block block) {
		super(block);
		this.setHasSubtypes(true);
	}
	
	public int getMetadata(int meta){
		return meta;
	}
	
	public String getUnlocalizedName(ItemStack stack){
		if(stack.getItemDamage() == 0){
			return this.getUnlocalizedName() + "_normal";
		}else if(stack.getItemDamage() == 1){
			return this.getUnlocalizedName() + "_chiseled";
		}else if(stack.getItemDamage() == 2){
			return this.getUnlocalizedName() + "_smooth";
		}else{
			return this.getUnlocalizedName();
		}
	}

}
