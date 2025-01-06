package net.geforcemods.securitycraft.mixin.camera;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.geforcemods.securitycraft.entity.camera.CameraController;
import net.geforcemods.securitycraft.entity.camera.CameraViewAreaExtension;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher.CompiledChunk;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher.RenderChunk;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
	@Shadow
	private ChunkRenderDispatcher chunkRenderDispatcher;
	@Shadow
	private ClientLevel level;

	/**
	 * When rendering the world in a frame, the necessary visible sections are captured manually within SecurityCraft. Vanilla
	 * usually does the same process in setupRender, so that method is exited early when a frame feed is rendered.
	 */
	@Inject(method = "setupRender", at = @At(value = "HEAD"), cancellable = true)
	private void securitycraft$onSetupRender(Camera camera, Frustum frustum, boolean hasCapturedFrustum, boolean isSpectator, CallbackInfo callbackInfo) {
		if (CameraController.currentlyCapturedCamera != null)
			callbackInfo.cancel();
	}

	/**
	 * Updates the camera view area with the refreshed section render dispatcher when F3+A is pressed
	 */
	@Inject(method = "allChanged", at = @At(value = "INVOKE", target = "Ljava/util/concurrent/atomic/AtomicReference;set(Ljava/lang/Object;)V"))
	private void securitycraft$onAllChanged(CallbackInfo callbackInfo) {
		CameraViewAreaExtension.allChanged(chunkRenderDispatcher, level);
	}

	/**
	 * If rendering a frame camera, makes sure that all compiled sections within the camera view area extension are properly
	 * treated as compiled (e.g. for the purpose of entity rendering)
	 */
	@Inject(method = "isChunkCompiled", at = @At("HEAD"), cancellable = true)
	private void securitycraft$onIsSectionCompiled(BlockPos pos, CallbackInfoReturnable<Boolean> callbackInfo) {
		if (CameraController.currentlyCapturedCamera != null) {
			SectionPos sectionPos = SectionPos.of(pos);
			RenderChunk renderSection = CameraViewAreaExtension.rawFetch(sectionPos.x(), sectionPos.y(), sectionPos.z(), false);

			if (renderSection != null && renderSection.compiled.get() != CompiledChunk.UNCOMPILED)
				callbackInfo.setReturnValue(true);
		}
	}
}
