package org.freeforums.geforce.securitycraft.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemCodebreaker extends Item {

	public ItemCodebreaker() {	
		super();
		this.maxStackSize = 1;
	}
	
	@SideOnly(Side.CLIENT)
 	public boolean hasEffect(ItemStack par1ItemStack){
        return true;
    }	

    /**
     * Return an item rarity from EnumRarity
     */
	@SideOnly(Side.CLIENT)
    public EnumRarity getRarity(ItemStack par1ItemStack){
        return EnumRarity.rare;
    }

}
