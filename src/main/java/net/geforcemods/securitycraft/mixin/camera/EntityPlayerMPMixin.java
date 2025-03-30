package net.geforcemods.securitycraft.mixin.camera;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.entity.player.EntityPlayerMP;

/**
 * Makes sure the server does not move the player mounting a camera to the camera's position
 */
@Mixin(value = EntityPlayerMP.class, priority = 1100)
public class EntityPlayerMPMixin {
	@Redirect(method = "onUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/EntityPlayerMP;setPositionAndRotation(DDDFF)V"))
	private void securitycraft$onUpdate(EntityPlayerMP player, double x, double y, double z, float yaw, float pitch) {
		if (!PlayerUtils.isPlayerMountedOnCamera(player))
			player.setPositionAndRotation(x, y, z, yaw, pitch);
	}
}
