package net.geforcemods.securitycraft.mixin.reinforced;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.geforcemods.securitycraft.api.IReinforcedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.state.properties.NoteBlockInstrument;

/**
 * Fixes instruments of reinforced blocks to match their vanilla counterparts
 */
@Mixin(NoteBlockInstrument.class)
public class NoteBlockInstrumentMixin {
	@Inject(method = "byState", at = @At("HEAD"), cancellable = true)
	private static void securitycraft$delegateReinforcedBlockToVanillaCounterpart(BlockState state, CallbackInfoReturnable<NoteBlockInstrument> cir) {
		if (state.getBlock() instanceof IReinforcedBlock)
			cir.setReturnValue(NoteBlockInstrument.byState(((IReinforcedBlock) state.getBlock()).getVanillaBlock().defaultBlockState()));
	}
}
