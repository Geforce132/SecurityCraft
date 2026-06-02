package net.geforcemods.securitycraft.mixin.camera;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.geforcemods.securitycraft.renderers.FrameBlockEntityRenderer.WrappingGeometryRenderer;
import net.minecraft.client.renderer.feature.CustomFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;

/**
 * CustomFeatureRenderer does not render the animations of textures. This mixin fixes that for
 * the cases where SecurityCraft uses a specific geometry renderer
 */
@Mixin(CustomFeatureRenderer.class)
public class CustomFeatureRendererMixin {
	@WrapOperation(method = "buildGroup", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/feature/CustomFeatureRenderer;getVertexBuilder(Lnet/minecraft/client/renderer/rendertype/RenderType;)Lcom/mojang/blaze3d/vertex/VertexConsumer;"))
	private VertexConsumer securitycraft$renderAnimation(CustomFeatureRenderer instance, RenderType renderType, Operation<VertexConsumer> original, @Local CustomFeatureRenderer.Submit submit) {
		if (submit.customGeometryRenderer() instanceof WrappingGeometryRenderer wrapper)
			return original.call(instance, wrapper.spriteId.renderType(RenderTypes::entitySolid));
		else
			return original.call(instance, renderType);
	}
}
