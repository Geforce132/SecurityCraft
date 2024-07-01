package net.geforcemods.securitycraft.mixin.dev;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.renderer.GlDebugTextUtils;
import net.minecraftforge.fml.loading.FMLEnvironment;

/**
 * This spams my log in a development environment, so it's being turned off
 */
@Mixin(GlDebugTextUtils.class)
public class GlDebugTextUtilsMixin {
	@Inject(method = "printDebugLog", at = @At("HEAD"), cancellable = true)
	private static void securitycraft$silenceDebugLogInDev(int i1, int i2, int i3, int i4, int i5, long l1, long l2, CallbackInfo ci) {
		if (!FMLEnvironment.production)
			ci.cancel();
	}
}
