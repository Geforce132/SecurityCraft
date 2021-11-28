package net.geforcemods.securitycraft.mixin.camera;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.geforcemods.securitycraft.entity.camera.SecurityCameraEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;

/**
 * Makes sure the camera zooming works, because the fov is only updated when the camera entity is the player itself
 */
@Mixin(GameRenderer.class)
public class GameRendererMixin
{
	@ModifyConstant(method="updateFovModifierHand", constant=@Constant(floatValue=1.0F))
	private float modifyInitialFValue(float f)
	{
		if(Minecraft.getInstance().renderViewEntity instanceof SecurityCameraEntity)
			return ((SecurityCameraEntity)Minecraft.getInstance().renderViewEntity).getZoomAmount();
		else return f;
	}
}
