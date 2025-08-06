package net.geforcemods.securitycraft.mixin.sri;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.geforcemods.securitycraft.ClientHandler;
import net.minecraft.client.resources.model.AtlasSet;
import net.minecraft.client.resources.model.ModelManager;

/**
 * Used to capture the atlas preparations for rebaking the standalone models with a different
 * {@link net.minecraft.client.resources.model.BlockModelRotation}, as that's not possible by default
 */
@Mixin(ModelManager.class)
public class ModelManagerMixin {
	@SuppressWarnings({"rawtypes", "unchecked"})
	@WrapOperation(method = "loadModels", at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;"))
	private static Object securitycraft$captureAtlasPreparations(Map atlasPreperations, Object textureAtlas, Operation<AtlasSet.StitchResult> original) {
		ClientHandler.setAtlasPreperations(atlasPreperations);
		return original.call(atlasPreperations, textureAtlas);
	}
}
