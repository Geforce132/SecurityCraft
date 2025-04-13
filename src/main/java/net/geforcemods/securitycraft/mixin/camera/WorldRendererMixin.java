package net.geforcemods.securitycraft.mixin.camera;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.entity.camera.CameraController;
import net.geforcemods.securitycraft.entity.camera.CameraViewAreaExtension;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.vector.Matrix4f;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
	@Shadow
	private ChunkRenderDispatcher chunkRenderDispatcher;
	@Shadow
	private ClientWorld level;

	/**
	 * When rendering the world in a frame, the necessary visible sections are captured manually within SecurityCraft. Vanilla
	 * usually does the same process in setupRender, so that method is exited early when a frame feed is rendered. However, when
	 * Embeddium or Sodium is installed, these mods may perform their visible section capture themselves since it's much more
	 * performant, and since that happens in setupRender too, the method is not exited early in this case.
	 */
	@Inject(method = "setupRender", at = @At("HEAD"), cancellable = true)
	public void securitycraft$onSetupRender(ActiveRenderInfo camera, ClippingHelper frustum, boolean hasCapturedFrustum, int frameCount, boolean isSpectator, CallbackInfo ci) {
		if (CameraController.isCapturingCamera() && !SecurityCraft.isASodiumModInstalled)
			ci.cancel();
	}

	/**
	 * Updates the camera view area with the refreshed section render dispatcher when F3+A is pressed
	 */
	@Inject(method = "allChanged", at = @At("TAIL"))
	private void securitycraft$onAllChanged(CallbackInfo ci) {
		CameraViewAreaExtension.allChanged(chunkRenderDispatcher);
	}

	/**
	 * Captures the last used rendering and projection matrices used by rendering any level. This happens regardless of if any
	 * frame is active, though the memory implications from this should be minimal.
	 */
	@Inject(method = "renderLevel", at = @At("HEAD"))
	private void securitycraft$captureMainLevelRenderMatrix(MatrixStack renderMatrix, float partialTick, long nanos, boolean renderBlockOutline, ActiveRenderInfo renderInfo, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projectionMatrix, CallbackInfo ci) {
		CameraController.setLastUsedRenderMatrix(new Matrix4f(renderMatrix.last().pose()));
		CameraController.setLastUsedProjectionMatrix(projectionMatrix);
	}

	/**
	 * Sets the correct fog distance for rendering a frame feed, depending on clientside view distance configuration settings.
	 * Note that the frame block entity chunk loading distance option is not respected for this, since it is only supposed to
	 * affect the server by setting a limit on forceloaded chunks and unfit to be handled on the client side.
	 */
	@ModifyVariable(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/FogRenderer;setupColor(Lnet/minecraft/client/renderer/ActiveRenderInfo;FLnet/minecraft/client/world/ClientWorld;IF)V"), ordinal = 1)
	private float securitycraft$modifyFogRenderDistance(float original) {
		if (CameraController.isCapturingCamera())
			return CameraController.getFrameFeedViewDistance(null) * 16;

		return original;
	}

	/**
	 * Prevents entity outlines from rendering in a frame feed, which fixes Minecraft unbinding the feed's frame buffer when
	 * setting up the appropriate entity outline renderer
	 */
	@Inject(method = "shouldShowEntityOutlines", at = @At("HEAD"), cancellable = true)
	private void preventEntityOutlines(CallbackInfoReturnable<Boolean> cir) {
		if (CameraController.isCapturingCamera())
			cir.setReturnValue(false);
	}
}
