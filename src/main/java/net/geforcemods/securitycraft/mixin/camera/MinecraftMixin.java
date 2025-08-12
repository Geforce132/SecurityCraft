package net.geforcemods.securitycraft.mixin.camera;

import java.util.Arrays;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.entity.camera.CameraController;
import net.geforcemods.securitycraft.entity.camera.SecurityCamera;
import net.geforcemods.securitycraft.network.ClientProxy;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.util.text.TextFormatting;

@Mixin(value = Minecraft.class, priority = 1100)
public class MinecraftMixin {
	@Shadow
	public EntityPlayerSP player;

	/**
	 * Fixes players being able to change to third person while being mounted to a camera
	 */
	@ModifyConstant(method = "processKeyBinds", constant = @Constant(intValue = 2))
	private int securitycraft$resetView(int i) {
		if (ClientProxy.isPlayerMountedOnCamera())
			return -1; //return a low value so the check passes and causes the thirdPersonView field to be set to 0

		return i;
	}

	/**
	 * Fixes players being able to move mounted entities while being mounted to a camera, by updating keybinds used by the camera
	 * after they get set, but before they get used in World#updateEntities
	 */
	@Inject(method = "runTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;runTickKeyboard()V", shift = At.Shift.AFTER))
	private void securitycraft$updateCameraKeyBindings(CallbackInfo callback) {
		if (PlayerUtils.isPlayerMountedOnCamera(player))
			CameraController.handleKeybinds();
	}

	/**
	 * When the debug feature of camera reset tracing is enabled, this mixin observes any calls to setCameraEntity and checks
	 * if the camera entity is changed back after a Security Camera has been mounted. If such a camera entity reset is
	 * detected, a message is sent in chat and the stacktrace is logged, so it's easier to find out if there is another mod
	 * responsible for the reset.
	 */
	@Inject(method = "setRenderViewEntity", at = @At("HEAD"))
	private void securitycraft$onSetCameraEntity(Entity newCameraEntity, CallbackInfo ci) {
		if (ConfigHandler.debugCameraResetTracing && Minecraft.getMinecraft().getRenderViewEntity() instanceof SecurityCamera && !(newCameraEntity instanceof SecurityCamera)) {
			StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
			StackTraceElement[] cutStacktrace = Arrays.copyOfRange(stacktrace, 1, stacktrace.length); //0 is this mixin, 1 is setCameraEntity
			Exception exception = new Exception();
			long millisSinceLastMount = CameraController.getMillisSinceLastMount();

			PlayerUtils.sendMessageToPlayer(Minecraft.getMinecraft().player, Utils.localize(SCContent.securityCamera), Utils.localize("messages.securitycraft:security_camera.camera_reset_detected", millisSinceLastMount), TextFormatting.RED, true);
			SecurityCraft.LOGGER.warn("Detected camera entity reset {}ms after the camera was mounted. If this time interval is very short, a mod incompatibility could be at fault. See stacktrace below for potential culprits", millisSinceLastMount);
			exception.setStackTrace(cutStacktrace);
			exception.printStackTrace();
		}
	}
}
