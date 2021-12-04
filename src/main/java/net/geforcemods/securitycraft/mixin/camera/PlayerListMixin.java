package net.geforcemods.securitycraft.mixin.camera;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.geforcemods.securitycraft.entity.camera.SecurityCameraEntity;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.IPacket;
import net.minecraft.server.management.PlayerList;
import net.minecraft.world.dimension.DimensionType;

/**
 * When a player is viewing a camera, enables sounds near the camera to be played, while sounds near the player entity are suppressed
 */
@Mixin(PlayerList.class)
public class PlayerListMixin {
	@Inject(method = "sendToAllNearExcept", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/entity/player/ServerPlayerEntity;getPosZ()D"), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
	private void broadcastToCameras(PlayerEntity except, double x, double y, double z, double radius, DimensionType dimension, IPacket<?> packet, CallbackInfo callback, int iteration, ServerPlayerEntity player) {
		if (PlayerUtils.isPlayerMountedOnCamera(player)) {
			SecurityCameraEntity camera = (SecurityCameraEntity)player.getSpectatingEntity();
			double dX = x - camera.getPosX();
			double dY = y - camera.getPosY();
			double dZ = z - camera.getPosZ();

			if(dX * dX + dY * dY + dZ * dZ < radius * radius) {
				player.connection.sendPacket(packet);
			}

			callback.cancel();
		}
	}
}
