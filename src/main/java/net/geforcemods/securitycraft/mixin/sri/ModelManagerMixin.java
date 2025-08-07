package net.geforcemods.securitycraft.mixin.sri;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.geforcemods.securitycraft.ClientHandler;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.client.model.ForgeModelBakery;

/**
 * Used to capture the model bakery for rebaking the standalone models with a different
 * {@link net.minecraft.client.resources.model.BlockModelRotation}, as that's not possible by default
 */
@Mixin(ModelManager.class)
public class ModelManagerMixin {
	@Inject(method = "prepare", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILSOFT)
	private void securitycraft$captureModelBakery(ResourceManager resourceManager, ProfilerFiller profiler, CallbackInfoReturnable<ForgeModelBakery> cir, ForgeModelBakery modelBakery) {
		ClientHandler.setModelBakery(modelBakery);
	}
}
