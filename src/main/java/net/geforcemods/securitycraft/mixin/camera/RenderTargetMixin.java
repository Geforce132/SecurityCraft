package net.geforcemods.securitycraft.mixin.camera;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.pipeline.RenderTarget;

import net.geforcemods.securitycraft.entity.camera.CameraController;
import net.minecraft.client.Minecraft;

/**
 * Prevents the game from binding its main render target while a frame camera is being rendered, to force everything captured
 * by that frame camera to be displayed in the frame instead of overlaying the regular world
 */
@Mixin(RenderTarget.class)
public class RenderTargetMixin {
	@SuppressWarnings("unlikely-arg-type")
	@Inject(method = "bindWrite", at = @At("HEAD"), cancellable = true)
	private void securitycraft$cancelUnwantedBinding(boolean setViewport, CallbackInfo ci) {
		if (CameraController.currentlyCapturedCamera != null && equals(Minecraft.getInstance().getMainRenderTarget()))
			ci.cancel();
	}
}
