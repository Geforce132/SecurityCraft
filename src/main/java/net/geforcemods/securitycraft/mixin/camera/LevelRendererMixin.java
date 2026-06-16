package net.geforcemods.securitycraft.mixin.camera;

import org.joml.Matrix4fc;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;

import net.geforcemods.securitycraft.entity.camera.CameraViewAreaExtension;
import net.geforcemods.securitycraft.entity.camera.FrameFeedHandler;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.ViewArea;
import net.minecraft.client.renderer.chunk.CompiledSectionMesh;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Util;
import net.minecraft.util.profiling.Profiler;

@Mixin(value = LevelRenderer.class, priority = 1100)
public abstract class LevelRendererMixin {
	@Shadow
	private SectionRenderDispatcher sectionRenderDispatcher;
	@Shadow
	@Final
	private GameRenderer gameRenderer;

	/**
	 * Allows the compile task to run on render sections stored in our CameraViewAreaExtension, which allows those sections
	 * to actually compile and render something
	 */
	@WrapOperation(method = "compileSections", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ViewArea;getRenderSection(J)Lnet/minecraft/client/renderer/chunk/SectionRenderDispatcher$RenderSection;"))
	private SectionRenderDispatcher.RenderSection securitycraft$getSectionToCompile(ViewArea instance, long sectionNode, Operation<SectionRenderDispatcher.RenderSection> original) {
		if (FrameFeedHandler.isCapturingCamera()) {
			SectionRenderDispatcher.RenderSection renderSection = CameraViewAreaExtension.rawFetch(sectionNode, false);

			if (renderSection != null)
				return renderSection;
		}

		return original.call(instance, sectionNode);
	}

	/**
	 * Prevents the section occlusion graph from updating its position to the frame feed's one, to fix visible section
	 * discovery of the player's surroundings when viewing a far away frame feed
	 */
	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SectionOcclusionGraph;update(Lnet/minecraft/client/renderer/state/level/CameraRenderState;ILnet/minecraft/client/renderer/state/level/ChunkLoadingRenderState;)V"), cancellable = true)
	private void securitycraft$cancelSectionOcclusionGraphUpdate(GraphicsResourceAllocator resourceAllocator, DeltaTracker deltaTracker, boolean renderOutline, CameraRenderState cameraState, Matrix4fc modelViewMatrix, GpuBufferSlice terrainFog, Vector4f fogColor, boolean shouldRenderSky, CallbackInfo ci) {
		if (FrameFeedHandler.isCapturingCamera()) {
			Profiler.get().pop();
			ci.cancel();
		}
	}

	/**
	 * Prevents the view are from potentially repositioning itself and updating the section occlusion graph along the way
	 * when a frame feed is being captured, to fix visible section discovery of the player's surroundings
	 */
	@Inject(method = "repositionCamera", at = @At(value = "HEAD"), cancellable = true)
	private void securitycraft$cancelRepositionCamera(CameraRenderState camera, CallbackInfo ci) {
		if (FrameFeedHandler.isCapturingCamera())
			ci.cancel();
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
