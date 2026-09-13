package net.geforcemods.securitycraft.mixin.taser;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.geforcemods.securitycraft.items.TaserItem;
import net.minecraft.client.player.FirstPersonHandsAndItems;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.state.level.FirstPersonHandsAndItemsRenderState.HandRenderSelection;

/**
 * Makes sure that when a taser is held, only the hand that is holding the taser is rendered - prioritizing the main hand
 */
@Mixin(FirstPersonHandsAndItems.class)
public class FirstPersonHandsAndItemsMixin {
	@Inject(method = "evaluateWhichHandsToRender", at = @At("HEAD"), cancellable = true)
	private static void securitycraft$onlyRenderMainHandWhenHoldingTaser(LocalPlayer player, CallbackInfoReturnable<HandRenderSelection> cir) {
		if (player.getMainHandItem().getItem() instanceof TaserItem)
			cir.setReturnValue(HandRenderSelection.RENDER_MAIN_HAND_ONLY);
		else if (player.getOffhandItem().getItem() instanceof TaserItem)
			cir.setReturnValue(HandRenderSelection.RENDER_OFF_HAND_ONLY);
	}
}
