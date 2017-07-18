package net.geforcemods.securitycraft.items;

import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemCodebreaker extends Item {

	public ItemCodebreaker() {	
		super();
		this.maxStackSize = 1;
		setMaxDamage(4); //5 uses because when the damage is 0 the item has one more use
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
        return EnumRarity.RARE;
    }

	@Override
	public boolean isBookEnchantable(ItemStack stack, ItemStack book)
	{
		return false;
	}
}
