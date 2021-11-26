package net.geforcemods.securitycraft.mixin.camera;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.geforcemods.securitycraft.entity.camera.SecurityCamera;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * When a player is viewing a camera, enables sounds near the camera to be played, while sounds near the player entity are suppressed
 */
@Mixin(PlayerList.class)
public class PlayerListMixin {
	@Inject(method = "broadcast", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/server/level/ServerPlayer;getZ()D"), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
	private void broadcastToCameras(Player except, double x, double y, double z, double radius, ResourceKey<Level> dimension, Packet<?> packet, CallbackInfo callback, int iteration, ServerPlayer player) {
		if (PlayerUtils.isPlayerMountedOnCamera(player)) {
			SecurityCamera camera = (SecurityCamera)player.getCamera();
			double dX = x - camera.getX();
			double dY = y - camera.getY();
			double dZ = z - camera.getZ();

			if(dX * dX + dY * dY + dZ * dZ < radius * radius) {
				player.connection.send(packet);
			}

			callback.cancel();
		}
	}
}
