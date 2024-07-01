package net.geforcemods.securitycraft.mixin.reinforced;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.geforcemods.securitycraft.SCContent;
import net.minecraft.block.BlockState;
import net.minecraft.block.BubbleColumnBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;

/**
 * The bubble column class hardcodes which blocks create bubble columns. This mixin exists to add support for SecurityCraft's
 * reinforced soul sand and reinforced magma block.
 */
@Mixin(BubbleColumnBlock.class)
public class BubbleColumnBlockMixin {
	@Inject(method = "getDrag", at = @At("TAIL"), cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT)
	private static void securitycraft$getDragOfReinforcedVersions(IBlockReader level, BlockPos pos, CallbackInfoReturnable<Boolean> cir, BlockState state) {
		if (state.is(SCContent.REINFORCED_SOUL_SAND.get()))
			cir.setReturnValue(false);
		else if (state.is(SCContent.REINFORCED_MAGMA_BLOCK.get()))
			cir.setReturnValue(true);
	}

	@Inject(method = "canSurvive", at = @At("TAIL"), cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT)
	private void securitycraft$reinforcedVersionsCanSurvive(BlockState state, IWorldReader level, BlockPos pos, CallbackInfoReturnable<Boolean> cir, BlockState stateBelow) {
		if (stateBelow.is(SCContent.REINFORCED_SOUL_SAND.get()) || stateBelow.is(SCContent.REINFORCED_MAGMA_BLOCK.get()))
			cir.setReturnValue(true);
	}
}
