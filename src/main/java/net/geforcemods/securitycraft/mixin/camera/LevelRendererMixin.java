package net.geforcemods.securitycraft.mixin.camera;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;

import net.geforcemods.securitycraft.SecurityCraftClient;
import net.geforcemods.securitycraft.compat.ium.IumCompat;
import net.geforcemods.securitycraft.entity.camera.CameraController;
import net.geforcemods.securitycraft.entity.camera.CameraViewAreaExtension;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.ViewArea;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;

@Mixin(value = LevelRenderer.class, priority = 1100)
public class LevelRendererMixin {
	@Shadow
	@Final
	private Minecraft minecraft;
	@Shadow
	private SectionRenderDispatcher sectionRenderDispatcher;
	@Shadow
	private ClientLevel level;

	/**
	 * Fixes camera chunks disappearing when the player entity moves while viewing a camera (e.g. while being in a minecart or
	 * falling)
	 */
	@WrapWithCondition(method = "setupRender", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ViewArea;repositionCamera(DD)V"))
	public boolean securitycraft$shouldRepositionCamera(ViewArea viewArea, double x, double z) {
		return !PlayerUtils.isPlayerMountedOnCamera(minecraft.player);
	}

	/**
	 * When rendering the world in a frame, we capture the necessary visible sections ourselves, which vanilla usually does in
	 * setupRender, so we no-op in that method when a frame feed is rendered. When Embeddium is installed, we let that mod
	 * capture the visible sections for us, so we need to run setupRender (which Embeddium hooks into).
	 */
	@Inject(method = "setupRender", at = @At(value = "HEAD"), cancellable = true)
	public void securitycraft$onSetupRender(Camera camera, Frustum frustum, boolean hasCapturedFrustum, boolean isSpectator, CallbackInfo callbackInfo) {
		if (CameraController.currentlyCapturedCamera != null && SecurityCraftClient.INSTALLED_IUM_MOD == IumCompat.NONE)
			callbackInfo.cancel();
	}

	/**
	 * Updates the camera view area with the refreshed section render dispatcher when F3+A is pressed
	 */
	@Inject(method = "allChanged", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SectionOcclusionGraph;waitAndReset(Lnet/minecraft/client/renderer/ViewArea;)V"))
	private void securitycraft$onAllChanged(CallbackInfo callbackInfo) {
		CameraViewAreaExtension.allChanged(sectionRenderDispatcher, level);
	}

	/**
	 * Marks a render section as compiled when contained within the camera view area, to allow rendering entities in these chunks
	 */
	@Inject(method = "isSectionCompiled", at = @At("HEAD"), cancellable = true)
	private void securitycraft$onIsSectionCompiled(BlockPos pos, CallbackInfoReturnable<Boolean> callbackInfo) {
		if (CameraController.currentlyCapturedCamera != null) {
			SectionPos sectionPos = SectionPos.of(pos);
			SectionRenderDispatcher.RenderSection renderSection = CameraViewAreaExtension.rawFetch(sectionPos.x(), sectionPos.y(), sectionPos.z(), false);

			if (renderSection != null && renderSection.compiled.get() != SectionRenderDispatcher.CompiledSection.UNCOMPILED)
				callbackInfo.setReturnValue(true);
		}
	}
}
