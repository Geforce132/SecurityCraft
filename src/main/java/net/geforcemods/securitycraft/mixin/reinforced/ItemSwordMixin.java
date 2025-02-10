package net.geforcemods.securitycraft.mixin.reinforced;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.geforcemods.securitycraft.SCContent;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;

/**
 * Allows reinforced cobweb to be mined faster using swords
 */
@Mixin(value = ItemSword.class)
public class ItemSwordMixin {
	@Inject(method = "getDestroySpeed", at = @At("HEAD"), cancellable = true)
	private void securitycraft$allowSwordsToMineReinforcedCobwebFaster(ItemStack stack, IBlockState state, CallbackInfoReturnable<Float> cir) {
		if (state.getBlock() == SCContent.reinforcedCobweb)
			cir.setReturnValue(15.0F);
	}

	@Inject(method = "canHarvestBlock", at = @At("HEAD"), cancellable = true)
	private void securitycraft$setSwordToCorrectToolForDroppingReinforcedCobweb(IBlockState state, CallbackInfoReturnable<Boolean> cir) {
		if (state.getBlock() == SCContent.reinforcedCobweb)
			cir.setReturnValue(true);
	}
}
