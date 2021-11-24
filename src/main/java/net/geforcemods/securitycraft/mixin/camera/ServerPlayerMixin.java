package net.geforcemods.securitycraft.mixin.camera;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.server.level.ServerPlayer;

/**
 * Makes sure the server does not move the player viewing a camera to the camera's position
 */
@Mixin(ServerPlayer.class)
public class ServerPlayerMixin
{
	@Redirect(method="tick", at=@At(value="INVOKE", target="Lnet/minecraft/server/level/ServerPlayer;absMoveTo(DDDFF)V"))
	private void tick(ServerPlayer player, double x, double y, double z, float yaw, float pitch)
	{
		if(!PlayerUtils.isPlayerMountedOnCamera(player))
			player.absMoveTo(x, y, z, yaw, pitch);
	}
}
