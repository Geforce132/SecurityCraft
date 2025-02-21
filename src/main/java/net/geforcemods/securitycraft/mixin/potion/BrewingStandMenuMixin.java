package net.geforcemods.securitycraft.mixin.potion;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.world.inventory.BrewingStandMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;

/**
 * SecurityCraft's recipes for fake water/lava require potions in the ingredient slot. This causes any potion to be
 * shift-clickable into the ingredient slot, which is not the vanilla behavior. This mixin prevents any potion from being
 * shift-clicked into the ingredient slot, making them always end up in the potion slots
 */
@Mixin(value = BrewingStandMenu.class, priority = 1100)
public class BrewingStandMenuMixin {
	@Redirect(method = "quickMoveStack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/Slot;mayPlace(Lnet/minecraft/world/item/ItemStack;)Z"))
	private boolean securitycraft$preventPotionShiftClickToTopSlot(Slot ingredientSlot, ItemStack stack) {
		return !(stack.getItem() instanceof PotionItem) && ingredientSlot.mayPlace(stack);
	}
}