package net.geforcemods.securitycraft.items;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockReinforcedStainedHardenedClay extends ItemBlock {

	public ItemBlockReinforcedStainedHardenedClay(Block block) {
		super(block);
		this.setHasSubtypes(true);
	}
	
	public int getMetadata(int meta){
		return meta;
	}
	
	public String getUnlocalizedName(ItemStack stack){
		if(stack.getItemDamage() == 0){
			return this.getUnlocalizedName() + "_white";
		}else if(stack.getItemDamage() == 1){
			return this.getUnlocalizedName() + "_orange";
		}else if(stack.getItemDamage() == 2){
			return this.getUnlocalizedName() + "_magenta";
		}else if(stack.getItemDamage() == 3){
			return this.getUnlocalizedName() + "_light_blue";
		}else if(stack.getItemDamage() == 4){
			return this.getUnlocalizedName() + "_yellow";
		}else if(stack.getItemDamage() == 5){
			return this.getUnlocalizedName() + "_lime";
		}else if(stack.getItemDamage() == 6){
			return this.getUnlocalizedName() + "_pink";
		}else if(stack.getItemDamage() == 7){
			return this.getUnlocalizedName() + "_gray";
		}else if(stack.getItemDamage() == 8){
			return this.getUnlocalizedName() + "_silver";
		}else if(stack.getItemDamage() == 9){
			return this.getUnlocalizedName() + "_cyan";
		}else if(stack.getItemDamage() == 10){
			return this.getUnlocalizedName() + "_purple";
		}else if(stack.getItemDamage() == 11){
			return this.getUnlocalizedName() + "_blue";
		}else if(stack.getItemDamage() == 12){
			return this.getUnlocalizedName() + "_brown";
		}else if(stack.getItemDamage() == 13){
			return this.getUnlocalizedName() + "_green";
		}else if(stack.getItemDamage() == 14){
			return this.getUnlocalizedName() + "_red";
		}else if(stack.getItemDamage() == 15){
			return this.getUnlocalizedName() + "_black";
		}else{
			return this.getUnlocalizedName();
		}
	}

	@Override
	public int getColorFromItemStack(ItemStack stack, int renderPass)
	{
		return 0x999999;
	}
}
