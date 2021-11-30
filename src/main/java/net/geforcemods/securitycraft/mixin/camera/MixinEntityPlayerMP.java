package net.geforcemods.securitycraft.mixin.camera;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.entity.player.EntityPlayerMP;

/**
 * Makes sure the server does not move the player viewing a camera to the camera's position
 */
@Mixin(EntityPlayerMP.class)
public class MixinEntityPlayerMP
{
	@Redirect(method="onUpdate", at=@At(value="INVOKE", target="Lnet/minecraft/entity/player/EntityPlayerMP;setPositionAndRotation(DDDFF)V"))
	private void onUpdate(EntityPlayerMP player, double x, double y, double z, float yaw, float pitch)
	{
		System.out.println(PlayerUtils.isPlayerMountedOnCamera(player));
		if(!PlayerUtils.isPlayerMountedOnCamera(player))
			player.setPositionAndRotation(x, y, z, yaw, pitch);
	}
}
