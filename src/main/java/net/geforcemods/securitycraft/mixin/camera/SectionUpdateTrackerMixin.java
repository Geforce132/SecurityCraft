package net.geforcemods.securitycraft.mixin.camera;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.geforcemods.securitycraft.entity.camera.CameraViewAreaExtension;
import net.geforcemods.securitycraft.entity.camera.FrameFeedHandler;
import net.minecraft.client.SectionUpdateTracker;
import net.minecraft.client.SectionUpdateTracker.SectionDirtyState;


@Mixin(SectionUpdateTracker.class)
public class SectionUpdateTrackerMixin {
	/**
	 * Marks chunks within the frame camera view area as dirty when e.g. a block has been changed in them, so the frame feed
	 * updates appropriately
	 */
	@Inject(method = "setDirty", at = @At("HEAD"))
	private void securitycraft$onSetChunkDirty(int sectionX, int sectionY, int sectionZ, boolean playerChanged, CallbackInfo ci) {
		CameraViewAreaExtension.setDirty(sectionX, sectionY, sectionZ, playerChanged);
	}

	/**
	 * Ensures that dirty chunks within the camera storage are recompiled when the frame feed is captured
	 */
	@Inject(method = "getDirtyState", at = @At("HEAD"), cancellable = true)
	private void securitycraft$onGetDirtyState(long sectionNode, CallbackInfoReturnable<SectionDirtyState> cir) {
		if (FrameFeedHandler.isCapturingCamera()) {
			SectionDirtyState dirtyState = CameraViewAreaExtension.getDirtyState(sectionNode);

			if (dirtyState != null)
				cir.setReturnValue(dirtyState);
		}
	}
}
