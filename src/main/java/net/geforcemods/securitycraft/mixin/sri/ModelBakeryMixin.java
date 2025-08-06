package net.geforcemods.securitycraft.mixin.sri;

import java.util.Map;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.geforcemods.securitycraft.ClientHandler;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;

/**
 * Used to capture the standalone models for rebaking them with a different
 * {@link net.minecraft.client.resources.model.BlockModelRotation}, as that's not possible by default
 */
@Mixin(ModelBakery.class)
public class ModelBakeryMixin {
	@Shadow
	@Final
	Map<ResourceLocation, UnbakedModel> unbakedModels;

	@Inject(method = "bakeModels", at = @At("HEAD"))
	private void securitycraft$captureStandaloneModels(ModelBakery.TextureGetter textureGetter, CallbackInfo ci) {
		ClientHandler.setStandaloneModels(unbakedModels);
	}
}
