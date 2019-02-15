package net.geforcemods.securitycraft.items;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemCodebreaker extends Item {

	public ItemCodebreaker() {
		super();
		maxStackSize = 1;
		setMaxDamage(4); //5 uses because when the damage is 0 the item has one more use
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean hasEffect(ItemStack stack){
		return true;
	}

	/**
	 * Return an item rarity from EnumRarity
	 */
	@Override
	@OnlyIn(Dist.CLIENT)
	public EnumRarity getRarity(ItemStack stack){
		return EnumRarity.RARE;
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment)
	{
		return false;
	}

	@Override
	public boolean isBookEnchantable(ItemStack stack, ItemStack book)
	{
		return false;
	}

	@Override
	public boolean isEnchantable(ItemStack stack)
	{
		return false;
	}
}
