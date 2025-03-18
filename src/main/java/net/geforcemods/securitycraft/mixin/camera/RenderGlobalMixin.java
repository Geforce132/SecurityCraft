package net.geforcemods.securitycraft.mixin.camera;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.geforcemods.securitycraft.entity.camera.CameraController;
import net.minecraft.client.renderer.ChunkRenderContainer;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.entity.Entity;

@Mixin(RenderGlobal.class)
public class RenderGlobalMixin {
	@Shadow
	private ChunkRenderContainer renderContainer;

	/**
	 * When rendering the world in a frame, the necessary visible sections are captured manually within SecurityCraft. Vanilla
	 * usually does the same process in setupRender, so that method is exited early when a frame feed is rendered. However,
	 * ChunkRenderContainer still needs to be initialized with the correct origin position, or else chunks will render from the
	 * player's perspective instead of the camera's.
	 */
	@Inject(method = "setupTerrain", at = @At("HEAD"), cancellable = true)
	public void securitycraft$onSetupRender(Entity viewEntity, double partialTicks, ICamera camera, int frameCount, boolean playerSpectator, CallbackInfo ci) {
		if (CameraController.currentlyCapturedCamera != null) {
			renderContainer.initialize(viewEntity.posX, viewEntity.posY, viewEntity.posZ);
			ci.cancel();
		}
	}
}
