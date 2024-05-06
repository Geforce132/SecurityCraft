package net.geforcemods.securitycraft.mixin.datafix;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.serialization.Dynamic;

import net.geforcemods.securitycraft.components.DataFixHandler;
import net.minecraft.util.datafix.fixes.ItemStackComponentizationFix;

/**
 * Makes sure SecurityCraft's items get inserted items converted, as well as their NBT data converted to components properly
 */
@Mixin(ItemStackComponentizationFix.class)
public class ItemStackComponentizationFixMixin {
	@Inject(method = "fixItemStack", at = @At("TAIL"))
	private static void securitycraft$fixItemStacks(ItemStackComponentizationFix.ItemStackData itemStackData, Dynamic<?> dynamic, CallbackInfo ci) {
		DataFixHandler.fix(itemStackData, dynamic);
	}
}
