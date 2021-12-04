package net.geforcemods.securitycraft.mixin.camera;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.geforcemods.securitycraft.entity.camera.CameraController;
import net.geforcemods.securitycraft.network.ClientProxy;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;

@Mixin(Minecraft.class)
public class MixinMinecraft
{
	@Shadow
	public EntityPlayerSP player;

	@ModifyConstant(method = "processKeyBinds", constant = @Constant(intValue=2))
	private int resetView(int i)
	{
		if(ClientProxy.isPlayerMountedOnCamera())
			return -1; //return a low value so the check passes and causes the thirdPersonView field to be set to 0

		return i;
	}

	/**
	 * Updates keybinds after they get set, but before they get used (in World#updateEntities) for proper handling
	 */
	@Inject(method = "runTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;runTickKeyboard()V", shift = At.Shift.AFTER))
	private void updateCameraKeyBindings(CallbackInfo callback) {
		if (PlayerUtils.isPlayerMountedOnCamera(player))
			CameraController.handleKeybinds();
	}
}
