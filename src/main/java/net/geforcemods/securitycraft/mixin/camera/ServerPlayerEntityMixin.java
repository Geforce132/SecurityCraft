package net.geforcemods.securitycraft.mixin.camera;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.entity.player.ServerPlayerEntity;

/**
 * Makes sure the server does not move the player mounting a camera to the camera's position
 */
@Mixin(value = ServerPlayerEntity.class, priority = 1100)
public class ServerPlayerEntityMixin {
	@Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/ServerPlayerEntity;absMoveTo(DDDFF)V"))
	private void securitycraft$tick(ServerPlayerEntity player, double x, double y, double z, float yaw, float pitch) {
		if (!PlayerUtils.isPlayerMountedOnCamera(player))
			player.absMoveTo(x, y, z, yaw, pitch);
	}
}
