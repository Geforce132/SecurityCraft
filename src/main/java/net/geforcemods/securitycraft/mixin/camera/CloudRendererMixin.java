package net.geforcemods.securitycraft.mixin.camera;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.geforcemods.securitycraft.entity.camera.FrameFeedHandler;
import net.minecraft.client.CloudStatus;
import net.minecraft.client.renderer.CloudRenderer;
import net.minecraft.world.phys.Vec3;

/**
 * Completely disables cloud rendering within frame feeds, to prevent crashes and other instabilities arising from the cloud
 * buffer being used and rotated too many times when multiple frame feeds are captured.
 */
@Mixin(CloudRenderer.class)
public class CloudRendererMixin {
	@Inject(method = "render", at = @At("HEAD"), cancellable = true)
	private void securitycraft$disableCloudRendering(int color, CloudStatus cloudStatus, float bottomY, int range, Vec3 cameraPosition, long gameTime, float partialTicks, CallbackInfo ci) {
		if (FrameFeedHandler.isCapturingCamera())
			ci.cancel();
	}
}
