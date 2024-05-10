package net.geforcemods.securitycraft.mixin.camera;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;

import net.geforcemods.securitycraft.ClientHandler;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;

/**
 * Disallows players from pressing F5 (by default) to change to third person while viewing a camera
 */
@Mixin(value = Minecraft.class, priority = 1100)
public class MinecraftMixin {
	@WrapWithCondition(method = "handleKeybinds", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Options;setCameraType(Lnet/minecraft/client/CameraType;)V"))
	private boolean securitycraft$mayChangeCameraType(Options options, CameraType newType) {
		return !ClientHandler.isPlayerMountedOnCamera();
	}
}
