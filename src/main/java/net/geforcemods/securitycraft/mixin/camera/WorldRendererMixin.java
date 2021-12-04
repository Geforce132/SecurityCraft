package net.geforcemods.securitycraft.mixin.camera;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.ViewFrustum;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
	@Shadow
	@Final
	private Minecraft mc;

	/**
	 * Fixes camera chunks disappearing when the player entity moves while viewing a camera (e.g. while being in a minecart or falling)
	 */
	@Redirect(method = "setupTerrain", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ViewFrustum;updateChunkPositions(DD)V"))
	private void onRepositionCamera(ViewFrustum viewFrustum, double x, double z) {
		if (!PlayerUtils.isPlayerMountedOnCamera(mc.player))
			viewFrustum.updateChunkPositions(x, z);
	}

	/*
	 * Fixes players not being able to see themselves when viewing a camera
	 */
	@Redirect(method = "updateCameraAndRender", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ActiveRenderInfo;getRenderViewEntity()Lnet/minecraft/entity/Entity;", ordinal = 3))
	private Entity makePlayerRenderable(ActiveRenderInfo activeRenderInfo) {
		if (PlayerUtils.isPlayerMountedOnCamera(mc.player))
			return mc.player;

		return activeRenderInfo.getRenderViewEntity();
	}
}
