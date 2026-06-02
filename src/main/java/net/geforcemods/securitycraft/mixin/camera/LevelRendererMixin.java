package net.geforcemods.securitycraft.mixin.camera;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.geforcemods.securitycraft.entity.camera.CameraViewAreaExtension;
import net.geforcemods.securitycraft.entity.camera.FrameFeedHandler;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.chunk.CompiledSectionMesh;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Util;

@Mixin(value = LevelRenderer.class, priority = 1100)
public abstract class LevelRendererMixin {
	@Shadow
	private SectionRenderDispatcher sectionRenderDispatcher;
	@Shadow
	@Final
	private GameRenderer gameRenderer;

	/**
	 * Updates the camera view area with the refreshed section render dispatcher when F3+A is pressed
	 */
	@Inject(method = "invalidateCompiledGeometry", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SectionOcclusionGraph;waitAndReset(Lnet/minecraft/client/renderer/ViewArea;)V"))
	private void securitycraft$onAllChanged(CallbackInfo ci) {
		CameraViewAreaExtension.allChanged(sectionRenderDispatcher, gameRenderer.mainCamera().entity().level());
	}

	/**
	 * If rendering a frame camera, makes sure that all compiled sections within the camera view area extension are properly
	 * treated as compiled (e.g. for the purpose of entity rendering)
	 */
	@Inject(method = "isSectionCompiledAndVisible", at = @At("HEAD"), cancellable = true)
	private void securitycraft$onIsSectionCompiled(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
		if (FrameFeedHandler.isCapturingCamera()) {
			SectionPos sectionPos = SectionPos.of(pos);
			SectionRenderDispatcher.RenderSection renderSection = CameraViewAreaExtension.rawFetch(sectionPos.x(), sectionPos.y(), sectionPos.z(), false);

			if (renderSection != null && renderSection.sectionMesh.get() != CompiledSectionMesh.UNCOMPILED && renderSection.getVisibility(Util.getMillis()) >= 0.3F)
				cir.setReturnValue(true);
		}
	}
}
