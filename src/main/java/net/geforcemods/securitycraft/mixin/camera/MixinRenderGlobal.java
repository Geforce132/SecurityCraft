package net.geforcemods.securitycraft.mixin.camera;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.ViewFrustum;

/**
 * Fixes camera chunks disappearing when the player entity moves while viewing a camera (e.g. while being in a minecart or falling)
 */
@Mixin(RenderGlobal.class)
public class MixinRenderGlobal {
	@Shadow
	@Final
	private Minecraft mc;

	@Redirect(method = "setupTerrain", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ViewFrustum;updateChunkPositions(DD)V"))
	private void onRepositionCamera(ViewFrustum viewFrustum, double x, double z) {
		if (!PlayerUtils.isPlayerMountedOnCamera(mc.player))
			viewFrustum.updateChunkPositions(x, z);
	}
}

