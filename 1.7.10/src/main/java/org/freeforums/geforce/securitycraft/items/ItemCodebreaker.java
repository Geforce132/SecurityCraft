package org.freeforums.geforce.securitycraft.items;

import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import org.freeforums.geforce.securitycraft.interfaces.IHelpInfo;

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
	
	public String getHelpInfo() {
		return "The codebreaker will crack any keypad, password-protected chest, or password-protected furnace's code by right-clicking on it.";
	}

	public String[] getRecipe() {
		return new String[]{"The codebreaker requires: 2 diamonds, 2 gold ingots, 2 redstone, 1 nether star, 1 emerald, 1 redstone torch", "UVU", "WXW", "YZY", "U = diamond, V = redstone torch, W = gold ingot, X = nether star, Y = redstone, Z = emerald"};
	}
	
}
