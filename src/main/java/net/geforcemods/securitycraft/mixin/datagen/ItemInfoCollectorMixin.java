package net.geforcemods.securitycraft.mixin.datagen;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.neoforged.fml.loading.FMLEnvironment;

@Mixin(targets = "net.minecraft.client.data.models.ModelProvider$ItemInfoCollector")
public class ItemInfoCollectorMixin {
	@Inject(method = "finalizeAndValidate", at = @At("HEAD"), cancellable = true)
	private void finalizeAndValidate(CallbackInfo ci) {
		if (!FMLEnvironment.production)
			ci.cancel();
	}
}
