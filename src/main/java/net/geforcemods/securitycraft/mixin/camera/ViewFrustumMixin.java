package net.geforcemods.securitycraft.mixin.camera;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.geforcemods.securitycraft.entity.camera.SecurityCamera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ViewFrustum;
import net.minecraft.entity.Entity;

/**
 * This mixin fixes camera chunks disappearing when the player entity moves while viewing a camera (e.g. while being in a
 * minecart or falling).
 */
@Mixin(value = ViewFrustum.class, priority = 1100)
public class ViewFrustumMixin {
	@Inject(method = "repositionCamera", at = @At("HEAD"), cancellable = true)
	public void securitycraft$preventCameraRepositioning(double x, double z, CallbackInfo ci) {
		Entity camera = Minecraft.getInstance().cameraEntity;

		if (camera instanceof SecurityCamera && (x != camera.getX() || z != camera.getZ()))
			ci.cancel();
	}
}
