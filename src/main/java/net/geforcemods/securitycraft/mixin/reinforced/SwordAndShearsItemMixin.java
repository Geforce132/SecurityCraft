package net.geforcemods.securitycraft.mixin.reinforced;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import net.geforcemods.securitycraft.SCContent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.block.state.BlockState;

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

	@ModifyReturnValue(method = "isCorrectToolForDrops", at = @At("RETURN"))
	private boolean securitycraft$setItemToCorrectToolForDroppingReinforcedCobweb(boolean toReturn, BlockState state) {
		return toReturn || state.is(SCContent.REINFORCED_COBWEB.get());
	}
}
