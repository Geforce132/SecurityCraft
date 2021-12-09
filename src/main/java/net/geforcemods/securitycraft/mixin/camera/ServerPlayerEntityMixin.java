package net.geforcemods.securitycraft.mixin.camera;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.entity.player.ServerPlayerEntity;

/**
 * Makes sure the server does not move the player viewing a camera to the camera's position
 */
@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin
{
	@Redirect(method="tick", at=@At(value="INVOKE", target="Lnet/minecraft/entity/player/ServerPlayerEntity;setPositionAndRotation(DDDFF)V"))
	private void tick(ServerPlayerEntity player, double x, double y, double z, float yaw, float pitch)
	{
		if(!PlayerUtils.isPlayerMountedOnCamera(player))
			player.setPositionAndRotation(x, y, z, yaw, pitch);
	}
}
