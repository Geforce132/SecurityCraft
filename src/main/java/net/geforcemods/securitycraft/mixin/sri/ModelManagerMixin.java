package net.geforcemods.securitycraft.mixin.sri;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.google.common.collect.HashMultimap;

import net.geforcemods.securitycraft.ClientHandler;
import net.minecraft.client.resources.model.AtlasSet.StitchResult;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.profiling.ProfilerFiller;

/**
 * Used to capture the atlas preparations for rebaking the standalone models with a different
 * {@link net.minecraft.client.resources.model.BlockModelRotation}, as that's not possible by default
 */
@Mixin(ModelManager.class)
public class ModelManagerMixin {
	@Redirect(method = "loadModels", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/HashMultimap;create()Lcom/google/common/collect/HashMultimap;"))
	private HashMultimap<ModelResourceLocation, Material> securitycraft$captureAtlasPreparations(ProfilerFiller profiler, Map<ResourceLocation, StitchResult> atlasPreperations, ModelBakery modelBakery) {
		ClientHandler.setModelBakery(modelBakery);
		ClientHandler.setAtlasPreperations(atlasPreperations);
		return HashMultimap.create();
	}
}