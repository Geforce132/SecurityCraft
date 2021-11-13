package net.geforcemods.securitycraft.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.geforcemods.securitycraft.entity.camera.SecurityCamera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;

@Mixin(GameRenderer.class)
public class GameRendererMixin
{
	@ModifyConstant(method="tickFov", constant=@Constant(floatValue=1.0F))
	private float modifyInitialFValue(float f)
	{
		if(Minecraft.getInstance().cameraEntity instanceof SecurityCamera cam)
			return cam.getZoomAmount();
		else return f;
	}
}
