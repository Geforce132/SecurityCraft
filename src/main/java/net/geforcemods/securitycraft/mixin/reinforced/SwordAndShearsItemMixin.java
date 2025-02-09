package net.geforcemods.securitycraft.mixin.reinforced;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.geforcemods.securitycraft.SCContent;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShearsItem;
import net.minecraft.item.SwordItem;

/**
 * Allows reinforced cobweb to be mined faster using swords and shears
 */
@Mixin(value = {
		SwordItem.class, ShearsItem.class
})
public class SwordAndShearsItemMixin {
	@Inject(method = "getDestroySpeed", at = @At("HEAD"), cancellable = true)
	private void securitycraft$allowItemToMineReinforcedCobwebFaster(ItemStack stack, BlockState state, CallbackInfoReturnable<Float> cir) {
		if (state.is(SCContent.REINFORCED_COBWEB.get()))
			cir.setReturnValue(15.0F);
	}

	@Inject(method = "isCorrectToolForDrops", at = @At("HEAD"), cancellable = true)
	private void securitycraft$setItemToCorrectToolForDroppingReinforcedCobweb(BlockState state, CallbackInfoReturnable<Boolean> cir) {
		if (state.is(SCContent.REINFORCED_COBWEB.get()))
			cir.setReturnValue(true);
	}
}
