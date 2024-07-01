package net.geforcemods.securitycraft.mixin.reinforced;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.geforcemods.securitycraft.SCTags;
import net.minecraft.block.BlockState;
import net.minecraft.world.gen.feature.TreeFeature;

/**
 * Enables saplings to grow when placed on the blocks contained in {@link SCTags.Blocks#REINFORCED_DIRT}
 */
@Mixin(TreeFeature.class)
public class TreeFeatureMixin {
	@Inject(method = "lambda$isGrassOrDirtOrFarmland$4", at = @At("HEAD"), cancellable = true)
	private static void securitycraft$onCheckGrassOrDirt(BlockState state, CallbackInfoReturnable<Boolean> callback) {
		if (state.is(SCTags.Blocks.REINFORCED_DIRT))
			callback.setReturnValue(true);
	}
}
