package net.geforcemods.securitycraft.mixin.camera;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.geforcemods.securitycraft.entity.camera.FrameFeedHandler;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;

@Mixin(value = GameRenderer.class, priority = 1100)
public class GameRendererMixin {
	/**
	 * Makes sure distortion effects are not rendered in camera feeds
	 */
	@WrapOperation(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;lerp(FFF)F"))
	private float securitycraft$disableFeedDistortion(float delta, float start, float end, Operation<Float> original) {
		if (FrameFeedHandler.isCapturingCamera())
			return 0.0F;
		else
			return original.call(delta, start, end);
	}

	/**
	 * Provides a hook for capturing the necessary levels for frame feeds. This is done immediately after main level
	 * rendering, but before GUI rendering, to fix screen flickering with Iris.
	 */
	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GameRenderer;tryTakeScreenshotIfNeeded()V"))
	private void securitycraft$afterLevelRendering(DeltaTracker deltaTracker, boolean renderLevel, CallbackInfo ci) {
		FrameFeedHandler.captureFrameFeeds(deltaTracker);
	}
}
