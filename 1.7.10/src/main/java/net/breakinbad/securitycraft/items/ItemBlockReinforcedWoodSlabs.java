package net.breakinbad.securitycraft.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.breakinbad.securitycraft.main.mod_SecurityCraft;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlockWithMetadata;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class ItemBlockReinforcedWoodSlabs extends ItemBlockWithMetadata {

	public ItemBlockReinforcedWoodSlabs(Block par1) {
		super(par1, par1);
	}
	
	/**
     * Gets an icon index based on an item's damage value
     */
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int par1){
        return mod_SecurityCraft.reinforcedWoodSlabs.getIcon(2, par1);
    }
	
	public String getUnlocalizedName(ItemStack stack){
		if(stack.getItemDamage() == 0){
			return this.getUnlocalizedName() + "_oak";
		}else if(stack.getItemDamage() == 1){
			return this.getUnlocalizedName() + "_spruce";
		}else if(stack.getItemDamage() == 2){
			return this.getUnlocalizedName() + "_birch";
		}if(stack.getItemDamage() == 3){
			return this.getUnlocalizedName() + "_jungle";
		}else if(stack.getItemDamage() == 4){
			return this.getUnlocalizedName() + "_acacia";
		}else if(stack.getItemDamage() == 5){
			return this.getUnlocalizedName() + "_darkoak";
		}else{
			return this.getUnlocalizedName();
		}
	}

}
