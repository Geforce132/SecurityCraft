package net.geforcemods.securitycraft.mixin.camera;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.server.level.ServerPlayer;

/**
 * Makes sure the server does not move the player mounting a camera to the camera's position
 */
@Mixin(value = ServerPlayer.class, priority = 1100)
public class ServerPlayerMixin {
	@Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;absMoveTo(DDDFF)V"))
	private void securitycraft$tick(ServerPlayer player, double x, double y, double z, float yaw, float pitch) {
		if (!PlayerUtils.isPlayerMountedOnCamera(player))
			player.absMoveTo(x, y, z, yaw, pitch);
	}
}
