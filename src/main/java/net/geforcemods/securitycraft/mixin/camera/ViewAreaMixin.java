package net.geforcemods.securitycraft.mixin.camera;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.geforcemods.securitycraft.entity.camera.CameraViewAreaExtension;
import net.geforcemods.securitycraft.entity.camera.SecurityCamera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ViewArea;

@Mixin(value = ViewArea.class, priority = 1100)
public class ViewAreaMixin {
	/**
	 * Marks chunks within the frame camera view area as dirty when e.g. a block has been changed in them, so the frame feed
	 * updates appropriately
	 */
	@Inject(method = "setDirty", at = @At("HEAD"))
	private void securitycraft$onSetChunkDirty(int cx, int cy, int cz, boolean reRenderOnMainThread, CallbackInfo callbackInfo) {
		CameraViewAreaExtension.setDirty(cx, cy, cz, reRenderOnMainThread);
	}

	/**
	 * Fixes camera chunks disappearing when the player entity moves while viewing a camera (e.g. while being in a minecart or
	 * falling).
	 */
	@Inject(method = "repositionCamera", at = @At("HEAD"), cancellable = true)
	private void securitycraft$preventCameraRepositioning(double x, double z, CallbackInfo ci) {
		if (Minecraft.getInstance().cameraEntity instanceof SecurityCamera camera && (x != camera.getX() || z != camera.getZ()))
			ci.cancel();
	}
}
