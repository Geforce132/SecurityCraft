package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.SecurityCraft;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.EnumHelper;

public class IncognitoMaskItem extends ItemArmor {
	private static final ItemArmor.ArmorMaterial INCOGNITO_MASK_ARMOR_MATERIAL = EnumHelper.addArmorMaterial("incognito_mask", "securitycraft:incognito_mask", 0, new int[] {0, 0, 0, 0}, 0, SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, 0.0F);

	public IncognitoMaskItem() {
		super(INCOGNITO_MASK_ARMOR_MATERIAL, 0, EntityEquipmentSlot.HEAD);
		setCreativeTab(SecurityCraft.TECHNICAL_TAB);
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
		return false;
	}
}
