package net.geforcemods.securitycraft.mixin.camera;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.renderpearl.api.commands.RenderPass;
import com.mojang.renderpearl.api.textures.GpuTextureView;

import net.geforcemods.securitycraft.entity.camera.FrameFeedHandler;
import net.minecraft.client.CloudStatus;
import net.minecraft.client.renderer.CloudRenderer;
import net.minecraft.client.renderer.oit.OitRenderPassProvider.Parameters;
import net.minecraft.client.renderer.oit.OitStage;

/**
 * Completely disables cloud rendering within frame feeds, to prevent crashes and other instabilities arising from the cloud
 * buffer being used and rotated too many times when multiple frame feeds are captured.
 */
@Mixin(CloudRenderer.class)
public class CloudRendererMixin {
	@Inject(method = "render(Lnet/minecraft/client/CloudStatus;Lcom/mojang/renderpearl/api/commands/RenderPass;)V", at = @At("HEAD"), cancellable = true)
	private void securitycraft$disableCloudRendering(CloudStatus cloudStatus, RenderPass renderPass, CallbackInfo ci) {
		if (FrameFeedHandler.isCapturingCamera())
			ci.cancel();
	}

	@Inject(method = "renderOit", at = @At("HEAD"), cancellable = true)
	private void securitycraft$disableCloudRendering(CloudStatus cloudStatus, OitStage stage, GpuTextureView mainDepthTextureView, Parameters params, CallbackInfo ci) {
		if (FrameFeedHandler.isCapturingCamera())
			ci.cancel();
	}
}
