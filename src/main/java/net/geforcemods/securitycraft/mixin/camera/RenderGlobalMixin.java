package net.geforcemods.securitycraft.mixin.camera;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.geforcemods.securitycraft.entity.camera.CameraController;
import net.minecraft.client.Minecraft;
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
	private void securitycraft$onSetupRender(Entity viewEntity, double partialTicks, ICamera camera, int frameCount, boolean playerSpectator, CallbackInfo ci) {
		if (CameraController.isCapturingCamera()) {
			renderContainer.initialize(viewEntity.posX, viewEntity.posY, viewEntity.posZ);
			ci.cancel();
		}
	}

	/**
	 * Fixes block updates not leading to the clientside render chunks being re-compiled properly due to constant translucency
	 * recompilations, which happen because vanilla tries to redo translucency in chunks every time the player moves, cancelling
	 * the chunk rebuild process since both happen through the same system
	 */
	@ModifyVariable(method = "renderBlockLayer(Lnet/minecraft/util/BlockRenderLayer;DILnet/minecraft/entity/Entity;)I", at = @At("HEAD"), argsOnly = true)
	private Entity securitycraft$preventTranslucencyRebuild(Entity originalViewEntity) {
		if (CameraController.isCapturingCamera())
			return Minecraft.getMinecraft().player;

		return originalViewEntity;
	}
}
