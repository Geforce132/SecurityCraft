package net.geforcemods.securitycraft.mixin.camera;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.geforcemods.securitycraft.entity.camera.CameraViewAreaExtension;
import net.minecraft.client.renderer.ViewArea;

/**
 * Marks chunks within the camera view area as dirty when e.g. a block has been changed in them, so the frame feed updates
 * appropriately
 */
@Mixin(ViewArea.class)
public class ViewAreaMixin {
	@Inject(method = "setDirty", at = @At("HEAD"))
	private void onSetChunkDirty(int cx, int cy, int cz, boolean reRenderOnMainThread, CallbackInfo callbackInfo) {
		CameraViewAreaExtension.setDirty(cx, cy, cz, reRenderOnMainThread);
	}
}
