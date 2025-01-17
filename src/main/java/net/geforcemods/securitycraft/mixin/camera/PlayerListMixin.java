package net.geforcemods.securitycraft.mixin.camera;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.geforcemods.securitycraft.entity.camera.SecurityCamera;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.IPacket;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.World;

/**
 * When a player is mounted to a camera, enables sounds near the camera to be played, while sounds near the player entity are
 * suppressed
 */
@Mixin(value = PlayerList.class, priority = 1100)
public class PlayerListMixin {
	@Inject(method = "broadcast", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/entity/player/ServerPlayerEntity;getZ()D"), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
	private void securitycraft$broadcastToCameras(PlayerEntity except, double x, double y, double z, double radius, RegistryKey<World> dimension, IPacket<?> packet, CallbackInfo callback, int iteration, ServerPlayerEntity player) {
		if (PlayerUtils.isPlayerMountedOnCamera(player)) {
			SecurityCamera camera = (SecurityCamera) player.getCamera();
			double dX = x - camera.getX();
			double dY = y - camera.getY();
			double dZ = z - camera.getZ();

			if (dX * dX + dY * dY + dZ * dZ < radius * radius)
				player.connection.send(packet);

			callback.cancel();
		}
	}
}
