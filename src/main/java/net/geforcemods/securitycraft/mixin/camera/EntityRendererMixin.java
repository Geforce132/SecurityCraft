package net.geforcemods.securitycraft.mixin.camera;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.geforcemods.securitycraft.entity.camera.SecurityCamera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;

/**
 * Makes sure the camera zooming works, because the fov is only updated when the camera entity is the player itself
 */
@Mixin(value = EntityRenderer.class, priority = 1100)
public class EntityRendererMixin {
	@ModifyConstant(method = "updateFovModifierHand", constant = @Constant(floatValue = 1.0F))
	private float modifyInitialFValue(float f) {
		if (Minecraft.getMinecraft().getRenderViewEntity() instanceof SecurityCamera)
			return ((SecurityCamera) Minecraft.getMinecraft().getRenderViewEntity()).getZoomAmount();
		else
			return f;
	}
}
