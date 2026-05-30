package net.geforcemods.securitycraft.mixin.camera;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.client.player.LocalPlayer;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin {
	/**
	 * Fixes players mounted to cameras not sending movement packets to the server, which causes them to be immovable from
	 * the server's perspective
	 */
	@WrapOperation(method = "sendPosition", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isControlledCamera()Z"))
	private boolean securitycraft$onIsControlledCamera(LocalPlayer player, Operation<Boolean> original) {
		if (PlayerUtils.isPlayerMountedOnCamera(player))
			return true;

		return original.call(player);
	}
}
