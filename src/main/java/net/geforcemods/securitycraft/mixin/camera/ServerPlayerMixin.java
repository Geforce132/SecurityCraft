package net.geforcemods.securitycraft.mixin.camera;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.geforcemods.securitycraft.entity.camera.SecurityCamera;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

@Mixin(value = ServerPlayer.class, priority = 1100)
public abstract class ServerPlayerMixin {
	@Shadow
	public abstract Entity getCamera();

	/**
	 * Makes sure the server does not move the player mounting a camera to the camera's position
	 */
	@Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;absMoveTo(DDDFF)V"))
	private void securitycraft$tick(ServerPlayer player, double x, double y, double z, float yaw, float pitch) {
		if (!PlayerUtils.isPlayerMountedOnCamera(player))
			player.absMoveTo(x, y, z, yaw, pitch);
	}

	/**
	 * Ensures that players in spectator mode are able to see players that are currently viewing a camera, because players
	 * that are spectating another entity are usually invisible for other spectators
	 */
	@Inject(method = "broadcastToPlayer", at = @At("HEAD"), cancellable = true)
	private void securitycraft$broadcastPlayerViewingCamera(ServerPlayer player, CallbackInfoReturnable<Boolean> cir) {
		if (getCamera() instanceof SecurityCamera && player.isSpectator())
			cir.setReturnValue(true);
	}
}
