package net.geforcemods.securitycraft.mixin.sri;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.geforcemods.securitycraft.ClientHandler;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.block.model.UnbakedBlockStateModel;
import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;

/**
 * Used to capture the standalone models for rebaking them with a different
 * {@link net.minecraft.client.resources.model.BlockModelRotation}, as that's not possible by default
 */
@Mixin(ModelBakery.class)
public class ModelBakeryMixin {
	@Inject(method = "<init>(Lnet/minecraft/client/model/geom/EntityModelSet;Ljava/util/Map;Ljava/util/Map;Ljava/util/Map;Lnet/minecraft/client/resources/model/UnbakedModel;Ljava/util/Map;)V", at = @At("TAIL"))
	private void securitycraft$captureStandaloneModels(EntityModelSet entityModelSet, Map<ModelResourceLocation, UnbakedBlockStateModel> unbakedBlockStateModels, Map<ResourceLocation, ClientItem> unbakedItemStackModels, Map<ResourceLocation, UnbakedModel> unbakedPlainModels, UnbakedModel missingModel, Map<ResourceLocation, UnbakedModel> standaloneModels, CallbackInfo ci) {
		ClientHandler.setStandaloneModels(standaloneModels);
	}
}
