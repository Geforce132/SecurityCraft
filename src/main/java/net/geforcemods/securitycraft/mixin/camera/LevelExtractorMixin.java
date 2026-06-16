package net.geforcemods.securitycraft.mixin.camera;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.geforcemods.securitycraft.compat.ium.IumCompat;
import net.geforcemods.securitycraft.entity.camera.CameraViewAreaExtension;
import net.geforcemods.securitycraft.entity.camera.FrameFeedHandler;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.extract.LevelExtractor;

@Mixin(value = LevelExtractor.class, priority = 1100)
public abstract class LevelExtractorMixin {
	@Shadow
	@Final
	private LevelRenderer levelRenderer;

	/**
	 * When rendering the world in a frame, the necessary visible sections are captured manually within SecurityCraft. Vanilla
	 * usually does the same process in extract, so that part of the method is skipped when a frame feed is rendered. However,
	 * when Embeddium or Sodium is installed, these mods may perform their visible section capture themselves since it's much
	 * more performant, and since that happens in extract too, the method workflow is not modified in that case.
	 */
	//TODO Test Ium mods
	@WrapOperation(method = "extract", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;getCapturedFrustum()Lnet/minecraft/client/renderer/culling/Frustum;"))
	private Frustum securitycraft$preventVisibleSectionUpdating(Camera instance, Operation<Frustum> original) {
		if (FrameFeedHandler.isCapturingCamera() && !IumCompat.isActive())
			return FrameFeedHandler.DUMMY_FRUSTUM;

		return original.call(instance);
	}

	/**
	 * Updates the camera view area with the refreshed section render dispatcher when F3+A is pressed
	 */
	@Inject(method = "extract", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;invalidateCompiledGeometry(Lnet/minecraft/client/multiplayer/ClientLevel;Lnet/minecraft/client/Options;Lnet/minecraft/client/Camera;Lnet/minecraft/client/color/block/BlockColors;)V", shift = Shift.AFTER))
	private void securitycraft$onInvalidateGeometry(DeltaTracker deltaTracker, Camera camera, float deltaPartialTick, CallbackInfo ci) {
		CameraViewAreaExtension.allChanged(levelRenderer.sectionRenderDispatcher(), Minecraft.getInstance().gameRenderer.mainCamera().entity().level());
	}
}

