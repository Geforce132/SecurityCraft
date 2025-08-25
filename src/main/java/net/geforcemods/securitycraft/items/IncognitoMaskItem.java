package net.geforcemods.securitycraft.items;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;

public class IncognitoMaskItem extends ArmorItem {
	private static final ArmorMaterial INCOGNITO_MASK_ARMOR_MATERIAL = new IncognitoMaskArmorMaterial();

	public IncognitoMaskItem(Properties properties) {
		super(INCOGNITO_MASK_ARMOR_MATERIAL, ArmorItem.Type.HELMET, properties);
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
		return false;
	}

	private static class IncognitoMaskArmorMaterial implements ArmorMaterial {
		@Override
		public int getDurabilityForType(ArmorItem.Type type) {
			return 0;
		}

		@Override
		public int getDefenseForType(ArmorItem.Type type) {
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
