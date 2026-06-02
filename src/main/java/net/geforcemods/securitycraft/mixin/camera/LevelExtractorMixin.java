package net.geforcemods.securitycraft.mixin.camera;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.geforcemods.securitycraft.compat.ium.IumCompat;
import net.geforcemods.securitycraft.entity.camera.FrameFeedHandler;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.extract.LevelExtractor;

@Mixin(value = LevelExtractor.class, priority = 1100)
public abstract class LevelExtractorMixin {
	/**
	 * When rendering the world in a frame, the necessary visible sections are captured manually within SecurityCraft. Vanilla
	 * usually does the same process in setupRender, so that method is exited early when a frame feed is rendered. However, when
	 * Embeddium or Sodium is installed, these mods may perform their visible section capture themselves since it's much more
	 * performant, and since that happens in setupRender too, the method is not exited early in this case.
	 */
	//TODO: Needed in LevelRendererMixin#securitycraft$onAllChanged as well? Part of the method from 26.1 got split into where
	// this mixin injects, and the other part still exists in LevelRenderer.
	@Inject(method = "extract", at = @At("HEAD"), cancellable = true)
	private void securitycraft$onSetupRender(DeltaTracker deltaTracker, Camera camera, float deltaPartialTick, CallbackInfo ci) {
		if (FrameFeedHandler.isCapturingCamera() && !IumCompat.isActive())
			ci.cancel();
	}
}
