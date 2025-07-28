package net.geforcemods.securitycraft.mixin.camera;

import java.util.List;

import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;

import net.geforcemods.securitycraft.compat.ium.IumCompat;
import net.geforcemods.securitycraft.entity.camera.CameraViewAreaExtension;
import net.geforcemods.securitycraft.entity.camera.FrameFeedHandler;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.chunk.CompiledSectionMesh;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;

@Mixin(value = LevelRenderer.class, priority = 1100)
public abstract class LevelRendererMixin {
	@Shadow
	private SectionRenderDispatcher sectionRenderDispatcher;
	@Shadow
	private ClientLevel level;
	@Shadow
	@Final
	private List<Entity> visibleEntities;
	@Shadow
	private int visibleEntityCount;
	@Unique
	private boolean securitycraft$entityOutlineRendered;

	@Shadow
	protected abstract boolean collectVisibleEntities(Camera camera, Frustum frustum, List<Entity> output);

	/**
	 * The first purpose of this mixin is to allow entities to render in the first frame of a render section being rendered when
	 * Sodium is installed. This needs to be done because entities would never render at all in the first frame not render due
	 * to a bug within Sodium. To allow entities to render in such circumstances, it is attempted to collect all renderable
	 * entities a second time after LevelRenderer#setupRender has been called, since this is required for Sodium's entity
	 * culling logic to work properly. Note: Only allowing this mixin to work when the first collection of renderable entities
	 * has found 0 entities does not work correctly, since some entities, like ones with the Glowing effect, are unaffected by
	 * Sodium entity culling and thus always rendered.
	 * The second purpose of this mixin is to capture the fog color used when rendering a frame world, to be able to render it
	 * in the background of a frame feed.
	 */
	@Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;compileSections(Lnet/minecraft/client/Camera;)V"))
	private void securitycraft$afterSetupRender(GraphicsResourceAllocator graphicsResourceAllocator, DeltaTracker deltaTracker, boolean renderBlockOutline, Camera camera, Matrix4f frustumMatrix, Matrix4f projectionMatrix, GpuBufferSlice bufferSlice, Vector4f fogColor, boolean renderSky, CallbackInfo ci, @Local Frustum frustum) {
		if (IumCompat.isActive() && FrameFeedHandler.hasFeeds()) {
			ProfilerFiller profiler = Profiler.get();

			profiler.popPush("cullEntities");
			visibleEntities.clear();
			securitycraft$entityOutlineRendered = collectVisibleEntities(camera, frustum, visibleEntities);
			visibleEntityCount = visibleEntities.size();
			profiler.popPush("compile_sections");
		}

		if (FrameFeedHandler.isCapturingCamera())
			FrameFeedHandler.getCurrentlyCapturedFeed().setBackgroundColor(fogColor);
	}

	/**
	 * If the collection of renderable entities within the mixin above returned that one or more entities should be rendered with
	 * an outline, correctly modify the respective flag in LevelRenderer#renderLevel so the shader post chain respects the
	 * outline.
	 */
	@ModifyVariable(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;compileSections(Lnet/minecraft/client/Camera;)V"), ordinal = 3)
	private boolean securitycraft$modifyEntityOutlineRendered(boolean original) {
		if (IumCompat.isActive() && securitycraft$entityOutlineRendered) {
			securitycraft$entityOutlineRendered = false;
			return true;
		}

		return original;
	}

	/**
	 * When rendering the world in a frame, the necessary visible sections are captured manually within SecurityCraft. Vanilla
	 * usually does the same process in setupRender, so that method is exited early when a frame feed is rendered. However, when
	 * Embeddium or Sodium is installed, these mods may perform their visible section capture themselves since it's much more
	 * performant, and since that happens in setupRender too, the method is not exited early in this case.
	 */
	@Inject(method = "setupRender", at = @At("HEAD"), cancellable = true)
	private void securitycraft$onSetupRender(Camera camera, Frustum frustum, boolean hasCapturedFrustum, boolean isSpectator, CallbackInfo ci) {
		if (FrameFeedHandler.isCapturingCamera() && !IumCompat.isActive())
			ci.cancel();
	}

	/**
	 * Updates the camera view area with the refreshed section render dispatcher when F3+A is pressed
	 */
	@Inject(method = "allChanged", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SectionOcclusionGraph;waitAndReset(Lnet/minecraft/client/renderer/ViewArea;)V"))
	private void securitycraft$onAllChanged(CallbackInfo ci) {
		CameraViewAreaExtension.allChanged(sectionRenderDispatcher, level);
	}

	/**
	 * If rendering a frame camera, makes sure that all compiled sections within the camera view area extension are properly
	 * treated as compiled (e.g. for the purpose of entity rendering)
	 */
	@Inject(method = "isSectionCompiled", at = @At("HEAD"), cancellable = true)
	private void securitycraft$onIsSectionCompiled(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
		if (FrameFeedHandler.isCapturingCamera()) {
			SectionPos sectionPos = SectionPos.of(pos);
			SectionRenderDispatcher.RenderSection renderSection = CameraViewAreaExtension.rawFetch(sectionPos.x(), sectionPos.y(), sectionPos.z(), false);

			if (renderSection != null && renderSection.sectionMesh.get() != CompiledSectionMesh.UNCOMPILED)
				cir.setReturnValue(true);
		}
	}
}
