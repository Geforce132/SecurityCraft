package net.geforcemods.securitycraft.mixin.camera;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.geforcemods.securitycraft.entity.camera.SecurityCamera;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;

@Mixin(value = EntityPlayerMP.class, priority = 1100)
public abstract class EntityPlayerMPMixin {
	@Shadow
	public abstract Entity getSpectatingEntity();

	/**
	 * Makes sure the server does not move the player mounting a camera to the camera's position
	 */
	@Redirect(method = "onUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/EntityPlayerMP;setPositionAndRotation(DDDFF)V"))
	private void securitycraft$onUpdate(EntityPlayerMP player, double x, double y, double z, float yaw, float pitch) {
		if (!PlayerUtils.isPlayerMountedOnCamera(player))
			player.setPositionAndRotation(x, y, z, yaw, pitch);
	}

	/**
	 * Ensures that players in spectator mode are able to see players that are currently viewing a camera, because players
	 * that are spectating another entity are usually invisible for other spectators
	 */
	@Inject(method = "isSpectatedByPlayer", at = @At("HEAD"), cancellable = true)
	private void securitycraft$broadcastPlayerViewingCamera(EntityPlayerMP player, CallbackInfoReturnable<Boolean> cir) {
		if (getSpectatingEntity() instanceof SecurityCamera && player.isSpectator())
			cir.setReturnValue(true);
	}
}
