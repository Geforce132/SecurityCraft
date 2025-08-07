package net.geforcemods.securitycraft.mixin.sri;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.geforcemods.securitycraft.ClientHandler;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.ModelManager;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraftforge.client.model.ModelLoader;

/**
 * Used to capture the model bakery for rebaking the standalone models with a different
 * {@link net.minecraft.client.renderer.model.ModelRotation}, as that's not possible by default
 */
@Mixin(ModelManager.class)
public class ModelManagerMixin {
	@Inject(method = "prepare", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILSOFT)
	private void securitycraft$captureModelBakery(IResourceManager resourceManager, IProfiler profiler, CallbackInfoReturnable<ModelBakery> cir, ModelLoader modelBakery) {
		ClientHandler.setModelBakery(modelBakery);
	}
}
