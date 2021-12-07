package net.geforcemods.securitycraft.mixin.camera;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.geforcemods.securitycraft.entity.camera.EntitySecurityCamera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;

/**
 * Makes sure the camera zooming works, because the fov is only updated when the camera entity is the player itself
 */
@Mixin(EntityRenderer.class)
public class MixinEntityRenderer {
	@ModifyConstant(method = "updateFovModifierHand", constant = @Constant(floatValue = 1.0F))
	private float modifyInitialFValue(float f) {
		if (Minecraft.getMinecraft().getRenderViewEntity() instanceof EntitySecurityCamera)
			return ((EntitySecurityCamera)Minecraft.getMinecraft().getRenderViewEntity()).getZoomAmount();
		else
			return f;
	}
}
