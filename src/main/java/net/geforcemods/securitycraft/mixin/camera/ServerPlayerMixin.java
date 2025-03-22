package net.geforcemods.securitycraft.mixin.camera;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;

import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.server.level.ServerPlayer;

/**
 * Makes sure the server does not move the player viewing a camera to the camera's position
 */
@Mixin(value = ServerPlayer.class, priority = 1100)
public class ServerPlayerMixin {
	@WrapWithCondition(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;absSnapTo(DDDFF)V"))
	private boolean securitycraft$shouldMoveTo(ServerPlayer player, double x, double y, double z, float yaw, float pitch) {
		return !PlayerUtils.isPlayerMountedOnCamera(player);
	}
}
