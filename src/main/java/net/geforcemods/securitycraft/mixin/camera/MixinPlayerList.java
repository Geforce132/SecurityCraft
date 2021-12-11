package net.geforcemods.securitycraft.mixin.camera;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.geforcemods.securitycraft.entity.camera.EntitySecurityCamera;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.server.management.PlayerList;

/**
 * When a player is viewing a camera, enables sounds near the camera to be played, while sounds near the player entity are suppressed
 */
@Mixin(PlayerList.class)
public class MixinPlayerList {
	@Inject(method = "sendToAllNearExcept", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/EntityPlayerMP;posZ:D", opcode = Opcodes.GETFIELD), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
	private void broadcastToCameras(EntityPlayer except, double x, double y, double z, double radius, int dimension, Packet<?> packet, CallbackInfo callback, int iteration, EntityPlayerMP player) {
		if (PlayerUtils.isPlayerMountedOnCamera(player)) {
			EntitySecurityCamera camera = (EntitySecurityCamera)player.getSpectatingEntity();
			double dX = x - camera.posX;
			double dY = y - camera.posY;
			double dZ = z - camera.posZ;

			if(dX * dX + dY * dY + dZ * dZ < radius * radius) {
				player.connection.sendPacket(packet);
			}

			callback.cancel();
		}
	}
}
