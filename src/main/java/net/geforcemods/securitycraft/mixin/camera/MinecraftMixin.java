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
@Mixin(Minecraft.class)
public class MinecraftMixin
{
	@Redirect(method="processKeyBinds", at=@At(value="INVOKE", target="Lnet/minecraft/client/GameSettings;setPointOfView(Lnet/minecraft/client/settings/PointOfView;)V"))
	private void processKeyBinds(GameSettings gameSettings, PointOfView newPov)
	{
		if(!ClientHandler.isPlayerMountedOnCamera())
			gameSettings.setPointOfView(newPov);
	}
}
