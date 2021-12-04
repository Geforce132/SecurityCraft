package net.geforcemods.securitycraft.mixin.camera;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.geforcemods.securitycraft.ClientHandler;
import net.minecraft.client.Minecraft;

/**
 * Disallows players from pressing F5 (by default) to change to third person while viewing a camera
 */
@Mixin(Minecraft.class)
public class MinecraftMixin
{
	@ModifyConstant(method = "processKeyBinds", constant = @Constant(intValue=2))
	private int resetView(int i)
	{
		if(ClientHandler.isPlayerMountedOnCamera())
			return -1; //return a low value so the check passes and causes the thirdPersonView field to be set to 0

		return i;
	}
}
