package net.geforcemods.securitycraft.mixin.f3;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.llamalad7.mixinextras.sugar.Local;

import net.geforcemods.securitycraft.misc.F3Spoofer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

/**
 * Changes the targeted block/fluid text on the right side of the screen to the disguised block/fluid, so that the player
 * cannot see the actual block (sand mine, fake water, inventory scanner (if disguised))
 */
@Mixin(targets = {
		"me.cominixo.betterf3.modules.TargetModule"
})
public class BetterF3TargetModuleMixin {
	@ModifyVariable(method = "update", name = "blockState", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/multiplayer/ClientLevel;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"))
	private BlockState securitycraft$spoofBlockState(BlockState state, @Local(ordinal = 0) BlockPos blockPos) {
		return F3Spoofer.spoofBlockState(state, blockPos);
	}

	@ModifyVariable(method = "update", name = "fluidState", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/multiplayer/ClientLevel;getFluidState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/material/FluidState;"))
	private FluidState securitycraft$spoofFluidState(FluidState originalState) {
		return F3Spoofer.spoofFluidState(originalState);
	}
}
