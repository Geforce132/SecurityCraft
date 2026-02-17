package net.geforcemods.securitycraft.mixin.camera;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import net.geforcemods.securitycraft.entity.camera.SecurityCamera;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;

/**
 * Makes sure camera zooming works, because the fov is only updated when the camera entity is the player itself
 */
@Mixin(Camera.class)
public class CameraMixin {
	@Shadow
	@Final
	private Minecraft minecraft;

	@ModifyExpressionValue(method = "tickFov", at = @At(value = "CONSTANT", args = "floatValue=1.0F"))
	private float securitycraft$modifyInitialFValue(float original) {
		if (minecraft.getCameraEntity() instanceof SecurityCamera cam)
			return cam.getZoomAmount();
		else
			return original;
	}
}
