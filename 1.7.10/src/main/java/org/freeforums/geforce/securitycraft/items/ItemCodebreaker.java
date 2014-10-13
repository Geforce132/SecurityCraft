package org.freeforums.geforce.securitycraft.items;

import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemCodebreaker extends Item{

	public ItemCodebreaker() {	
		super();
		this.maxStackSize = 1;
	}
	
	@SideOnly(Side.CLIENT)
 	public boolean hasEffect(ItemStack par1ItemStack)
    {
        return true;
    }
	
	@SideOnly(Side.CLIENT)

    /**
     * Return an item rarity from EnumRarity
     */
    public EnumRarity getRarity(ItemStack par1ItemStack)
    {
        return EnumRarity.rare;
    }
	
	@SideOnly(Side.CLIENT)
	public boolean isFull3D(){
		return true;
	}

}
