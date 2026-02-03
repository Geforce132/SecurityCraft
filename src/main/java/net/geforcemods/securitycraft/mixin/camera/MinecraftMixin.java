package net.geforcemods.securitycraft.mixin.camera;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.entity.camera.CameraController;
import net.geforcemods.securitycraft.entity.camera.SecurityCamera;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.world.entity.Entity;

@Mixin(value = Minecraft.class, priority = 1100)
public class MinecraftMixin {
	/**
	 * Disallows players from pressing F5 (by default) to change to third person while being mounted to a camera
	 */
	@WrapWithCondition(method = "handleKeybinds", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Options;setCameraType(Lnet/minecraft/client/CameraType;)V"))
	private boolean securitycraft$mayChangeCameraType(Options options, CameraType newType) {
		return !ClientHandler.isPlayerMountedOnCamera();
	}

	/**
	 * When the debug feature of camera reset tracing is enabled, this mixin observes any calls to setCameraEntity and checks
	 * if the camera entity is changed back after a Security Camera has been mounted. If such a camera entity reset is
	 * detected, a message is sent in chat and the stacktrace is logged, so it's easier to find out if there is another mod
	 * responsible for the reset.
	 */
	@Inject(method = "setCameraEntity", at = @At("HEAD"))
	private void securitycraft$onSetCameraEntity(Entity newCameraEntity, CallbackInfo ci) {
		if (ConfigHandler.CLIENT.debugCameraResetTracing.get() && Minecraft.getInstance().getCameraEntity() instanceof SecurityCamera && !(newCameraEntity instanceof SecurityCamera)) {
			StackTraceElement[] stacktrace = StackWalker.getInstance().walk(frames -> frames.skip(1).map(StackWalker.StackFrame::toStackTraceElement).toArray(StackTraceElement[]::new)); //0 is this mixin, 1 is setCameraEntity
			Exception exception = new Exception();
			long millisSinceLastMount = CameraController.getMillisSinceLastMount();

			PlayerUtils.sendMessageToPlayer(Minecraft.getInstance().player, Utils.localize(SCContent.SECURITY_CAMERA.get().getDescriptionId()), Utils.localize("messages.securitycraft:security_camera.camera_reset_detected", millisSinceLastMount), ChatFormatting.RED, true);
			SecurityCraft.LOGGER.warn("Detected camera entity reset {}ms after the camera was mounted. If this time interval is very short, a mod incompatibility could be at fault. See stacktrace below for potential culprits", millisSinceLastMount);
			exception.setStackTrace(stacktrace);
			exception.printStackTrace();
		}
	}
}
