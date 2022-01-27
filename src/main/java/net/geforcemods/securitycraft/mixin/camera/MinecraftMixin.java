package net.geforcemods.securitycraft.mixin.camera;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.geforcemods.securitycraft.ClientHandler;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.PointOfView;

/**
 * Disallows players from pressing F5 (by default) to change to third person while viewing a camera
 */
@Mixin(value = Minecraft.class, priority = 1100)
public class MinecraftMixin {
	@Redirect(method = "handleKeybinds", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/GameSettings;setCameraType(Lnet/minecraft/client/settings/PointOfView;)V"))
	private void handleKeybinds(GameSettings gameSettings, PointOfView newPov) {
		if (!ClientHandler.isPlayerMountedOnCamera())
			gameSettings.setCameraType(newPov);
	}
}
