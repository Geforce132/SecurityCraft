package org.freeforums.geforce.securitycraft.items;

import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import org.freeforums.geforce.securitycraft.api.IHelpInfo;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemCodebreaker extends Item implements IHelpInfo {

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
	
	public String[] getRecipe() {
		return new String[]{"The codebreaker requires: 2 diamonds, 2 gold ingots, 2 redstone, 1 nether star, 1 emerald, 1 redstone torch", "UVU", "WXW", "YZY", "U = diamond, V = redstone torch, W = gold ingot, X = nether star, Y = redstone, Z = emerald"};
	}
	
}
