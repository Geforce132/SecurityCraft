package net.geforcemods.securitycraft.mixin.reinforced;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.geforcemods.securitycraft.SCContent;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;

/**
 * Allows reinforced cobweb and wool to be mined faster using shears
 */
@Mixin(value = ItemShears.class)
public class ItemShearsMixin {
	@Inject(method = "getDestroySpeed", at = @At("HEAD"), cancellable = true)
	private void securitycraft$allowShearsToMineReinforcedCobwebAndWoolFaster(ItemStack stack, IBlockState state, CallbackInfoReturnable<Float> cir) {
		if (state.getBlock() == SCContent.reinforcedCobweb)
			cir.setReturnValue(15.0F);
		else if (state.getBlock() == SCContent.reinforcedWool)
			cir.setReturnValue(5.0F);
	}

	@Inject(method = "canHarvestBlock", at = @At("HEAD"), cancellable = true)
	private void securitycraft$setSwordToCorrectToolForDroppingReinforcedCobweb(IBlockState state, CallbackInfoReturnable<Boolean> cir) {
		if (state.getBlock() == SCContent.reinforcedCobweb)
			cir.setReturnValue(true);
	}
}
