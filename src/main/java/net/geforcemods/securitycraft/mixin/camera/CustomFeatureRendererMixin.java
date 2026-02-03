package net.geforcemods.securitycraft.mixin.camera;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.geforcemods.securitycraft.renderers.FrameBlockEntityRenderer.WrappingGeometryRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.SubmitNodeCollector.CustomGeometryRenderer;
import net.minecraft.client.renderer.feature.CustomFeatureRenderer;

@Mixin(CustomFeatureRenderer.class)
public class CustomFeatureRendererMixin {
	@WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SubmitNodeCollector$CustomGeometryRenderer;render(Lcom/mojang/blaze3d/vertex/PoseStack$Pose;Lcom/mojang/blaze3d/vertex/VertexConsumer;)V"))
	private void render(CustomGeometryRenderer renderer, Pose pose, VertexConsumer vertexConsumer, Operation<Void> original, SubmitNodeCollection collection, BufferSource buffer) {
		if (renderer instanceof WrappingGeometryRenderer wrapper)
			renderer.render(pose, wrapper.material.buffer(Minecraft.getInstance().getAtlasManager(), buffer, RenderType::entitySolid));
		else
			original.call(renderer, pose, vertexConsumer);
	}
}
