package net.geforcemods.securitycraft.mixin.camera;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.geforcemods.securitycraft.entity.camera.CameraViewAreaExtension;
import net.minecraft.client.SectionUpdateTracker;

/**
 * Marks chunks within the frame camera view area as dirty when e.g. a block has been changed in them, so the frame feed
 * updates appropriately
 */
@Mixin(SectionUpdateTracker.class)
public class SectionUpdateTrackerMixin {
	@Inject(method = "setDirty", at = @At("HEAD"))
	private void securitycraft$onSetChunkDirty(int sectionX, int sectionY, int sectionZ, boolean playerChanged, CallbackInfo ci) {
		CameraViewAreaExtension.setDirty(sectionX, sectionY, sectionZ, playerChanged);
	}
}
