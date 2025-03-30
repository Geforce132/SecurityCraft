package net.geforcemods.securitycraft.mixin.camera;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.geforcemods.securitycraft.entity.camera.CameraController;
import net.geforcemods.securitycraft.network.ClientProxy;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.Entity;

/**
 * Fixes players not being able to see themselves while mounted to a camera or viewing a camera frame feed
 */
@Mixin(value = RenderPlayer.class, priority = 1100)
public class RenderPlayerMixin {
	@Redirect(method = "doRender(Lnet/minecraft/client/entity/AbstractClientPlayer;DDDFF)V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/entity/RenderManager;renderViewEntity:Lnet/minecraft/entity/Entity;", opcode = Opcodes.GETFIELD))
	private Entity securitycraft$checkForCamera(RenderManager renderManager, AbstractClientPlayer entity, double x, double y, double z, float entityYaw, float partialTicks) {
		return ClientProxy.isPlayerMountedOnCamera() || CameraController.currentlyCapturedCamera != null ? entity : renderManager.renderViewEntity;
	}
}
