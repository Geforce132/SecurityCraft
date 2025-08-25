package net.geforcemods.securitycraft.items;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;

public class IncognitoMaskItem extends ArmorItem {
	private static final IArmorMaterial INCOGNITO_MASK_ARMOR_MATERIAL = new IncognitoMaskArmorMaterial();

	public IncognitoMaskItem(Properties properties) {
		super(INCOGNITO_MASK_ARMOR_MATERIAL, EquipmentSlotType.HEAD, properties);
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
		return false;
	}

	private static class IncognitoMaskArmorMaterial implements IArmorMaterial {
		@Override
		public int getDurabilityForSlot(EquipmentSlotType type) {
			return 0;
		}

		@Override
		public int getDefenseForSlot(EquipmentSlotType type) {
			return 0;
		}

		@Override
		public int getEnchantmentValue() {
			return 0;
		}

		@Override
		public SoundEvent getEquipSound() {
			return SoundEvents.ARMOR_EQUIP_GENERIC;
		}

		@Override
		public Ingredient getRepairIngredient() {
			return Ingredient.EMPTY;
		}

		@Override
		public String getName() {
			return "securitycraft:incognito_mask";
		}

		@Override
		public float getToughness() {
			return 0;
		}

		@Override
		public float getKnockbackResistance() {
			return 0;
		}
	}
}
