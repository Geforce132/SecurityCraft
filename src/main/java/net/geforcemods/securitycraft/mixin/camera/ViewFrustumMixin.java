package net.geforcemods.securitycraft.mixin.camera;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.geforcemods.securitycraft.entity.camera.CameraViewAreaExtension;
import net.geforcemods.securitycraft.entity.camera.SecurityCamera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ViewFrustum;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Mixin(value = ViewFrustum.class, priority = 1100)
public class ViewFrustumMixin {
	/**
	 * Marks chunks within the frame camera view area as dirty when e.g. a block has been changed in them, so the frame feed
	 * updates appropriately
	 */
	@Inject(method = "markBlocksForUpdate", at = @At("HEAD"))
	private void securitycraft$onSetChunkDirty(int minX, int minY, int minZ, int maxX, int maxY, int maxZ, boolean updateImmediately, CallbackInfo ci) {
		int maxChunkX = MathHelper.intFloorDiv(maxX, 16);
		int maxChunkY = MathHelper.intFloorDiv(maxY, 16);
		int maxChunkZ = MathHelper.intFloorDiv(maxZ, 16);

		for (int x = MathHelper.intFloorDiv(minX, 16); x <= maxChunkX; x++) {
			for (int y = MathHelper.intFloorDiv(minY, 16); y <= maxChunkY; y++) {
				for (int z = MathHelper.intFloorDiv(minZ, 16); z <= maxChunkZ; z++) {
					CameraViewAreaExtension.setDirty(x, y, z, updateImmediately); //TODO this does not work yet in faraway chunks
				}
			}
		}
	}

	/**
	 * Fixes camera chunks disappearing when the player entity moves while viewing a camera (e.g. while being in a minecart or
	 * falling).
	 */
	@Inject(method = "updateChunkPositions", at = @At("HEAD"), cancellable = true)
	public void securitycraft$preventCameraRepositioning(double x, double z, CallbackInfo ci) {
		Entity camera = Minecraft.getMinecraft().getRenderViewEntity();

		if (camera instanceof SecurityCamera && (x != camera.posX || z != camera.posZ))
			ci.cancel();
	}
}
