package net.geforcemods.securitycraft.mixin.potion;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.inventory.ContainerBrewingStand;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;

/**
 * SecurityCraft's recipes for fake water/lava require potions in the ingredient slot. This causes any potion to be
 * shift-clicked into the ingredient slot, which is not the vanilla behavior. This mixin prevents any potion from being
 * shift-clicked into the ingredient slot, making them always end up in the potion slots
 */
@Mixin(ContainerBrewingStand.class)
public class MixinContainerBrewingStand {
	@Redirect(method = "transferStackInSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/inventory/Slot;isItemValid(Lnet/minecraft/item/ItemStack;)Z"))
	private boolean preventPotionShiftClickToTopSlot(Slot ingredientSlot, ItemStack stack) {
		return !(stack.getItem() instanceof ItemPotion) && ingredientSlot.isItemValid(stack);
	}
}