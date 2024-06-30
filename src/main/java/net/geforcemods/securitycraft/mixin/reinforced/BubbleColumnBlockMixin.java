package net.geforcemods.securitycraft.mixin.reinforced;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.geforcemods.securitycraft.SCContent;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BubbleColumnBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * The bubble column class hardcodes which blocks create bubble columns. This mixin exists to add support for SecurityCraft's
 * reinforced soul sand and reinforced magma block.
 */
@Mixin(BubbleColumnBlock.class)
public class BubbleColumnBlockMixin {
	@Inject(method = "getColumnState", at = @At("TAIL"), cancellable = true)
	private static void securitycraft$supportReinforcedVersions(BlockState state, CallbackInfoReturnable<BlockState> cir) {
		if (state.is(SCContent.REINFORCED_SOUL_SAND.get()))
			cir.setReturnValue(Blocks.BUBBLE_COLUMN.defaultBlockState().setValue(BubbleColumnBlock.DRAG_DOWN, false));
		else if (state.is(SCContent.REINFORCED_MAGMA_BLOCK.get()))
			cir.setReturnValue(Blocks.BUBBLE_COLUMN.defaultBlockState().setValue(BubbleColumnBlock.DRAG_DOWN, true));
	}
}
