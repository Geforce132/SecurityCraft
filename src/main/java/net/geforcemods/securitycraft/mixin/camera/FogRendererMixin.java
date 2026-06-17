package net.geforcemods.securitycraft.mixin.camera;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.buffers.GpuBuffer;

import net.geforcemods.securitycraft.entity.camera.CameraFeed;
import net.geforcemods.securitycraft.entity.camera.FrameFeedHandler;
import net.minecraft.client.renderer.MappableRingBuffer;
import net.minecraft.client.renderer.fog.FogRenderer;

/**
 * Makes the fog renderer use a different ring buffer when rendering a frame feed, to prevent crashes from rotating the
 * vanilla ring buffer too much when multiple frame feeds are captured
 */
@Mixin(FogRenderer.class)
public class FogRendererMixin {
	@WrapOperation(method = {"getBuffer", "updateBuffer(Lnet/minecraft/client/renderer/fog/FogData;)V"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/MappableRingBuffer;currentBuffer()Lcom/mojang/blaze3d/buffers/GpuBuffer;"))
	private GpuBuffer securitycraft$useFrameFogBuffer(MappableRingBuffer instance, Operation<GpuBuffer> original) {
		CameraFeed currentFeed = FrameFeedHandler.getCurrentlyCapturedFeed();

		if (currentFeed != null)
			return original.call(currentFeed.fogRenderBuffer());

		return original.call(instance);
	}
}
