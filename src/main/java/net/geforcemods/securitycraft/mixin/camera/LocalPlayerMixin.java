package net.geforcemods.securitycraft.mixin.camera;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.client.player.LocalPlayer;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin {
	@Shadow
	protected abstract boolean isControlledCamera();

	/**
	 * Fixes players mounted to cameras not sending movement packets to the server, which causes them to be immovable from
	 * the server's perspective
	 */
	@Redirect(method = "sendPosition", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isControlledCamera()Z"))
	private boolean securitycraft$onIsControlledCamera(LocalPlayer player) {
		if (PlayerUtils.isPlayerMountedOnCamera(player))
			return true;

		return isControlledCamera();
	}
}
